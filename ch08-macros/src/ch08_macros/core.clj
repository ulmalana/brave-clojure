(ns ch08-macros.core
  (:gen-class))

;; macro with destructuring
(defmacro infix-2
  [[operand1 op operand2]]
  (list op operand1 operand2))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
