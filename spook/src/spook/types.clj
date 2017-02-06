(ns spook.types
  (:require [clojure.java.io :as io]
            [clojure.spec :as spec]))


(spec/def ::directory (spec/and #(string? %)
                                #(false? (empty? %))))

;(spec/def ::directory (spec/and #(string? %)
;                                #(and (string? %)(.exists (io/file %)))))

;(spec/def ::text (spec/and #(not= empty? %) #(coll? %) #(every? string? %)))

(spec/def ::state (spec/and #(false? (nil? (:endure-good %)))
                            #(true? (pos? (:endure-good %)))
;                            #(fn? (:do-good %))
                            #(spec/valid? ::directory (:working-dir %))))

(defn rec? [r]
  (try
    (associative? r)  ; this causes a RuntimeException for records like {:a 1 2}
    (catch java.lang.RuntimeException e false)
    (catch Exception e false)))


(spec/def ::logstate (spec/and #(rec? %)
                               #(spec/valid? ::directory (:logfile %))))

