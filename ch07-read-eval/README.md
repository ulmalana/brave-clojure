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

## The Reader

The reader converts the **textual code** into Clojure **data structure**. The textual representation of data structures is called **reader form**. Once Clojure read the stream and characters and produce the corresponding data structures, it evaluates them. Reading and evaluation are two stages that we can perform independently. We can use `read-string` function to read the stream of characters and `eval` to evaluate them.
```
(read-string "(+ 1 2)")
; => (+ 1 2)

(list? (read-string "(+ 1 2)"))
; => true

(eval (+ 1 2))
; => 3

(eval (read-string "(+ 1 2)"))
; => 3
```
Some examples of reader forms that directly map to the data structures they represent:
1. **()**: List reader form
2. **str**: Symbol reader form
3. **[1 2]**: Vector reader form containing two number reader forms
4. **{:sound "hoot"}**: Map reader form with a keyword reader form and a string reader form.

We can read anonymous functions well:
```
(#(+ 1 %) 3)
; => 4

(read-string "#(+ 1 %)")
; => (fn* [p1__6060#] (+ 1 p1__6060#))
```

### Reader Macros
Reader macros are sets of rules for transforming text into data structures. They are designated by **macro characters** like `'`, `#`, and `@`.

```
(read-string "'(a b c)")
; => (quote (a b c))

(read-string "; ignore\n(+ 1 2_)")
; => (+ 1 2)
```
