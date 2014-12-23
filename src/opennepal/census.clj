(ns opennepal.census
  (:require
    [opennepal.core :refer :all]))

(def url "http://data.opennepal.net/sites/all/modules/pubdlcnt/pubdlcnt.php?file=http://data.opennepal.net/sites/default/files/resources/Population.csv&nid=151")

;; "./resources/census.csv"
(def csv-data (read-csv url))

;; finds digits in a string anc converts it into number
(defn parse-int [s]
  (let [d (re-find #"\d+" s)]
    (if (nil? d)
      0
      (Integer. d))))

;; creates a map to represent a district census details
(defn district-map
  [yr dist m-cnt f-cnt h-cnt]
  {:year      (parse-int yr)
   :district  dist
   :male      (parse-int m-cnt)
   :female    (parse-int f-cnt)
   :household (parse-int h-cnt)})

;; creates a vector of district census maps
(defn census-map
  ([xs] (census-map [] xs))
  ([acc-s xs]
    (if (empty? xs)
      acc-s
      (let [[m-row f-row h-row & more] xs

            [yr dist _ m-cnt] m-row
            [_ _ _ f-cnt] f-row
            [_ _ _ h-cnt] h-row

            dist-map (district-map yr dist m-cnt f-cnt h-cnt)]
        (recur (conj acc-s dist-map) more)))))

(def census-data (census-map (into [] (rest csv-data))))

(json-to-file census-data "./resources/census.json")
