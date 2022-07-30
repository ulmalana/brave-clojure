# Chapter 11 - Concurrent Processes with core.async

Everything in this world can be deconstructed into a set of behaviour that follow the general from "when *x* happens then do *y*". Inside Clojure's core.async is the process, a concurrently running unit of logic that can respond to events. We can create multiple independent processes within a single program and there is no central control mechanism pulling the strings.

## Getting started with core.async

There are several functions in core.async that can be used when dealing with processes. These functions will be explained using examples.
```
(def echo-chan (chan))
; => #'ch11-core-async.core/echo-chan

(go (println (<! echo-chan)))
; => #object[clojure.core.async.impl.channels.ManyToManyChannel 0x55c08dd8 "clojure.core.async.impl.channels.ManyToManyChannel@55c08dd8"]

(>!! echo-chan "halo")
; => halo
; => true
```
In the example above, `chan` is used to create a **channel** named `echo-chan`. Channels communicate messages, and we can **put on** or **take messages off** channels. Moreover, processes **wait for the completion** of put and take (do nothing until put or take succeeds).

`go` is used to **create a new process**. Everything inside **`go` block** runs concurrently on a separate thread from a thread pool (no need to create a new thread). Inside `go` block above, we want to retrieve a message from `echo-chan` channel using `<!` operator, then print the message using `println`. 

Lastly, `>!!` is used to put a message on a channel. In the case above, we put `"halo"` to `echo-chan`. Then, the process created in `go` block will retrieve `"halo"` and print it. When we put a message on a channel, we **need to have at least one process that is listening** to take the message, **otherwise the put process will wait indefinitely** for some process to retrieve the message.

## Buffering

We can create a channel with a buffer and then we can put values without waiting. The example below shows how we create a channel with buffer size 2. If we want put the third value, then it will block indefinitely.
```
(def echo-buffer (chan 2))
; => #'ch11-core-async.core/echo-buffer

(>!! echo-buffer "halo 1")
; => true
(>!! echo-buffer "halo 2")
; => true

;; buffer is full, this will block indefinitely
(>!! echo-buffer "halo 3")
```

Other ways of creating buffer is `sliding-buffer` which works in **FIFO fashion** and `dropping-buffer` which works in **LIFO fashion**. None of them will never cause `>!!` to block. 

## Blocking and Parking

Varieties of put and take operators. The differences are in the efficiency.

|     | **Inside go block** | **Outside go block** |
| --- | --- | --- |
| put | `>!` or `>!!` | `>!!` |
| take | `<!` oe `<!!` | `<!!` |

Because `go` block can create many process on a limited size of thread pool, then **most processes need to wait**. There are two kinds of wait: **parking** and **blocking**.

### Blocking
The usual kind of wait. A thread **stops execution until a task is complete**. We need to *create a new thread* to continue working. `>!!` and `<!!` are **blocking put** and **blocking take**, respectively.

### Parking
Parking **frees up thread** so it can keep doing work (ie. no need to create a new thread). If a process is using a thread to wait for a take or put, we can move this process off the thread so that other process can use it. It is similar to **interleaving**. Parking is only possible **within** `go` block and use `>!` (**parking put**) or `<!` (**parking take**)

## `thread`

There are times when blocking is preferable than parking and we can use `thread` instead of `go`. `thread` is similar to `future`: it creates a new thread and executes process on that, but instead of returning a value like `future`, it **returns a channel**. We can take the value from the returned channel.
```
;; thread returned a channel and bind it to ret-channel
(let [ret-channel (thread "halo")]
    ;; take the value "halo" from ret-channel
    (<!! ret-channel))
```

If we want to perform a long running task, `go` **may use all of our threads** and we cant continue working. So it is preferable to use `thread` which to use one thread even thoug it does blocking. In short, use `go` for parking which could improve performance, and use `thread` for long-running tasks.
