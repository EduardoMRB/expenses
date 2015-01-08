(ns expenses.parser
  (:require [expenses.financial-record :refer :all]
            [ofx-clj.core :as ofx]
            [clj-time.coerce :as c])
  (:import [net.sf.ofx4j.domain.data.creditcard CreditCardResponseMessageSet 
                                                CreditCardStatementResponseTransaction
                                                CreditCardStatementResponse]))

(defmethod ofx/parse-data CreditCardResponseMessageSet
  [message-set]
  (ofx/obj-to-map message-set
                  :type [.getType str]
                  :version .getVersion
                  :messages [.getResponseMessages #(map ofx/parse-data %)]))

(defmethod ofx/parse-data CreditCardStatementResponseTransaction
  [transaction]
  (ofx/obj-to-map transaction
                  :message [.getMessage ofx/parse-data]
                  :wrapped-message [.getWrappedMessage ofx/parse-data]))

(defmethod ofx/parse-data CreditCardStatementResponse
  [response]
  (ofx/obj-to-map response
                  :account [.getAccount ofx/parse-data]
                  :available-balance [.getAvailableBalance ofx/parse-data]
                  :currency-code .getCurrencyCode
                  :ledger-balance [.getLedgerBalance ofx/parse-data]
                  :marketing-info .getMarketingInfo
                  :response-message-name .getResponseMessageName
                  :transaction-list [.getTransactionList ofx/parse-data]))

(defn transaction->FinancialRecord [transaction]
  (let [trans-map {:date (c/from-long (:date-posted transaction))
                   :origin nil
                   :description (:memo transaction)
                   :balance-date nil
                   :doc-number nil
                   :value (:amount transaction)}]
    (map->FinancialRecord trans-map)))

(defn transactions->FinancialRecord [transactions]
  (map transaction->FinancialRecord transactions))

(defn- banking-or-credit-card? [response]
  (contains? #{"creditcard" "banking"} (:type response)))

(defn- find-response [message]
  (get-in message [:message 
                   :transaction-list 
                   :transactions]))

(defn- find-transactions [response]
  (let [messages (mapcat :messages response)]
    (mapcat find-response messages)))

(defn transform-file [file]
  (let [parsed-file (ofx/parse file)
        transactions (->> parsed-file
                          (filter banking-or-credit-card?)
                          (find-transactions))]
    (transactions->FinancialRecord transactions)))

(defn transform-files 
  "Takes a file-sequence, reads it's ofx files 
  and return a lazy sequence of FinancialRecord"
  [fseq]
  (let [ofx-files (filter #(re-seq #"\.ofx$" (str %)) fseq)]
    (mapcat transform-file ofx-files)))
