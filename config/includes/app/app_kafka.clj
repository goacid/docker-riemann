(ns includes.app.app_kafka ;; nom du namespace lié a la ou on est
  "ES"
  (:require [riemann.streams :refer :all] ; on importe des namespace
            [riemann.config :refer :all]
            [riemann.test :refer :all]
            [clojure.tools.logging :refer :all]
            ))

(def topic-msg-in-per-sec
  (where (service "kafka_server_messagesinpersec-m1_rate")
         (by [:datacenter_name :mbean_kafka_topic_keyword]
             (coalesce 5 ; https://mcorbin.fr/posts/09-08-2017-coalesce, pas d'ordre d'envoi.
                       (where  { :service "kafka_server_messagesinpersec-m1_rate" }
                         (smap riemann.folds/sum ; le fait sur le champs metric automatiquement
                               (with  { :host nil :service "kafka_cluster_topic_messagesinpersec_rate_m1" } 
                                 #(info %)
                                 (tap :kk-tap)
                                 ); fin du with, on a info et tap en stream enfant du with
                               )
                         )
                       )
             )
         )
  )

(defn remove-time
  [events]
  (map #(dissoc % :time) events))
; on vire le temps sur une liste d'évenement, d'ou le map qui applique la fonctions a tous les events
(tests
  (deftest check-kk-test
    (let [result (inject! [includes.app.app_kafka/topic-msg-in-per-sec]
                           [
                            {:host "test1" ; premier event envoyé direct, ici pacy, on le retrouve donc dans les resultats attendu
                             :service "kafka_server_messagesinpersec-m1_rate"
                             :time 1
                             :metric 1
                             :datacenter_name "pacy"
                             :mbean_kafka_topic_keyword "metric_system"
                             }
                            {:host "test1"
                             :service "kafka_server_messagesinpersec-m1_rate"
                             :time 1
                             :metric 2 
                             :datacenter_name "noe"
                             :mbean_kafka_topic_keyword "metric_system"
                             }
                            {:host "test2"
                             :service "kafka_server_messagesinpersec-m1_rate"
                             :time 3
                             :metric 200
                             :datacenter_name "noe"
                             :mbean_kafka_topic_keyword "metric_system"
                             }
                            {:host "test2" ; premier event envoyé direct, ici noa, on le retrouve donc dans les résultat attendu
                             :service "kafka_server_messagesinpersec-m1_rate"
                             :time 4
                             :metric 100
                             :datacenter_name "noe"
                             :mbean_kafka_topic_keyword "metric_system"
                             }
                            {:host "test1"
                             :servi3e "kafka_server_messagesinpersec-m1_rate"
                             :time 3
                             :metric 200
                             :datacenter_name "pacy"
                             :mbean_kafka_topic_keyword "metric_system"
                             }
                            {:host "test1"
                             :service "kafka_server_messagesinpersec-m1_rate"
                             :time 4
                             :metric 400
                             :datacenter_name "pacy"
                             :mbean_kafka_topic_keyword "metric_system"
                             }
                            ]
                           )]
          (is (= (includes.app.app_kafka/remove-time (:kk-tap result))
                 [ 
                            {; premier event envoyé direct, ici pacy, on le retrouve donc dans les resultats attendu
                             :service "kafka_cluster_topic_messagesinpersec_rate_m1"
                             :metric 1
                             :datacenter_name "pacy"
                             :mbean_kafka_topic_keyword "metric_system"
                             }
                            {; premier event envoyé direct, ici noa, on le retrouve donc dans les résultat attendu
                             :service "kafka_cluster_topic_messagesinpersec_rate_m1"
                             :metric 2
                             :datacenter_name "noe"
                             :mbean_kafka_topic_keyword "metric_system"
                             }
                             {:service "kafka_cluster_topic_messagesinpersec_rate_m1"
                             :metric 302
                             :datacenter_name "noe"
                             :mbean_kafka_topic_keyword "metric_system"
                             }
                            {; premier event envoyé direct, ici noa, on le retrouve donc dans les résultat attendu
                             :service "kafka_cluster_topic_messagesinpersec_rate_m1"
                             :metric 601
                             :datacenter_name "pacy"
                             :mbean_kafka_topic_keyword "metric_system"
                             }
                  ])))))

