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
    [reitit.core :as reitit]
    [reitit.frontend.easy :as rfe]
    [clojure.string :as string])
  (:import goog.History))

(defn nav-link [uri title page]
  [:a.navbar-item
   {:href   uri
    :class (when (= page @(rf/subscribe [:common/page-id])) :is-active)}
   title])

(defn navbar [] 
  (r/with-let [expanded? (r/atom false)]
              [:nav.navbar.is-dark>div.container
               [:div.navbar-brand
                [:a.navbar-item {:href "/" :style {:font-weight :bold}} "poet-two"]
                [:span.navbar-burger.burger
                 {:data-target :nav-menu
                  :on-click #(swap! expanded? not)
                  :class (when @expanded? :is-active)}
                 [:span][:span][:span]]]
               [:div#nav-menu.navbar-menu
                {:class (when @expanded? :is-active)}
                [:div.navbar-start
                 [nav-link "#/" "Home" :home]]]]))

(defn generate-sentence-button []
  [:button
   {:id "make_sentence"
    :class "button"
    :on-click (fn [e]
                (.preventDefault e)
                (js/console.log "fyf!")
                (rf/dispatch [:generate-sentence])
                )}
   "Generate Sentence"])

(defn save-sentence-button []
  [:button
   {:id "save_sentence"
    :class "button"
    :on-click (fn [e]
                (.preventDefault e)
                (js/console.log "fyf a lot.")
                (rf/dispatch [:save-sentence]))}
   "Save Sentence"])

;; (defn home-page []
;;   [:section.section>div.container>div.content
;;    (when-let [docs @(rf/subscribe [:docs])]
;;      [:div {:dangerouslySetInnerHTML {:__html (md->html docs)}}])])
(defn home-page []
  [:section.section>div.container>div.content
   [generate-sentence-button]
   [save-sentence-button]
   [:div#sentence-container
    "The sentence will go here!"]
   ])

(defn page []
  (if-let [page @(rf/subscribe [:common/page])]
    [:div
     [navbar]
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

(defn init! []
  (start-router!)
  (ajax/load-interceptors!)
  (mount-components))
