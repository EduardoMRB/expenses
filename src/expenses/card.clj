(ns expenses.card
  (:require [expenses.financial-record :refer :all]
            [ofx-clj.core :as ofx])
  (:import [net.sf.ofx4j.domain.data.creditcard CreditCardResponseMessageSet]))

(def the-file "resources/card/faturaCartao.ofx")

(defmethod ofx/parse-data CreditCardResponseMessageSet
  [message-set]
  (ofx/obj-to-map message-set
                  :type [.getType str]
                  :version .getVersion
                  :mesages [.getResponseMessages #(map ofx/parse-data %)]))

(ofx/parse the-file)
