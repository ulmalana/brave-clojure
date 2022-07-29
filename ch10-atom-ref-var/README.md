# Chapter 10 - Atom, Ref, and Var

Many of the problems in concurrent programming happen because of **shared access to mutable state**. Clojure is specifically designed to address the problems that arise from shared access to mutable state.

## Object-oriented Metaphysics

In OO world, the object has **properties that may change over time**, but it is still treated as **a single constant object**. Example: zombies decay over time but we still call it zombies. However, in multithread environment, it is still subject to **nondeterministic results** (reference cell). The data/state/properties could be incosistent. We could still handle that inconsistency with *mutex*. The point of OO is the **stable interface** to interact with the object.

## Clojure Metaphysics

In Clojure, we would never have the same object twice. Each zombie that has its properties changed is different. It is about **succession of values**.

In Clojure, each value is **atomic**, meaning that they it is a single irreducible units. It is **indivisible, unchanging, stable entity**. Example: number 15 wont mutate into another number, we need to **do operation** on it and get another number (**deriving**).

Compared to OO, Clojure has a different conception of **identity**. It is about a **succession of unchanging values** produced by a process over time. We can use **names** to refer to series of *individual states*, where **states are the value of an identity at a point in time**. Rather than saying the information has changed, we would say we have **received new information**.

To handle this sort of change, Clojure has **reference types** that can be used to manage identities (naming them and retrieving its states).

## Atom

Atom allow us to create a succession of values with an identity.
```
;; create new atom and bind it to fred
;; this atom refers to the map
(def fred (atom {:cuddle-hunger-level 0
                 :percent-deteriorated 0}))
                 
;; get the current state
@fred
; => {:cuddle-hunger-level 0, :percent-deteriorated 0}
```

To update the state of atom, we can use `swap!`. It takes an atom and a function as arguments. The function is applied to the current state to produce a new value, then update the atom to **refer** to the new value.

```
(swap! fred
       (fn [current-state]
         (merge-with + current-state {:cuddle-hunger-level 1})))
@fred
; => {:cuddle-hunger-level 1, :percent-deteriorated 0}
```

We can create our own function for updating the state:

```
(defn increase-cuddle-hunger-level
  [zombie-state increase-by]
  (merge-with + zombie-state {:cuddle-hunger-level increase-by}))

;; only return a new state, not update the atom
(increase-cuddle-hunger-level @fred 10)

;; update the atom with the function
(swap! fred increase-cuddle-hunger-level 10)

@fred
; => {:cuddle-hunger-level 11, :percent-deteriorated 0}
```

We can also use `update-in` function to update the atom. It takes 3 arguments: a collection, a vector for identifying which value to update, and a function to update the value.

```
(swap! fred update-in [:cuddle-hunger-level] + 10)

@fred
; => {:cuddle-hunger-level 21, :percent-deteriorated 0}
```
We can check the past states as well:

```
(let [num (atom 1)
      s1 @num]
  (swap! num inc)
  (println "State 1: " s1)
  (println "Current state: " @num))
; => State 1: 1
; => Current state: 2
```

If two threads try to call `swap!` on the same atom, both calls wont lost because Clojure use **compare-and-set** semantics to check each calls (ie. **both calls will be executed**).

How *compare-and-set* semantics work:
1. It reads the current state of the atom.
2. It then applies the update function to the state.
3. Next. it checks **whether the value it read in step 1 is identical to the atom's current state** (no change in atom that maybe caused by other threads).
4. If it is, then `swap!` updates the atom to refer to the result of step 2 (ie. no change and safe to update the state).
5. If it is not, then `swap` retries, going through the process again with step 1 (ie. state changed by other threads and try again from step 1).

We can reset the atom to certain value:
 
```
(reset! fred {:cuddle-hunger-level 0
              :percent-deteriorated 0})
```

## Watches and Validators

### Watches
We can use watches to check in on our reference types' every move. A *watch* is a function that takes **4** arguments: a **key**, the **reference being watched**, its **previous state**, and its **new state**. We can register any number of watches to a reference type. One possible **use case** of watch is to use it to **log/print messages after executing `swap!`**.

We can attach watch functions to reference types with `add-watch`.

The snippet below shows the example of `shuffle-alert` watch function in actions (see the definition in `core.clj`)
```
(reset! fred {:cuddle-hunger-level 22
              :percent-deteriorated 2})
; => {:cuddle-hunger-level 22, :percent-deteriorated 2}

@fred
; => {:cuddle-hunger-level 22, :percent-deteriorated 2}

(add-watch fred :fred-shuffle-alert-key shuffle-alert)
; => #<Atom@530c66f9: {:cuddle-hunger-level 22, :percent-deteriorated 2}>

(swap! fred update-in [:percent-deteriorated] + 1)
; => All izz well with  :fred-shuffle-alert-key
; => Cuddle hunger:  22
; => Percent deteriorated:  3
; => SPH:  2134
; => {:cuddle-hunger-level 22, :percent-deteriorated 3}

(swap! fred update-in [:cuddle-hunger-level] + 30)
; => Run you fool!
; => The zombie's SPH is  5044
; => This message brought to your courtesy of  :fred-shuffle-alert-key
; => {:cuddle-hunger-level 52, :percent-deteriorated 3}
```

### Validators

