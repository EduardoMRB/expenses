(ns expenses.core
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [hiccups.core :refer [html]])
  (:require [cljs.core.async :as async :refer [>! <! chan put!]]
            [domina :as dom]
            [expenses.ajax :as ajax]
            [expenses.template :as template])
  (:import [goog.i18n DateTimeFormat TimeZone NumberFormat]))

(enable-console-print!)

(defn ^:export init []
  (let [c (ajax/get-financial-records)]
    (go 
      (while true
        (let [container (dom/by-id "container")
              records (<! c)]
          (dom/append! container (template/records-table records)))))
    c))
