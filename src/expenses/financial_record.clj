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

(defn income [records]
  (let [income? (fn [record]
                  (let [excludes-descriptions #{"S A L D O" "Saldo Anterior"}]
                    (and 
                      (not (contains? excludes-descriptions
                                      (:description record)))
                      (pos? (read-string (:value record))))))
        without-balance (filter income? records)
        values (map #(read-string (:value %)) without-balance)]
    (reduce + values)))

(defn debt [expenses]
  (let [expense-values? (fn [expense]
                          (neg? (read-string (:value expense))))
        without-incomes (filter expense-values? expenses)
        values (map #(* -1 (read-string (:value %))) without-incomes)]
    (reduce + values)))
