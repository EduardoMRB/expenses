(ns user
  (:require [clojure.java.io :as io]
            [midje.repl :refer [autotest]]
            [clojure.repl :refer :all]
            [clojure.string :as s]
            [clojure.pprint :refer [pprint]]
            [cljs.repl.browser :as brepl]
            [cemerick.piggieback :as pb]
            [expenses.server :as server]))

(defn browser-repl []
  (pb/cljs-repl :repl-env
                (brepl/repl-env :port 9000)))

(defn start-server []
  (server/run-dev))

(defn stop-server []
  (server/stop-server))

(defn restart-server []
  (server/stop-server)
  (server/run-dev))
