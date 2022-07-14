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
(defn test-function
  "This is the description of test-function"
  [name]
  (str "halo, " name " this is test-function"))

;; 0 arity
(defn no-params
  []
  "I take no params")

;; 1 arity
(defn one-param
  [x]
  (str "I take one parameter: " x))

;; 2 arity
(defn two-params
  [x y]
  (str "I take two params: " x " and " y))

(defn x-chop
  "Describe the kind of chop"
  ([name chop-type]
   (str "I " chop-type " chop " name "! Take that!"))
  ([name]
   (x-chop name "karate"))
  )

(defn codger-comm
  [whipper]
  (str "Get off my lawn" whipper "!!!"))

;; variable-arity with &
(defn codger
  [& whippers]
  (map codger-comm whippers))

;; mixing normal and rest parameter
(defn buy-these
  [name & things]
  (str "Hi " name ", please buy these things: "
       (clojure.string/join ", " things))
  )

;; destructuring
(defn my-first
  [[x y]]
  x)

;; take first two and ignore the rest
(defn take-two
  [[x y & rest]]
  (println (str "first: " x))
  (println (str "second: " y))
  (println (str "the rest: "
              (clojure.string/join ", " rest))))

;; destructuring maps
(defn location
  [{lat :lat lng :lng}]
  (println (str "latitude: " lat))
  (println (str "longiture: " lng)))

;; alternative of destructuring maps
;; if the keys are not lat and lng, then it can bind the values and display nothing
(defn location'
  [{:keys [lat lng]}]
  (println (str "latitude: " lat))
  (println (str "longitude: " lng)))


;; accesing the original maps argument with :as
(defn location''
  [{:keys [lat lng] :as point}]
  (println (str "latitude: " lat))
  (println (str "longitude: " lng))
  (println (str "original: " point)))

;; return closure
(defn inc-maker
  "create a custom incrementor"
  [inc-by]
  #(+ % inc-by))

(def inc3 (inc-maker 3))
