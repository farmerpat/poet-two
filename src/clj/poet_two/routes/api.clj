(ns poet-two.routes.api
  (:require
   [clojure.java.io :as io]
   [poet-two.dict :as dict]
   [poet-two.db :as db]
   [poet-two.layout :as layout]
   [poet-two.middleware :as middleware]
   [poet-two.util :refer [keywordize-orders]]
   [ring.util.response]
   [ring.util.http-response :as response]))

(defn sentence-generate [req]
  (response/ok {:sentence (dict/sentence)}))

;; TODO
;; This is the same shape as keywordize-orders
;; factor?
;; make keywordize-orders deal with it?
(defn colize-orders [s]
  (let [sub-orders (:order (:subject s))
        pred-orders (:order (:predicate s))]
    (assoc-in
     (assoc-in s [:subject :order] (if (coll? sub-orders)
                                            sub-orders
                                            [sub-orders]))
     [:predicate :order] (if (coll? pred-orders)
                           pred-orders
                           [pred-orders]))))

(defn sentence-save [req]
  (let [s (:sentence (:params req))
        sen (colize-orders s)
        sentence (keywordize-orders sen)
        insert-result (db/sentence-insert sentence)]
    (response/ok {:save-success (= "OK" (first insert-result))})))

(defn api-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/api/sentence/generate" {:get sentence-generate}]
   ["/api/sentence/save" {:get sentence-save}]])
