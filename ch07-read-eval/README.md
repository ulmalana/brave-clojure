# Chapter 07 - Clojure Alchemy: Reading Evaluation, Macros

## Clojure's Evaluation Model

Clojure has two phase of evaluation:
1. **Read textual source code,** producing data structures.
2. **Evaluating the data structures** from reader.

The relationship between source code and data like this is called **homoiconicity** ("code as data"). Compiler or intrepreter construct **Abstract Syntax Tree (AST)** to be evaluated. In most programming languages, this AST is **inacessible**. However, in Clojure and other Lisp language, AST is accessible and we can play with it as the result of homoiconicity.

We can evaluate a list in Clojure using `eval`:
```
;; create a list
(def addition-list (list + 1 2))

;; evaluate the list
(eval addition-list)
; => 3

(eval (concat addition-list [10]))
; (eval (concat (list + 1 2) [10]))
; (eval (list + 1 2 10))
; => 13
```

So, Clojure is *homoiconic*: it represents **AST using lists**, and we **write those lists when we code** in Clojure.
