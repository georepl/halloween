(ns spookparty.main-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [spookparty.main :as main]))


(deftest main-test

  (testing "append-path"
    (is (= "path/tofile"
           (#'main/append-path "path/" "tofile")))
    (is (= "path\\tofile"
           (#'main/append-path "path\\" "tofile")))
    (is (= "path/tofile"
           (#'main/append-path "path" "tofile")))
    (is (= "just/one/more/path/to/a/file"
           (#'main/append-path "just" "one" "more" "path" "to" "a" "file"))))

  (testing "mkdir"
    (is (= "testo"
           (#'main/mkdir "testo")))
    (is (and (.exists (io/file "testo"))
             (.isDirectory (io/file "testo"))))
    (is (= "just/another/test/bin"
           (#'main/mkdir "just/another/test/bin")))
    (is (and (.exists (io/file "just/another/test/bin"))
             (.isDirectory (io/file "just/another/test/bin")))))

  (testing "setup-globals"
    (is (= "just/another/test/var"
           (#'main/setup-globals {:path "just/another/test/var"}))))
  (testing "setup-locals"
    (let [rec {:workingsubdirs ["one" "two" "three"]
               :spook-source "../spook/target/uberjar/spook-0.1.0-SNAPSHOT-standalone.jar"
               :spook-target "tgt"
               :spook-script-name "merrygoround.spk"
               :spook-script-content { }
               :start-script-name "buzz.s"}]
      (#'main/setup-locals "just/another/test" rec)
      (is (and (.exists (io/file "just/another/test/tgt/one"))
               (.isDirectory (io/file "just/another/test/tgt/one"))))
      (is (and (.exists (io/file "just/another/test/tgt/two"))
               (.isDirectory (io/file "just/another/test/tgt/two"))))
      (is (and (.exists (io/file "just/another/test/tgt/three"))
               (.isDirectory (io/file "just/another/test/tgt/three"))))))

  (testing "setup-spook")

  (testing "start-interpreter")

  (testing "init-spook"
    (is (= 1 1)))

  (testing "main"
    (is (nil? (#'main/-main nil)))
    (is (nil? (#'main/-main "")))
    (is (nil? (#'main/-main "itzen")))
    (is (nil? (#'main/-main "test/workbench/test-empty.spl")))
    (is (nil? (#'main/-main "test/workbench/test-nonsense.spl")))
    (is (nil? (#'main/-main "test/workbench/test-missing-firstrec.spl")))
    (is (nil? (#'main/-main "test/workbench/test-spook-source-or-target-missing.spl")))
    (is (nil? (#'main/-main "test/workbench/test-scenario-carmen.spl"))))
    (is (and (.exists (io/file "test/workbench/sandbox/A001zmb/etc"))
             (.isDirectory (io/file "test/workbench/sandbox/A001zmb/etc"))))

  (testing "rmdir"
    (is (false? (#'main/rmdir "~/grmpf/doesntexist.foo")))
    (is (true? (#'main/rmdir "testo")))
;    (is (true? (#'main/rmdir "test/workbench/sandbox")))
    (is (true? (#'main/rmdir "just")))
  ))
