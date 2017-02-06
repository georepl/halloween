(ns spook.main-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [spook.state :as state]
            [spook.main :refer :all]))

(defn abs [x]
  (if (neg? x) (- 0 x) x))

(deftest wait-test
  (testing "wait function"
    (let [curtime (System/currentTimeMillis)]
      (wait 11)
      (let [now (- (System/currentTimeMillis) curtime)]
        (is (<= (abs (- 12000 now)) 1000))))))

(deftest control-files-test
  (testing "existance test of hold.txt and stop.txt, respectively"
    (is (= :break (#'something-happened "test/workbench" (* (System/currentTimeMillis) 1))))
    (is (= :break (#'something-happened "test/workbench/" (* (System/currentTimeMillis) 1))))))

(deftest something-happened-test
  (testing "stop and hold plus precedence (stop before hold before time)"
    (spit "test/workbench/stop.txt" "")
    (is (= :stop (#'something-happened "test/workbench" (* (System/currentTimeMillis) 2))))
    (io/delete-file "test/workbench/stop.txt")
    (spit "test/workbench/hold.txt" "")
    (is (= :hold (#'something-happened "test/workbench" (* (System/currentTimeMillis) 2))))
    (spit "test/workbench/stop.txt" "")
    (is (= :stop (#'something-happened "test/workbench/" (* (System/currentTimeMillis) 2))))
    (is (= :stop (#'something-happened "test/workbench/" (System/currentTimeMillis))))
    (io/delete-file "test/workbench/stop.txt")
    (io/delete-file "test/workbench/hold.txt")
    (is (= :break (#'something-happened "test/workbench" (System/currentTimeMillis))))
    (is (= :continue (#'something-happened "test/workbench" (* (System/currentTimeMillis) 2))))
))

(deftest build-good-execution-test
  (testing "build-good-execution"
    (state/init-state { :endure-good 5 :do-good #(+ 2 3) :logs { :logfile "test/testprocid.txt"
                                                                 :progname "testprocid" } })
    (let [[f g] (first (build-good-execution))]
      (is (and (fn? f)(fn? g))))))

(deftest build-evil-execution-test
  (testing "build-evil-execution"
    (state/init-state { :delay-evil 5 :do-evil #(* 2 3) :logs { :logfile "test/testprocid.txt"
                                                                :progname "testprocid" } })
    (let [[f g] (first (build-evil-execution))]
      (is (and (fn? f)(fn? g))))))

(deftest execute-test
  (prn "Start execute test")
  (testing "execute with good execution"
    (let [ste { :endure-good 14 :workingdir "test/workbench"
                :do-good #(do (prn "Do good for 14s: " (System/currentTimeMillis))(wait 1))
                :logs { :logfile "x.txt"
                              :progname "testprocid" } }
          curtime (System/currentTimeMillis)]
      (state/init-state ste)
      (is (= :break (execute (build-good-execution))))
      (is (= (int (/ (- (System/currentTimeMillis) curtime) 1000)) (:endure-good ste)))))
  (testing "hold and stop"
    (let [ste { :endure-good 5 :workingdir "test/workbench/"
                :do-good #(do (prn "Do good for 5s: " (System/currentTimeMillis))(wait 1)) }]
      (state/init-state ste)
      (.start (Thread. (do (wait 4)(prn "create stop file")(spit "test/workbench/stop.txt" ""))))
      (is (= :stop (execute (build-good-execution))))
      (io/delete-file "test/workbench/stop.txt")

      (.start (Thread. (do
                         (wait 4)
                         (prn "create hold file")
                         (spit "test/workbench/hold.txt" "")
                         (wait 4)
                         (io/delete-file "test/workbench/hold.txt")
                         (prn "deleted hold file"))))
      (is (= :break (execute (build-good-execution))))
      ))
  (testing "execute with evil execution"
    (let [ste { :delay-evil 13 :workingdir "test/workbench"
                :do-evil #(do (prn "Do evil for 13s: " (System/currentTimeMillis))(wait 1))
                :logs { :logfile "test/workbench/testprocid.txt"
                        :progname "testprocid" } }
          curtime (System/currentTimeMillis)]
      (state/init-state ste)
      (is (= :break (execute (build-evil-execution))))
      (is (= (int (/ (- (System/currentTimeMillis) curtime) 1000)) (:delay-evil ste))))))


(defn foo []
  (reduce * (range 1 (inc 20))))

(deftest dispatch-test
;  (is (= :break
;        (dispatch { :working-dir "."
;                 :do-good '#(do  (prn "FUTSCH!!!")(Thread/sleep 2000) (/ 1 2))
;                 :endure-good 3
;                 :do-evil '#(do  (prn "AUTSCH!!!")(Thread/sleep 1000) (/ 1 1))
;                 :delay-evil 2
;                 :log-params { :progname "main spook" :logfile "log.txt"}})))
;  (is (= :break
;        (dispatch { :working-dir "test/workbench/sandbox/A001mgr/bin"
;                 :log-params { :progname "vampire" :logfile "test/log.txt"}
;                 :endure-good 30
;                 :delay-evil 20
;                 :do-good '#(prn ("DOITNOW: " take 92 (map first (iterate (fn[[a b]] [b (+ a b)]) [0 1]))))
;                 :do-evil '#(map (do (Thread/sleep(* 1000  1000)) range))
;               })))
  (is (= :break
         (dispatch {
                     :working-dir "."
                     :log-params
                     {
                       :progname "vampire"
                       :logfile "./log.txt"
                     }
                     :endure-good 120
                     :delay-evil 120
                     :do-good (fn* [] (do (prn "BUZZ ...") (Thread/sleep 2000) (/ 1 2)))
                     :do-evil (fn* [] (do (prn "OUCH!!!") (Thread/sleep 1000) (/ 1 1)))
                   })))
  (is (= :break
        (dispatch {
                    :working-dir "."
                    :log-params
                    {
                      :progname "vampire"
                      :logfile "test/log.txt"
                    }
                    :endure-good 30
                    :delay-evil 20
                    :do-good foo
                  }))))

(deftest main-test
  (is (= :break
         (-main "test/workbench/test.spk")))
  (is (= :break
         (-main "test/workbench/test-1.spk"))))
