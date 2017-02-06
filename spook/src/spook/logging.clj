(ns spook.logging
  (:require [spook.types :as types]
            [spook.state :as state]
            [clojure.spec :as spec]))


;; getTime returns the ms since epoch (1-1-1970) which was a Thursday (day 4).
;; 86400000 are the number of ms a day has
(defn day-of-week []
  (let [dayssince (int (/ (.getTime (new java.util.Date)) 86400000))]
    (mod (+ 4 dayssince) 7)))


(defn init [options]
  (let [state (:log-params options)
        dayofweek (day-of-week)]
    (if (spec/valid? ::types/logstate state)
      (let [s (format "%s%d" (:logfile state) (day-of-week))
            new-log-state (assoc state :logfile s)]
        (dissoc  (assoc options :logs new-log-state) :log-params))
      nil)))


(defn output [s qual]
  (let [state (state/current-state)
        pid 12345 ;; NYI, TODO: read http://www.rgagnon.com/javadetails/java-0651.html when Java 9 is out!!!
        logfile (:logfile (:logs state))
        progname (:progname (:logs state))
        timestamp (.format (java.text.SimpleDateFormat. "MM-dd-yyyy  hh:mm:ss") (new java.util.Date))
        cont (format "%s PID %s:  %s  %s   %s\n"
                                                 progname
                                                 pid
                                                 timestamp
                                                 qual
                                                 s)]
      (let [file (java.io.File. logfile)]
        (if (.exists file)
          (spit file cont :append true)
          (spit file cont)))))


(defn trace [s]
  (output s "TRACE"))

(defn info [s]
  (output s "INFO"))

(defn error [s]
  (output s "ERROR"))
