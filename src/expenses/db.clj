(ns expenses.db
  (:require [expenses.financial-record :refer :all]
            [datomic.api :as d]))

;; TODO: Setup local database and find a way to start the connection by config.
(def conn nil)
(def rules
  '[[(financial-records ?posted-at ?description ?value)
     [?f :financial-record/posted-at ?posted-at]
     [?f :financial-record/description ?description]
     [?f :financial-record/value ?value]]])

(defn- qe 
  "Takes a datomic query and arbitrary number of arguments and execute query"
  [query & args]
  (apply (partial d/q query (d/db conn) rules) args))

(defn add-financial-record [record]
  @(d/transact conn [{:db/id (d/tempid :db.part/user)
                      :financial-record/description (:description record)
                      :financial-record/posted-at (:date record)
                      :financial-record/value (:value record)}]))

(defn find-all-financial-records []
  (map vec->FinancialRecord 
       (qe '[:find ?posted-at ?description ?value 
             :in $ %
             :where 
             (financial-records ?posted-at ?description ?value)])))

(defn find-financial-record [description]
  (first (map vec->FinancialRecord 
              (qe '[:find ?posted-at ?description ?value
                    :in $ % ?description
                    :where
                    (financial-records ?posted-at ?description ?value)]
                  description))))

(defn import-financial-records! 
  "Inserts a collection of FinancialRecord into the database"
  [records]
  (doseq [record records]
    (add-financial-record record)))
