(ns app.ui.view)


;;;;---------------------------------

(defmulti page-contents
  "Multimethod to create different pages.

   In order to create a new page/screen/view, use `defmethod` to add multimethods
   for page-contents. Example:

   ```
   (defmethod page-contents :a-item []
     (fn []
       (let [routing-data (session/get :route)
             item (get-in routing-data [:route-params :item-id])]
         [:span.main
          [:h1 (str \"Item \" item \" of A\")]
          [:p [:a {:href (bidi/path-for app-routes :items)} \"Back to the list of A-items\"]]])))

   (defmethod page-contents :b-item []
     (fn []
       (let [routing-data (session/get :route)
             item (get-in routing-data [:route-params :item-id])]
         [:span.main
          [:h1 (str \"Item \" item \" of B\")]
          [:p [:a {:href (bidi/path-for app-routes :items)} \"Back to the list of B-items\"]]])))
   ```
  "
  identity)
