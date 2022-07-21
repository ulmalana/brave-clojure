(ns ch06-organization.core
  (:gen-class))

(def great-books ["Inferno" "Silmarillion"])

;; overwrite great-books
(def great-books ["Harry Potter" "Moby Dick"])

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
