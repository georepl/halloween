(ns spook.logging
  (:require [spook.types :as types]
            [spook.state :as state]
            [clojure.spec :as spec]))


;; getTime returns the ms since epoch (1-1-1970) which was a Thursday (day 4).
;; are the number of ms a day has
(defn day-of-week []
  (let [dayssince (int (/ (.getTime (new java.util.Date)) 86400000))]
    (mod (+ 4 dayssince) 7)))

(defn init [options]
  (let [state (:log-params options)
        dayofweek (day-of-week)]
    (when (spec/valid? ::types/logstate state)
      (let [new-log-state (assoc state :logfile (format "%s%d" (:logfile state) (day-of-week)))]
        (dissoc  (assoc options :logs new-log-state) :log-params)))))


(defn trace [s]
  (let [state (state/current-state)
        pid 12345 ;; read http://www.rgagnon.com/javadetails/java-0651.html when Java 9 is out
        logfile (:logfile (:logs state))
        progname (:progname (:logs state))
        timestamp (.format (java.text.SimpleDateFormat. "MM-dd-yyyy  hh:mm:ss") (new java.util.Date))]
    (spit logfile (format "%s PID %s:  %s  TRACE  %s\n"
                                           progname
                                           pid
                                           timestamp
                                           s) :append true)))


(defn info [s]
  (let [state (state/current-state)
        pid 12345 ;; read http://www.rgagnon.com/javadetails/java-0651.html when Java 9 is out
        logfile (:logfile (:logs state))
        progname (:progname (:logs state))
        timestamp (.format (java.text.SimpleDateFormat. "MM-dd-yyyy  hh:mm:ss") (new java.util.Date))]
    (spit logfile (format "%s PID %s:  %s  INFO   %s\n"
                                           progname
                                           pid
                                           timestamp
                                           s) :append true)))


(defn error [s]
  (let [state (state/current-state)
        pid 12345 ;; read http://www.rgagnon.com/javadetails/java-0651.html when Java 9 is out
        logfile (:logfile (:logs state))
        progname (:progname (:logs state))
        timestamp (.format (java.text.SimpleDateFormat. "MM-dd-yyyy  hh:mm:ss") (new java.util.Date))]
    (spit logfile (format "%s PID %s:  %s  ERROR  %s\n"
                                           progname
                                           pid
                                           timestamp
                                           s) :append true)))
