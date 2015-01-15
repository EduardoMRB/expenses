(ns expenses.service-test
  (:require [midje.sweet :refer :all]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]
            [expenses.service :as service]
            [expenses.db-test :as db-test]
            [expenses.db :as db]
            [cheshire.core :as json]))

(def service
  (::bootstrap/service-fn (bootstrap/create-servlet service/service)))

(fact "There is no home page"
  (:status (response-for service :get "/")) => 404)

(facts "financial records"
  (:status (response-for service :get "/financial-record")) => 200
  (get-in (response-for service :get "/financial-record")
          [:headers "Content-Type"]) => "application/json;charset=UTF-8"
  (fact "imported financial records are displayed in json"
    (with-redefs [db/conn (db-test/create-empty-in-memory-db)]
      (do
        (db/add-financial-record db-test/test-record)
        (db/add-financial-record db-test/test-record2)
        (:body (response-for service :get "/financial-record"))))
    => (json/generate-string [db-test/test-record db-test/test-record2]))
  (fact "we can see the representation of a specific record"
    (with-redefs [db/conn (db-test/create-empty-in-memory-db)]
      (do
        (db/add-financial-record db-test/test-record)
        (let [eid (#'expenses.db/find-financial-record-id
                    (datomic.api/db db/conn) db-test/test-record)
              url (str "/financial-record/" eid)]
          (:body (response-for service :get url)))))
    => (json/generate-string db-test/test-record)))
