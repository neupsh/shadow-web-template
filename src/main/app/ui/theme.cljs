(ns app.ui.theme
  (:require
    [reagent.core :as r]
    [re-frame.core :as rf :refer [reg-event-db after reg-sub]]
    ;[iron.re-utils :as re-utils :refer [<sub >evt event->fn sub->fn]]
    ["@material-ui/core" :as mui]
    ["@material-ui/core/styles" :refer [createMuiTheme withStyles withTheme]]
    ["@material-ui/core/colors" :as mui-colors]
    ["@material-ui/icons" :as mui-icons]
    [goog.object :as gobj]))


(defn create-mui-theme [attrs]
  (createMuiTheme (clj->js attrs)))


(defn get-mui-color [hue shade]
  (gobj/get (aget mui-colors hue) shade))


(def default-palette
  {:primary {
             :main (get-mui-color "red" "100")
             }
   :contrastThreshold 3
   :tonalOffset 0.2}
  )

#_(def cyan-teal-palette
    {:primary-color-dark "#00796B"
     :primary-color "#009688"
     :primary-color-light "#B2DFDB"
     :primary-color-text "#FFFFFF"
     :accent-color "#00BCD4"
     :primary-text-color "#212121"
     :secondary-text-color "#757575"
     :divider-color "#BDBDBD"
     })

(def cyan-teal-palette
  {:primary {:main "#00796B"}
   :secondary {:main "#757575"}})

(def dark-cyan-palette
  {:primary {
             :main (get-mui-color "blueGrey" "700")
             :contrastText (get-mui-color "blueGrey" "50")
             ;:contrastText "#"
             }
   :secondary {
               :main (get-mui-color "teal" "800")
               :contrastText (get-mui-color "teal" "A400")
               }
   :error mui-colors/red
   :contrastThreshold 3
   :tonalOffset 0.2})

(def red-palette
  {:primary mui-colors/indigo
   :secondary mui-colors/cyan
   :error mui-colors/red
   :contrastThreshold 3
   :tonalOffset 0.2})

(def purple-palette
  {:primary {:main "#ff4400"}
   :secondary {:main "#0044ff"}
   :contrastThreshold 3
   :tonalOffset 0.2})

(def nepal-palette
  {:primary {:main "#f50057"}
   :secondary {:main "#3f51b5"}
   :contrastThreshold 3
   :tonalOffset 0.2
   })


(def palette-for {:default-theme default-palette
                  :red-theme red-palette
                  :purple-theme purple-palette
                  :nepal-theme nepal-palette
                  :cyan-teal-theme cyan-teal-palette
                  :dark-cyan-theme dark-cyan-palette})


(def default-theme
  (create-mui-theme
    {:palette default-palette
     :typography {:useNextVariants true}}))

(defn create-theme-from-palette [palette]
  (create-mui-theme
    {:palette palette
     :typography {:useNextVariants true}}))


(def themes {:default-theme default-theme
             :nepal-theme (create-theme-from-palette nepal-palette)
             ;:red-theme (create-theme-from-palette red-palette)
             ;:purple-theme (create-theme-from-palette purple-palette)
             :cyan-teal-theme (create-theme-from-palette cyan-teal-palette)
             :dark-cyan-theme (create-theme-from-palette dark-cyan-palette)
             })

(def cycle-theme
  (let [theme-names (atom (rest (cycle (keys themes))))]
    (fn [] (let [c (first @theme-names)
                 r (rest @theme-names)] (reset! theme-names r) c))))

(defn theme-spacing
  ([theme] (theme-spacing theme 1))
  ([theme unit] ((.. theme -spacing) unit)))

(defn default-styles [theme]
  (let [unit-spacing (theme-spacing theme)]
    (clj->js {:.root-component {:marginLeft unit-spacing
                                :marginRight unit-spacing}
              :button {:margin unit-spacing}
              :textField {:width 200
                          :marginLeft unit-spacing
                          :marginRight unit-spacing}
              :top-nav-toolbar-size (.. theme -mixins -toolbar)

              })))

(defn with-custom-styles
  "Wraps the given component with material `withStyle` js function.
  component - reagent component
  styles    - function that returns a js object (clj->js on a map) given a `theme` object
  "
  [component styles]
  ((withStyles styles #js {:withTheme true}) (r/reactify-component component)))

#_(defn set-theme [t]
    )




;; Themes in reframe db

(reg-event-db
  :add-theme
  ;; handler function (db, event) -> db
  (fn [db [_ t]]
    (update-in db [:themes] (fnil conj []) t)))

(reg-sub
  :get-themes
  (fn [db _]
    (:themes db)))

(reg-event-db
  :set-theme
  ;; handler function (db, event) -> db
  (fn [db [_ t]]
    (assoc db :current-theme t)))

(reg-event-db
  :cycle-theme
  ;; handler function (db, event) -> db
  (fn [db _]
    (assoc db :current-theme (cycle-theme))))

(reg-sub
  :current-theme
  (fn [db _]
    (:current-theme db)))

;(>evt [:set-theme] :default-theme)
;(>evt [:set-theme] :cyan-teal-theme)
;(rf/dispatch [:set-theme default-theme])


(defn get-theme [theme]
  ;(if (keyword? theme) (themes theme) theme)
  (if (keyword? theme)
    #_(create-theme-from-palette (palette-for theme))
    (create-mui-theme
      {:palette (palette-for theme)
       :typography {:useNextVariants true}})
    theme))

(defn with-theme
  [component]
  (fn [props]
    (let [cur-theme    (rf/subscribe [:current-theme])
          actual-theme (get-theme @cur-theme)]
      ;; fragment
      [:<>
       [:> mui/CssBaseline]
       [:> mui/MuiThemeProvider
        ;{:theme (get-default-theme)}
        {:theme actual-theme}
        [:> component]]])))

(defn themed-component
  [component]
  (withTheme (r/reactify-component component)))


