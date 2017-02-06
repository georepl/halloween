(defproject spookparty "0.1.0-SNAPSHOT"
  :description "Setup a simulation of the software running on computer systems"
  :url "https://github.com/georepl/halloween/tree/master/spookparty"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojure "1.9.0-alpha13"]]
  :main ^:skip-aot spookparty.main
  :target-path "target/%s"
  :profiles {:dev {:plugins [[lein-cloverage "1.0.6"]]}
             :uberjar {:aot :all}})
