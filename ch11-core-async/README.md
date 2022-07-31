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

## Hot Dog Machine Process

Lets create a hot dog machine:
```
;; hot dog machine
(defn hot-dog-machine
  []
  (let [in (chan)
        out (chan)]
    (go (<! in) ; take money in
        (>! out "hot dog")) ; return a hotdog
    [in out])) ; return in and out channel
```
In the definition above, there is no input validation, so we can put anything and get a hotdog
```
(let [[in out] (hot-dog-machine)]
    (>!! in "pocket lint")
    (<!! out))

; => "hot dog"
```
Lets create the better version

```
;; better version of hot dog machine
(defn hot-dog-machine-v2
  [hot-dog-count]
  (let [in (chan)
        out (chan)]
    (go (loop [hc hot-dog-count]
          ;; if hotdog is still available
          (if (> hc 0)
            (let [input (<! in)]
              (if (= 3 input)
                (do (>! out "hot dog")
                    (recur (dec hc)))
                (do (>! out "wilted lettuce")
                    (recur hc))))
            ;; if no hotdog left, close the channel
            (do (close! in)
                (close! out)))))
    [in out]))
```
In above version, we set the hotdog price `3` and also set how many hotdogs we have in our store. If sold out, then return nil.
```
(let [[in out] (hot-dog-machine-v2 2)]
    ;; buy without money
    (>!! in "pocket lint")
    (println (<!! out))
    
    ;; buy one
    (>!! in 3)
    (println (<!! out))
    
    ;; buy another
    (>!! in 3)
    (println (<!! out))
    
    ;; buy the third. but since there are only two and already sold out, we will get nothing
    (>!! in 3)
    (<!! out))

; => wilted lettuce
; => hot dog
; => hot dog
; => nil
```

We can create a pipeline of processes with put and take:
```
(let [c1 (chan) ;; create three channels
      c2 (chan)
      c3 (chan)]
  ;; take c1, uppercase it, and put in c2
  (go (>! c2 (clojure.string/upper-case (<! c1))))

  ;; take c2, reverse it, and put in c3
  (go (>! c3 (clojure.string/reverse (<! c2))))

  ;; take c3 and print it
  (go (println (<! c3)))

  ;; put something in c1
  (>!! c1 "redrum"))

; => MURDER
```

## `alts!!`

`alts!!` function allows us to use **the result of the first successful channel operation** among a collection of operations. The other channels are still available if we want to take their values.
```
(let [c1 (chan)
      c2 (chan)
      c3 (chan)]
  (upload "serious.jpg" c1)
  (upload "fun.jpg" c2)
  (upload "sassy.jpg" c3)
  
  ;; choose the first result from these channels 
  (let [[headshot channel] (alts!! [c1 c2 c3])]
    (println "Sending headshot notification for" headshot)))
; => Sending headshot notification for sassy.jpg
```

We can also set a **timeout** to channels in `alts!!` so we have a time limit on concurrent operations

```clj
(let [c1 (chan)]
  (upload "sad.jpg" c1)
  (let [[headshot channel] (alts!! [c1 (timeout 20)])]
    (if headshot
      (println "Sending headshot notification for " headshot)
      (println "Timed out"))))

; => Timed out
```
