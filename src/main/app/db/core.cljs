(ns app.db.core
  (:require [clojure.spec.alpha :as s]))

;; spec of app-db
(s/def ::counter number?)
(s/def ::app-db
  (s/keys :req-un [::counter]))

;; initial state of app-db
(defonce app-db {:counter 0
                 :current-theme :default-theme})
