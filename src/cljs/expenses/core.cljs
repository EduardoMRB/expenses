(ns expenses.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :as async :refer [>! <! put! chan timeout]]
            [domina :as dom]
            [domina.events :as ev]
            [ajax.core :as ajax]
            [expenses.template :as template])
  (:import [goog.i18n DateTimeFormat TimeZone NumberFormat]))

(enable-console-print!)

(def api-uri "http://localhost:8080")

(defn- error-handler [resp]
  (.log js/console (str resp)))

(defn get-financial-records []
  (let [c (chan)]
    (ajax/GET (str api-uri "/financial-record")
              {:handler #(put! c %)
               :error-handler error-handler
               :response-format :edn})
    c))

(defn ^:export init []
  (let [c (get-financial-records)]
    (go (while true
          (let [records (<! c)
                container (dom/by-id "container")]
            (dom/append! container (template/records-table records)))))
    c))
