(ns expenses.service
  (:require [io.pedestal.http :as bootstrap]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.ring-middlewares :as middleware]
            [io.pedestal.http.route.definition :refer [defroutes]]
            [io.pedestal.interceptor :as interceptor]
            [ring.util.response :as ring-resp]
            [clojure.java.io :as io]
            [expenses.db :as db]
            [expenses.parser :as parser]))

(defn home-page [request]
  (-> (ring-resp/response (slurp "resources/public/index.html"))
      (ring-resp/content-type "text/html")))

(defn list-financial-records [_]
  (ring-resp/response (db/all-financial-records)))

(defn list-financial-record [request]
  (let [id (get-in request [:path-params :id])]
    (ring-resp/response (db/financial-record-by-id (Long/parseLong id)))))

(defn import-file [request]
  (let [file (get-in request [:multipart-params "file" :tempfile])]
    (-> file
        parser/transform-file
        db/import-financial-records!))
  (ring-resp/redirect "/financial-record"))

(interceptor/defon-request long-content-length 
  [request]
  (if-let [c-len (get-in request [:headers "content-length"])]
    (assoc-in request [:headers "content-length"] (long c-len))))

(defroutes routes
  [[["/" {:get [:home-page home-page]}
     ^:interceptors [(body-params/body-params)
                     long-content-length
                     (middleware/multipart-params)
                     (middleware/file-info)
                     bootstrap/json-body]
     ["/financial-record" {:get [:financial-record#list list-financial-records]
                           :post [:financial-record#import import-file]} 
      ["/:id" {:get [:financial-record#show list-financial-record]}]]]]])

;; See bootstrap/default-interceptors for additional options you can configure
(def service {:env :prod
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; ::bootstrap/interceptors []

              ::bootstrap/routes routes

              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ;; "http://localhost:8080"
              ;;
              ;;::bootstrap/allowed-origins ["scheme://host:port"]

              ;; Root for resource interceptor that is available by default.
              ::bootstrap/resource-path "public"

              ::bootstrap/type :jetty
              ;;::bootstrap/host "localhost"
              ::bootstrap/port 8080})
