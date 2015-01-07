(ns expenses.financial-record
  (:require [clj-time.format :as f])
  (:import org.joda.time.DateTime))

(defrecord FinancialRecord 
  [date origin description balance-date doc-number value])

(defn vec->FinancialRecord [expense]
  (apply ->FinancialRecord expense))

(def br-format (f/formatter "dd/MM/YYYY"))

(defmulti date class)

(defmethod date FinancialRecord
  [{date :date}]
  (if (instance? DateTime date)
    date
    (f/parse br-format date)))

(defn- sum-expenses-by 
  "Takes a predicate fn and a coll of FinancialRecord and return
  the sum of values filtered by the predicate fn"
  [pred records]
  (reduce #(+ %1 (:value %2)) 0 (filter pred records)))

(defn income [records]
  (letfn [(income? [record]
                  (let [excludes-descriptions #{"S A L D O" "Saldo Anterior"}]
                    (and 
                      (not (contains? excludes-descriptions
                                      (:description record)))
                      (pos? (:value record)))))]
    (sum-expenses-by income? records)))

(defn debt [records]
  (letfn [(expense-values? [record]
            (neg? (:value record)))]
    (sum-expenses-by expense-values? records)))
