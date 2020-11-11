(ns aceitacao.financeiro.filtros-aceitacao-test
  (:require [midje.sweet :refer :all]
            [cheshire.core :as json]
            [financeiro.db :as db]
            [clj-http.client :as http]
            [aceitacao.financeiro.auxiliares :refer :all])
)

(def transacoes-aleatorias '({:valor 7.0M :tipo "despesa" :rotulos ["sorvete" "entretenimento"]}
                             {:valor 88.0M :tipo "despesa" :rotulos ["livro" "educação"]}
                             {:valor 106.0M :tipo "despesa" :rotulos ["curso" "educação"]}
                             {:valor 8000.0M :tipo "receita" :rotulos ["salário"]}))

(against-background [(before :facts [(iniciar-servidor porta-padrao) (db/limpar)])
                     (after :facts (parar-servidor))]

  (fact "Não existem receitas" :aceitacao
    (json/parse-string (conteudo "/receitas") true) => {:transacoes '()}
  )

  (fact "Não existem despesas" :aceitacao
    (json/parse-string (conteudo "/despesas") true) => {:transacoes '()}
  )

  (fact "Não existem transacoes" :transacoes
    (json/parse-string (conteudo "/transacoes") true) => {:transacoes '()}
  )

  (against-background [(before :facts (doseq [transacao transacoes-aleatorias] (db/registrar transacao)))
                       (after :facts (db/limpar))]

    (fact "Existem 3  despesas" :aceitacao
          (count (:transacoes (json/parse-string (conteudo "/despesas") true))) => 3
          )

    (fact "Existe 1 receita" :aceitacao
          (count (:transacoes (json/parse-string (conteudo "/receitas") true))) => 1
          )

    (fact "Existe 1 receita" :aceitacao
          (count (:transacoes (json/parse-string (conteudo "/transacoes") true))) => 4
          )

    (fact "Existe 1 receita  com rótulo 'salário'" :bla
          (count (:transacoes (json/parse-string (conteudo "/transacoes?rotulos=salário") true))) => 1)

    (fact "Existem 2 despesas com o rótulo 'livro' e 'curso'" :bla
          (count (:transacoes (json/parse-string (conteudo "/transacoes?rotulos=livro&rotulos=curso") true))) => 2)

    (fact "Existem 2 despesas para o rótulo 'educação'" :bla
          (count (:transacoes (json/parse-string (conteudo "/transacoes?rotulos=educação") true))) => 2)
  )
)

