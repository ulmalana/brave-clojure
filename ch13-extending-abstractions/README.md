# Chapter 13 - Multimethods, Protocol, and Records

In Clojure, an **abstraction** is a collection of operations, and **data types** implement abstractions. For example: the **seq abstraction** consists of operations like `first` and `rest`, and th **vector data type** is an implementation of seq. A specific vector like `[1 2 3]` is an **instance** of that data type.

## Polymorphism

### Multimethods

With multimethods, we can **associate a name with multple implementations** by defining a *dispatching function* which is used to determine which method to use. We can always add new methods depending on the dispatching value, even in different namespaces.

```clj
;; create a multimethod and check :were-type of the argument to decide
;; which method needs to be dispatched.
;; the anonymous function is named dispatching function.
(defmulti full-moon-behaviour (fn [were-creature] (:were-type were-creature)))

;; the first method for handling :were-type = :wolf (dispatching value is :wolf)
(defmethod full-moon-behaviour :wolf
  [were-creature]
  (str (:name were-creature) " will howl and murder."))

;; the second method for handling :were-type = :simmons (dispatching value is :simmons)
(defmethod full-moon-behaviour :simmons
  [were-creature]
  (str (:name were-creature) " will encourage people."))

;; the third method for handling nil dispatching value
(defmethod full-moon-behaviour nil
  [were-creature]
  (str (:name were-creature) " will stay home and eat ice cream"))

;; the fourth method for the default dispatching value (ie. if no other methods match
;; the dispatching function, then run this method)
(defmethod full-moon-behaviour :default
  [were-creature]
  (str (:name were-creature) " will stay up all night"))


(full-moon-behaviour {:name "Andy" :were-type :wolf})
; => "Andy will howl and murder."

;; dispatching function can return arbitrary values
(defmulti types (fn [x y] [(class x) (class y)]))

;; method if both arguments are string
(defmethod types [java.lang.String java.lang.String]
  [x y]
  "Two strings")

(types "String 1" "String 2")
; => "Two strings"
```
