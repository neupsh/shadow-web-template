(ns app.features.navigation
  (:require
    [accountant.core :as accountant]
    [reagent.core :as r]
    [reagent.session :as session]
    [bidi.bidi :as bidi]
    [clerk.core :as clerk]))


;; routes
(def app-routes
  ["/" {"" :index
        "a-items" {"" :a-items
                   ["/item-" :item-id] :a-item}
        "b-items" {"" :b-items
                   ["/item-" :item-id] :b-item}
        "about" :about
        "missing-route" :missing-route
        true :four-o-four}])

#_(defn navigate-to [path]
    (r/after-render clerk/after-render!)
    (let [match (bidi/match-route app-routes path)
          current-page (:handler match)
          route-params (:route-params match)]
      (session/put! :route {:current-page current-page
                            :route-params route-params}))
    (clerk/navigate-page! path))


(defn path-for [route]
  "Returns path for the given route. It will return itself it is not a keyword (route)."
  (if (keyword? route)
    (bidi/path-for app-routes route)
    route))

(defn navigate-to!
  "Navigates to the given route (keyword) or path (string)"
  ([route]
   (accountant/navigate! (path-for route)))
  ([route query]
   (accountant/navigate! (path-for route) query)))

