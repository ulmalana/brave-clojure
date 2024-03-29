# Chapter 09 - Concurrent and Parallel Programming

## Concurrency vs. Parallelism

**Concurrency** refers to managing more than one task at the same time. Meanwhile, **parallelism** refers to executing more than one task at the same time. We may manage more than one task but not executing at the same time (**inteleaving**). **Parallelism is a subclass of concurrency**: we have to manage multiple tasks first, then execute multiple tasks simultaneously.

## JVM Threads

A thread is a subprogram which executes its own set of instructions while enjoying shared access to program's state. One program can have many threads. JVM provides its own platform-independent thread managemenet functionality and Clojure uses it.

## Concurrency Challenges

### Reference cell
Reference cell problem happens when **two threads can read and write to the same location**. Thus, the value depends on the order of reads and writes.

### Mutual exclusion
Mutual exclusion is a way to **claim an exclusive access**, blocking other threads temporarily.

### Deadlock
Deadlock is a situation when **each thread cant proceed because each waits for another thread to take action**.


## Futures, Delays, and Promises

When we write serial code, we bind together these three events:
1. Task definition
2. Task execution
3. Requiring the task's result

Learning concurrent programming is about **identifying when these chronological couplings arent necessary**. Future, delays, and promises allow us **to separate** task definition, task execution, and requiring the result.

## Futures

Futures is for **defining a task and place it on another thread** without requireing the result immediately. 

```
(do 
    (future (Thread/sleep 4000)
            (println "4 seconds later"))
    (println "this is now"))

; => this is now
; => 4 seconds later
```

`future` function returns **a reference value** that we can use to request the result. If the future isnt done computing, we will have to wait. Requesting a future's result is called **deferencing the future** and we can do it with `deref` function or `@` macro.

```
(let [result (future (println "this prints once")
                     (+ 1 1))]
    (println "deref: " (deref result))
    (println "@: " @result))

; => this prints once
; => deref:  2
; => @:  2
```

We can place a time limit on how long to wait for future by passing an additional argument to `deref`. In the snippet below, `deref` returns `5` if the future doesnt return a value within 10 ms:
```
(deref (future (Thread/sleep 1000) 0) 10 5)
; => 5
```

We can also check if the future is done computing with `realized?` function.

### Delays

We can use delays to **define a task without having to execute it or require the result immediately**. For forcing evaluation, we can use `force`, `deref`, or `@`. Delay is **only run once** and its result is cached.

```
(def jackson-5-delay
  (delay (let [message "Just call my name and I'll be there"]
           (println "First deref: " message)
           message)))

(force jacson-5-delay)
; => First deref: Just call my name and I'll be there
; => "Just call my name and I'll be there"

@jackson-5-delay
; => "Just call my name and I'll be there"
```

### Promises

Promises let us expect a result **without** having to define the task that should produce it or when the task should run. To deliver the result, we can use `deliver` function.

```
(def my-promise (promise))
(deliver my-promise (+ 1 2))
@my-promise
; => 3
```

We can only **deliver once**. When the derefence process takes a long time, we can set the time limit. In the example below, we will wait for 100 ms to derefencing and return "timed out" if it takes more than 100 ms:

```
(let [p (promise)]
    (deref p 100 "timed out"))
```
