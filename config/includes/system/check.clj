(ns includes.system.check ;; nom du namespace lié a la ou on est
  "Monitor disk Usage"
  (:require [riemann.streams :refer :all] ; on importe des namespace
	    [riemann.config :refer :all]
	    [riemann.test :refer :all]
	    [clojure.tools.logging :refer :all]
	    ))

(defn check-threshold
  "Check against"
  [servicename threshold]
  (where (and (service servicename
	      ( over (:metric event) threshold)))
	 (by [:host]
		       (tap :check-th-tap)
		       #(info %)))); si on écrit (io#(info %)))))) alors les tests n'execute pas la commande (cas ou ca envoi des mail :)
(tests
  (deftest check-th-test
    (let [result (inject! [(includes.system.check/check-threshold "disk" 10)]
			  [
			   {:host "localhost"
			    :service "disk"
			    :time 0
			    :metric 100
			    }]
			  )]
      (is (= (:check-th-tap result)
	     [ {:host "localhost"
		:service "disk"
		:time 0
		:metric 10}
	      ])))))
