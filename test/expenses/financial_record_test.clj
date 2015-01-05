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

(deftest financial-record-date
  (testing "date function get date from string"
    (is (= (date string-date-record) (date-time 2014 11 10))))
  (testing "if it's a DateTime object, just return it"
    (is (= (date date-time-record) (date-time 2014 11 11)))))
