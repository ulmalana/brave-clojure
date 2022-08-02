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


;; protocol ;;
(defprotocol Psychodynamics
  "This is the docstring description for how Psychodynamics is defined"
  (thoughts [x] "This method only takes one argument")
  (feelings-about [x] [x y] "This method can take one or two arguments"))

;; after defining a protocol, we can extend types to implement such protocol. in this example, we extend string type to support Psychodynamics
;; all methods in a protocol needs to be implemented
(extend-type java.lang.String
  Psychodynamics
  ;; implement thoughts method for string
  (thoughts [x]
    (str x " thinks that characters define the data type"))

  ;; implement feelings-about method for string
  (feelings-about
    ([x] (str x " is longing for a simpler life"))
    ([x y] (str x " is envious of " y "'s simpler life"))))

;; for providing default implementation, we can extend java.lang.Object to support our Psychodynamics protocol,
;; because every type in Java and Clojure is descendant of java.lang.Object.
(extend-type java.lang.Object
  Psychodynamics
  (thoughts [x] (str "default thoughts implementation with arg: " x))
  (feelings-about
    ([x] (str "default feelings-about impl with arg: " x))
    ([x y] (str "default feelings-about impl with args: " x " and " y))))

;; with extend-type, we can extend the type to implement protocols
;; with extend-protocol, we can implement protocols for multiple types at once
(defprotocol Psychodynamics2
  "This is the docstring description for how Psychodynamics is defined"
  (thoughts2 [x] "This method only takes one argument")
  (feelings-about2 [x] [x y] "This method can take one or two arguments"))

(extend-protocol Psychodynamics2
  java.lang.Object
  (thoughts2 [x] (str "default thoughts implementation with arg: " x))
  (feelings-about2
    ([x] (str "default feelings-about impl with arg: " x))
    ([x y] (str "default feelings-about impl with args: " x " and " y)))


  java.lang.Number
  (thoughts2 [x] (str "I'm thinking of number " x))
  (feelings-about2
    ([x] (str x " is kinda meh"))
    ([x y] (str x " is better than " y))))

;; records ;;
;; define a record
(defrecord Person [name occupation])

;; how to create instance of record
;; 1. interop way
(Person. "Rick" "Mad Scientist")

;; 2. with -> factory function (automatically created after defining)
(->Person "Mory" "Student")

;; 3. with map-> factory function
(map->Person {:name "Jerry" :occupation "Office Worker"})

;; how to get values from records
(def beth (Person. "Beth" "Vet"))

;; 1. interop way
(.name beth)
(.occupation beth)

;; 2. keywords way
(:name beth)
(:occupation beth)

;; 3. get function
(get beth :name)

;; extending a protocol in records
(defprotocol WereCreature
  (full-moon-behaviour2 [x]))

(defrecord WereWolf [name title]
  WereCreature ; put the protocol here
  (full-moon-behaviour2 [x]
    (str name " will howl and murder")))

(full-moon-behaviour2 (map->WereWolf {:name "Remus" :title "Professor"}))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
