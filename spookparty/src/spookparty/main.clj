(ns spookparty.main
  (:require [clojure.java.io :as io]
            [clojure.spec :as spec]
            [spookparty.types :as types]
            [clojure.tools.nrepl.server :as repl])
  (:gen-class))


(defn- setup-spook [spookplan]
  (prn spookplan))

(defn- start-interpreter []
  (loop [s (read-line)]
    (if (or (= s "bye")(= s "exit"))
      nil
      (do
        (case s
          "start"
          "24:00"
          "midnight"(prn "start the spook")
          "stop"
          "01:00"
          "one'o'clock"
          "1 am"    (prn "stop the spook")
                    (prn "NIX"))
       (recur (read-line))))))

(defn- init-spook [spookplan]
  "get the spookplan, start the Repl and start the plan"
  (setup-spook spookplan)
  (start-interpreter))


(defn -main [arg]
  "read in spookplan file and start the show"
  (when (spec/valid? ::types/directory arg)
    (try
      (with-open [rdr (io/reader arg)]
        (let [spookplan (map str (concat (line-seq rdr)))]
          (if (spec/valid? ::types/text spookplan)
            (init-spook spookplan)
            (prn "spookplan is not valid"))))
      (catch java.io.FileNotFoundException e (prn (.getMessage e)))
      (catch java.lang.RuntimeException e (prn (.getMessage e))))))
