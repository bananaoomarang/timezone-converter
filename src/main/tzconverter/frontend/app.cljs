(ns tzconverter.frontend.app
  (:require
   [goog.dom :as gdom]
   [reagent.core :as r]
   ["react-dom/client" :refer [createRoot]]))

(defn app []
  [:h1 "hello"])

(defonce root (createRoot (gdom/getElement "root")))

(defn init []
  (.render root (r/as-element [app])))

;; Hot reloading reagent app with shadow-cljs
(defn ^:dev/after-load re-render
  []
  (init))
