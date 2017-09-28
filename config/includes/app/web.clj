(ns includes.app.web ;; nom du namespace lié a la ou on est
  "ES"
  (:require [riemann.streams :refer :all] ; on importe des namespace
            [riemann.config :refer :all]
            [riemann.test :refer :all]
            [clojure.tools.logging :refer :all]
            ))

(def api-percentiles
  (percentiles 30 [0.5 0.95 0.99]
               #(info "Percentile :" %)
               )
  )

(def api-rate
  (with { :metric 1
         :service "api_request_rate"}
    (rate 5
          #(info "Rate" %))))

(def api-mean
  (fixed-time-window 30 
                     (smap riemann.folds/mean
                           (index)
                           #(info "Mean" %))))


(def web
  (where (service "api_request")
         (by [:host]
             (tag "api"
                  (index)
                  api-percentiles ; calcul et affiche les percentile via une fonction
                  api-rate
                  api-mean))))
