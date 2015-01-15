(ns expenses.db
  (:require [expenses.financial-record :refer :all]
            [datomic.api :as d]))

;; TODO: Setup local database and find a way to start the connection by config.
(def uri "datomic:dev://localhost:4334/expenses")
(def conn (d/connect uri))
(def rules
  '[[(financial-records ?posted-at ?description ?value ?f)
     [?f :financial-record/posted-at ?posted-at]
     [?f :financial-record/description ?description]
     [?f :financial-record/value ?value]]])

(defn- qe 
  "Takes a datomic query,db and arbitrary number of arguments and execute query"
  [query db & args]
  (apply (partial d/q query db rules) args))

(defn- find-financial-record-id [db {:keys [date description value]}]
  (ffirst (qe '[:find ?f
                :in $ % ?posted-at ?description ?value
                :where
                (financial-records ?posted-at ?description ?value ?f)]
              db date description value)))

(defn add-financial-record [record]
  @(d/transact conn [{:db/id (d/tempid :db.part/user)
                      :financial-record/description (:description record)
                      :financial-record/posted-at (:date record)
                      :financial-record/value (:value record)}]))

(defn update-financial-record [old-record {:keys [date description value]}]
  (let [db (d/db conn)
        eid (find-financial-record-id db old-record)]
    @(d/transact conn [{:db/id eid
                        :financial-record/description description
                        :financial-record/posted-at date
                        :financial-record/value value}])))

(defn- find-all-financial-records [db]
  (qe '[:find ?posted-at ?description ?value 
        :in $ %
        :where 
        (financial-records ?posted-at ?description ?value ?f)]
      db))

(defn all-financial-records []
  (map vec->FinancialRecord (find-all-financial-records (d/db conn))))

(defn find-financial-record [db description]
  (first (map vec->FinancialRecord 
              (qe '[:find ?posted-at ?description ?value
                    :in $ % ?description
                    :where
                    (financial-records ?posted-at ?description ?value ?f)]
                  db
                  description))))

(defn- find-financial-record-by-id [db id]
  (first (qe '[:find ?posted-at ?description ?value
                :in $ % ?f
                :where
                (financial-records ?posted-at ?description ?value ?f)]
              db
              id)))

(defn financial-record-by-id [id]
  (vec->FinancialRecord (find-financial-record-by-id (d/db conn) id)))

(defn import-financial-records! 
  "Inserts a collection of FinancialRecord into the database"
  [records]
  (doseq [record records]
    (add-financial-record record)))
