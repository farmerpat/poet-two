(ns poet-two.routes.api
  (:require
   [clojure.java.io :as io]
   [poet-two.dict :as dict]
   [poet-two.layout :as layout]
   [poet-two.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]))

(defn sentence-generate [req]
  (response/ok {:sentence (dict/sentence)}))

(defn sentence-save [req]
  (let [sentence (:sentence (:params req))
        ts (:timestamp (:info sentence))]
    (println "HERE'S da sentence:")
    (println sentence)
    (response/ok {:save-success true})))

(defn api-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/api/sentence/generate" {:get sentence-generate}]
   ["/api/sentence/save" {:get sentence-save}]])
