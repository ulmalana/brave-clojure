# Chapter 05 - Functional Programming

## Pure Functions: What and Why

A function is **pure** if it meets two conditions:

* **It always returns the same result**, given the same arguments. (**Referential transparency**)
* It **cant cause any side effects**, ie. doesnt make any changes that are observable outside the function itself. For example: writing to a file.

## Living with Immutable Data Structures

Ensuring that data is immutable by default is a good thing.

## Cool Things to Do with Pure Functions

We can derive new functions from existing functions, just like deriving new data from existing data.


### `comp`

With `comp` we can compose new functions. Example:

```
    ;; similar to ((inc . *) 2 3)
    ((comp inc *) 2 3)
    
    ; (inc (* 2 3))
    ; (inc 6)
    ; => 7
```

### `memoize`

Since pure functions are referentially transparent, we can store the result of a function and use it for later with `memoize`. The subsequent calls with the same arguments can return the result almost immediatelu. This is useful for functions that take a lot of time to run.
