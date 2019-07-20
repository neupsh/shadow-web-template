(ns example.ui.theme
  (:require
    ["@material-ui/core" :as mui]
    ["@material-ui/core/styles" :refer [createMuiTheme withStyles]]
    ["@material-ui/core/colors" :as mui-colors]
    ["@material-ui/icons" :as mui-icons]
    [goog.object :as gobj]))



(defn get-mui-theme [theme]
  (mui/get-mui-theme (aget js/MaterialUIStyles theme)))

(defn create-mui-theme [attrs]
  (createMuiTheme (clj->js attrs)))

(defn wrap-with-theme [theme component]
  (mui/mui-theme-provider
    {:mui-theme theme}
    component))


(defn get-mui-color [hue shade]
  (gobj/get (aget mui-colors hue) shade))


(def default-palette
  {:primary {
             :main (get-mui-color "red" "100")
             }
   :contrastThreshold 3
   :tonalOffset 0.2}
  )

(def cyan-teal-palette
  {:primary-color-dark "#00796B"
   :primary-color "#009688"
   :primary-color-light "#B2DFDB"
   :primary-color-text "#FFFFFF"
   :accent-color "#00BCD4"
   :primary-text-color "#212121"
   :secondary-text-color "#757575"
   :divider-color "#BDBDBD"
   })

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
   :secondary {:main "#0044ff"}})


(def default-theme
  (create-mui-theme
    {;:palette {:primary {:main (gobj/get (.-red mui-colors) 100)}}
     ;:palette purple-palette                                ;red-palette
     :palette default-palette                                ;dark-cyan-pallete
     :typography {:useNextVariants true}}))


(defn get-default-theme []
  ;(get-mui-theme "chromeLight")
  default-theme
  )

(defn default-styles [theme]
  (clj->js {:root-component {:marginLeft (.. theme -spacing -unit)
                             :marginRight (.. theme -spacing -unit)}
            :button {:margin (.. theme -spacing -unit)}
            :textField {:width 200
                        :marginLeft (.. theme -spacing -unit)
                        :marginRight (.. theme -spacing -unit)}
            :top-nav-toolbar-size (.. theme -mixins -toolbar)

            }))

(defn wrap-component-with-styles
  "Wraps the given component with material `withStyle` js function.
  component - reagent component
  styles    - function that returns a js object (clj->js on a map) given a `theme` object
  "
  [component styles]
  ((withStyles styles) (r/reactify-component component)))



(defn wrap-with-default-theme
  ([component] (wrap-with-default-theme component default-styles))
  ([component style-fn]
   [:> mui/CssBaseline
    [:> mui/MuiThemeProvider
     {:theme (get-default-theme)}
     [:> (wrap-component-with-styles component style-fn)]]]))
