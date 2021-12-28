(ns poet-two.events
  (:require
    [re-frame.core :as rf]
    [clojure.walk :as walk]
    [ajax.core :as ajax]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]))

(rf/reg-event-db
 :ajax-failure
 (fn [db [_ _]]
   (js/console.log "AJAX FAILURE!")
   db))

(rf/reg-event-fx
 :save-sentence
 (fn [{:keys [db]} _]
   (let [s (:sentence db)]
     (js/console.log "here's the sentence we're trying to save:")
     (js/console.log s)
     {:http-xhrio {:method :get
                   :uri "/api/sentence/save"
                   :params {:sentence s}
                   :request-format (ajax/json-request-format)
                   :response-format (ajax/json-response-format {:keywords true})
                   :on-success [:save-sentence-success]
                   :on-failure [:ajax-failure]}})))

(rf/reg-event-fx
 :generate-sentence
 (fn [{:keys [db]} _]
   {:http-xhrio {:method :get
                 :uri "/api/sentence/generate"
                 :params {}
                 :request-format (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords true})
                 :on-success [:generate-sentence-success]
                 :on-failure [:ajax-failure]}}))

(defn keywordize-orders [sentence]
  (let [sub-orders (:order (:subject sentence))
        pred-orders (:order (:predicate sentence))]
    (assoc-in
      (assoc-in sentence [:subject :order] (map keyword sub-orders))
      [:predicate :order] (map keyword pred-orders))))

(rf/reg-event-db
 :generate-sentence-success
 (fn
   [db [_ res]]
   (let [s (get res "sentence")
         sentence (walk/keywordize-keys s)]
     (js/console.log "got a generated sentence: ")
     (js/console.log s)
     (assoc db :sentence (keywordize-orders sentence)))))

(rf/reg-event-db
 :save-sentence-success
 (fn
   [db [_ res]]
   (let [save-success (get res "save-success")]
     (if save-success
       (js/console.log "save-success!")
       (js/console.log "not save-success!")))))

(rf/reg-event-db
 :common/navigate
 (fn [db [_ match]]
   (let [old-match (:common/route db)
         new-match (assoc match :controllers
                          (rfc/apply-controllers (:controllers old-match) match))]
     (assoc db :common/route new-match))))

(rf/reg-fx
  :common/navigate-fx!
  (fn [[k & [params query]]]
    (rfe/push-state k params query)))

(rf/reg-event-fx
  :common/navigate!
  (fn [_ [_ url-key params query]]
    {:common/navigate-fx! [url-key params query]}))

(rf/reg-event-db
  :common/set-error
  (fn [db [_ error]]
    (assoc db :common/error error)))

(rf/reg-event-fx
  :page/init-home
  (fn [_ _]
    {}))

;;subscriptions

(rf/reg-sub
 :sentence
 (fn [db _]
   (:sentence db)))

(rf/reg-sub
 :common/route
 (fn [db _]
   (-> db :common/route)))

(rf/reg-sub
  :common/page-id
  :<- [:common/route]
  (fn [route _]
    (-> route :data :name)))

(rf/reg-sub
  :common/page
  :<- [:common/route]
  (fn [route _]
    (-> route :data :view)))

;; (rf/reg-sub
;;   :docs
;;   (fn [db _]
;;     (:docs db)))

(rf/reg-sub
  :common/error
  (fn [db _]
    (:common/error db)))
