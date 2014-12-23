(ns opennepal.core
  (:require [cheshire.core :refer :all]))

(def link "http://data.opennepal.net/sites/all/modules/pubdlcnt/pubdlcnt.php?file=http://data.opennepal.net/sites/default/files/resources/Population.csv&nid=151")



(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(def petr_prods
  (-> "http://data.opennepal.net/sites/default/files/resources/petroleumproducts.json"
     slurp
     (parse-string true)))

(keys (first petr_prods))


(map #(vals (select-keys % [:date :np_petrol_nrs_ltr])) petr_prods)

(map + [1 2 3 4 5])
(select-keys (first petr_prods) [:date :np_petrol_nrs_ltr])

(keys {:date "4-4-1996",
 :np_petrol_nrs_ltr "31",
 :np_diesel_nrs_ltr "13.5",
 :np_lpgas_nrs_cylinder "",
 :np_kerosene_nrs_ltr "9.5",
 :in_petrol_nrs_ltr "",
 :in_diesel_nrs_ltr "",
 :in_kerosene_nrs_ltr ""}
      )

(group-by :np_petrol_nrs_ltr petr_prods)



