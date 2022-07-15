# Chapter 04 - Core Functions in Depth

## Programming to Abstractions

Clojure tends to define a function in terms of **sequence abstraction**, not in terms of specific data structures. As long as the data structures respond to the core sequence abstraction, it will work when we apply the function. For example, `map` and `reduce` **take a sequence** and can work with list, vector, or other data structures. If **the core sequence functions** `first`, `rest`, and `cons` work on a data structure, then we can say **that data structure implements the sequence abstraction**.

* `first`: returns the first element of a collection.
* `rest`: returns the second until the last element of a collection.
* `cons`: adds a new element to the beginning of a collection.

### Abstraction through Indirection

* **Indirection**: a generic term for the mechanism a language employs so that one name can have multiple related meanings. (ex: `first` can handle multiple data structure).

* One way to provide indirection: **polymorphism**
* Clojure also use type conversion with `seq`as indirection so that the data structure can work with `first`, `rest`, and `cons`.
    * ```
        (seq '(1 2 3))
        ; => (1 2 3)
        
        (seq [1 2 3])
        ; => (1 2 3)
        
        (seq #{1 2 3})
        ; => (1 2 3)
        
        (seq {:name "luffy" :occupation "pirate"})
        ; => ([:name "luffy"] [:occupation "pirate"])
        
        ; convert back from seq
        (into {} (seq {:a 1 :b 2 :c 3}))
        ; => {:a 1 :b 2 :c 3}
        
      ```

## Seq Function Examples

### `map`

`map` can take **multiple collections** as arguments and take **collection of functions** as argument as well. Keyword (`:example`) can also be used as function for mapping.

### `reduce`

`reduce` processes each element in a sequence to build a result. See `core.clj` of this project to see two examples of using `reduce` for transforming a map's value and for filtering keys out of a map.

### `take`, `drop`, `take-while`, `drop-while`

* `take`: returns the first n elements of the sequence.
* `drop`: returns the sequence with the first n elements removed.
* `take-while`: keep taking the element as long as the predicate functions is true.
* `drop-while`: keep dropping the element as long as the predicate function is true.

### `filter` and `some`

* `filter`: return all elements that test true for a predicate.
* `some`: check if a collection contains any values that test true for a predicate function.

### `sort` and `sort-by`

* `sort`: ordinary sorting function.
* `sort-by`: add a **key function** to sort a collection.
    * `(sort-by count ["aaa" "c" "bb"])` -> `("c" "bb" "aaa")`

### `concat`

* `concat`: append the member of a sequence to the end of another.
    * `(concat [1 2] [3 4])` -> `(1 2 3 4)`
