(ns financeiro.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-body ]]
            [cheshire.core :as json]
            [financeiro.db :as db]
            [financeiro.transacoes :as transacoes]))


(defn com-json [conteudo & [status]]
  {:status (or status 200)
   :headers {"Content-type" "application/json; charset=utf-8"}
   :body (json/generate-string conteudo)}
)

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/saldo" [] (com-json {:saldo (db/saldo)}))
  (POST "/transacoes" request
    (if (transacoes/valida? (:body request))
      (-> (db/registrar (:body request))
          (com-json 201))
      (com-json {:mensagem "Requisição inválida"} 422)
    )
  )
  (GET "/transacoes" [] (com-json {:transacoes (db/transacoes)}))
  (GET "/receitas" [] (com-json {:transacoes (db/transacoes-do-tipo "receita")}))
  (GET "/despesas" [] (com-json {:transacoes (db/transacoes-do-tipo "despesa")}))
  (route/not-found "Not Found"))

(def app
  (-> (wrap-defaults app-routes api-defaults)
      (wrap-json-body {:keywords? true :bigdecimals? true})
  )
)
