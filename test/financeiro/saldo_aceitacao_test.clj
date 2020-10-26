(ns financeiro.saldo-aceitacao-test
  (:require [midje.sweet :refer all]
    [financeiro.handler :refer [app]]
            [ring.adapter.jetty :refer [run-jetty]])
)

(def servidor (atom nil))

(defn iniciar-servidor [porta]
  (swap! servidor (fn [_] (run-jetty app {:port porta :join? false})))
)

(defn parar-servidor []
  (.stop @servidor)
)

(fact "Iniciar o servidor"
  (iniciar-servidor 3001)
  (parar-servidor)
)