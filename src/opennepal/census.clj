(ns opennepal.census
  (:require
    [opennepal.core :refer :all]))

;; National population and household census(1971-2011)
;; http://data.opennepal.net/content/national-population-and-household-census

;; "./resources/pop_n_household_census_1971_2011.csv"

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

(defn read-census [csv-file]
  (let [raw-data (into [] (rest (read-csv csv-file)))]
    (letfn [(extract [acc data]
                     (if (empty? data)
                       acc
                       (let [[m f h & more] data
                             [yr dt _ m-count] m
                             [_ _ _ f-count] f
                             [_ _ _ h-count] h
                             dt-map (district-map yr dt m-count f-count h-count)]
                         (recur (conj acc dt-map) more))))]
      (extract [] raw-data))))

;(read-census "./resources/pop_n_household_census_1971_2011.csv")

(save-as-json (read-census "./resources/pop_n_household_census_1971_2011.csv") "./resources/pop_n_household_census_1971_2011.json")
