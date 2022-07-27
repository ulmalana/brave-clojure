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
