(ns expenses.db-test
  (:require [midje.sweet :refer :all]
            [expenses.db :refer :all]
            [datomic.api :as d]
            [expenses.test-helper :refer :all]))

(defn- contains-financial-records
  ([]
   (has every? (contains {:date (partial instance? java.util.Date)
                          :description string?
                          :id integer?
                          :value (partial instance? java.math.BigDecimal)})))
  ([record]
   (let [compare-map {:date (partial instance? java.util.Date)
                      :description string?
                      :id integer?
                      :value (partial instance? java.math.BigDecimal)}]
     (has every? (contains (assoc (merge compare-map record) :id integer?))))))

(defn- contains-financial-record [record]
  (contains (assoc record :id integer?)))

(fact "we can retrieve a FinancialRecord from the database"
  (with-local-conn
    (add-financial-record test-record)
    (all-financial-records)) 
  => (contains-financial-records test-record))

(fact "we can update an existing FinancialRecord"
  (let [new-record (assoc test-record :description "New Description")]
    (with-local-conn
      (add-financial-record test-record)
      (update-financial-record test-record new-record)
      (all-financial-records))
    => (contains-financial-records new-record)))

(fact "we can retrieve multiple FinancialRecords from the database"
  (with-local-conn
    (add-financial-record test-record)
    (add-financial-record test-record2)
    (all-financial-records)) 
  => (contains-financial-records))

(fact "we can retrieve a FinancialRecord by description"
  (with-local-conn
    (add-financial-record test-record)
    (find-financial-record (d/db conn) "description")
    => (contains-financial-record test-record)))

(fact "we can import various FinancialRecords"
  (with-local-conn
    (import-financial-records! [test-record test-record2])
    (all-financial-records))
  => (contains-financial-records))

;; Going to think about what validation should be
#_(fact "we can't import the same FinancialRecord twice"
  (with-local-conn
    (import-financial-records! [test-record test-record])
    (all-financial-records))
  => (contains-financial-records test-record))

(fact "we can retrieve a financial record by id"
  (with-local-conn
    (add-financial-record test-record)
    (let [eid (#'expenses.db/find-financial-record-id (d/db conn) test-record)]
      (financial-record-by-id eid)))
  => (contains-financial-record test-record))
