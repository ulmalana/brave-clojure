(ns ch10-atom-ref-var.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

;; atom
(def fred (atom {:cuddle-hunger-level 0
                 :percent-deteriorated 0}))

@fred

;; update current state state
(swap! fred
       (fn [current-state]
         (merge-with + current-state {:cuddle-hunger-level 1})))
@fred

;; update both keys
(swap! fred
       (fn [current-state]
         (merge-with + current-state {:cuddle-hunger-level 1
                                      :percent-deteriorated 1})))
@fred

;; create  a function to update the atom
(defn increase-cuddle-hunger-level
  [zombie-state increase-by]
  (merge-with + zombie-state {:cuddle-hunger-level increase-by}))

;; only return a new state, not update the atom
(increase-cuddle-hunger-level @fred 10)

;; update the atom with the function
(swap! fred increase-cuddle-hunger-level 10)

@fred

;; use update-in instead to update the atom
(swap! fred update-in [:cuddle-hunger-level] + 10)

@fred

;; see the past state and current state
(let [num (atom 1)
      s1 @num]
  (swap! num inc)
  (println "State 1: " s1)
  (println "Current state: " @num))

;; reset the atom
(reset! fred {:cuddle-hunger-level 0
              :percent-deteriorated 0})

;; watches ;;

;; calculate zombie's shuffle speed
(defn shuffle-speed
  [zombie]
  (* (:cuddle-hunger-level zombie)
     (- 100 (:percent-deteriorated zombie))))

;; display an alert if the shuffle speed is > 5000
;; otherwise print current state.
;; we can attach this function to fred with add-watch
(defn shuffle-alert
  ;; watched and old-state are necessary but we may ignore it in the body,
  ;; only use when we need to.
  [key watched old-state new-state]
  (let [sph (shuffle-speed new-state)]
    (if (> sph 5000)
      (do
        (println "Run you fool!")
        (println "The zombie's SPH is " sph)
        (println "This message brought to your courtesy of " key))
      (do
        (println "All izz well with " key)
        (println "Cuddle hunger: " (:cuddle-hunger-level new-state))
        (println "Percent deteriorated: " (:percent-deteriorated new-state))
        (println "SPH: " sph)))))

;; Validators ;;

;; validating a value that should be between 0 and 100
(defn percent-deteriorated-validator
  [{:keys [percent-deteriorated]}]
  (and (>= percent-deteriorated 0)
       (<= percent-deteriorated 100)))

;; same with function above but throws and exception
(defn percent-deteriorated-validator'
  [{:keys [percent-deteriorated]}]
  (or (and (>= percent-deteriorated 0)
           (<= percent-deteriorated 100))
      (throw (IllegalStateException. "That's not mathy!"))))

;; attach validator during atom creation
(def bobby
  (atom
   {:cuddle-hunger-level 0 :percent-deteriorated 0}
   :validator percent-deteriorated-validator'))

;; ref: sock transfer ;;

(def sock-varieties
  #{"darned" "argyle" "wool" "horsehair" "mulleted"
    "passive-aggresive" "striped" "polkadot" "athletic"
    "business" "power" "invisible" "gollumed"})

(defn sock-count
  [sock-variety count]
  {:variety sock-variety
   :count count})

(defn generate-sock-gnome
  "Create an initial sock gnome state with no socks"
  [name]
  {:name name
   :socks #{}})

;; create a ref for gnome and dryer
(def sock-gnome (ref (generate-sock-gnome "Gnome51")))
(def dryer (ref {:name "GoodDryer X123"
                 :socks (set (map #(sock-count % 2) sock-varieties))}))

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

(defn similar-socks
  [target-sock sock-set]
  (filter #(= (:variety %) (:variety target-sock)) sock-set))


(defn show-transaction
  "This function demonstrates that in-transaction state cant be accessed by outsider.
  It will print 1, 0, 2."
  []
  (let [counter (ref 0)]
    (future
      (dosync
       (alter counter inc)
       (println @counter)
       (Thread/sleep 500)
       (alter counter inc)
       (println @counter)))
    (Thread/sleep 250)
    (println @counter)))

;; commute
(defn sleep-print-update
  [sleep-time thread-name update-fn]
  (fn [state]
    (Thread/sleep sleep-time)
    (println (str thread-name ": " state))
    (update-fn state)))

(defn show-safe-commute
  "This function shows how safe commute work"
  []
  (let [counter (ref 0)]
    ;; A run first, read (0) and then update counter (1). After that it sleeps
    (future
      (dosync
       (commute counter (sleep-print-update 100 "Thread A" inc))))

    ;; B run second, read (1) and then update counter.
    (future
      (dosync
       (commute counter (sleep-print-update 150 "Thread B" inc))))))

(defn show-unsafe-commute
  "This function shows how unsafe commute work. It will show that reveiver-a and receiver-b receives 1 from giver (it should be either of them, not both)  [ONLY WORKS WHEN WE INPUT THE COMMANDS ONE BY ONE]"
  []
  (let [receiver-a (ref #{})
        receiver-b (ref #{})
        giver (ref #{1})]
    (do
      (future
        (dosync (let [gift (first @giver)]
                  (Thread/sleep 10)
                  (commute receiver-a conj gift)
                  (commute giver disj gift))))
      (future
        (dosync (let [gift (first @giver)]
                  (Thread/sleep 50)
                  (commute receiver-b conj gift)
                  (commute giver disj gift)))))

      (println (str "Receiver-a:" @receiver-a))
      (println (str "Receiver-b:" @receiver-b))
      (println (str "Giver: " @giver))))

;; var ;;
(def ^:dynamic *troll-thought* nil)
(defn troll-riddle
  [your-answer]
  (let [number "man meat"]
    (when (thread-bound? #'*troll-thought*)
      (set! *troll-thought* number))
    (if (= number your-answer)
      "TROLL: You may cross the bridge"
      "TROLL: Time to eat you")))

;; parallelism with map
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
(def orc-name-abbrevs (random-string-list 20000 300))

;; `pmap` with partition
(defn ppmap
  "Partitioned pmap, for grouping map ops together to make parallel overhead worthwhile"
  [grain-size f & colls]
  (apply concat
         (apply pmap
                (fn [& pgroups] (doall (apply map f pgroups)))
                (map (partial partition-all grain-size) colls))))

;; run with
;; (time (dorun (ppmap 1000 clojure.string/lower-case orc-name-abbrevs)))
