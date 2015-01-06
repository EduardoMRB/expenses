(ns expenses.account
  (:require [clojure.java.io :as io]
            [clojure-csv.core :as csv]
            [expenses.financial-record :refer :all]))

(defn read-extract [file]
  (with-open [reader (io/reader file)]
    (doall
      (rest (map pop (csv/parse-csv reader))))))

(defn transform-file [file]
  (let [expenses (read-extract file)]
    (map vec->FinancialRecord expenses)))

(defn transform-files [files]
  (mapcat transform-file files))
