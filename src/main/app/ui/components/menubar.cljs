(ns app.ui.components.menubar
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [app.features.navigation :refer [navigate-to!]]
            [app.ui.theme :as theme]
    ;["@material-ui/core" :as mui :refer [AppBar Toolbar IconButton Typography Badge Menu MenuItem]]
            ["@material-ui/core/AppBar" :default AppBar]
            ["@material-ui/core/Toolbar" :default Toolbar]
            ["@material-ui/core/IconButton" :default IconButton]
            ["@material-ui/core/Typography" :default Typography]
            ["@material-ui/core/Badge" :default Badge]
            ["@material-ui/core/Menu" :default Menu]
            ["@material-ui/core/MenuItem" :default MenuItem]
    ;["@material-ui/icons" :as mui-icons]
            ["@material-ui/core/styles/colorManipulator" :refer [fade]]
            ["@material-ui/core/Avatar" :default Avatar]
            ["@material-ui/icons/Android" :default AndroidIcon]
            ["@material-ui/icons/AccountCircle" :default AccountCircle]
            ["@material-ui/icons/Info" :default InfoIcon]
            ["@material-ui/icons/Home" :default HomeIcon]
            ["@material-ui/icons/Notifications" :default NotificationsIcon]
            ["@material-ui/icons/Mail" :default MailIcon]
            ["@material-ui/icons/Menu" :default MenuIcon]
            ["@material-ui/icons/GridOn" :default GridOnIcon]
            ["@material-ui/icons/Apps" :default AppsIcon]
            ["@material-ui/icons/InvertColors" :default InvertColorsIcon]
            ))


;; -----------------------------------------------------------------------
;; MAIN APP BAR
;; -----------------------------------------------------------------------
#_(defn MenuBar [{:keys [classes] :as props}]
    [:> AppBar
     {:position "relative"}
     [:> mui/Toolbar
      [:> mui/Typography
       {:variant "h6"                                       ; "title"
        :color "inherit"}
       "ॐ"]


      [:> mui-icons/Home
       {:type "button"
        :on-click #(rf/dispatch [:set-active-page :home])}]
      [:> mui-icons/Delete]
      [:> mui/Button
       {:variant "contained"
        :class (.-button classes)
        :color "secondary"
        :on-click #(rf/dispatch [:set-active-page :about])}
       "About"]
      ]
     ]
    )


;(.. target e -target -checked)

(defn menu-styles [theme]
  (clj->js {:root {:width "100%"}
            :grow {:flexGrow 1}
            :menuButton {:marginLeft -12
                         :marginRight 20}
            :title {:display "none"}}))


(defn ^private MenuComponent [state]
  [:> Menu {:anchorEl (get @state :anchorEl)
            :anchorOrigin {:vertical "top" :horizontal "right"}
            :transformOrigin {:vertical "top" :horizontal "right"}
            :open (if (get @state :anchorEl) true false)}
   [:> MenuItem {:on-click #(swap! state assoc :clicked :first)} "Profile"]
   [:> MenuItem {:on-click #(swap! state assoc :clicked :second)} "My account"]])

(defn MenuBarComponent [{:keys [classes] :as props}]
  (let [state (r/atom {:anchorEl nil
                       :mobileMoreAnchorEl nil})]
    [:div.container
     [:> AppBar
      {:position "relative"}
      [:> Toolbar
       [:> IconButton {:class (.-menuButton classes) :color "inherit"}
        [:> MenuIcon]]
       [:> IconButton
        {:color "inherit"
         :on-click #(navigate-to! :index)}
        [:> HomeIcon]]
       [:> IconButton
        {:color "inherit"
         :on-click #(navigate-to! :a-items)}
        [:> AppsIcon]]

       [:div {:class (.-grow classes)}]
       [:div {:class (.-sectionDesktop classes)}
        [:> IconButton {:color "inherit"}
         [:> Badge {:badgeContent @(rf/subscribe [:notification-unread-count]) :color "secondary"}
          [:> NotificationsIcon]]]

        [:> IconButton {:color "inherit"}
         [:> InvertColorsIcon]]

        [:> IconButton {:color "inherit"}
         [:> AccountCircle]]
        [:> IconButton {:color "inherit"
                        :on-click #(rf/dispatch [:set-active-page :about])}
         [:> InfoIcon]]]
       ;[MenuComponent state]
       ]]])
  )

(defn MenuBar [{:keys [classes] :as props}]
  [:> (theme/component-with-styles MenuBarComponent menu-styles)])