Validators can be used to control and restrict what states are allowable. For example, controlling the percentage between 0 and 100. We can attach the validators **during atom creation**. The snippet below displays how to attache `percent-deteriorated-validator` to a new zombie named `bobby`. (See the definition of `percent-deteriorated-validator` in `core.clj`)
```
(def bobby
  (atom
   {:cuddle-hunger-level 0 :percent-deteriorated 0}
   :validator percent-deteriorated-validator'))
   
(swap! bobby update-in [:percent-deteriorated] + 200)
; => Execution error (IllegalStateException) at ch10-atom-ref-var.core/percent-deteriorated-validator' (core.clj:95).
; => That's not mathy!
```

## Ref: Modeling Sock Transfers

Refs let us to **update the state of multiple identities** using transactions semantics. These transactions have three characteristics:
1. **Atomic**: all refs are updated *or* none of them are.
2. **Consistent**: Refs always appear to have valid states.
3. **Isolated**: transactions behave as if they executed serially.

Now, for updating the refs value, we can use `alter`. `dosync` **intiates a transaction** we must run `alter` inside it. The following function describes when a gnome steals a sock, we need to 1) add a sock to the gnome, 2) remove one sock of the stolen pair.
```
(defn steal-sock
  [gnome dryer]
  (dosync
   (when-let [pair (some #(if (= (:count %) 2) %) (:socks @dryer))]
     (let [updated-count (sock-count (:variety pair) 1)]
       ; steal one sock and put it to gnome
       (alter gnome update-in [:socks] conj updated-count)

       ; remove the the stolen pair (because it is only one sock now)
       (alter dryer update-in [:socks] disj pair)

       ; update the dryer to have one sock stolen
       (alter dryer update-in [:socks] conj updated-count)))))

;; steal a sock
(steal-sock sock-gnome dryer)

;; check what the gnome has now
(:socks @sock-gnome)
#{{:variety "gollumed", :count 1}}
```

**NOTE**: when we `alter` a refs, the change **isnt immediately visibile outside of the current transaction** until it commits the change. When transaction A and transaction B are doing transactions and **transaction B commits first, then transaction A has to retry** the transaction.

### Commute
`commute` is similar to `alter` for updating the ref, but it doesnt force a transaction retry (`alter` force retry). This can improve the performance but **may result in invalid state**.

## Var

Vars are **associations between symbols and objects**, and are created with `def`. Usually `def` is used to define a constant, but we can define **dynamic vars** as well whose binding can be changed. Dynamic vars are useful for creating a global name that can refer to different values in different contexts.

```
;; create a dynamic var
;; notice the ^:dynamic and enclosing **
(def ^:dynamic *notification-address* "dobby@elf.org")
```

We can change the binding of dynamic vars *temporarily* using `binding`:
```
(binding [*notification-address* "test1@elf.org"]
    (println *notification-address*)
    (binding [*notification-address* "test2@elf.org"]
        (println *notification-address*))
    (println *notification-address*))
; => test1@elf.org
; => test2@elf.org
; => test1@elf.org
```

Dynamic vars are most often used **to name a resource** that one or more functions target. For example `*out*` to represent the standard output for print operations. The snippet below shows how to rebind `*out*` to a file writer instead of the usual print operation. The contents will be saved in a file instead of displaying them.

```
(binding [*out* (clojure.java.io/writer "print-output")]
    (println "This is the result of rebinding *out* to a writer, instead of the usual print"))

;; check the result in print-output file
(slurp "print-output")
```

We can use `set!` to rebind the dynamic vars to a new value.

### Altering the Var Root

When we create a var with `def`, its initial value is called its **root**. For example, the following var has `"four"` as its root. We can **permanently** change this root with `alter-var-root`. `alter-var-root` takes a function to change the root.
```
(def quad "four")

;; change the value of quad from "four" to "empat"
(alter-var-root #'quad (fn [_] "empat"))
```

## Stateless Concurrency and Parallelism with `pmap`

`pmap` is the paralellized version of `map`. It handles the running of each application of mapping function on a separate thread. The following snippet compares the performance of `map` and `pmap` for transfroming thousands of very long strings.
```
(def alphabet-length 26)

;; vector of chars, A-Z
(def letters (mapv (comp str char (partial + 65)) (range alphabet-length)))

(defn random-string
  "Returns a random string of specified length"
  [length]
  (apply str (take length (repeatedly #(rand-nth letters)))))

(defn random-string-list
  [list-length string-length]
  (doall (take list-length (repeatedly (partial random-string string-length)))))

(def orc-names (random-string-list 3000 7000))

(time (dorun (map clojure.string/lower-case orc-names)))
; => "Elapsed time: 339.936864 msecs"

;; pmap is faster than map
ch10-atom-ref-var.core> (time (dorun (pmap clojure.string/lower-case orc-names)))
; => "Elapsed time: 256.386671 msecs"
```

In some cases, `pmap` is **slower** than `map` because of the **overhead for thread coordination**. The solution to this is to increase the *grain size* or *batch size* (so each thread has more works). By default, the grain size is **1**, and we can increase it by partitioning the collection with `partition-all`. 
```
(def numbers [1 2 3 4 5 6 7 8 9 10])

;; partition the numbers into a group of 3
(partition-all 3 numbers)
; => ((1 2 3) (4 5 6) (7 8 9) (10))
```

See `ppmap` function in `core.clj` for the definition of `pmap` that support partitioning.
