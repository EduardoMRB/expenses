(ns expenses.financial-record-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [expenses.financial-record :refer :all]
            [clj-time.core :as t :refer [date-time]]))

(def test-records 
  [(->FinancialRecord (date-time 2014 11 11) "Saldo Anterior" 111.87M 27274)
   (->FinancialRecord (date-time 2014 5 5) "Compra com Cartão - 02/06 21:50    KILO BYTE" -4.20M 28287)
   (->FinancialRecord (date-time 2014 5 8) "Compra com Cartão - 31/05 23:11 RIA ROSARIO" -15.75M 289578)])

(fact "`income` sums all incomes correctly"
  (income test-records) => 0)

(fact "`debt sums all debts correctly`"
  (debt test-records) => -19.95M)

(fact "FinancialRecord->map"
  (FinancialRecord->map (first test-records)) => {:date (date-time 2014 11 11)
                                                 :description "Saldo Anterior"
                                                 :value 111.87M
                                                 :id 27274})
