(ns bunsen.notebook.service
  (:gen-class)
  (:require [environ.core :refer [env]]
            [bunsen.common.helper.utils :as u]
            [bunsen.common.helper.json :as j]
            [com.stuartsierra.component :as component]
            [bunsen.common.component.database :refer [database]]
            [bunsen.notebook.component.server :refer [server]]))

(defn service [config]
  (j/enable-date-serialization)
  (j/enable-uuid-json-serialization)
  (-> (component/system-map
        :database (database (assoc config :seed-readers {'file u/read-resource-file}))
        :server (component/using
                  (server config)
                  {:database :database}))))

(defn -main [& args]
  (component/start
    (service {:server-port (Integer. (:notebook-port env))
              :seed-file (:notebook-seed-file env)
              :cookie-salt (:cookie-salt env)
              :allow-seed (:allow-seed env)
              :database-uri (:notebook-database-uri env)})))