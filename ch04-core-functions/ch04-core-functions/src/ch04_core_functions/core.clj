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

;; lazy seq
(def vampire-database
  {0 {:makes-blood-puns? false, :has-pulse? true :name "McFishWich"}
   1 {:makes-blood-puns? false, :has-pulse? true :name "McMackson"}
   2 {:makes-blood-puns? true, :has-pulse? false :name "Salvatore"}
   3 {:makes-blood-puns? true, :has-pulse? true :name "Mickey"}})

(defn vampire-related-details
  [social-security-number]
  (Thread/sleep 1000)
  (get vampire-database social-security-number))

(defn vampire?
  [record]
  (and (:makes-blood-puns? record)
       (not (:has-pulse? record))
       record))

(defn identify-vampire
  [social-security-nums]
  (first (filter vampire?
                 (map vampire-related-details social-security-nums))))

;; this call will take about one second
(time (vampire-related-details 0))

;; because the seq produced by range is a lazy seq
;; this call will finish almost instaneously.
;; in nonlazy scenario, it may take 1000000 seconds.
(time (def mapped-details (map vampire-related-details (range 0 1000000))))

;; accessing the first element of mapped-details
;; should have taken only 1 second, but it takes
;; more than that since Clojure also realizes
;; the next elements.
(time (first mapped-details))

;; accessing the first element again will take
;; almost no time since the element has been
;; realized
(time (first mapped-details))

;; find the real vampire
(time (identify-vampire (range 0 1000000)))

;; infinite seq
;; generate infinite even numbers
(defn even-numbers
  ([] (even-numbers 0))
  ([n] (cons n (lazy-seq (even-numbers (+ n 2))))))

(take 10 (even-numbers))

;; define conj in terms of into
(defn my-conj
  [target & additions]
  (into target additions))

;; define into in terms of conj
(defn my-into
  [target additions]
  (apply conj target additions))

;; partially applied function
(def add10 (partial + 10))
(add10 3)

(def add-missing-elements
  (partial conj ["water" "earth" "air"]))

(add-missing-elements "hydrogen" "nitrogen")

;; define our own partial
(defn my-partial
  [partialized-fn & args]
  (fn [& more-args]
    (apply partialized-fn (into args more-args))))

(def add20 (my-partial + 20))

(add20 3)

;; logger
(defn lousy-logger
  [log-level message]
  (condp = log-level
    :warn (clojure.string/lower-case message)
    :emergency (clojure.string/upper-case message)))

(def warn (partial lousy-logger :warn))

(warn "Red light ahead")


;; complement
(def not-vampire? (complement vampire?))

;; human test
(defn identify-humans
  [social-security-nums]
  (filter not-vampire?
          (map vampire-related-details social-security-nums)))

;; define our own complement
(defn my-complement
  [fun]
  (fn [& args]
    (not (apply fun args))))
(def my-pos? (my-complement neg?))
