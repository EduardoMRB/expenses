(ns expenses.parser-test
  (:require [clojure.test :refer :all]
            [expenses.parser :refer :all]
            [expenses.financial-record :refer :all]
            [clj-time.core :as t :refer [date-time]]
            [clojure.java.io :as io]))

(def test-file "test/fixtures/test_extract.ofx")
(def test-dir "test/fixtures")
(def test-fs (->> test-dir io/file file-seq))
(def test-records 
  [(->FinancialRecord (date-time 2013 12 04) nil "Reference" nil nil 598.36M)
   (->FinancialRecord (date-time 2013 12 07) nil "BLIZZARD ENT*WOW SUB   800-592-5499" nil nil -12.9M)])

(deftest can-parse-files
  (testing "takes a file and transforms it into a seq of FinancialRecords"
    (is (= (transform-file test-file) test-records)))
  (testing "do the same on file-seqs"
    (is (every? #(record? %) (transform-files test-fs)))))