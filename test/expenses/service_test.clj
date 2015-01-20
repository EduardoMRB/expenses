(ns expenses.service-test
  (:require [midje.sweet :refer :all]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]
            [expenses.service :as service]
            [expenses.test-helper :refer :all]
            [cheshire.core :as json]
            [expenses.db :as db]))

(def service
  (::bootstrap/service-fn (bootstrap/create-servlet service/service)))

(fact "There is no home page"
  (:status (response-for service :get "/")) => 404)

(facts "financial records"
  (:status (response-for service :get "/financial-record")) => 200
  (get-in (response-for service :get "/financial-record")
          [:headers "Content-Type"]) => "application/json;charset=UTF-8"

  (fact "imported financial records are displayed in json"
    (with-local-conn
      (db/add-financial-record test-record)
      (db/add-financial-record test-record2)
      (:body (response-for service :get "/financial-record")))
    => (contains (str (json/generate-string (dissoc test-record2 :id))
                      (json/generate-string (dissoc test-record :id)))
                 :gaps-ok))

  (fact "we can see the representation of a specific record"
    (with-local-conn
      (db/add-financial-record test-record)
      (let [eid (#'expenses.db/find-financial-record-id
                  (datomic.api/db db/conn) test-record)
            url (str "/financial-record/" eid)]
        (:body (response-for service :get url))))
    => (contains (json/generate-string (dissoc test-record :id))
                 :gaps-ok)))
