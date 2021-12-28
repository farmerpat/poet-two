(ns poet-two.app
  (:require [poet-two.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
