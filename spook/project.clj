(defproject spook "0.1.0-SNAPSHOT"
  :description "the model misbehaving program"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojure "1.9.0-alpha13"]]
  :main ^:skip-aot spook.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})