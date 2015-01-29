(ns expenses.ajax
  (:require [cljs.core.async :as async :refer [put! chan]]
            [ajax.core :as ajax]))

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
