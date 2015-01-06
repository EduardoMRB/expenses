(ns expenses.financial-record-test
  (:require [clojure.test :refer :all]
            [expenses.financial-record :refer :all]
            [clj-time.core :as t :refer [date-time]]))

(def string-date-record 
  (map->FinancialRecord {:date "10/11/2014"
                         :origin "Origem"
                         :description "Desc"
                         :balance-date "11/11/2014"
                         :doc-number 213231
                         :value 11.23}))

(def date-time-record 
  (map->FinancialRecord {:date (date-time 2014 11 11)
                         :origin "Origem"
                         :description "Desc"
                         :balance-date "11/11/2014"
                         :doc-number 213231
                         :value 11.23}))
(def test-records 
  [(->FinancialRecord "05/30/2014" "" "Saldo Anterior" "" "0" "111.87")
   (->FinancialRecord "06/02/2014" "2960-2" "Compra com Cartão - 02/06 21:50    KILO BYTE" "" "178653" "-4.20")
   (->FinancialRecord "06/02/2014" "2960-2" "Compra com Cartão - 31/05 23:11 RIA ROSARIO" "" "183473" "-15.75")])

(deftest financial-record-date
  (testing "date function get date from string"
    (is (= (date string-date-record) (date-time 2014 11 10))))
  (testing "if it's a DateTime object, just return it"
    (is (= (date date-time-record) (date-time 2014 11 11)))))

(deftest sum-of-income-entries
  (testing "it sums the incomes value correctly"
    (is (zero? (income test-records)))))

(deftest sum-all-expenses
  (testing "sum of expenses"
    (is (= (debt test-records) 19.95))))
