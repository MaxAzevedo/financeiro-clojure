(ns aceitacao.financeiro.saldo-aceitacao-test
  (:require [midje.sweet :refer :all]
            [aceitacao.financeiro.auxiliares :refer :all]
            [cheshire.core :as json]
            [clj-http.client :as http]
            [financeiro.db :as db])
)

(against-background [ (before :facts [(iniciar-servidor porta-padrao) (db/limpar)])
                      (after :facts (parar-servidor))]
  (fact "Saldo incial é 0" :aceitacao
    (json/parse-string (conteudo "/saldo") true) => {:saldo 0}
  )

  (fact "O saldo é 10 quando a única transação é uma receita de 10" :aceitacao
        (:status (http/post (endereco-para "/transacoes" ) {:content-type
                                               :json
                                               :body (json/generate-string {:id 1 :valor 10 :tipo "receita"})})) => 201
  )

  (fact "O saldo é 1000" :aceitacao
    (http/post (endereco-para "/transacoes")
               {:content-type :json :body (json/generate-string {:valor 200 :tipo "receita"})})
    (json/parse-string (conteudo "/saldo") true) => {:saldo 200}
  )

  (fact "O saldo 1000 quando criamos duas receitas de 2000 e uma despesa da 3000" :aceitacao
    (http/post (endereco-para "/transacoes") (receita 2000))
    (http/post (endereco-para "/transacoes") (receita 2000))
    (http/post (endereco-para "/transacoes") (despesa 3000))

    (json/parse-string (conteudo "/saldo") true) => {:saldo 1000}
  )


)