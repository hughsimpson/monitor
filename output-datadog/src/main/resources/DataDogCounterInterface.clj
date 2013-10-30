(ns org.eigengo.monitor.output.datadog.DataDogCounterInterface
  (:gen-class
    :name org.eigengo.monitor.output.datadog.DataDogCounterInterface
    :implements [org.eigengo.monitor.output.CounterInterface]
    :methods [[incrementCounter [String "[Ljava.lang.String;"] void]
              [decrementCounter [String "[Ljava.lang.String;"] void]
              [recordGaugeValue [String int "[Ljava.lang.String;"] void]
              [recordExecutionTime [String int "[Ljava.lang.String;"] void]
              #^{:static true} [binomial [int int] double]]
;              #^{:static true} [logSuccess [] void]] ; example of how to declare a static method for java interop
    )
  (:require clojure.java.io)
  (:import (java.net InetAddress DatagramPacket DatagramSocket)))

(def udp-server (ref nil))
(def port 8125)
(def prefix "")

; Not 100% convinced this (`localhost`) should be defn and not def. Why would it *ever* need to be 'recalculated'?
(defn localhost [] (. InetAddress getLocalHost))

(defn message [text]
  (new DatagramPacket (. text getBytes) (. text length) (localhost) port ))
(defn send-message [text]
  (.send @static-udp-server (message text)))


; use this to declare the udp server as a sorta static thingummybob. Remove the port parameter (somehow!) to just hit up any old port
; (diff. between this and the ref, is that this is essentially a lazy val that gets calculated once the first time it's used, whereas
; the other thing requires consciously creating the server socket. I think.):
(def static-udp-server []
  (DatagramSocket. port))

; use these if it makes more sense to redeclare the udp server than simply hold it static....
(defn create-udp-server []
  (DatagramSocket. port))
(defn start-udp-server []
  (dosync (ref-set udp-server (create-udp-server))))
(defn stop-udp-server []
  (.close @udp-server))

; Java-callable wrappers for our methods...

(defn -incrementCounter [s args]
  (increment-counter s args))
(defn -decrementCounter [s args]
  (decrement-counter s args))
(defn -recordGaugeValue [s i args]
  (record-gauge-value s i args))
(defn -recordExecutionTime [s i args]
  (record-execution-time s i args))

; Common methods for datagram construction

(defn tag-string [args]
  (if (= (alength args) 0)
    ""
    (str "|#" (reduce (fn [a b] (str a "," b)) args))
    )
  )

(defn create-format-message [format-string prefix counter-name delta args]
  (format format-string prefix counter-name delta tag-string(args)))

; Finally: the methods we're interested in....

(defn increment-counter [s args]
  (send-message (create-format-message "%s%s:%d|c%s" prefix s 1 args)))
(defn decrement-counter [s args]
  (send-message (create-format-message "%s%s:%d|c%s" prefix s (- 1) args)))
(defn record-gauge-value [s i args]
  (send-message (create-format-message "%s%s:%d|g%s" prefix s 1 args)))
(defn record-execution-time [s i args]
  (send-message (create-format-message "%s%s:%s|h%s" prefix s (* (i 0.001)) args)))

; For testing the clojure compilation:
(defn binomial
  "Calculate the binomial coefficient."
  [n k]
  (let [a (inc n)]
    (loop [b 1
           c 1]
      (if (> b k)
        c
        (recur (inc b) (* (/ (- a b) b) c))))))

(defn -binomial
  "A Java-callable wrapper around the 'binomial' function."
  [n k]
  (binomial n k))