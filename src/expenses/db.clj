(ns expenses.db
  (:require [expenses.financial-record :refer :all]
            [datomic.api :as d]))

(def conn nil)

(defn add-financial-record [record]
  @(d/transact conn [{:db/id (d/tempid :db.part/user)
                      :financial-record/description (:description record)
                      :financial-record/posted-at (:date record)
                      :financial-record/value (:value record)}]))

(defn find-all-financial-records []
  (map vec->FinancialRecord 
       (d/q '[:find ?posted-at ?description ?value 
              :where 
              [?f :financial-record/posted-at ?posted-at]
              [?f :financial-record/description ?description]
              [?f :financial-record/value ?value]]
            (d/db conn))))
