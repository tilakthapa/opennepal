(ns opennepal.core
  (:require
    [clojure.data.csv :as csv]
    [clojure.java.io :as io]
    [cheshire.core :as json]))

(defn read-csv
  [dest]
  (with-open [in-file (io/reader dest)]
    (doall
      (csv/read-csv in-file))))

(defn json-to-file
  [data dest]
  (json/generate-stream data (io/writer dest)))