(defproject expenses "0.1.0-SNAPSHOT"
  :description "Clojure library to calculate incomes and debts based on documents provided by Banco do Brasil"
  :url "http://github.com/EduardoMRB/expenses"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [net.sf.ofx4j/ofx4j "1.6"]
                 [cc.artifice/ofx-clj "0.1"]
                 [clj-time "0.9.0"]

                 [com.datomic/datomic-pro "0.9.5078" :exclusions [joda-time]]

                 [io.pedestal/pedestal.service "0.3.1"]
                 [io.pedestal/pedestal.jetty "0.3.1"]

                 [ch.qos.logback/logback-classic "1.1.2" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.7"]
                 [org.slf4j/jcl-over-slf4j "1.7.7"]
                 [org.slf4j/log4j-over-slf4j "1.7.7"]]
  :resource-paths ["config" "resources"]
  :datomic {:schemas ["resources/datomic/schema.edn"]}
  :profiles {:dev {:dependencies [[midje "1.6.3"]
                                  [io.pedestal/pedestal.service-tools "0.3.1"]]
                   :aliases {"run-dev" ["trampoline" "run" "-m" "expenses.server/run-dev"]}
                   :datomic {:config "resources/datomic/dev-transactor-template.properties"
                             :db-uri "datomic:dev://localhost:4334/expenses"}}}
  :repositories {"my.datomic.com" {:url "https://my.datomic.com/repo"
                                   :creds :gpg}}
  :main ^{:skip-aot true} expenses.server)
