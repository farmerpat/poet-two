(ns poet-two.routes.api
  (:require
   [clojure.java.io :as io]
   [poet-two.dict :as dict]
   [poet-two.layout :as layout]
   [poet-two.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]))

(defn sentence-generate [req]
  (let [sentence (dict/sentence)
        sentence-and-meta {:sentence sentence
                           :meta (meta sentence)}]
    (response/ok {:sentence sentence-and-meta})))

(defn sentence-save [req]
  (let [sentence (:sentence (:params req))]
    (println "HERE'S da sentence:")
    (println sentence)
    (response/ok {:save-success true})))

(defn api-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/api/sentence/generate" {:get sentence-generate}]
   ["/api/sentence/save" {:get sentence-save}]])
