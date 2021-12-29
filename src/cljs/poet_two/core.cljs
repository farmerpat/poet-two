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
                (rf/dispatch [:generate-sentence]))}
   "Generate Sentence"])

(defn save-sentence-button []
  [:button
   {:id "save_sentence"
    :class "button"
    :on-click (fn [e]
                (.preventDefault e)
                (rf/dispatch [:save-sentence]))}
   "Save Sentence"])

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

(defn sentence-subject [s]
  [:div.subject
   [:div.part-of-sentence-description "Subject"]
   [:div.part-of-speech-container
     (map (fn [k]
            (let [word (get (:subject s) k)
                  pos (:part-of-speech word)
                  text-word (:word word)]
              [:div.word
               [:div.word-part-of-speech pos]
               [:div.word-text text-word]]))
          (:order (:subject s)))]])

(defn sentence-predicate [s]
  [:div.predicate
   [:div.part-of-sentence-description "Predicate"]
   [:div.part-of-speech-container
     (map (fn [k]
            (let [word (get (:predicate s) k)
                  pos (:part-of-speech word)
                  text-word (:word word)]
              [:div.word
               [:div.word-part-of-speech pos]
               [:div.word-text text-word]]))
          (:order (:predicate s)))]])

;; TODO
;; Probably ought to create visual containers
;; for each part of speech.
;; Probably want a "show details" checkbox
;; or some such control.
(defn sentence []
  (let [sub (rf/subscribe [:sentence])]
    (fn []
      (let [s @sub]
        (if (nil? s)
          [:div.empty-sentence]
          (do
            [:div (sentence-extract-string s)
             [:div.sentence-structure
              [sentence-subject s]
              [sentence-predicate s]]]))))))

(defn home-page []
  [:section.section>div.container>div.content
   [:div.controls-container
   [generate-sentence-button]
   [save-sentence-button]]
   [:div#sentence-container
    [sentence]]])

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

;; TODO: init-db where :sentence => nil
(defn init! []
  (start-router!)
  (ajax/load-interceptors!)
  (mount-components))
