(ns ch08-macros.core
  (:gen-class))

;; macro with destructuring
(defmacro infix-2
  [[operand1 op operand2]]
  (list op operand1 operand2))

(defmacro my-print
  [expression]
  (list 'let ['result expression]
        (list 'println 'result)
        'result))

;; macros with simple quoting
(defmacro code-critic
  "Phrases are courtesy Hermes Conrad from Futurama"
  [bad good]
  (list 'do
        (list 'println
              "Great squid of Madrid, this is bad code:"
              (list 'quote bad))
        (list 'println
              "Sweet gorilla of Manila, this is good code:"
              (list 'quote good))))

(code-critic (1 + 1) (+ 1 1))

;; the same code-critic macro but with syntax quoting
(defmacro code-critic'
  [bad good]
  `(do (println "Great squid of Madrid, this is bad code:"
                (quote ~bad))
       (println "Sweet gorilla of Manila, this is good code:"
                (quote ~good))))

(code-critic' (1 + 1) (+ 1 1))

;; Refactoring code-critic
(defn criticize-code
  [criticism code]
  `(println ~criticism (quote ~code)))

(defmacro code-critic''
  [bad good]
  `(do ~(criticize-code "Cursed bacteria of Liberia, this is bad code:" bad)
       ~(criticize-code "Sweet sacred boa of Samoa, this is good code" good)))

;; Refactoring with map and unquote splicing
(defmacro code-critic'''
  [bad good]
  `(do ~@(map #(apply criticize-code %)
             [["Jolly Bee of Hawaii, this is bad code" bad]
              ["Gee this is good code:" good]])))

;; variable capture
(def message "Good job!")
(defmacro with-mischief
  [& stuff-to-do]
  (concat (list 'let ['message "Oh big deal"])
          stuff-to-do))

(defmacro without-mischief
  [& stuff-to-do]
  (let [macro-message (gensym 'message)]
    `(let [~macro-message "Oh big deal"]
       ~@stuff-to-do
       (println "I still need to say: " ~macro-message))))

;; double evaluation
;; to-try will be evaluated twice
(defmacro report
  [to-try]
  `(if ~to-try
     (println (quote ~to-try) "was successful:" ~to-try)
     (println (quote ~to-try) "wasnt successful:" ~to-try)))

;; bind to-try to result# and evaluate once
(defmacro report'
  [to-try]
  `(let [result# ~to-try]
     (if result#
       (println (quote ~to-try) "was success:" result#)
       (println (quote ~to-try) "wasnt success:" result#))))


;;; Validation Function ;;;

;; details below are invalid
(def order-details
  {:name "Rick Sanchez"
   :email "rick.sanchezexample.com"})

(def order-details-validations
  {:name
   ["Please enter a name" not-empty] ; validation function and its error message

   :email
   ["Please enter an email address" not-empty

    "Your email address seems invalid"
    #(or (empty %) (re-seq #"@" %))]})


(defn error-messages-for
  "Return a seq of error messages"
  [to-validate message-validator-pairs]
  (map first (filter #(not ((second %) to-validate))
                     (partition 2 message-validator-pairs))))

(error-messages-for "" ["Enter a name" not-empty])
; => ["Enter a name"]

(defn validate
  "Return a map with a vector of errors for each key"
  [to-validate validations]
  (reduce (fn [errors validation]
            (let [[fieldname validation-check-groups] validation
                  value (get to-validate fieldname)
                  error-messages (error-messages-for value validation-check-groups)]
              (if (empty? error-messages)
                errors
                (assoc errors fieldname error-messages))))
          {}
          validations))

;; ordinary validation, which may be repetitive
(let [errors (validate order-details order-details-validations)]
  (if (empty? errors)
    (println :success)
    (println :failure errors)))


;; validation with macro
(defmacro if-valid
  "Handle validation more concisely"
  [to-validate validations errors-name & then-else]
  `(let [~errors-name (validate ~to-validate -validations)]
     (if (empty? ~errors-name)
       ~@then-else)))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
