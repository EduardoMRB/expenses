(ns expenses.connect
  (:require [figwheel.client :as fw]))

(enable-console-print!)

(fw/start {:on-jsload (print "reloaded!")})
