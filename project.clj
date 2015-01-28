(defproject expenses "0.1.0-SNAPSHOT"
  :description "Clojure library to calculate incomes and debts based on documents provided by Banco do Brasil"
  :url "http://github.com/EduardoMRB/expenses"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2740"]
                 [net.sf.ofx4j/ofx4j "1.6"]
                 [cc.artifice/ofx-clj "0.1"]
                 [clj-time "0.9.0"]

                 [com.datomic/datomic-pro "0.9.5078" :exclusions [joda-time
                                                                  org.slf4j/slf4j-nop]]

                 [io.pedestal/pedestal.service "0.3.1"]
                 [io.pedestal/pedestal.jetty "0.3.1"]

                 [ch.qos.logback/logback-classic "1.1.2" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.7"]
                 [org.slf4j/jcl-over-slf4j "1.7.7"]
                 [org.slf4j/log4j-over-slf4j "1.7.7"]

                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]

                 [ns-tracker "0.2.2"]

                 [domina "1.0.3"]
                 [hiccups "0.3.0"]
                 [cljs-ajax "0.3.9"]
                 [org.omcljs/om "0.8.6"]
                 [com.cemerick/piggieback "0.1.5"]]

  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :injections [(require '[cljs.repl.browser :as brepl]
                        '[cemerick.piggieback :as pb])
               (defn browser-repl []
                 (pb/cljs-repl :repl-env (brepl/repl-env :port 9000)))]

  :source-paths ["src/clj"]
  :resource-paths ["config" "resources"]

  :datomic {:schemas ["resources/datomic/schema.edn"]}

  :profiles {:dev {:dependencies [[midje "1.6.3"]
                                  [io.pedestal/pedestal.service-tools "0.3.1"]]
                   :aliases {"run-dev" ["trampoline" "run" "-m" "expenses.server/run-dev"]}
                   :datomic {:config "config/dev-transactor-template.properties"
                             :db-uri "datomic:dev://localhost:4334/expenses"}}}

  :repositories {"my.datomic.com" {:url "https://my.datomic.com/repo"
                                   :creds :gpg}}
  :main ^{:skip-aot true} expenses.server

  :plugins [[lein-cljsbuild "1.0.4"]]

  :cljsbuild
  {:builds
   [{:id "dev"
     :source-paths ["src/cljs"]
     :compiler {:optimizations :none
                :main expenses.core
                :output-to "resources/public/js/expenses.js"
                :output-dir "resources/public/js/out"
                :source-map true
                :pretty-print true}}
    {:id "adv"
     :source-paths ["src/cljs"]
     :compiler {:optimizations :advanced
                :pretty-print false
                :static-fns true
                :output-dir "out-adv"
                :output-to "resources/public/js/expenses.js"}}]})
