(ns unitarios.financeiro.saldo-aceitacao-test
  (:require [midje.sweet :refer :all]
            [financeiro.handler :refer [app]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.mock.request :as mock]
            [clj-http.client :as http]
            [cheshire.core :as json])
)

(def servidor (atom nil))

(facts "Saldo incial é 0" (against-background (json/generate-string {:saldo 0}) => "{\"saldo\":0}")
  (let [response (app (mock/request :get "/saldo"))]
    (fact "O status da resposta é 200"
      (:status response) => 200
    )
    (fact "A resposta é {saldo 0}"
      (:body response) => "{\"saldo\":0}"
    )
  )
)