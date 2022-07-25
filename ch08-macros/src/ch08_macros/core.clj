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

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
