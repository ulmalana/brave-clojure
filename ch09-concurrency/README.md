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
