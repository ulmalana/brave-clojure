(ns ch11-core-async.core
  (:require [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close!
                     thread alts! alts!! timeout]])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

;; hot dog machine
(defn hot-dog-machine
  []
  (let [in (chan)
        out (chan)]
    (go (<! in) ; take money in
        (>! out "hot dog")) ; return a hotdog
    [in out])) ; return in and out channel

;; better version of hot dog machine
(defn hot-dog-machine-v2
  [hot-dog-count]
  (let [in (chan)
        out (chan)]
    (go (loop [hc hot-dog-count]
          ;; if hotdog is still available
          (if (> hc 0)
            (let [input (<! in)]
              (if (= 3 input)
                (do (>! out "hot dog")
                    (recur (dec hc)))
                (do (>! out "wilted lettuce")
                    (recur hc))))
            ;; if no hotdog left, close the channel
            (do (close! in)
                (close! out)))))
    [in out]))

;; we can create a pipeline of processes with put and take
(let [c1 (chan)
      c2 (chan)
      c3 (chan)]
  ;; take c1, uppercase it, and put in c2
  (go (>! c2 (clojure.string/upper-case (<! c1))))

  ;; take c2, reverse it, and put in c3
  (go (>! c3 (clojure.string/reverse (<! c2))))

  ;; take c3 and print it
  (go (println (<! c3)))

  ;; put something in c1
  (>!! c1 "redrum"))

;; alts!!
(defn upload
  [headshot c]
  (go (Thread/sleep (rand 100))
      (>! c headshot)))

;; simulate uploading photos
(let [c1 (chan)
      c2 (chan)
      c3 (chan)]
  (upload "serious.jpg" c1)
  (upload "fun.jpg" c2)
  (upload "sassy.jpg" c3)
  (let [[headshot channel] (alts!! [c1 c2 c3])]
    (println "Sending headshot notification for" headshot)))

;; set timeout on alts!!
(let [c1 (chan)]
  (upload "sad.jpg" c1)
  (let [[headshot channel] (alts!! [c1 (timeout 20)])]
    (if headshot
      (println "Sending headshot notification for " headshot)
      (println "Timed out"))))

;; queues
(defn append-to-file
  "Write a string to the end of a file"
  [filename s]
  (spit filename s :append true))

(defn format-quote
  "Delineate the beginning and the end of a quote"
  [quote]
  (str "=== BEGIN ===\n" quote "=== END ===\n\n"))

(defn random-quote
  "Retrieve a random quote and format it"
  []
  (format-quote (slurp "http://www.braveclojure.com/random-quote")))

(defn snag-quotes
  [filename num-quotes]
  (let [c (chan)]
    (go (while true (append-to-file filename (<! c))))
    (dotimes [n num-quotes] (go (>! c (random-quote))))))

;; escape callback hell
(defn upper-caser
  [in]
  (let [out (chan)]
    (go (while true (>! out (clojure.string/upper-case (<! in)))))
    out))

(defn reverser
  [in]
  (let [out (chan)]
    (go (while true (>! out (clojure.string/reverse (<! in)))))
    out))

(defn printer
  [in]
  (go (while true (println (<! in)))))

;; pipelining processes
(def in-chan (chan))
(def upper-caser-out (upper-caser in-chan))
(def reverser-out (reverser upper-caser-out))
(printer reverser-out)

;; => MURDER
(>!! in-chan "redrum")

;; DIAPER
(>!! in-chan "repaid")
