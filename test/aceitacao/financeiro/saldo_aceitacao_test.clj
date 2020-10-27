(ns aceitacao.financeiro.saldo-aceitacao-test
  (:require [midje.sweet :refer :all]
            [aceitacao.financeiro.auxiliares :refer :all]
            [cheshire.core :as json])
)

(against-background [ (before :facts (iniciar-servidor porta-padrao))
                      (after :facts (parar-servidor)) ]
  (fact "Saldo incial é 0" :aceitacao
    (conteudo "/saldo") => "0"
  )
)