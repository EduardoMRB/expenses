(ns expenses.card
  (:require [expenses.financial-record :refer :all]
            [ofx-clj.core :as ofx])
  (:import [net.sf.ofx4j.domain.data.creditcard CreditCardResponseMessageSet
                                                CreditCardStatementResponseTransaction
                                                CreditCardStatementResponse]))

(def the-file "resources/card/faturaCartao.ofx")

(defmethod ofx/parse-data CreditCardResponseMessageSet
  [message-set]
  (ofx/obj-to-map message-set
                  :type [.getType str]
                  :version .getVersion
                  :mesages [.getResponseMessages #(map ofx/parse-data %)]))

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

(ofx/parse the-file)
