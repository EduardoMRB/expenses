(ns expenses.financial-record-test
  (:require [clojure.test :refer :all]
            [expenses.financial-record :refer :all]
            [clj-time.core :as t :refer [date-time]]))

(def test-records 
  [(->FinancialRecord "05/30/2014" "" "Saldo Anterior" "" "0" 111.87M)
   (->FinancialRecord "06/02/2014" "2960-2" "Compra com Cartão - 02/06 21:50    KILO BYTE" "" "178653" -4.20M)
   (->FinancialRecord "06/02/2014" "2960-2" "Compra com Cartão - 31/05 23:11 RIA ROSARIO" "" "183473" -15.75M)])

(deftest sum-of-income-entries
  (testing "it sums the incomes value correctly"
    (is (zero? (income test-records)))))

(deftest sum-all-expenses
  (testing "sum of expenses"
    (is (= (debt test-records) -19.95M))))
