(ns spookparty.types
  (:require [clojure.spec :as spec]))

(spec/def ::directory (spec/and #(not= nil? %) #(not= empty? %) string?))
(spec/def ::text (spec/and #(not= empty? %) coll? #(every? string? %)))
