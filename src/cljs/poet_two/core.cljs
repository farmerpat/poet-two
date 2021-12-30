(ns poet-two.core
  (:require
    [day8.re-frame.http-fx]
    [reagent.dom :as rdom]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [goog.events :as events]
    [goog.history.EventType :as HistoryEventType]
    [markdown.core :refer [md->html]]
    [poet-two.ajax :as ajax]
    [poet-two.events]
    [poet-two.util :as u]
    [poet-two.views :as v]
    [reitit.core :as reitit]
    [reitit.frontend.easy :as rfe]
    [clojure.string :as string])
  (:import goog.History))

(defn home-page []
  [:section.section>div.container>div.content
   [:div.controls-container
   [v/generate-sentence-button]
   [v/save-sentence-button]]
   [:div#sentence-container
    [v/sentence]]])

(defn page []
  (if-let [page @(rf/subscribe [:common/page])]
    [:div
     [v/navbar]
     [page]]))

(defn navigate! [match _]
  (rf/dispatch [:common/navigate match]))

(def router
  (reitit/router
    [["/" {:name        :home
           :view        #'home-page
           :controllers [{:start (fn [_] (rf/dispatch [:page/init-home]))}]}]]))

(defn start-router! []
  (rfe/start!
    router
    navigate!
    {}))

;; -------------------------
;; Initialize app
(defn ^:dev/after-load mount-components []
  (rf/clear-subscription-cache!)
  (rdom/render [#'page] (.getElementById js/document "app")))

;; TODO: init-db where :sentence => nil
(defn init! []
  (start-router!)
  (ajax/load-interceptors!)
  (mount-components))
