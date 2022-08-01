(ns ch13-extending-abstractions.core
  (:gen-class))

;; create a multimethod and check :were-type of the argument to decide
;; which method needs to be dispatched.
;; the anonymous function is named dispatching function.
(defmulti full-moon-behaviour (fn [were-creature] (:were-type were-creature)))

;; the first method for handling :were-type = :wolf (dispatching value is :wolf)
(defmethod full-moon-behaviour :wolf
  [were-creature]
  (str (:name were-creature) " will howl and murder."))

;; the second method for handling :were-type = :simmons (dispatching value is :simmons)
(defmethod full-moon-behaviour :simmons
  [were-creature]
  (str (:name were-creature) " will encourage people."))

;; the third method for handling nil dispatching value
(defmethod full-moon-behaviour nil
  [were-creature]
  (str (:name were-creature) " will stay home and eat ice cream"))

;; the fourth method for the default dispatching value (ie. if no other methods match
;; the dispatching function, then run this method)
(defmethod full-moon-behaviour :default
  [were-creature]
  (str (:name were-creature) " will stay up all night"))


(full-moon-behaviour {:name "Andy" :were-type :wolf})

;; dispatching function can return arbitrary values
(defmulti types (fn [x y] [(class x) (class y)]))

;; method if both arguments are string
(defmethod types [java.lang.String java.lang.String]
  [x y]
  "Two strings")

(types "String 1" "String 2")

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
