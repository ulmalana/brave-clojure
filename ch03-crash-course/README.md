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

## Functions

### Calling functions
* Syntax: `(operator operand1 operand2 ...operandn)`
  ```
          (+ 1 2 3 4)
          (first [1 2 3 4])
      
          ((or + -) 1 2 3)
          ; => 6
      
          ((and (= 1 1) +) 1 2 3)
          ; => 6
  ```
      
* Higher order function example: `map`
    * `(map inc [0 1 2 3])` -> `(1 2 3 4)`
* Clojure evaluates all arguments **recursively**.
  ```
        (+ (inc 199) (/ 100 (- 7 2)))
            
        (+ 200 (/ 100 (- 7 2)))
            
        (+ 200 (/ 100 5))
            
        (+ 200 20)
            
        200
  ```
### Function calls, Macro calls, and Special Forms

Special forms (such as `if`) and Macros are for **implementing Clojure core functionality**. They **dont always evaluate their operands** and we **cant use them as arguments**.

### Defining functions

Functions are composed in five parts:
1. `defn`
2. function name
3. docstring function description (optional, can be accessed with `(doc fn-name)`)
4. parameters in brackets
5. function body

```
    (defn test-function
        "This is the description of test-function"
        [param]
        (str "halo, this is" param))
```

#### Function arity
The number of arguments that a function can take.

#### Arity overloading
A function can support multiple arrity that will have different behaviour depending on the number of passed arguments.
```
    (defn multi-arity
        ;; 3 arity and its body
        ([first-arg second-arg third-arg]
            (do-things first-arg second-arg third-arg))
        
        ;; 2 arity
        ([first-arg second-arg]
            (do-things first-arg second-arg))

        ;;1 arity
        ([first-arg]
            (do-things first-arg)))
```

#### Variable-arity function
We can define a function that can **take abritrary number of arguments (resr paramter)** with `&`. With `&`, all arguments will be put in a list.

#### Mixing normal parameter with rest parameter

We can mix normal parameters with rest parameters, but **rest parameter should be the last**.

#### Destructuring functions
Similar to pattern matching, we can destructure arguments of functions.

```
    ;; this function always return the first element, no matter how long the argument is.
    (defn my-first
        [[first-thing]]
        first-thing)
        
    ;; this function only cares about the first two args, and ignores the rest
    (defn take-two
        [[x y & rest]]
        (println (str "this is the first arg: " x))
        (println (str "this is the second arg:" y))
        (println (str "and the rest: "
                     (clojure.string/join ", " rest))))
```

We can also destructure `maps`

```
    (defn location
        [{lat :lat lng :lng}]
        (println (str "latitude: " lat))
        (println (str "longitude: " lng)))
```

### Anonymous function

```
    (fn [param-list]
        function-body)
```
* **Example: **
    * `((fn [x] (* x x)) 4)` -> `16`
* **Compact version** of anonymous function with `#`:
    * `(#(* % %) 4)` -> `16`
    * `(#(* %1 %2) 4 5)` -> `20`
    * `(#(identity %&) 1 :riz "hehe")` -> `(1 :riz "hehe")`
    
### Returning functions

We can create a function that returns a function. The returned function is called **closure**. Example:

```
    (defn inc-maker
        "create a custom incrementor"
        [inc-by]
        #(+ % inc-by))
        
    ;; caller function
    (def inc3 (inc-maker 3))
    
    ;; call inc3
    (inc3 7)
    ; => 10
```
