(ns expenses.template
  (:require-macros [hiccups.core :refer [html]])
  (:require [hiccups.runtime])
  (:import [goog.i18n DateTimeFormat TimeZone NumberFormat]))

(def currency
  (.-CURRENCY (.-Format (.-NumberFormat (.-i18n (.-goog js/window))))))

(defn parse-value [value]
  (let [f (NumberFormat. currency)]
    (html [:span (.format (NumberFormat. currency) value)])))

(defn value-td [record]
  (let [v (parse-value (:value record))]
    (if (pos? (:value record))
      (html [:td.info v])
      (html [:td.danger v]))))

(defn parse-date [date]
  (let [f (DateTimeFormat. "EEEE, d 'de' MMMM 'de' y")]
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
