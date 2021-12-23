(ns poet-two.db
  (:require
   [clojure.string :as string]
   [taoensso.carmine :as car :refer (wcar)]))

(def server1-conn {:pool {} :spec {:uri "redis://localhost"}})
(defmacro wcar* [& body] `(car/wcar server1-conn ~@body))

(defn search [word]
  (wcar* (car/get word)))

(defn insert [word]
  (wcar* (car/set (:word word) word)))

(defn delete [word]
  (wcar* (car/del word)))

(defn get-all []
  (let [keys (wcar* (car/keys "*"))]
    (into {}
          (map (fn [k]
                 [k (wcar* (car/get k))])
               keys))))
;; I think the move is to append? all noun keys
;; to a "THE-LIST-OF-NOUNS" value or some such thing...
;; I think this sort of thing has to be done for each
;; thing I would normal perform a "where" on...
;; maybe sqlite would have been good enough
;; or even better...
