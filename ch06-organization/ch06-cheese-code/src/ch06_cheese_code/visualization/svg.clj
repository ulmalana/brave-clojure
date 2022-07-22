(ns ch06-cheese-code.visualization.svg
  (:require [clojure.string :as s])
  (:refer-clojure :exclude [min max]))

(defn latlng->point
  "Convert lat/lng map to comma-separated string"
  [latlng]
  (str (:lat latlng) "," (:lng latlng)))

(defn comparator-over-maps
  [comparison-fn ks]
  (fn [maps]
    (zipmap ks
            (map (fn [k] (apply comparison-fn (map k maps)))
                 ks))))

(def min (comparator-over-maps clojure.core/min [:lat :lng]))
(def max (comparator-over-maps clojure.core/max [:lat :lng]))

(defn points
  [locations]
  (s/join " " (map latlng->point locations)))

(defn translate-to-00
  [locations]
  (let [mincoords (min locations)]
    (map #(merge-with - % mincoords) locations)))

(defn scale
  [width height locations]
  (let [maxcoords (max locations)
        ratio {:lat (/ height (:lat maxcoords))
               :lng (/ width (:lng maxcoords))}]
    (map #(merge-with * % ratio) locations)))

(defn line
  [points]
  (str "<polyline points=\"" points "\" />"))

(defn transform
  "Just chains of other functions"
  [width height locations]
  (->> locations
       translate-to-00
       (scale width height)))

(defn xml
  "svg template, which also flips the coordinate systems"
  [width height locations]
  (str "<svg height=\"" height "\" width=\"" width "\">"
       ;; these 2 <g> tags change the coordinate system
       ;; so that 0,0 is in the lower-left cornder, instead of
       ;; SVG default upper-left corner
       "<g transform=\"translate(0," height ")\">"
       "<g transform=\"rotate(-90)\">"
       (-> (transform width height locations)
           points
           line)
       "</g></g>"
       "</svg>"))
