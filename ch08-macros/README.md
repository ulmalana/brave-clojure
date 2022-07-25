 Chapter 08 - Macros

## Macros vs. Functions

Macro definitions is similar to function definition. One key difference between macros and functions:
1. Function arguments are **fully evaluated** before passing them to the function.
2. Macros arguments are **unevaluated data**.

```
(defmacro infix-2
    [[operand1 op operand2]]
    (list op operand1 operand2))

;; (3 + 4) is unevaluated data
(macroexpand '(infix-2 (3 + 4)))
; => (+ 3 4)
```

## Building Lists for Evaluation

When building lists for evaluation with macros, we need to be careful with **symbol** and its **value** (ie. unevaluated vs. evaluated data). We can **turn off** the evaluation in our macros with **quoting**, so that the macros can focus only on building the list, without evaluating the elements.

### Simple Quoting

With `'` or `quote`, we can tell Clojure not to evaluate the symbols. 

```
(+ 1 2)
; => 3

(quote (+ 1 2))
; => (+ 1 2)

'(+ 1 2)
; => (+ 1 2)

'hehe
; => hehe

;; print the expression and return the result instead of nil
(defmacro my-print
    [expression]
    (list 'let ['result expression]
           (list 'println 'result)
           'result))
```

### Syntax Quoting

Two differences between syntax quoting `\`` and simple quoting `'`:
1. Syntax quoting will return the **fully qualified symbols**.
```
'+
; => +

`+
; => clojure.core/+

'clojure.core/+
; => clojure.core/+

'(+ 1 2)
; => (+ 1 2)

`(+ 1 2)
; => (clojure.core/+ 1 2)
```
2. Syntax quoting allows **unquoting** with `~`, meaning that some form with `~` will be evaluated.
```
`(+ 1 (inc 1))
; => (clojure.core/+ 1 (clojure.core/inc 1))

`(+ 1 ~(inc 1))
; => (clojure.core/+ 1 2)

;; simple and syntax quoting comparison
(list '+ 1 (inc 1))
; => (+ 1 2)

`(+ 1 ~(inc 1))
; => (clojure.core/+ 1 2)
```
### Unquote Splicing

Unquote splicing `~@` **unwraps a seqable data structure**, placing its contents directly within the enclosing syntax-quoted data structure.
```
`(+ ~(list 1 2 3))
; => (clojure.core/+ (1 2 3))

`(+ ~@(list 1 2 3))
; => (clojure.core/+ 1 2 3)
```
