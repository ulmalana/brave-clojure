(ns ch09-concurrency.core
  (:gen-class))

;; future
(do 
  (future (Thread/sleep 4000)
          (println "4 seconds later"))
  (println "this is now"))

;; dereferencing future
(let [result (future (println "this prints once")
                     (+ 1 1))]
  (println "deref: " (deref result))
  (println "@: " @result))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
