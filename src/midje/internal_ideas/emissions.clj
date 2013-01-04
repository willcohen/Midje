(ns ^{:doc "Emissions change global state: the output and the pass/fail record."}
  midje.internal-ideas.emissions
  (:require [midje.ideas.reporting.report :as report]
            [midje.clojure-test-facade :as ctf]
            [midje.ideas.reporting.levels :as levelly]
            [midje.internal-ideas.state :as state]))

(defn forget-complete-past []
  (state/reset-output-counters!)
  (ctf/zero-counters)
  (reset! levelly/last-namespace-shown nil))

;; TODO: For the time being, this includes clojure.test failures
;; Once Midje emissions are completely separated from clojure.test
;; reporting, that can go away.
(defn midje-failures []
  (+ (state/output-counters:midje-failures) (:fail (ctf/counters))))

(alter-var-root #'state/emission-functions 
                (constantly {:pass (fn []
                                     (state/output-counters:inc:midje-passes!)
                                     (ctf/note-pass))}))

(defmacro make [symbol]
  `(defn ~symbol [& args#]
     (apply (~(keyword symbol) state/emission-functions) args#)))
       
(make pass)
