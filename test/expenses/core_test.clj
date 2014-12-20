(ns expenses.core-test
  (:require [clojure.test :refer :all]
            [expenses.core :refer :all]))

(def test-file "test/fixtures/test_extract.csv")
(def fixtures-folder "test/fixtures")
(def fixtures-fs (rest (file-seq (clojure.java.io/file fixtures-folder))))
(def test-expenses 
  [(->FinancialRecord "05/30/2014" "" "Saldo Anterior" "" "0" "111.87")
   (->FinancialRecord "06/02/2014" "2960-2" "Compra com Cartão - 02/06 21:50    KILO BYTE" "" "178653" "-4.20")
   (->FinancialRecord "06/02/2014" "2960-2" "Compra com Cartão - 31/05 23:11 RIA ROSARIO" "" "183473" "-15.75")])

(deftest reader-can-read-extracts
  (testing "read extract"
    (is (> (count (read-extract "resources/account/extrato.csv")) 1))))

(deftest convert-vector-to-record
  (testing "vector read from file is converted to FinancialRecord"
    (is (= (transform-file test-file) test-expenses))))

(deftest sum-of-income-entries
  (testing "it sums the incomes value correctly"
    (is (zero? (income test-expenses)))))

(deftest sum-all-expenses
  (testing "sum of expenses"
    (is (= (debt test-expenses) 19.95))))

(deftest whole-directory-can-be-read
  (testing "it takes all files of a directory, reads them, transforms then and spits incomes"
    (is (zero? (income (transform-files fixtures-fs)))))
  (testing "it takes all files and spits the sum of debts"
    (is (= (debt (transform-files fixtures-fs)) 19.95))))

(run-tests)
