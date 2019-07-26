(ns app.db.events
  (:require
    [app.util :refer [non-neg-minus]]
    [re-frame.core :refer [reg-event-db after reg-sub]]
    [clojure.spec.alpha :as s]
    [app.db.core :as db :refer [app-db]]
    [day8.re-frame.tracing :refer-macros [fn-traced]]
    ))

;; -- Interceptors ------------------------------------------------------------
;;
;; See https://github.com/Day8/re-frame/blob/master/docs/Interceptors.md
;;
(defn check-and-throw
  "Throw an exception if db doesn't have a valid spec."
  [spec db [event]]
  (when-not (s/valid? spec db)
    (let [explain-data (s/explain-data spec db)]
      (throw (ex-info (str "Spec check after " event " failed: " explain-data) explain-data)))))

(def validate-spec
  (if goog.DEBUG
    (after (partial check-and-throw ::db/app-db))
    []))

;; -- Handlers --------------------------------------------------------------

(reg-event-db
 :initialize-db
 validate-spec
 (fn [_ _]
   app-db))

(reg-event-db
 :inc-counter
 validate-spec
 (fn [db [_ _]]
   (update db :counter inc)))

;;; ------------------------------------------------------------
;;; Notification related
;;; ------------------------------------------------------------
(reg-event-db
  :set-notification-unread-count
  ;; handler function (db, event) -> db
  (fn [db [_ notification-unread-count]]
    (assoc db :notification-unread-count notification-unread-count)))

(reg-event-db
  :add-notification-unread-count
  ;; handler function (db, event) -> db
  (fn [db [_ & inc-by]]
    (update-in db [:notification-unread-count] (fnil + 0) (or inc-by 1))))

(reg-event-db
  :subtract-notification-unread-count
  ;; handler function (db, event) -> db
  (fn [db [_ & dec-by]]
    (update-in db [:notification-unread-count] (fnil non-neg-minus 0) (or dec-by 1))))

(reg-sub
  :notification-unread-count
  (fn [db _]
    (or (:notification-unread-count db) 0)))
