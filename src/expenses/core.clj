(ns expenses.core
  (:require [clojure.java.io :as io]
            [expenses.parser :refer :all]
            [expenses.financial-record :refer :all]))

(defn file? [file]
  (.isFile file))

(defn -main [dir & _]
  (let [fs (->> dir 
                (io/file) 
                (file-seq)
                (filter file?))
        transformed (transform-files fs)]
    (println (income transformed))
    (println (debt transformed))))
