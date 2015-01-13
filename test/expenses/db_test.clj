(ns expenses.db-test
  (:require [midje.sweet :refer :all]
            [expenses.db :refer :all]
            [datomic.api :as d]
            [expenses.financial-record :refer :all]
            [clj-time.core :as t :refer [date-time]]))

(defn create-empty-in-memory-db []
  (let [uri "datomic:mem://expenses-test-db"]
    (d/delete-database uri)
    (d/create-database uri)
    (let [conn (d/connect uri)
          schema (load-file "resources/datomic/schema.edn")]
      (d/transact conn schema)
      conn)))

(def test-record
  (->FinancialRecord (.toDate (date-time 2015 1 1)) "description" 11.20M))
(def test-record2
  (->FinancialRecord (.toDate (date-time 2014 11 11)) 
                     "another description" 
                     -26.53M))

(fact "we can retrieve a FinancialRecord from the database"
  (with-redefs [conn (create-empty-in-memory-db)] 
    (do
      (add-financial-record test-record)
      (find-all-financial-records))) 
  => [test-record])

(fact "we can retrieve multiple FinancialRecords from the database"
  (with-redefs [conn (create-empty-in-memory-db)]
    (do
      (add-financial-record test-record)
      (add-financial-record test-record2)
      (find-all-financial-records))) 
   => (concat [test-record] [test-record2]))

(fact "we can retrieve a FinancialRecord by description"
  (with-redefs [conn (create-empty-in-memory-db)]
    (do
      (add-financial-record test-record)
      (find-financial-record "description"))
    => test-record))
