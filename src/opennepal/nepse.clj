(ns opennepal.nepse
  (:import (java.net URL))
  (:require [net.cgrand.enlive-html :as html]
            [opennepal.core :refer :all]))

(defn fetch-url [url]
  (html/html-resource (URL. url)))

;; nepse data
;(def data (fetch-url "http://nepalstock.com.np/todaysprice/export"))

;; headers
;(mapcat :content (html/select data [:tr :th]))

(defn fetch-data [url]
  (html/select (fetch-url url) [:tr :td]))

(defn into-double [s]
  (Double. s))

(defn nepse-today
  [url]
  (let [raw-data (into [] (mapcat :content (fetch-data url)))]
    (letfn [(extract [acc data]
                     (if (empty? data)
                       acc
                       (let [[company txn max min closing shares amt prev-closing diff & more] data]
                         (recur (conj acc {:company       company
                                           :txns     (into-double txn)
                                           :max           (into-double max)
                                           :min           (into-double min)
                                           :closing       (into-double closing)
                                           :traded-shares (into-double shares)
                                           :amount        (into-double amt)
                                           :prev-closing  (into-double prev-closing)
                                           :diff          (into-double diff)}) more))))]
      (extract [] raw-data))))

(-> (nepse-today "http://nepalstock.com.np/todaysprice/export")
    (save-as-json "./resources/nepse.json"))
