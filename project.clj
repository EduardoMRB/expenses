(defproject expenses "0.1.0-SNAPSHOT"
  :description "Clojure library to calculate incomes and debts based on documents provided by Banco do Brasil"
  :url "http://github.com/EduardoMRB/expenses"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [net.sf.ofx4j/ofx4j "1.6"]
                 [cc.artifice/ofx-clj "0.1"]
                 [clj-time "0.9.0"]
                 [com.datomic/datomic-free "0.9.5078" :exclusions [joda-time]]]
  :profiles {:dev {:dependencies [[midje "1.6.3"]]}}
  :repositories {"my.datomic.com" {:url "https://my.datomic.com/repo"
                                   :creds :gpg}}
  :main expenses.core)
