(ns spook.main
  (:require [clojure.java.io :as io]
            [spook.logging :as log]
            [spook.types :as types]
            [spook.state :as state]
            [clojure.spec :as spec])
  (:gen-class))


(defn wait [time]
  (Thread/sleep(* 1000 time)))

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
;;    (log/trace (apply str (concat "check side effect (hold/stop) or timeout:" (str ret))))
    ret))

(defn build-good-execution []
  (let [state (state/current-state)
        wake-at (+ (System/currentTimeMillis) (* (:endure-good state) 1000))]
    (cycle [[(partial something-happened (:workingdir state) wake-at)(:do-good state)]])))


(defn build-evil-execution []
  (let [state (state/current-state)
        wake-at (+ (System/currentTimeMillis) (* (:delay-evil state) 1000))]
    (cycle [[(partial something-happened (:workingdir state) wake-at)(:do-evil state)]])))


(defn execute [coll]
  "process the current execution list as long as something-happened returns :continue.
   Other return values: :stop exits the program; :break finishes the current execute step :hold waits"
  (let [state (state/current-state)
        todo-coll (drop-while (fn[[f g]](do (eval g)(= (f) :continue))) coll)
        res ((first (first todo-coll)))]
    (case res
      :break (do
               (log/info "timeout for current execution")
               res)
      :stop  (do
               (log/info "stopping run")
               res)
      :hold (do
             (log/info "waiting for hold file to vanish")
             (drop-while (fn[[f g]](do (eval g)(= (f) :hold)))
                         (cycle [[(partial something-happened (:workingdir state) (System/currentTimeMillis))(wait 1)]]))
             (execute coll))
      :continue (execute coll))))


(defn dispatch [rec]
  (if (spec/valid? ::types/state rec)
    (let [state (state/init-state (log/init rec))]
      (try
        (log/info "spook started, doing good")

        (if (= (execute (build-good-execution)) :stop)
          :stop
          (do
            (log/info "spook intensified, doing evil")
            (execute (build-evil-execution))))
        (catch Exception e
          (prn "Exception:" (.getMessage e)))))
    (prn "Argument invalid: " rec)))


(defn -main [& args]
  "the fully equipped git ready to be configured to all sorts of mischief"
  (let [tmp (first args)]
    (if (coll? tmp)
      (dispatch tmp)
      (if (and (string? tmp)(.exists (io/file tmp)))
        (dispatch (read-string (slurp tmp)))
        (prn "Argument " tmp " is not valid")))))
