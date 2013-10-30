(ns org.eigengo.monitor.output.datadog.DataDogCounterInterface
  (:gen-class
    :name org.eigengo.monitor.output.datadog.DataDogCounterInterface
    :methods [[incrementCounter "[Ljava.lang.String;" ["[Ljava.lang.String;"] void]
              #^{:static true} [logSuccess [] void]]
    )
  (:require clojure.java.io)
  (:import (java.net InetAddress DatagramPacket DatagramSocket)))

(def udp-server (ref nil))
(def port 8125)

; Not 100% convinced this (`localhost`) should be defn and not def. Why would it *ever* need to be 'recalculated'?
(defn localhost [] (. InetAddress getLocalHost))

(defn message [text]
  (new DatagramPacket (. text getBytes) (. text length) (localhost) port ))
(defn send-message [text]
  (.send @static-udp-server (message text)))


; use this to declare the udp server as a sorta static thingummybob. Remove the port parameter (somehow!) to just hit up any old port
; (diff. between this and the ref, is that this is essentially a lazy val that gets calculated once the first time it'sused, whereas
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
  (incrementCounter s args))
(defn -recordGaugeValue [s i args]
  (recordGaugeValue s i args))
(defn -recordExecutionTime [s i args]
  (recordExecutionTime s i args))

; Finally: the methods we're interested in....

(defn incrementCounter [s args])      ; count(s, 1, args)
; == send(String.format("%s%s:%d|c%s", prefix, s, delta, tagString(args))) --so, need to create this formatted string
; FIRST: tagString = "" if no args and no 'constant tags'
; Otherwise tagString = "|#${tag1},${tag2},...." where 'constant tags' come before any args.Also, constant tags is probably gonna be null, tbh.
(defn tagString [args]
  (if (= (alength args) 0)       ; in progress!!! not sure on if _or_ fold syntax
    ""                   ; brackets or no brackets?
    (str "|#" (reduce str args))
    )
  )
;
; probably like... s"${prefix}${s}:1|c${tagString}" = (format "%s%s:%d|c%s" prefix s 1 tagString) -- again, prefix is currently ""


; == try {;executor.execute(new Runnable() {           -- bluh. Looks like 'hardcore' ('with threads') java, dunnit?
;                                  @Override public void run() {           -- then do the send
;                                                                blockingSend(message);
;                                                                } });}
;catch (Exception e) {handler.handle(e); == noop }                         -- and fuck the exception.( handler is null handler)

;; `blockingSend(message)` is the next target:
;
;try {
;      final byte[] sendData = message.getBytes();
;  final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length);
;      clientSocket.send(sendPacket);
;      } catch (Exception e) {
;                              handler.handle(e);
;                              }               -- i.e. (send-message message) ^_^


(defn recordGaugeValue [s i args])    ; doesn't do anything yet
(defn recordExecutionTime [s i args]) ; doesn't do anything yet