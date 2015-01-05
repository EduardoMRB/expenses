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
