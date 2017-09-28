(ns includes.system.disk ;; nom du namespace lié a la ou on est
  "Monitor disk Usage"
  (:require [riemann.streams :refer :all] ; on importe des namespace
	    [riemann.config :refer :all]
	    [riemann.test :refer :all]
	    [clojure.tools.logging :refer :all]
	    ))

(def disk-free-threshold 80)
(def check-disk
  (where (and (service "disk-percent")
	      ( > (:metric event) disk-free-threshold))
	 (by [:host]
	     (throttle 1 30
		       (tap :check-disk-tap)
		       #(info %))))); si on écrit (io#(info %)))))) alors les tests n'execute pas la commande (cas ou ca envoi des mail :)

(tests
  (deftest check-disk-test
    (let [result (inject! [includes.system.disk/check-disk]
			  [
			   {:host "localhost"
			    :service "disk-percent"
			    :time 0
			    :metric 100
			    }]
			  )]
      (is (= (:check-disk-tap result)
	     [ {:host "localhost"
		:service "disk-percent"
		:time 0
		:metric 100}
	      ])))))
