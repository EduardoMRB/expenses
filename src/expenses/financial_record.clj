(ns expenses.financial-record)

(defrecord FinancialRecord 
  [date origin description balance-date doc-number value])

(defn vec->FinancialRecord [expense]
  (apply ->FinancialRecord expense))
