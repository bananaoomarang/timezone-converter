(ns tzconverter.frontend.app
  (:require
   [goog.dom :as gdom]
   [reagent.core :as r]
   ["react-dom/client" :refer [createRoot]]
   ["date-fns-tz" :refer [zonedTimeToUtc, utcToZonedTime, format]]))

;; Use now for defaults
(def now-date (.now js/Date))

(def timezones (.supportedValuesOf js/Intl "timeZone"))
(def user-timezone
  (-> js/Intl
      .DateTimeFormat
      .resolvedOptions
      .-timeZone))

(defonce selected-time (r/atom (format now-date "yyyy-MM-dd'T'HH:mm")))
(defonce selected-from-timezone (r/atom user-timezone))
(defonce selected-to-timezone (r/atom "America/Los_Angeles"))

;;
;; Returns string for valid inputs
;; Or nil for invalid ones
;;
(defn translate-timezone [datetime from-zone to-zone]
  (let [utc-date (zonedTimeToUtc (js/Date. datetime) from-zone)
        zoned-date (utcToZonedTime utc-date to-zone)]
    (try
      (format zoned-date "do 'of' LLLL yyyy 'at' h:mm b" (clj->js {:timeZone to-zone}))
      (catch :default _
        nil))))

(defn select-timezone [label value]
  [:label label
   [:select {:value @value
             :on-change #(reset! value (-> % .-target .-value))}
    (for [timezone timezones]
      ^{:key timezone}
      [:option {:value timezone} timezone])]])

(defn select-time [label value]
  [:label label
   [:input {:type "datetime-local"
            :value @value
            :on-change #(reset! value (-> % .-target .-value))}]])

(defn result []
  (let [result-time (translate-timezone
                     @selected-time
                     @selected-from-timezone
                     @selected-to-timezone)]
    [:div.result
     "Result time is: "

     (if result-time
       [:b result-time]
       [:b.error "Invalid time"])]))

(defn app []
  [:div
   [:div.inputs
    [select-time "Input Time" selected-time]
    [select-timezone "Input Timezone" selected-from-timezone]
    [select-timezone "Output Timezone" selected-to-timezone]]
   [result]])

(defonce root (createRoot (gdom/getElement "root")))

(defn init []
  (.render root (r/as-element [app])))

;; Hot reloading reagent app with shadow-cljs
(defn ^:dev/after-load re-render
  []
  (init))
