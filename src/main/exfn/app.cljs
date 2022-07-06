(ns exfn.app
  (:require [reagent.dom :as dom]
            [re-frame.core :as rf]
            [exfn.subscriptions]
            [exfn.events]
            [exfn.interpreter :as bf]))

(defn inputs []
  (let [source @(rf/subscribe [:source])
        input @(rf/subscribe [:input])]
    (js/console.log source)
    [:div.row
     [:div.col-lg-6
      [:p.header ".code"]
      [:textarea.code
       {:on-change #(rf/dispatch-sync [:update-source (-> % .-target .-value)])
        :value source}]]
     [:div.col-lg-6
      [:p.header ".input"]
      [:textarea.input 
       {:on-change #(rf/dispatch-sync [:update-input (-> % .-target .-value)])
        :value input}]]]))

(defn output []
  (let [output @(rf/subscribe [:output])]
    [:div.row
     [:div.col-lg-12
      [:p.header ".output"]
      [:textarea.output
       {:value output
        :readonly true}]]]))

(defn other-projects []
  [:div {:style {:float :right}}
   [:a {:href "https://stuartstein777.github.io/"} "other projects"]])

(defn examples []
  [:div
   [:button.btn-primary.example-btn 
    {:on-click #(rf/dispatch-sync [:load-example [bf/hello-world ""]])}
    "Hello World"]
   [:button.btn-primary.example-btn 
    {:on-click #(rf/dispatch-sync [:load-example [bf/fib ""]])}
    "Fibonacci"]
   [:button.btn-primary.example-btn 
    {:on-click #(rf/dispatch-sync [:load-example [bf/sorter "ZBFJIJQKLAMFMAUBFBXYACED"]])}
    "Sorter"]
   [:button.btn-primary.example-btn 
    {:on-click #(rf/dispatch-sync [:load-example [bf/bf-generator "Enter message to generate here..."]])}
    "Brainfuck generator"]
   [:button.btn-primary.example-btn
    {:on-click #(rf/dispatch-sync [:load-example [bf/reverse-input "ABCDEFGHI"]])}
    "Reverse input"]
   [:div
    [:label "Examples from"]
    [:a {:href "http://brainfuck.org/"} " http://brainfuck.org/"]]])

;; -- App -------------------------------------------------------------------------
(defn app []
  [:div.container
   [:h1 "Brainfuck Interpreter"]
   [:p "Note: Comments not supported yet."]
   [inputs]
   [:div
    [:button.btn-primary
     {:on-click #(rf/dispatch-sync [:execute])}
     "Execute"]]
   [output]
   [examples]
   [other-projects]])

;; -- After-Load --------------------------------------------------------------------
;; Do this after the page has loaded.
;; Initialize the initial db state.
(defn ^:dev/after-load start
  []
  (dom/render [app]
              (.getElementById js/document "app")))

(defn ^:export init []
  (start))

; dispatch the event which will create the initial state. 
(defonce initialize (rf/dispatch-sync [:initialize]))