(ns spookparty.main
  (:require [clojure.java.io :as io]
            [clojure.spec :as spec]
            [spookparty.types :as types])
  (:gen-class))


(defn append-path
  ([s t]
    (if (or (= \\ (last s))(= \/ (last s)))
      (apply str (concat s t))
      (apply str (concat s "/" t))))
  ([s t & more]
    (reduce append-path (append-path s t) more)))


(defn mkdir [path]
  (let [dir (java.io.File. path)]
    (when (false? (.exists dir))
      (.mkdirs dir))
    path))


(defn rmdir [path]
  (if-let [dir (java.io.File. path)]
    (if (.exists dir)
      (let [coll (map #(append-path path %) (.list dir))
            cl (map rmdir coll)
            ret (doall cl)]
        (.delete dir))
      false)
    false))


(defn setup-globals [glb-spk]
  (let [path (:path glb-spk)]
    (try
      (mkdir path)
      path
      (catch Exception e
        (str "Exception:" (.getMessage e))
        nil))))


(defn create-spook-bin [rec]
  (let [src (:spook-source rec)
        bin (first (:workingsubdirs rec))
        var (second (:workingsubdirs rec))]
    (if (or (nil? src) (nil? bin))
      (prn "spookplan is not valid, :spook-source = " src " and :spook-target = " bin)
      (do
        (io/copy (io/file src) (io/file (append-path bin "spook.jar")))
        (spit (:start-script-name rec) (str (:start-script-content rec)))
        (let [new-rec (assoc (:spook-script-content rec) :working-dir (.getAbsolutePath (io/file bin)))
              logfilename (append-path (.getAbsolutePath (io/file var))
                                       (:logfile (:log-params new-rec)))]
          (spit (:spook-script-name rec)
                (assoc-in new-rec [:log-params :logfile] logfilename)))))))


(defn setup-locals [path rec]
  (try
    (let [new-rec (assoc rec :spook-target (append-path path (:spook-target rec))
                             :spook-script-name (append-path path (:spook-target rec)(:spook-script-name rec))
                             :start-script-name (append-path path (:spook-target rec)(:start-script-name rec))
                             :workingsubdirs (doall (map #(mkdir (append-path path (:spook-target rec) %)) (:workingsubdirs rec))))]
      (mkdir (:spook-target new-rec))
      (create-spook-bin new-rec))
    (catch Exception e
      (prn "Failed reading package data:" (.getMessage e)))))


(defn- start-interpreter [spookplan]
  (when-let [path (setup-globals (first spookplan))]
    (doall
      (map #(setup-locals path %) (rest spookplan)))))


(defn- init-spook [spookplan]
  "get the spookplan, start the Repl and start the plan"
  (setup-globals spookplan)
  (start-interpreter spookplan)
  nil)


(defn -main [& args]
  (prn "reading spookplan " args)
  "read in spookplan file and start the show"
  (when (spec/valid? ::types/directory (first args))
    (try
      (let [spookplan (load-file (first args))]
        (if (spec/valid? ::types/spookplan spookplan)
          (init-spook spookplan)
          (prn "spookplan is not valid")))
      (catch Exception e (prn "Exception " (.getMessage e))))))
