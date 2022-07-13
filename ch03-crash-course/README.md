# Chapter 03 - Clojure Crash Course

## Syntax

### Forms

Clojure recognizes two kinds of structure:
* **Literal structure** (number, strings, maps, etc)
    * `1`
    * `"a string"`
    * `["this" "is" "vector"]`
* **Operations**
    * `(operator operand1 operand2 ... )`
    

Valid forms:
* `(+ 3 4 5)`
* `(str "hi " "this is " " riz")`

### Control Flow

#### If

Structure:

```
    (if boolean-form
        then-form
        optional-else-form)
```

#### Do
Handling multiline branches with `do`:

```
    (if true
        (do (println "true")
            "this is for true")
        (do (println "false")
            "this is for false"))
```

#### When

* A combination of `if` and `do`, but with no `else` branch.

```
    (when true
        (println "success")
        "another form here")
```
* Use `when` if we want to do multiple things when some condition is true, and always return nil when the condition is false.

#### Truth value
* `true`
* `false`
* `nil` : to indicate no value and `false`.
* **Except for `false` and `nil`, all other values are true**
* Clojure's equality operator: `=`
    * can be used for many types.
    
* Boolean operator
    * `or`: return the **first truthy** or **the last value**.
    * `and`: return the **first falsy** or **the last value**
    
### Naming values with def

* Syntax: `(def <identifier> <value>)`
    * Example: `(def names ["rick" "morty"])`
    
## Data Structures

### Numbers

Integer, floats, ratios.

```
    93
    2.71
    1/100
```

### Strings

* **Only allows double quote**.
* **Only allows concatenation with `str`**

```
    (def name "Meeseek")
    (str "Hi I'm Mr. " name ", look at me!")
```

### Maps

* Similar to **dictionary** or **hash**.
* There are two kinds of maps:
    * **Hash maps**
    * **Sorted maps**

* Empty map: `{}`
    
* map for representing name
    * `{:firstname "Shinichi" :lastname "Kudo"}`
    
* associate key with a function
    * `{"plus-sign" +}`
    
* nested maps
    * `{:name {:first "Jack" :middle nil :last "Reacher"}}`
    
* create map with hash-map
    * `(hash-map :a 1 :b 2)`

* Get a value in a map with `get`
    * `(get {:a 1 :b 2} :b)` => `2`
    
* Return default value when we cant find a value
    * `(get {:a 0 :b 1} :c "not found")` => `"not found"`
    
* get a value in a nested map with get-in
    * `(get-in {:a 0 :b {:c "nested val"}} [:b :c])` => `"nested val"`

### Keywords

* Clojure keywords are primarily used as keys in maps.
    * `:this :is :keywords :example`
* Keywords can also be used as functions that lookup the corresponding value
    * `(:a {:a 1 :b 2})` -> `1`
    * `(:c {:a 1 :b 2} "not found")` -> `not found`

### Vectors

* 0-indexed **mixed** collection.

* basic vector
    * `[3 2 1]`
    
* get a value using its index
    * `(get [{:a 5} 2 "hoi"] 0)` => `{:a 5}`
    
* create vector with vector function
    * `(vector "ni" "hao" "ma")` => `["ni" "hao" "ma"]`
    
* append an element to a vector with conj
    * `(conj [1 2 3] 4)` => `[1 2 3 4]`
    

### Lists

* Mixed collection, similar to vectors.
* Use parenthesis with a single quote in the beginning, instead of square brackets.
    * `'(1 2 3 4)'`
* Use `nth` to get value, instead of `get`.
    * `(nth '(:a :b :c)  2)` -> `:c`
    * `nth` is **slower than ** `get`.
* Create a list with `list` function
    * `(list 1 :two {3 4})` -> `(1 "two" {3 4})`
* Elements are added in the beginning.
    * `(cont '(1 2 3) 4)` -> `(4 1 2 3)`  
* When should we use list or vector?
    * **writing macro** or easy add items to the beginning: **List**
    * **Otherwise**: Vector
    
### Sets

* Collections of **unique values**. Start with `#` and use brackets
    * `#{"first" 2 :three}`
* Create a set with `hash-set`
    * `(hash-set 2 2 3 3)` -> `#{2 3}`
* Add an element with `conj`
    * `(conj #{:a :b} :b)` -> `#{:a :b}`
* Convert from vectors or list to set with `set` function
    * `(set [1 2 3 4 4])` -> `#{1 2 3 4}`
* We can check the set membership using:
    * `contains?`
    * `get`
    * or the keyword
