(ns expenses.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :as async :refer [>! <! chan]]
            [domina :as dom]
            [domina.events :as ev]
            [expenses.ajax :as ajax]
            [expenses.template :as template])
  (:import [goog.i18n DateTimeFormat TimeZone NumberFormat]))

(enable-console-print!)

(defn ^:export init []
  (let [c (ajax/get-financial-records)]
    (go 
      (while true
        (let [records (<! c)
              container (dom/by-id "container")]
          (dom/append! container (template/records-table records)))))
    c))
