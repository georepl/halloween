(ns spookparty.types
  (:require [clojure.spec :as spec]))

(spec/def ::directory (spec/and #(string? %)
                                #(false? (empty? %))))

(spec/def ::text (spec/and #(not (empty? %)) #(coll? %)))


(spec/def ::global #(spec/valid? ::directory (:path %)))
(spec/def ::local (spec/and #(not (nil? (:endure-good %)))
                            #(fn? (:do-good %))
                            #(spec/valid? ::directory (:workingdir %))))

(spec/def ::spookplan (spec/and #(coll? %)
                                #(spec/valid? ::global (first %))
                                #(map (partial spec/valid? ::local) %)))
