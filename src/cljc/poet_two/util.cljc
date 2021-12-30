(ns poet-two.util
  (:require
   [clojure.string :as string]))

(defn keywordize-orders [sentence]
  (let [sub-orders (:order (:subject sentence))
        pred-orders (:order (:predicate sentence))]
    (assoc-in
      (assoc-in sentence [:subject :order] (vec (map keyword sub-orders)))
      [:predicate :order] (vec (map keyword pred-orders)))))

(defn sentence-extract-string [sentence]
  (let [subject (:subject sentence)
        predicate (:predicate sentence)
        subject-order (:order subject)
        predicate-order (:order predicate)]
    (string/capitalize
     (str
      (string/trim
       (str
        (reduce str (map (fn [k] (str (:word (get subject k)) " ")) subject-order))
        " "
        (reduce str (map (fn [k] (str (:word (get predicate k)) " ")) predicate-order))))
      "."))))
