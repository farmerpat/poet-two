(ns poet-two.views
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
    [reitit.core :as reitit]
    [reitit.frontend.easy :as rfe]
    [clojure.string :as string]))

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

(defn sentence-subject [s]
  [:div.subject
   [:div.part-of-sentence-description "Subject"]
   [:div.part-of-speech-container
    (map (fn [k]
           (let [word (get (:subject s) k)
                 key (:key word)
                 pos (:part-of-speech word)
                 text-word (:word word)]
             [:div.word {:key key}
              [:div.word-part-of-speech pos]
              [:div.word-text text-word]]))
         (:order (:subject s)))]])

(defn sentence-predicate [s]
  [:div.predicate
   [:div.part-of-sentence-description "Predicate"]
   [:div.part-of-speech-container
    (map (fn [k]
           (let [word (get (:predicate s) k)
                 key (:key word)
                 pos (:part-of-speech word)
                 text-word (:word word)]
             [:div.word {:key key}
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
            [:div (u/sentence-extract-string s)
             [:div.sentence-structure
              [sentence-subject s]
              [sentence-predicate s]]]))))))
