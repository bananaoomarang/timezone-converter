(ns tzconverter.frontend.app
  (:require
   [goog.dom :as gdom]
   [reagent.core :as r]
   ["react-dom/client" :refer [createRoot]]
   ["date-fns-tz" :refer [zonedTimeToUtc, utcToZonedTime, format]]))

;; Use now for defaults
(def now-date (.now js/Date))

(def timezones (.supportedValuesOf js/Intl "timeZone"))
;; (def timezones ["America/New_York" "America/Los_Angeles"])
(def user-timezone (-> js/Intl
                       .DateTimeFormat
                       .resolvedOptions
                       .-timeZone))

(defonce selected-date (r/atom (format now-date "yyyy-MM-dd")))
(defonce selected-time (r/atom (format now-date "HH:mm")))
(defonce selected-from-timezone (r/atom user-timezone))
(defonce selected-to-timezone (r/atom "America/Los_Angeles"))

(defn translate-timezone [datetime from-zone to-zone]
  (let [utc-date (zonedTimeToUtc (js/Date. datetime) from-zone)
        zoned-date (utcToZonedTime utc-date to-zone)]
    (format zoned-date "Mo 'of' LLLL yyyy 'at' h:mm b" (clj->js {:timeZone to-zone}))))

(defn select-timezone [label value]
  [:label label
   [:select {:value @value
             :on-change #(reset! value (-> % .-target .-value))}
    (for [timezone timezones]
      ^{:key timezone}
      [:option {:value timezone} timezone])]])

(defn select-time [label date-value time-value]
  [:label label
   [:input {:type "date"
            :value @date-value
            :on-change #(reset! date-value (-> % .-target .-value))}]
   [:input {:type "time"
            :value @time-value
            :on-change #(reset! time-value (-> % .-target .-value))}]])

(defn result []
  [:h3 "Result time is: "
   (translate-timezone
    (str @selected-date " " @selected-time)
    @selected-from-timezone
    @selected-to-timezone)])

(defn app []
  [:div
   [:div
    [select-time "Input Time" selected-date selected-time]
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
