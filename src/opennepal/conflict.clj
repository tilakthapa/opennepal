(ns opennepal.conflict
  (:require
    [opennepal.core :refer :all]))

;; http://data.opennepal.net/content/number-missing-and-dead-people-time-maoist-revolution
;; Number of missing and dead people at the time of Maoist revolution
;; https://www.icrc.org/eng/assets/files/reports/report-missing-persons-nepal-2013-english.pdf

(def url "http://data.opennepal.net/sites/all/modules/pubdlcnt/pubdlcnt.php?file=http://data.opennepal.net/sites/default/files/resources/Missing%20and%20dead%20person_0.csv&nid=4501")

;; conflict csv data
(def csv-data (read-csv "./resources/conflict.csv"))

(defn parse-int
  "Finds digits in a string and converts it into number."
  [s]
  (let [d (re-find #"\d+" s)]
    (if (nil? d)
      0
      (Integer. d))))

(defn date-map
  "Creates a date map from a string of format 'dd/mm/yyyy'."
  [date-str]
  (let [ss (clojure.string/split date-str #"/")
        [d m y] ss]
    (if (= 3 (count ss))
      {:day (parse-int d) :month (parse-int m) :year (parse-int y)}
      {:day nil :month nil :year nil})))

(defn r-map
  "Creates a map to represent a dead/missing record."
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
  {:dead-or-missing    dead-or-missing
   :district           district
   :name               name
   :gender             gender
   :date-of-birth      (date-map date-of-birth)
   :place-of-birth     place-of-birth
   :father-name        father-name
   :date-of-disappear  (date-map date-of-disappear)
   :place-of-disappear {:district district-of-disappear :place place-of-disappear}})

(defn coll-map
  ([sv] (coll-map [] sv))
  ([acc-sv sv]
    (if (empty? sv)
      acc-sv
      (let [[x & more] sv]
        (recur (conj acc-sv (r-map x)) more)))))

;; final map containing all data
(def conflict-data (coll-map (into [] (rest csv-data))))

(json-to-file conflict-data "./resources/conflict.json")
