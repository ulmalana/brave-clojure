# Working with JVM

In this chapter we try to understand how Clojure works as a hosted language in JVM.

## Packages and Imports

* `package`: similar to Clojure's namespaces. It defines how to organize the code.
* `import`: allows us to import classes.

## JAR files

JAR (*Java Archive*) files are used to bundle `.class` files into a single file. In the following example, we try to bundle some classes with entry point `PirateConversation` and we name it `conversation.jar`
```bash
$ jar cvfe conversation.jar PirateConversation PirateConversation.class pirate_phrases/*.class
$ java -jar conversation.jar
```

## Clojure App JAR

Clojure applications can generate Java classes using `(:gen-classes)` directive in the namespace declaration. With this directive, Clojure compiler will produce the bytecode necessary for the JVM. We can also set the entry point (main function) to our Clojure program in `project.clj`, so that when we generate JAR files it will be included in `manifest.mf`

## Java Interop

We can interact with Java's ecosystem in Clojure in several ways:

### Interop syntax

We can use Java objects and classes with `(.<methodName> <object>)` syntax. For example:

```clj
(.toUpperCase "this is clojure")
; => "THIS IS CLOJURE"


(.indexOf "Clojure" "j")
; => 3
```

Java version:

```java
"this is clojure".toUpperCase()
"Clojure".indexOf("j")
```

The *dot form* are macros and we can see its expansion:
```clj
(macroexpand-1 '(.toUpperCase "clojure"))
; => (. "clojure" toUpperCase)

(macroexpand-1 '(.indexOf "clojure" "r"))
; => (. "clojure" indexOf "r")

(macroexpand-1 '(Math/abs -4))
; => (. Math abs -4)
```

### Creating and Mutating Objects

We can create a new object with syntax `(new ClassName args)` or in dot version `(ClassName. args)`. Dot version is most used.

```clj
(new String)
; => ""

(String.)
; => ""

(String. "this is string")
; => "this is string"
```

To modify an object, we can all methods on it as well. The example below shows how to manipulate stack.

```clj
(java.util.Stack.)
; => []

;; use let binding
(let [stack (java.util.Stack.)]
    (.push stack "this is stack's content")
    stack)
; => ["this is stack's content"]

;; use doto macro to execute multiple methods on the same object
(doto (java.util.Stack.)
    (.push "content 1")
    (.push "content 2")
    (.push "content 3")
    (.pop))
; => ["content 1" "content 2"]
 
```

### Importing

We can import multiple classes using `import` function. The preferable way of importing is inside `ns` macro.
```clj
(ns ch12-jvm.core
    (:import [java.util Data Stack]
             [java.net Proxy URI]))
```

By default, Clojure **automatically imports** classes in `java.lang` (this includes `java.lang.String` and `java.lang.Math`).

## Commonly Used Java Classes

### `System` class

`System` class has useful class and methods for interacting with the environment in our system. Some examples:
```clj
(System/getenv)
(System/getProperty "user.dir")
; => "/home/riz/brave-clojure/ch12-jvm"
ch12-jvm.core> (System/getProperty "java.version")
; => "11.0.14.1"
```

### `Date` class

For working with dates using `java.util.Date`. NOTE: check `clj-time` library as well.


## Files and IO

Clojure simplifies Java's IO which isnt very straightforward. We can use `java.io.File` to interact with file's properties:

```clj
(let [file (java.io.File. "/")]
                 (println (.exists file))
                 (println (.canWrite file))
                 (println (.getPath file)))
; => true
; => false
; => /
```

Clojure also provides two simple functions to **write to a resource** (`spit`) and **reads from a resource** (`slurp`):

```clj
(spit "spit-result.txt" "- this file is generated using spit function
- you can read this with slurp function")
; => nil

(slurp "spit-result.txt")
; => "- this file is generated using spit function\n- you can read this with slurp function"
```

Lastly, Clojure has `with-open` macro which automatically close resources at the end of its body.
