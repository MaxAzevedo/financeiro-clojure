(ns unitarios.financeiro.handler-test
  (:require [midje.sweet :refer :all]
            [ring.mock.request :as mock]
            [financeiro.handler :refer :all]
            [financeiro.db :as db]))

(facts "Dá um 'Hellow Wolrd' na rota raiz"
  (fact "o status da resposta é 200"
    (let [response (app (mock/request :get "/"))]
      (:status response) => 200))

  (fact "O texto exibido é 'Hellow World'"
    (let [response (app (mock/request :get "/"))]
      (:body response) => "Hello World"))
)

(facts "Rota inválida"
  (fact "Resposta da rota é 404"
    (let [response (app (mock/request :get "/invalid"))]
      (:status response) => 404))
)

(facts "Registra uma receita no valor de 10"
  (against-background (db/registrar {:valor 10 :tipo "receita"}) => {:id 1 :valor 10 :tipo "receita"})
    (let [response (app (-> (mock/request :post "/transacoes")
                            (mock/json-body {:valor 10 :tipo "receita"})))]
      (fact "O status da respota é 201" (:status response) => 201)
      (fact "O corpo é um JSON com conteúdo e id" (:body response) => "{\"id\":1,\"valor\":10,\"tipo\":\"receita\"}")
    )
)
