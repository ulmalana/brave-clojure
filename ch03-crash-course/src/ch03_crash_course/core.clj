(ns ch03-crash-course.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defn error-message
  [severity]
  (str "HELP. He is "
       (if (= severity :mild)
         "MILDLY INCOVENIENCED"
         "DOOOMED")))
