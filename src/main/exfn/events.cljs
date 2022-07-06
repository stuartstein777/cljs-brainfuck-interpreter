(ns exfn.events
  (:require [re-frame.core :as rf]
            [exfn.interpreter :as bf]))

(rf/reg-event-db
 :initialize
 (fn [_ _]
   {:running? false
    :source bf/hello-world}))

;; Handles the event where the user types into the source code textarea
(rf/reg-event-db
 :update-source
 (fn [db [_ source]]
   (assoc db :source source)))

;; Handles the event where the user types into the input textarea
(rf/reg-event-db
 :update-input
 (fn [db [_ input]]
   (assoc db :input input)))

;; Handle the execute click
(rf/reg-event-db
 :execute
 (fn [db [_ _]]
   (let [source (:source db)
         input  (:input db)]
        (assoc db :output (bf/brain-fuck source input)))))

;; Handle the load examples buttons clicks
(rf/reg-event-db
 :load-example
 (fn [db [_ [example input]]]
   (-> db
       (assoc :source example)
       (assoc :input input)
       (assoc :output ""))))