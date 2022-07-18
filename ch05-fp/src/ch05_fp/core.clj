(ns ch05-fp.core
  (:gen-class))

(require '[clojure.string :as s])

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

;; this function is referentially transparent
;; because it always return the same value
(+ 1 2)

(defn wisdom
 [words]
 (str "You have to " words))

(wisdom "finish what you started")

;; meanwhile, this function is not referentially transparent
;; because it relies on random number generator.
(defn year-end-eval
  []
  (if (> (rand) 0.5)
    "You get a raise."
    "Try again"))

;; reading from a file (analyze-file) is also not referentially transparent
;; because the file contents can change. But (analysis) function
;; is referentially transparent
(defn analysis
  [text]
  (str "Character count: " (count text)))

(defn analyze-file
  [filename]
  (analysis (slurp filename)))

;; sum recursively
(defn sum
  ([vals] (sum vals 0))
  ([vals accum]
   (if (empty? vals)
     accum
     (sum (rest vals) (+ (first vals) accum)))))

;; sum with better performance with recur
(defn sum'
  ([vals]
   (sum vals 0))
  ([vals accum]
   (if (empty? vals)
     accum
     (recur (rest vals) (+ (first vals) accum)))))

;; clean the white spaces and upper-case lol
(defn clean
  [text]
  (s/replace (s/trim text) #"lol" "LOL"))


;; composing functions
(def character
  {:name "Monkey D. Luffy"
   :attributes {:intelligence 4
                :strength 9
                :flexibility 10}})

;; composing keywords that behave like functions
(def c-int (comp :intelligence :attributes))
(def c-str (comp :strength :attributes))
(def c-fle (comp :flexibility :attributes))

;; => 4
(c-int character)

;; => 9
(c-str character)

;; => 10
(c-fle character)

;; ordinary spell-slots
(defn spell-slots
  [char]
  (int (inc (/ (c-int char) 2))))

;; spell-slots with comp
(def spell-slots-comp
  (comp int inc #(/ % 2) c-int))

;; => 3
(spell-slots character)
(spell-slots-comp character)

;; define our own comp
(defn two-comp
  [f g]
  (fn [& args]
    (f (apply g args))))

;; sleep-identity with no memoize
(defn sleepy-identity
  "Return the given value after 1 secon"
  [x]
  (Thread/sleep 1000)
  x)

;; return after 1 second
(sleepy-identity "Halo")

;; return after 1 second
(sleepy-identity "halo")

;; sleep-idendity with memoize
(def memo-sleepy-identity
  (memoize sleepy-identity))

;; return after 1 second
(memo-sleepy-identity "hehe")

;; return immediately
(memo-sleepy-identity "haha")
