(ns unitarios.financeiro.handler-test
  (:require [midje.sweet :refer :all]
            [ring.mock.request :as mock]
            [financeiro.handler :refer :all]
            [financeiro.db :as db]
            [cheshire.core :as json]
  )
)

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
      (fact "O corpo é um JSON com conteúdo e id" (:body response) =>
            "{\"id\":1,\"valor\":10,\"tipo\":\"receita\"}")
    )
)

(facts "Saldo incial é 0"
  (against-background [(json/generate-string {:saldo 0}) => "{\"saldo\":0}"
                       (db/saldo) => 0])

  (let [response (app (mock/request :get "/saldo"))]

    (fact "O formato é aplication/json"
          (get-in response [:headers "Content-type"]) => "application/json; charset=utf-8")

    (fact "O status da resposta é 200" (:status response) => 200)

    (fact "JSon com chave 'saldo' e valor 0" (:body response) => "{\"saldo\":0}")
  )
)

(facts "Existe rota para lidar  com filtro  de transação por tipo"
  (against-background [(db/transacoes-do-tipo "receita") => '({:id 1 :valor 2000 :tipo "receita"})
                       (db/transacoes-do-tipo "despesa") => '({:id 2 :valor 89 :tipo "despesa"})
                       (db/transacoes) => '({:id 1 :valor 2000 :tipo "receita"}
                                            {:id 2 :valor 89 :tipo "despesa"})]

    (fact "Filtro por receita"
      (let [response (app (mock/request :get "/receitas"))]
        (:status response) => 200
        (:body response) => (json/generate-string {:transacoes '({:id 1 :valor 2000 :tipo "receita"})})
      )
    )

    (fact "Filtro por despesa"
      (let [response (app (mock/request :get "/despesas"))]
        (:status response) => 200
        (:body response) => (json/generate-string {:transacoes '({:id 2 :valor 89 :tipo "despesa"})})
      )
    )

    (fact "Sem filtro"
      (let [response (app (mock/request :get "/transacoes"))]
        (:status response) => 200
        (:body response) => (json/generate-string {:transacoes '({:id 1 :valor 2000 :tipo "receita"}
                                                                 {:id 2 :valor 89 :tipo "despesa"})})
      )
    )
  )
)
