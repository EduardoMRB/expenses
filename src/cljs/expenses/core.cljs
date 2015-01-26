(ns expenses.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :as async :refer [>! <! put! chan timeout]]
            [domina :as dom]
            [domina.events :as ev]))

(defn events [el type]
  (let [events-chan (chan)]
    (ev/listen! el type (fn [e] (put! events-chan e)))
    events-chan))

(defn print-mouse-location [e]
  (let [position-x (dom/by-id "position-x")
        position-y (dom/by-id "position-y")]
    (dom/set-text! position-x (:offsetX e))
    (dom/set-text! position-y (:offsetY e))))

(let [events (events (dom/by-id "canvas") :mousemove)]
  (go (while true
        (print-mouse-location (<! events))))
  events)
