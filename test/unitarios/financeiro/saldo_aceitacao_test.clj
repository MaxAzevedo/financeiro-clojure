(ns unitarios.financeiro.saldo-aceitacao-test
  (:require [midje.sweet :refer :all]
            [financeiro.handler :refer [app]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.mock.request :as mock]
            [clj-http.client :as http])
)

(def servidor (atom nil))

(defn iniciar-servidor [porta]
  (swap! servidor (fn [_] (run-jetty app {:port porta :join? false})))
)

(defn parar-servidor []
  (.stop @servidor)
)
(facts "Saldo incial é 0"
  (let [response (app (mock/request :get "/saldo"))]
    (fact "O status da resposta é 200"
      (:status response) => 200
    )
    (fact "A resposta é 0"
      (:body response) => "0"
    )
  )
)