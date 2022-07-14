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

;;;; Hit the Hobbit project ;;;;

;; body part
(def asym-hobbit-body-parts [{:name "head" :size 3}
                             {:name "left-eye" :size 1}
                             {:name "left-ear" :size 1}
                             {:name "mouth" :size 1}
                             {:name "nose" :size 1}
                             {:name "neck" :size 2}
                             {:name "left-shoulder" :size 3}
                             {:name "left-upper-arm" :size 3}
                             {:name "chest" :size 10}
                             {:name "back" :size 10}
                             {:name "left-forearm" :size 3}
                             {:name "abdomen" :size 6}
                             {:name "left-kidney" :size 1}
                             {:name "left-hand" :size 2}
                             {:name "left-knee" :size 2}
                             {:name "left-thigh" :size 4}
                             {:name "left-lower-leg" :size 3}
                             {:name "left-achilles" :size 1}
                             {:name "left-foot" :size 2}])


(defn matching-part
  [part]
  {:name (clojure.string/replace (:name part) #"^left-" "right-") :size (:size part)})

(defn symmetrize-body-parts
  "Expects a seq of maps that have a :name and :size"
  [asym-body-parts]
  ; we can create a new binding in loop
  (loop [remaining-asym-parts asym-body-parts ; remaining-asym-parts = asym-body-parts
         final-body-parts []] ; final-body-parts = []
    (if (empty? remaining-asym-parts)
      final-body-parts ; empty, all remaining part has been processed
      (let [[part & remaining] remaining-asym-parts] ; [part & remaining] = remaining-asym-parts
        (recur remaining ; keep looping until remaining is empty
               (into final-body-parts ; and put the new element into final-body-parts
                     (set [part (matching-part part)])))))))

(defn my-reduce
  ([f initial coll]
   (loop [result initial
          remaining coll]
     (if (empty? remaining)
       result
       (recur (f result (first remaining)) (rest remaining)))))
  ([f [head & tail]]
   (my-reduce f head tail)))

(defn my-loop
  []
  (loop [iteration 0]
    (println (str "Iteration " iteration))
    (if (> iteration 5)
      (println "Bye!")
      (recur (inc iteration)))))

(defn better-symmetrize-body-parts
  "Expects a seq of maps that have a :name and :size"
  [asym-body-parts]
  (reduce (fn [final-body-parts part]
            (into final-body-parts (set [part (matching-part part)]))) [] asym-body-parts)) 

(defn hit
  [asym-body-parts]

  ;; bind three new variables
  ;; sym-parts = new symmetry body parts
  ;; body-part-size-sum = sum of all body part size
  ;; target = randomly choose one value from 1 to body-part-size-sum
  (let [sym-parts (better-symmetrize-body-parts asym-body-parts)
        body-part-size-sum (reduce + (map :size sym-parts))
        target (rand body-part-size-sum)]
    ;; new binding again
    ;; [part & remaining] = sym-parts
    ;; accumulated-size = size of one of the entry
    (loop [[part & remaining] sym-parts
           accumulated-size (:size part)]
      (if (> accumulated-size target)
        part
        (recur remaining (+ accumulated-size (:size (first remaining))))))))
