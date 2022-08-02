# Chapter 13 - Multimethods, Protocol, and Records

In Clojure, an **abstraction** is a collection of operations, and **data types** implement abstractions. For example: the **seq abstraction** consists of operations like `first` and `rest`, and th **vector data type** is an implementation of seq. A specific vector like `[1 2 3]` is an **instance** of that data type.

## Polymorphism

### Multimethods

With multimethods, we can **associate a name with multple implementations** by defining a *dispatching function* which is used to determine which method to use. We can always add new methods depending on the **dispatching value**, even in different namespaces.

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

### Protocols

If we can use multimethods to dispatch methods according to **dispatching values**, we can use protocols to dispatch methods according to **argument's type**. Example, `count` needs **different methods** for vector, maps, and list. A multimethod is one polymorphic operation, while a protocol is a **collection of polymorphic operations**. The following example shows how to define a protocol which consist of two methods (all protocol's methods **belong to the namespace**):

```clj
(defprotocol Psychodynamics
  "This is the docstring description for how Psychodynamics is defined"
  (thoughts [x] "This method only takes one argument")
  (feelings-about [x] [x y] "This method can take one or two arguments"))
```

After defining a protocol, we can **extend types** to implement such protocol. In this example, we extend string type to support Psychodynamics. 
NOTE: 
* All methods in a protocol needs to be implemented.
* Protocol's limitation: **Methods cant have rest arguments**.

```clj
(extend-type java.lang.String
  Psychodynamics
  ;; implement thoughts method for string
  (thoughts [x]
    (str x " thinks that characters define the data type"))

  ;; implement feelings-about method for string
  (feelings-about
    ([x] (str x " is longing for a simpler life"))
    ([x y] (str x " is envious of " y "'s simpler life"))))

(thoughts "Rick")
; => "Rick thinks that characters define the data type"
(feelings-about "Rick" "Morty")
; => "Rick is envious of Morty's simpler life"
```

For providing default implementation, we can extend `java.lang.Object` to support our Psychodynamics protocol, because **every type** in Java and Clojure is descendant of `java.lang.Object`.

```clj
(extend-type java.lang.Object
  Psychodynamics
  (thoughts [x] (str "default thoughts implementation with arg: " x))
  (feelings-about
    ([x] (str "default feelings-about impl with arg: " x))
    ([x y] (str "default feelings-about impl with args: " x " and " y))))

(thoughts "Rick")
; => "Rick thinks that characters define the data type"
(thoughts 3)
; => "default thoughts implementation with arg: 3"
(feelings-about 3)
; => "default feelings-about impl with arg: 3"
(feelings-about 3 5)
; => "default feelings-about impl with args: 3 and 5"
(feelings-about "hi" 5)
; => "hi is envious of 5's simpler life"
```
With `extend-type`, we can extend the type to implement protocols. However, with `extend-protocol`, we can implement protocols **for multiple types at once**. In the following example, we implement `Psychodynamics2` for `java.lang.Objcet` and `java.lang.Number` simultaneously.

```
(extend-protocol Psychodynamics2
  java.lang.Object
  (thoughts2 [x] (str "default thoughts implementation with arg: " x))
  (feelings-about2
    ([x] (str "default feelings-about impl with arg: " x))
    ([x y] (str "default feelings-about impl with args: " x " and " y)))


  java.lang.Number
  (thoughts2 [x] (str "I'm thinking of number " x))
  (feelings-about2
    ([x] (str x " is kinda meh"))
    ([x y] (str x " is better than " y))))

(thoughts2 "hi")
; => "default thoughts implementation with arg: hi"
(thoughts2 5)
; => "I'm thinking of number 5"
(feelings-about2 6)
; => "6 is kinda meh"
(feelings-about2 "ho")
; => "default feelings-about impl with arg: ho"
```

## Records

Records are custom, **maplike** data type (ie. have keys and values). When creating records, we also have to specify the *fields* (or key). The difference between records and maps are:
* Records are **more performant** than maps.
* Records **support protocols**.

The following example shows how to define records, create instances, and get the values:

```clj
;; create a record
(defrecord Person [name occupation])

;; how to create instance of record
;; 1. interop way
(Person. "Rick" "Mad Scientist")

;; 2. with -> factory function (automatically created after defining)
(->Person "Mory" "Student")

;; 3. with map-> factory function
(map->Person {:name "Jerry" :occupation "Office Worker"})

;; how to get values from records
(def beth (Person. "Beth" "Vet"))

;; 1. interop way
(.name beth)
(.occupation beth)

;; 2. keywords way
(:name beth)
(:occupation beth)

;; 3. get function
(get beth :name)
```

Lastly, we can extend a protocol when defining a record:

```clj
(defprotocol WereCreature
  (full-moon-behaviour2 [x]))

(defrecord WereWolf [name title]
  WereCreature ; put the protocol here
  (full-moon-behaviour2 [x]
    (str name " will howl and murder")))

(full-moon-behaviour2 (WereWolf. "Remus" "Professor"))
; => "Remus will howl and murder"
```
