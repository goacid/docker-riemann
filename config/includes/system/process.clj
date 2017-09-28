(ns includes.system.process ;; nom du namespace lié a la ou on est
  "Monitor Process"
  (:require [riemann.streams :refer :all] ; on importe des namespace
            [riemann.config :refer :all]
            [riemann.test :refer :all]
            [clojure.tools.logging :refer :all]
            ))

(def p_critical 1)
(def process-critical
  (where (and (service #"^process_ps_count_processes")
              ( < (:metric event) p_critical))
         (by [:host :service]
             (throttle 1 30
                       (with  {:description "Alert process down" :state "critical"}
                       #(info %)
                       (tap :process-critical-tap)
                       ); fin du with, on a info et tap en stream enfant du with
                       ) ; fin throttle
             )
         )
  )

(tests
  (deftest check-process-test
    (let [result (inject! [includes.system.process/process-critical]
                          [
                           {:host "localhost"
                            :service "process_ps_count_processes_test1"
                            :time 0
                            :metric 0
                            }]
                          )]
      (is (= (:process-critical-tap result)
             [ {:host "localhost"
                :service "process_ps_count_processes_test1"
                :time 0
                :metric 0
                :description "Alert process down"
                :state "critical"
                }
              ])))))

