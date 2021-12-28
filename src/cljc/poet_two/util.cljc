(ns poet-two.util)

(defn keywordize-orders [sentence]
  (let [sub-orders (:order (:subject sentence))
        pred-orders (:order (:predicate sentence))]
    (assoc-in
      (assoc-in sentence [:subject :order] (vec (map keyword sub-orders)))
      [:predicate :order] (vec (map keyword pred-orders)))))
