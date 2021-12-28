(ns poet-two.db
  (:require
   [clojure.string :as string]
   [taoensso.carmine :as car :refer (wcar)]
   [poet-two.util :refer [keywordize-orders]]))

(def WORD-DB 0)
(def SENTENCE-DB 1)

(def server1-conn {:pool {} :spec {:uri "redis://localhost"}})
;; TODO?
;; could create word-wcar* and sentence-wcar* macros to avoid
;; having to select all over the place
(defmacro wcar* [& body] `(car/wcar server1-conn ~@body))

(defn word-search [word]
  (wcar*
   (car/select WORD-DB)
   (second (car/get word))))

(defn word-insert [word]
  (wcar*
   (car/select WORD-DB)
   (car/set (:word word) word)))

(defn word-delete [word]
  (wcar*
   (car/select WORD-DB)
   (car/del word)))

(defn word-get-all []
  (wcar* (car/select WORD-DB))
  (let [keys (wcar* (car/keys "*"))]
    (into {}
          (map (fn [k]
                 [k (wcar* (car/get k))])
               keys))))

(defn sentence-search [time-stamp]
  (let [result (wcar*
                (car/select SENTENCE-DB)
                (car/get time-stamp))]
    (keywordize-orders (second result))))

(defn sentence-insert [s]
  (wcar*
   (car/select SENTENCE-DB)
   (car/set (:timestamp (:info s)) s)))

(defn sentence-delete [time-stamp]
  (wcar*
   (car/select SENTENCE-DB)
   (car/del time-stamp)))

(defn sentence-delete-many [time-stamps]
  (map (fn [ts]
         (sentence-delete ts))
       time-stamps))

;; e.g.
;; (map (fn [s]
;;   (poet-two.dict/print-sentence s)) (sentence-get-all))

;; TODO:
;; It's possibly that if somehow this
;; returned a lazy seq and if a caller
;; could return early on some condition,
;; that something like get-sentence-where
;; could be written...
;; maybe redis + immutable DS is quick enough
;; maybe a sql is better.
(defn sentence-get-all []
  (wcar* (car/select SENTENCE-DB))
  (let [keys (wcar* (car/keys "*"))]
    (into []
          (map (fn [k]
                 (sentence-search k))
               keys))))

;; according to
;; (let [t1 (generate-current-timestamp)
;;     t2 (generate-current-timestamp)]
;;   (println t1)
;;   (println t2))
;; this resolution should be high enough
;; to ensure uniqueness..
(defn generate-current-timestamp []
  (let [now (java.time.Instant/now)]
    (str (.getEpochSecond now) "-" (.getNano now))))

;; I think the move is to append? all noun keys
;; to a "THE-LIST-OF-NOUNS" value or some such thing...
;; I think this sort of thing has to be done for each
;; thing I would normal perform a "where" on...
;; maybe sqlite would have been good enough
;; or even better...
