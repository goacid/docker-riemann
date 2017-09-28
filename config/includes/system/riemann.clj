(ns includes.system.riemann ;; nom du namespace lié a la ou on est
  "Monitor disk Usage"
  (:require [riemann.streams :refer :all] ; on importe des namespace
            [riemann.config :refer :all]
            [riemann.test :refer :all]
            [clojure.tools.logging :refer :all]
            ))

(def show-riemann
  (where  (service #"^riemann")
         ;#(info %)
         )); si on écrit (io#(info %)))))) alors les tests n'execute pas la commande (cas ou ca envoi des mail :)
