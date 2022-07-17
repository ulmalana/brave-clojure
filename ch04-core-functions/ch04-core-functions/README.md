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
```
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

## Lazy Seqs

* `map` first calls `seq` to convert the arguments to a sequence.
    * Many functions like `map` and `filter` also return **lazy seq**
* A**Lazy seq** is a seq whose members **arent computed until we try to access** them.
    * Similar to laziness in Haskell.
* We can use `time` function to know how long a function takes to make computation.
* A lazy seq consists of **two parts**:
    * a **recipe for how to realize** the elements of a seq.
    * the elements that **have been realized** so far.
* Unrealized elements will use *the recipe* to generate the element.
* When we try to realize an element for the **first time**, it may **take longer than we expected**.
    * Because Clojure **chunks** its lazy seq (ie. it **realizes some of the next elements as well**).
    * This almost always results in better perfomance.
    
### Infinite Sequences

* With lazy seq, we can create **infinite sequences**. 
* One way to construct an infinite seq is with `repeat`.
    * `(concat (take 8 (repeat "na")) ["Batman"])`
    * `=> ("na" "na" "na" "na" "na" "na" "na" "na" "Batman")`
* Or, with `repeatedly` that can take a function to generate each element.
    * `(take 3 (repeatedly (fn [] (rand-int 10))))`
    * `=> (2 6 5)`
* Generate infinte even numbers
```
    (defn even-numbers
        ([] (even-numbers 0))
        ([n] (cons n (lazy-seq (even-numbers (+ n 2))))))
    
    (take 10 (even-numbers))
    ; => (0 2 4 6 8 10 12 14 16 18)
        
```

## Collection Abstraction

**Sequence abstraction** (like `first` and `cons`) is about operating on **members individually**.
**Collection abstraction** is about the data structure as a whole (`count`, `empty?`, `every?`).

### `into`

Take **two collections** and *add all the elements from the second collection* to *the first collection*.
```
    (into [] '(1 2 3 4))
    ; => [1 2 3 4]
    
    (into #{"satu"} ["satu" "dua" "tiga"])
    ; => #{"satu" "dua" "tiga"}
```

### `conj`

`conj` is similar to `into` but we can pass many singular value (**non collection**) to be added to the first collection.

```
    (conj [0] [1])
    ; => [0 [1]]
    
    (into [0] [1])
    ; => [0 1]
    
    (conj [0] 1)
    ; => [0 1]
    
    (conj [0] 1 2 3 4)
    ; => [0 1 2 3 4]
    
    (conj {:time "midnight"} [:place "cemetarium"])
    ; => {:place "cemetarium" :time "midnight"}

```

`conj` defined in terms of `into`:

```
    (defn my-conj
        [target & additions]
        (into target additions))
        
    (my-conj [0] 1 2 3 4)
    ; => [0 1 2 3 4]
```

## Function Functions

### `apply`
`apply` **explodes** a seqable data structure so it can be passed to a function that **expects a rest parameter**.

```
    (max 0 1 2)
    ; => 2
    
    (max [0 1 2])
    ; => [0 1 2]
    
    ; similar to (max 0 1 2 3)
    (apply max [0 1 2])
    ; => 2
```


Defining `into` in terms of `conj`:

```
    (defn my-into
        [target additions]
        (apply conj taget additions))
        
    (my-into [0] [1 2 3])
    ; => [0 1 2 3]
```

### `partial`

`partial` is used to define a partially-applied function. 
It takes **a function** and **any number of arguments**, then returns a new function.

```
    (def add10 (partial + 10))
    
    (add10 3)
    ; => 13
    
    (def add-missing-elements
        (partial conj ["water" "earth" "air"]))
    
    (add-missing-elements "hydrogen" "nitrogen")
    ; => ["water" "earth" "air" "hydrogen" "nitrogen"]
``` 

### `complement`

Sometimes we want complement of a function. For example, in `vampire?` we want to check if a record is a vampire. What if we want to check if a record is not vampire (ie. human)? We can use `complement` function.

```
    ;; assuming we have define vampire? function
    (def not-vampire? (complement vampire?))
    
    ;; human test
    (defn identify-humans
        [social-security-numbers]
        (filter not-vampire?)
            (map vampire-related-details social-security-numbers))
```
