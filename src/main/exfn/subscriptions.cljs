(ns exfn.subscriptions
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :source
 (fn [db _] (db :source)))

(rf/reg-sub
 :input
 (fn [db _] (db :input)))

(rf/reg-sub
 :output
 (fn [db _] (db :output)))

(rf/reg-sub
 :running?
 (fn [db _] (db :running?)))
