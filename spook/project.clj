(defproject spook "0.1.0-SNAPSHOT"
  :description "the model misbehaving program"
  :url "https://github.com/georepl/halloween/tree/master/spook"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojure "1.9.0-alpha13"]
                 [clj-time "0.12.2"]]
  :main ^:skip-aot spook.main
  :target-path "target/%s"
  :profiles {:dev {:plugins [[lein-cloverage "1.0.6"]]}
             :uberjar {:aot :all}})
