(ns ch04-core-functions.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defn titleize
  [topic]
  (str topic " can be mapped"))

(map titleize ["Vector 1" "Vector 2"])
(map titleize '("List 1" "List 2"))
(map titleize #{"Set 1" "Set 2"})
(map #(titleize (second %)) {:key "Map 1"})

;; map ;;

;; multiple collections
(def human-consumption [1.2 2.3 3.4 4.5])
(def critter-consumption [0.2 0.5 1.1 2.0])
(defn unify-diet-data
  [human critter]
  {:human human
   :critter critter})

(map unify-diet-data human-consumption critter-consumption)

;; multiple functions
(def sum #(reduce + %))
(def avg #(/ (sum %) (count %)))
(defn stats
  [nums]
  (map #(% nums) [sum count avg]))

(stats [4 8 5 3 1])

;; keyword as function
(def identities
  [{:alias "Batman" :real "Bruce Wayne"}
   {:alias "Spiderman" :real "Peter Parker"}
   {:alias "Iron Man" :real "Tony Stark"}
   {:alias "Superman" :real "Clark Kent"}])

(map :real identities)

;; reduce ;;

;; transform the value in a map.
;; the anonymous function takes two args:
;; 1. empty map
;; 2. a vector that is destructured as [key val]
(reduce (fn [new-map [key val]]
          (assoc new-map key (inc val)))
        {}
        {:max 30 :min 31})

;; filter keys in a map
(reduce (fn [new-map [key val]]
          (if (> val 4)
            (assoc new-map key val)
            new-map))
        {}
        {:human 4.1 :critter 3.9})
