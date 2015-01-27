(ns expenses.core
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [hiccups.core :as h :refer [html]])
  (:require [cljs.core.async :as async :refer [>! <! put! chan timeout]]
            [domina :as dom]
            [domina.events :as ev]
            [ajax.core :as ajax]
            [hiccups.runtime])
  (:import [goog.i18n DateTimeFormat TimeZone NumberFormat]))

(enable-console-print!)

(def currency
  (.-CURRENCY (.-Format (.-NumberFormat (.-i18n (.-goog js/window))))))
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

(defn parse-value [value]
  (let [f (NumberFormat. currency)]
    (html [:span (.format (NumberFormat. currency) value)])))

(defn value-td [record]
  (let [v (parse-value (:value record))]
    (if (pos? (:value record))
      (html [:td.info v])
      (html [:td.danger v]))))

(defn parse-date [date]
  (let [f (DateTimeFormat. "EEEE d 'de' MMMM, y")]
    (.format f date)))

(defn record-rows [records]
  (html (for [record records]
          [:tr
           [:td (parse-date (:date record))]
           [:td (:description record)]
           (value-td record)])))

(defn records-table [records]
  (html [:table.table.table-stripped
         [:thead
          [:th "Data"]
          [:th "Descricao"]
          [:th "Valor"]]
         [:tbody
          (record-rows records)]]))

(let [c (get-financial-records)]
  (go (while true
        (let [records (<! c)
              container (dom/by-id "container")]
          (dom/append! container (records-table records)))))
  c)
