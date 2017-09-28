(ns includes.app.elasticsearch ;; nom du namespace lié a la ou on est
  "ES"
  (:require [riemann.streams :refer :all] ; on importe des namespace
            [riemann.config :refer :all]
            [riemann.test :refer :all]
            [clojure.tools.logging :refer :all]
            ))

(def elasticsearch-document-count-rate
  (where (service "elasticsearch_document_count")
         (by [:datacenter_name]
             (ddt ; calcul la diff metric / entre 2 valeur de temps et met a jour le champs metric
                  (with  { :service "elasticsearch_document_count_rate" }
                    #(info %)
                    (tap :es-tap)
                    ); fin du with, on a info et tap en stream enfant du with
                  )
             )
         )
  )


  (tests
    (deftest check-es-test
      (let [result (inject! [includes.app.elasticsearch/elasticsearch-document-count-rate]
                            [
                             {:host "localhost"
                              :service "elasticsearch_document_count"
                              :time 1
                              :metric 100
                              }
                             {:host "localhost"
                              :service "elasticsearch_document_count"
                              :time 2
                              :metric 200
                              }
                             ]
                            )]
        (is (= (:es-tap result)
               [ 
                {:host "localhost"
                  :service "elasticsearch_document_count_rate"
                  :time 2
                  :metric 100
                  }
                ])))))

