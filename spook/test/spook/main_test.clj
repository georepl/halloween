(ns spook.main-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [spook.main :refer :all]))

(defn abs [x]
  (if (neg? x) (- 0 x) x))

(deftest wait-test
  (testing "wait function"
    (let [curtime (System/currentTimeMillis)]
      (wait 11000)
      (let [now (- (System/currentTimeMillis) curtime)]
        (is (<= (abs (- 12 now)) 1))))))

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
    (let [[f g] (first (build-good-execution { :delay-good 5 :do-good #(+ 2 3) }))]
      (is (and (fn? f)(fn? g))))))

(deftest build-evil-execution-test
  (testing "build-evil-execution"
    (let [[f g] (first (build-evil-execution { :delay-evil 5 :do-evil #(* 2 3) }))]
      (is (and (fn? f)(fn? g))))))

(deftest execute-test
  (prn "Start execute test")
  (testing "execute with good execution"
    (let [state { :delay-good 15 :workingdir "test/workbench"
                  :do-good #(do (Thread/sleep(* 1000))(prn "Do good for 15s: " (System/currentTimeMillis))) }
          curtime (System/currentTimeMillis)]
      (is (= :break (execute state (build-good-execution state))))
      (is (= (int (/ (- (System/currentTimeMillis) curtime) 1000)) (:delay-good state)))))
  (testing "hold and stop"
    (let [state { :delay-good 5 :workingdir "test/workbench/"
                  :do-good #(do (Thread/sleep(* 2000))(prn "Do good for 5s: " (System/currentTimeMillis))) }]
      (.start (Thread. (do (wait 4000)(prn "create stop file")(spit "test/workbench/stop.txt" ""))))
      (is (= :stop (execute state (build-good-execution state))))
      (io/delete-file "test/workbench/stop.txt")

      (.start (Thread. (do
                         (wait 4000)
                         (prn "create hold file")
                         (spit "test/workbench/hold.txt" "")
                         (wait 4000)
                         (io/delete-file "test/workbench/hold.txt")
                         (prn "deleted hold file"))))
      (is (= :break (execute state (build-good-execution state))))
      ))
  (testing "execute with evil execution"
    (let [state { :delay-evil 13 :workingdir "test/workbench"
                  :do-evil #(do (Thread/sleep(* 1500))(prn "Do evil for 13s: " (System/currentTimeMillis))) }
          curtime (System/currentTimeMillis)]
      (is (= :break (execute state (build-evil-execution state))))
      (is (= (int (/ (- (System/currentTimeMillis) curtime) 1000)) (:delay-evil state))))))
