(ns spook.main
  (:require [spook.types :as types]
            [clojure.spec :as spec])
  (:gen-class))


(defn wait [time]
  (Thread/sleep(* 0.001 time)))

(defn control-files [workdir]
  (let [wd (if (= (last workdir) \/)
             workdir
             (concat workdir "/"))
        bmap (vec
               (map #(.exists (clojure.java.io/as-file %))
                    [(apply str (concat wd "stop.txt"))
                     (apply str (concat wd "hold.txt"))]))]
    bmap))


(defn _something-happened [wdir time]
  "check if the timer has expired or something happened in between"
  (let [bmap (control-files wdir)]
    (if (nth bmap 0)
     :stop
     (if (nth bmap 1)
       :hold
       (if (< (System/currentTimeMillis) time) :continue :break)))))

(defn something-happened [wdir time]
  (let [ret (_something-happened wdir time)]
(prn "Something happened:" ret)
    ret))

(defn build-good-execution [state]
  (let [curtime (System/currentTimeMillis)
        wake-at (+ curtime (* (:delay-good state) 1000))]
    (cycle [[(partial something-happened (:workingdir state) wake-at)(:do-good state)]])))


(defn build-evil-execution [state]
  (let [curtime (System/currentTimeMillis)
        wake-at (+ curtime (* (:delay-evil state) 1000))]
    (cycle [[(partial something-happened (:workingdir state) wake-at)(:do-evil state)]])))


(defn execute [state coll]
  "process the current execution list as long as something-happened returns :continue.
  Other return values: :stop exits the program; :break finishes the current execute step :hold waits"
  (let [todo-coll (drop-while (fn[[f g]](do (g)(= (f) :continue))) coll)
        res ((first (first todo-coll)))]
    (case res
      :break res
      :stop res
      :hold (do
             (drop-while (fn[[f g]](do (g)(= (f) :hold)))
                         (cycle [[(partial something-happened (:workingdir state) (System/currentTimeMillis))(wait 1000)]]))
             (execute state coll)))))


(defn -main
  "the fully equipped git ready to be configured to all sorts of mischief"
  [& args]
  (when (spec/valid? ::types/state args)
    (if (= (execute args (build-good-execution args)) :stop)
      :stop
      (execute args (build-evil-execution args)))))
