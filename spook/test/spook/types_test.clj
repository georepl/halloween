(ns spook.types-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojure.spec :as spec]
;;            [spook.types :refer :all]))
            [spook.types :as types]))


(deftest directory-test
  (testing "directory formats"
    (is (false? (spec/valid?   ::types/directory nil)))
    (is (false? (spec/valid? ::types/directory "")))
    (is (false? (spec/valid? ::types/directory 42)))
    (is (true? (spec/valid? ::types/directory "42")))
    (is (true? (spec/valid? ::types/directory "~")))
    (is (true? (spec/valid? ::types/directory "~/")))
    ))

(deftest logstate-test
  (testing "logstate formats"
    (is (false? (spec/valid? ::types/logstate nil)))
    (is (false? (spec/valid? ::types/logstate {})))
;;    (is (false? (spec/valid? ::logstate { :progname "test" :logfile "~" :a 1 2 })))
;;    (is (false? (spec/valid? ::logstate { :progname "test" :logfile "" :a 1 2 })))
    (is (true? (spec/valid? ::types/logstate { :progname "" :logfile "~" :a 42 })))
    (is (true? (spec/valid? ::types/logstate { :progname "test" :logfile "~" :a 42 })))
))

(deftest state-test
  (testing "state formats"
    (is (false? (spec/valid? ::types/state nil)))
    (is (false? (spec/valid? ::types/state {})))
    (is (true? (spec/valid? ::types/state { :working-dir "~/" :do-good #(/ 1 2) :endure-good 4711 :log-params { :progname "test" :logfile "~/"} })))
    (is (false? (spec/valid? ::types/state { :working-dir "~/" :do-good 42 :endure-good 4713.7 :log-params { :progname "test" :logfile "~/"} })))
    (is (true? (spec/valid? ::types/state { :working-dir "~/" :do-good #(/ 1 2) :endure-good 4711 })))
    (is (false? (spec/valid? ::types/state { :working-dir "~/" :do-good #(/ 1 2) :do-evil #(/ 1 0) :log-params { :progname "test" :logfile "~/"} })))
    (is (false? (spec/valid? ::types/state { :working-dir "" :do-good #(/ 1 2) :endure-good 4712 :log-params { :progname "test" :logfile "~/"} })))
    (is (false? (spec/valid? ::types/state { :working-dir "~/" :do-good 42 :endure-good "one" :log-params { :progname "test" :logfile "~/"} })))
    ))
