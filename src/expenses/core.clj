(ns expenses.core
  (:require [clojure-csv.core :as csv]
            [clojure.java.io :as io]))

(defrecord FinancialRecord 
  [date origin description balance-date doc-number value])

(defn read-extract [file]
  (with-open [reader (io/reader file)]
    (doall
      (rest (map pop (csv/parse-csv reader))))))

(defn vec->FinancialRecord [expense]
  (apply ->FinancialRecord expense))

(defn transform-file [file]
  (let [expenses (read-extract file)]
    (map vec->FinancialRecord expenses)))

(defn transform-files [files]
  (mapcat transform-file files))

(defn income [expenses]
  (let [income? (fn [expense]
                  (and 
                    (not (contains? 
                           #{"S A L D O" "Saldo Anterior"}
                           (:description expense)))
                    (pos? (read-string (:value expense)))))
        without-balance (filter income? expenses)
        values (map #(read-string (:value %)) without-balance)]
    (reduce + values)))

(defn debt [expenses]
  (let [expense-values? (fn [expense]
                          (neg? (read-string (:value expense))))
        without-incomes (filter expense-values? expenses)
        values (map #(* -1 (read-string (:value %))) without-incomes)]
    (reduce + values)))

(defn file? [file]
  (.isFile file))

(defn -main [dir & _]
  (let [fs (->> dir io/file file-seq (filter file?))
        transformed (transform-files fs)]
    (println (income transformed))
    (println (debt transformed))))
