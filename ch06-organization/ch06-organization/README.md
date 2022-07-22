# Chapter 06 - Organizing Your Project

## Our Project as a (real world) Library

Clojure maintains the associations between the identitifers and the shelf/box addresses (where the content are stored) using **namespaces**. Namespaces contain maps between human-friendly **symbol** and references to the shelf address (aka **var**), just like a book catalog in real world library. To check the current namespace we are in, we can run the following (usually the namespace is already shown in the REPL prompt):
```
    (ns-name *ns*)
    ; => ch06-organization.core
```
We can have more than one namespace but technically there might be an upper limit.

When we give Clojure **a symbol** like `map`, it finds the corresponding **var** in the current namespace, gets the address, then retrieves the object inside that address (in this case **the function** that `map` refers to).

We can treat any Clojure form as a data (ie. not to be evaluated)by single-quoting it.
```
    > inc
    #function[clojure.core/inc]
    
    > 'inc
    ; => inc
    
    > (map inc [1 2])
    ; => (2 3)
    
    > '(map inc [1 2])
    ; => (map inc [1 2])
```

## Storing Objects with `def`

When we store objects with the following code
```
    > (def great-books ["Inferno" "Silmarillion"])
    ; => #'ch06-organization.core/great-books
    
    > great-books
    ; => ["Inferno" "Silmarillion"]
```
this is what happens inside Clojure:
1. Update the current namespace's map with the association between `great-books` and its var.
2. Find a free storage shelf.
3. Store `["Inferno" "Simlarillion"]` on that shelf.
4. Write the address of the shelf on the var.
5. Return the var (ie. `#'ch06-organization.core/great-books`)

This process is called **interning a var**, and we can check the interned vars using:

```
    (ns-interns *ns*)
    ; => {great-books #'ch06-organization.core/great-books}
```

We can see the full map a namespace uses with `(ns-map *ns*)`. `#'ch06-organization.core/great-books'` is the **reader form** of a var and we can use `deref` to get the objects it points to.

```
    > (deref #'ch06-organization.core/great-books)
    ; => ["Inferno" "Silmarillion"]
    
    ;; the same as
    > great-books
    ; => ["Inferno" "Silmarillion"]
```

When we overwrite `great-books`, it **points to the new objects and Clojure cant find the first object**. It is called *name collision*

## Creating and Switching to Namespaces

Three tools for creating namespaces:
1. `create-ns` : only create and not move into.
2. `in-ns` : create and move into.
3. `ns` : a macro, mostly used.

By default, `user` namespace or `core` namespace created from `lein` refer to `clojure.core` that contains Clojure core functions. When we move to a new empty namespace, we need to use fully qualified name of the function. Example: `refer` becomes `clojure.core/refer`.

```
    ch06-organization.core> (create-ns 'cheese.taxonomy)
    #namespace[cheese.taxonomy]

    ch06-organization.core> (ns-name (create-ns 'cheese.taxonomy))
    cheese.taxonomy

    ch06-organization.core> (in-ns 'cheese.analysis)
    #namespace[cheese.analysis]

    cheese.analysis> (in-ns 'cheese.taxonomy)
    #namespace[cheese.taxonomy]

    cheese.taxonomy> (def cheddars ["mild" "mediaum" "strong"])
    #'cheese.taxonomy/cheddars

    cheese.taxonomy> (in-ns 'cheese.analysis)
    #namespace[cheese.analysis]

    cheese.analysis> cheddars
    Syntax error compiling at (*cider-repl brave-clojure/ch06-organization:localhost:42017(clj)*:1:7956).
    Unable to resolve symbol: cheddars in this context

    cheese.analysis> cheese.taxonomy/cheddars
    ["mild" "mediaum" "strong"]

    cheese.analysis> (in-ns 'cheese.taxonomy)
    #namespace[cheese.taxonomy]

    cheese.taxonomy> (def bries ["Wisconsin" "Somerset" "Brie de Meaux"])
    #'cheese.taxonomy/bries

    cheese.taxonomy> (in-ns 'cheese.analysis)
    #namespace[cheese.analysis]

    cheese.analysis> (clojure.core/refer 'cheese.taxonomy)
    nil

    cheese.analysis> bries
    ["Wisconsin" "Somerset" "Brie de Meaux"]

    cheese.analysis> cheddars
    ["mild" "mediaum" "strong"]
``` 

### `refer`
`refer` function can be used to **import objects from other namespaces**, so that we dont need to use their fully qualified name. We can pass filters `:only`, `:exclude`, and `:rename` when we import for finer control.

We can use `defn-` function to create private functions that can be accessed only inside a namespace. When we import a namespace, we cant use those private functions.

### `alias`
`alias` is used to shorten a namespace.

## Real Project Organization

`ns` macro is the primary way to **create and manage namespaces**. It is similar to `in-ns` function but with many additional features.

When we create a project with `lein`, all the code are placed in `src` directory. The structure inside `src` directory corresponds to the namespace. For example: `cheese-code.core` namespace corresponds to `src/cheese_code/core.clj` file.

We can use `require` to import objects/functions from another namespace. Another way to import namespaces is with `use`. `use` can automatically `refer` the namespace so that we dont need to use its fully qualified name. However, `use` is **not recommended for production code**.

### `ns` macro

`ns` is the recommended way to manage namespace because it incorporates all the previously explained functions like `require`, `use`, `in-ns`, `alias`, and `refer`. It also refers `clojure.core` namespace by default, so we can use shorter name for core functions.

There are six possible kinds of references in `ns`:
* `(:refer-clojure)`
* `(:require)` -> **similar to** `(require)` function
* `(:use)`
* `(:import)`
* `(:load)`  -> **seldom used**
* `(:gen-class)`

The following snippet is one example of using `ns`:
```
(ns cheese-code.core
    (:require [cheese-code.visualization.svg :as svg :refer [points]]
              [clojure.java.browse :as browse]))
```
