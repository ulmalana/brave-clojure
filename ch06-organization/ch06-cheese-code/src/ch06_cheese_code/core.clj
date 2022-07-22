(ns ch06-cheese-code.core
  (:require [clojure.java.browse :as browse]
            [ch06-cheese-code.visualization.svg :refer [xml]])
  (:gen-class))

;; ensure that the SVG code is evaluated (by using require)
;;(require 'ch06-cheese-code.visualization.svg)

;; refer to the namespace so that we dont need to use
;; the fully qualified name when referring to svg
;;(refer 'ch06-cheese-code.visualization.svg)

(def heists [{:location "Cologne, DE"
              :cheese-name "Cheese Pretzel"
              :lat 50.95
              :lng 6.97}
             {:location "Zurich, CH"
              :cheese-name "Standard Emmental"
              :lat 47.37
              :lng 8.55}
             {:location "Marseille, FR"
              :cheese-name "Frogame de Cosquer"
              :lat 43.30
              :lng 5.37}
             {:location "Zurich, CH"
              :cheese-name "Lesser Emental"
              :lat 47.37
              :lng 8.55}
             {:location "Vatican"
              :cheese-name "Cheese of Turin"
              :lat 41.90
              :lng 12.45}])

(defn url
  [filename]
  (str "file:///"
       (System/getProperty "user.dir")
       "/"
       filename))

(defn template
  [contents]
  (str "<style>polyline { fill:none; stroke:#5881d8; stroke-width:3}</style>"
       contents))

(defn -main
  [& args]
  (let [filename "map.html"]
    (->> heists
         (xml 50 100)
         template
         (spit filename))
    (browse/browse-url (url filename))))
