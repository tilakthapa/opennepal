(ns opennepal.census
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [cheshire.core :as json]))

(def url "http://data.opennepal.net/sites/all/modules/pubdlcnt/pubdlcnt.php?file=http://data.opennepal.net/sites/default/files/resources/Population.csv&nid=151")
;; "./resources/census.csv"
(def data (with-open [in-file (io/reader url)]
            (doall
              (csv/read-csv in-file))))

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

(def census-data (census-map (into [] (rest data))))

(json/generate-stream census-data (io/writer "./resources/census.json"))

;; death or missing
(def url-link "http://data.opennepal.net/sites/all/modules/pubdlcnt/pubdlcnt.php?file=http://data.opennepal.net/sites/default/files/resources/Missing%20and%20dead%20person_0.csv&nid=4501")

(def insurg-data (with-open [in-file (io/reader "./resources/conflict.csv")]
                   (doall
                     (csv/read-csv in-file))))

;; dd/mm/yyyy
(defn date-map
  [date-str]
  (let [ss (clojure.string/split date-str #"/")
        [d m y] ss]
    (if (= 3 (count ss))
      {:day (parse-int d) :month (parse-int m) :year (parse-int y)}
      {:day nil :month nil :year nil}

      )))

(defn r-map
  [[dead-or-missing
    district
    name
    gender
    date-of-birth
    _
    place-of-birth
    father-name
    date-of-disappear
    _
    place-of-disappear
    district-of-disappear
    :as v]]
  {:dead-or-missing       dead-or-missing
   :district              district
   :name                  name
   :gender                gender
   :date-of-birth         (date-map date-of-birth)
   :place-of-birth        place-of-birth
   :father-name           father-name
   :date-of-disappear     (date-map date-of-disappear)
   :place-of-disappear    {:district district-of-disappear :place place-of-disappear}})

(defn coll-map
  ([sv] (coll-map [] sv))
  ([acc-sv sv]
    (if (empty? sv)
      acc-sv
      (let [[x & more] sv]
        (recur (conj acc-sv (r-map x)) more)))))

(def conflict-data (coll-map (into [] (rest insurg-data))))

(json/generate-stream conflict-data (io/writer "./resources/conflict.json"))







