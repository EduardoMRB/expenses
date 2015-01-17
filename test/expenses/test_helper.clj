(ns expenses.test-helper
  (:require [expenses.financial-record :refer :all]
            [datomic.api :as d]
            [clj-time.core :as t]
            [expenses.db :as db :refer [conn]]))

(defn create-empty-in-memory-db []
  (let [uri "datomic:mem://expenses-test-db"]
    (d/delete-database uri)
    (d/create-database uri)
    (let [conn (d/connect uri)
          schema (load-file "resources/datomic/schema.edn")]
      (d/transact conn schema)
      conn)))

(def test-record
  (->FinancialRecord (.toDate (t/date-time 2015 1 1)) "description" 11.20M nil))
(def test-record2
  (->FinancialRecord (.toDate (t/date-time 2014 11 11)) 
                     "another description" 
                     -26.53M
                     nil))

(defmacro with-local-conn 
  "Takes an arbitrary number of operations and execute them
  in a context where the Var conn is redefined by the function
  create-empty-in-memory-db"
  [& body]
  `(with-redefs [conn (create-empty-in-memory-db)]
     (do ~@body)))
