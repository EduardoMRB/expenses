(ns expenses.core
  (:require [clojure.java.io :as io]
            [expenses.parser :as parser]
            [clojure.core.async :as async :refer [>! <! go chan go-loop]]
            [expenses.db :as db]))

(defn- file? [file]
  (.isFile file))

(defn- fseq-for [dir]
  (->> dir
       (io/file)
       (file-seq)
       (filter file?)))

(defn import-records [dir]
  (let [fs-chan (chan)
        rec-chan (chan)
        fseq (fseq-for dir)]
    (go
      (doseq [fs fseq]
        (>! fs-chan fs)))
    (go-loop []
      (when-let [file (<! fs-chan)]
        (>! rec-chan (parser/transform-file file))
        (recur)))
    (go-loop []
      (when-let [records (<! rec-chan)]
        (db/import-financial-records! records)
        (recur)))))
