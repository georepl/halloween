(ns spookparty.main-test
  (:require [clojure.test :refer :all]
            [spookparty.main :as main]))


(deftest setup-spook-test
  (testing "setup-spook"))

(defn- start-interpreter-test
  (testing "start-interpreter"))

(deftest init-spook-test
  (testing "init-spook"
    (is (= 1 1))))

(deftest main-test
  (testing "main"
    (is (nil? (#'core/-main nil)))
    (is (nil? (#'core/-main "")))
    (is (nil? (#'core/-main "itzen")))
    (is (nil? (#'core/-main "test/workbench/test-empty.spk")))
    (is (nil? (#'core/-main "test/workbench/test-nonsense.spk")))))
