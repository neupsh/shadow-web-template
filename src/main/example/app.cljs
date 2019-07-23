(ns example.app
  (:require
   [reagent.core :as r]
   [reagent.session :as session]
   [re-frame.core :as rf]
   [taoensso.timbre :as log                                 ;:refer-macros [log trace debug info warn]
    ]
   [example.db.events]
   [example.db.subs]
   [bidi.bidi :as bidi]
   [accountant.core :as accountant]
   [clerk.core :as clerk]
   [example.firebase :as firebase]
   [config.firebase-config :as fb-config]
   ))



(enable-console-print!)


;; routes
(def app-routes
  ["/" {"" :index
        "a-items" {"" :a-items
                   ["/item-" :item-id] :a-item}
        "b-items" {"" :b-items
                   ["/item-" :item-id] :b-item}
        "firebase-example" :firebase-example
        "about" :about
        "missing-route" :missing-route
        true :four-o-four}])

(defmulti page-contents identity)

(defmethod page-contents :index []
  (fn []
    [:span.main
     [:h1 "Welcome to routing-example"]
     [:ul
      [:li [:a {:href (bidi/path-for app-routes :a-items)} "Lots of items of type A"]]
      [:li [:a {:href (bidi/path-for app-routes :b-items)} "Many items of type B"]]
      [:li [:a {:href (bidi/path-for app-routes :missing-route)} "A Missing Route"]]
      [:li [:a {:href "/borken/link"} "A Borken Link"]]]
     [:p "Using "
      [:a {:href "https://reagent-project.github.io/"} "Reagent"] ", "
      [:a {:href "https://github.com/juxt/bidi"} "Bidi"] ", "
      [:a {:href "https://github.com/venantius/accountant"} "Accountant"] " & "
      [:a {:href "https://github.com/PEZ/clerk"} "Clerk"]
      ". Find this example on " [:a {:href "https://github.com/PEZ/reagent-bidi-accountant-example"} "Github"]]]))


(defmethod page-contents :a-items []
  (fn []
    [:span.main
     [:h1 "The Lot of A Items"]
     [:div#red {:style {:width "50%" :height "200px" :background-color "red"}}]
     [:ul
      (map (fn [item-id]
             [:li {:id (str "item-" item-id) :key (str "item-" item-id)}
              [:a {:href (bidi/path-for app-routes :item :item-id item-id)} "A-item: " item-id]])
        (range 1 42))]
     [:div {:style {:width "50%" :height "200px" :background-color "green"}}]
     [:div#b-item-100-link [:a {:href (str (bidi/path-for app-routes :b-items) "#item-50")} "B-item: 50"]]
     [:div {:style {:width "50%" :height "200px" :background-color "blue"}}]
     [:ul
      (map (fn [item-id]
             [:li {:id (str "item-" item-id) :key (str "item-" item-id)}
              [:a {:href (bidi/path-for app-routes :item :item-id item-id)} "A-item: " item-id]])
        (range 42 78))]
     [:div {:style {:width "50%" :height "200px" :background-color "yellow"}}]
     [:p [:a {:href (bidi/path-for app-routes :b-items)} "Top of b-items"]]]))


(defmethod page-contents :b-items []
  (fn []
    [:span.main
     [:h1 "The Many B Items"]
     [:ul (map (fn [item-id]
                 [:li {:id (str "item-" item-id) :key (str "item-" item-id)}
                  [:a {:href (bidi/path-for app-routes :item :item-id item-id)} "B-item: " item-id]])
            (range 1 117))]
     [:p [:a {:href (bidi/path-for app-routes :a-items)} "Top of a-items"]]]))


(defmethod page-contents :a-item []
  (fn []
    (let [routing-data (session/get :route)
          item (get-in routing-data [:route-params :item-id])]
      [:span.main
       [:h1 (str "Item " item " of A")]
       [:p [:a {:href (bidi/path-for app-routes :items)} "Back to the list of A-items"]]])))


(defmethod page-contents :b-item []
  (fn []
    (let [routing-data (session/get :route)
          item (get-in routing-data [:route-params :item-id])]
      [:span.main
       [:h1 (str "Item " item " of B")]
       [:p [:a {:href (bidi/path-for app-routes :items)} "Back to the list of B-items"]]])))


(defmethod page-contents :about []
  (fn [] [:span.main
          [:h1 "About routing-example"]]))

(defmethod page-contents :firebase-example []
  (fn [] [:span.main
          [firebase/ui]]))


(defmethod page-contents :four-o-four []
  "Non-existing routes go here"
  (fn []
    [:span.main
     [:h1 "404: It is not here"]
     [:pre.verse
      "What you are looking for,
I do not have.
How could I have,
what does not exist?"]]))


(defmethod page-contents :default []
  "Configured routes, missing an implementation, go here"
  (fn []
    [:span.main
     [:h1 "404: My bad"]
     [:pre.verse
      "This page should be here,
but it is not."]]))


;; -------------------------
;; Page mounting component

(defn current-page []
  (fn []
    (let [page (:current-page (session/get :route))]
      [:div
       [:header
        [:p#top [:a {:href (bidi/path-for app-routes :index)} "Go home"] " | "
         [:a {:href (bidi/path-for app-routes :firebase-example)} "Firebase Example"] " | "
         [:a {:href (bidi/path-for app-routes :about)} "See about"] " | "
         [:a {:href "#bottom"} "Bottom of page"]]
        ^{:key page} [page-contents page]
        [:footer
         [:div
          [:p "See also: " [:a {:href "https://github.com/neupsh/shadow-web-template"} "shadow-web-template github page"]]]
         [:div
          [:p#bottom [:a {:href (bidi/path-for app-routes :index)} "Go home"] " | "
           [:a {:href (bidi/path-for app-routes :about)} "See about"] " | "
           [:a {:href "#top"} "Top of page"]]]]]])))


#_(defn on-js-reload []
  (r/render-component [current-page]
    (. js/document (getElementById "app"))))

(defn mount-components []
  (rf/clear-subscription-cache!)
  (r/render [current-page] (.getElementById js/document "app")))


(defn start
  {:dev/after-load true}
  []
  (log/info "Starting app...")
  (mount-components))


(defn ^:export init []
  (log/info "Initializing app...")
  (rf/dispatch-sync [:initialize-db])
  (clerk/initialize!)
  (accountant/configure-navigation!
    {:nav-handler (fn
                    [path]
                    (r/after-render clerk/after-render!)
                    (let [match (bidi/match-route app-routes path)
                          current-page (:handler match)
                          route-params (:route-params match)]
                      (session/put! :route {:current-page current-page
                                            :route-params route-params}))
                    (clerk/navigate-page! path))
     :path-exists? (fn [path]
                     (boolean (bidi/match-route app-routes path)))})
  (accountant/dispatch-current!)
  (firebase/init fb-config/configObject)
  ;(on-js-reload)
  (start)
  )
