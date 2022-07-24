(ns ch07-read-eval.core
  (:gen-class))

;; create a macro
(defmacro backwards
  [form]
  (reverse form))

(backwards (" backwards" " am" "I" str))

(defmacro ignore-last-operand
  [function-call]
  (butlast function-call))

(ignore-last-operand (+ 1 2 10))
(ignore-last-operand (+ 3 4 (println "im ignored")))

(defmacro infix
  [infixed]
  (list (second infixed)
        (first infixed)
        (last infixed)))
(infix (1 + 5))

;; without threading macro
(defn read-resource
  "Read a resource into a string"
  [path]
  (read-string (slurp (clojure.java.io/resource path))))

;; with threading macro
(defn read-resource'
  "Read a resource into a string with -> macro"
  [path]
  (-> path
      clojure.java.io/resource
      slurp
      read-string))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
