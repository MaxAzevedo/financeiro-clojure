(ns unitarios.financeiro.db-test
  (:require [midje.sweet :refer :all]
            [financeiro.db :as db])
)

(facts "Guarda uma transação do átomo"
  (against-background [(before :facts (db/limpar))]

    (fact "A coleção de transações inicia vazia"
      (count (db/transacoes)) => 0
    )

    (fact "A transação é o primeiro registro"
      (db/registrar {:valor 7 :tipo "receita"}) => {:id 1 :valor 7 :tipo "receita"}
      (count (db/transacoes)) => 1
    )
  )
)

(facts "Calcula o saldo dada uma coleção de transação"
  (against-background [(before :facts (db/limpar))]

    (fact "Saldo é positivo qnd só tem receita"
      (db/registrar {:valor 1 :tipo "receita"})
      (db/registrar {:valor 10 :tipo "receita"})
      (db/registrar {:valor 100 :tipo "receita"})
      (db/registrar {:valor 1000 :tipo "receita"})
      (db/saldo) => 1111
    )

    (fact "Saldo é positivo qnd só tem receita"
      (db/registrar {:valor 2 :tipo "despesa"})
      (db/registrar {:valor 20 :tipo "despesa"})
      (db/registrar {:valor 200 :tipo "despesa"})
      (db/registrar {:valor 2000 :tipo "despesa"})
      (db/saldo) => -2222
    )

    (fact "Saldo é positivo qnd só tem receita"
      (db/registrar {:valor 10 :tipo "receita"})
      (db/registrar {:valor 10 :tipo "despesa"})
      (db/saldo) => 0
    )

  )
)