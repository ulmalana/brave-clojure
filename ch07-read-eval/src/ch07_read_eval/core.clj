(ns ch07-read-eval.core
  (:gen-class))

;; create a macro
(defmacro backwards
  [form]
  (reverse form))

(backwards (" backwards" " am" "I" str))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
