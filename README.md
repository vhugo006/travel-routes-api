# Travel Route Api

A aplicação representa uma interface REST utilizada para o cadastro e consulta de melhores rotas (mais baratas) para viagem.

## Como executar 

A aplicação está empacotada em um arquivo .war e tem o Tomcat 8 embarcado. Não é necessário a instalação de nenhum
 servidor de aplicação. Você pode executá-la utilizando o comando  ```java -jar```.
 
* Para executar a aplicação é necessário ter o JDK 1.8 e Maven 3.x
* Você pode empacotar a aplicação executando o comando ```mvn clean package``` na raiz do projeto
* Ao finalizar o empacotamento com sucesso, você pode executar o serviço executando um dos comandos abaixo:
```
        java -jar -Dspring.profiles.active=test target/travel-routes-api-1.0-SNAPSHOT.war
ou
        mvn spring-boot:run -Drun.arguments="spring.profiles.active=test"
```
* Verifique o console para se certificar que nenhuma exceção foi lançada durante a inicialização da aplicação

Uma vez que a aplicação foi inicializada, você deverá ver um log parecido com o abaixo:

```
2020-06-04 18:45:45.280  INFO 1287 --- [  restartedMain] br.com.bexs.ApplicationKt                : Started ApplicationKt in 10.068 seconds (JVM running for 10.429)
```

## Sobre o serviço

O serviço é um simples serviço REST para dar suporte à aplicação Kotlin standalone que retorna os dados formatados 
de rotas de viagens mais baratas independente do número de conexões. O serviço utiliza um banco de dados em memória (H2) 
para armazenar os dados. Se der tudo certo com as configurações do banco de dados, você poderá chamar os endpoints 
definidos em ```br.com.bexs.api.RouteController``` **porta 8080**.

 
Abaixo temos algumas informações a respeito da aplicação: 

* A aplicação tem integração total com Spring Framework: inversão de controle, injeção de dependências etc.
* Ela foi empacotada como a war file com container embarcado (tomcat 8): Não é necessário a instalação de um container 
separado. Utilize apenas o comando ``java -jar`` para executar a aplicação.
* O serviço suporta requisições e respostas JSON
* A aplicação tem um controller que captura as exceções lançadas pela aplicação e mapeia para uma resposta adequada
 e com detalhes da exceção no body. Ver ```br.com.bexs.api.ExceptionController```
* Utiliza *Spring Data* para acesso a camda de dados. O framework possui integração com JPA/Hibernate e permite a 
partir de uma simples configuração o acesso aos dados. 
* Serviço auto documentado utilizando anotações do Swagger2.

Abaixo estão os endpoints que você pode chamar:

### Criar uma conexão

```
POST /travel-routes/v1/routes
Accept: application/json
Content-Type: application/json

{
  "cost": 10.50,
  "from": "BEL",
  "to": "BSB"
}

RESPONSE: HTTP 201 (Created)
Location header: http://localhost:8080/travel-routes/v1/routes/8
```

### Consultar a uma rota por ID

```
GET /travel-routes/v1/routes/1
Accept: application/json
Content-Type: application/json

RESPONSE: 
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Tue, 09 Jun 2020 13:58:57 GMT

{
  "from": "GRU",
  "to": "SCL",
  "cost": 20
}
```

### Atualizar o custo de uma rota

```
PUT /travel-routes/v1/routes/1
Accept: application/json
Content-Type: application/json

{
  "cost": 20.00
}

RESPONSE: HTTP 200 (OK)
```

### Excluir rota

```
DELETE /travel-routes/v1/routes/1
Accept: application/json
Content-Type: application/json

RESPONSE: HTTP 200 (OK)
```

### Consultar a melhor rota de viagem

```
GET /travel-routes/v1/routes/from/GRU/to/CDG
Accept: application/json
Content-Type: application/json

RESPONSE: HTTP 200 (OK)

{
  "routes": [
    {
      "from": "GRU",
      "to": "SCL",
      "cost": 20
    },
    {
      "from": "SCL",
      "to": "ORL",
      "cost": 20
    },
    {
      "from": "ORL",
      "to": "CDG",
      "cost": 5
    }
  ],
  "totalCost": 45
}
```

### Consultar a melhor rota de viagem
```
GET /travel-routes/v1/routes?page=1&size=2
Accept: application/json
Content-Type: application/json

RESPONSE: 
HTTP/1.1 200 
Link: <http://localhost:8080?page=2&size=2>; rel="next", <http://localhost:8080?page=0&size=2>; rel="prev", <http://localhost:8080?page=0&size=2>; rel="first", <http://localhost:8080?page=4&size=2>; rel="last"
Content-Type: application/json
Transfer-Encoding: chunked
Date: Tue, 09 Jun 2020 13:27:07 GMT

{
  "routes": [
    {
      "from": "GRU",
      "to": "SCL",
      "cost": 20
    },
    {
      "from": "SCL",
      "to": "ORL",
      "cost": 20
    },
    {
      "from": "ORL",
      "to": "CDG",
      "cost": 5
    }
  ],
  "totalCost": 45
}
```

### Para visualizar a documentação gerada pelo Swagger

Execute a aplicação e acesse ```localhost:8080/swagger-ui.html```

# Add route curl examples
```   
curl -X POST -i -d '{ "from": "GRU", "to": "BRC", "cost": 10}' -H 'Content-Type: application/json' http://localhost:8080/travel-routes/v1/routes
curl -X POST -i -d '{ "from": "BRC", "to": "SCL", "cost": 5}' -H 'Content-Type: application/json' http://localhost:8080/travel-routes/v1/routes
curl -X POST -i -d '{ "from": "GRU", "to": "CDG", "cost": 75}' -H 'Content-Type: application/json' http://localhost:8080/travel-routes/v1/routes
curl -X POST -i -d '{ "from": "GRU", "to": "SCL", "cost": 20}' -H 'Content-Type: application/json' http://localhost:8080/travel-routes/v1/routes
curl -X POST -i -d '{ "from": "GRU", "to": "ORL", "cost": 56}' -H 'Content-Type: application/json' http://localhost:8080/travel-routes/v1/routes
curl -X POST -i -d '{ "from": "ORL", "to": "CDG", "cost": 5}' -H 'Content-Type: application/json' http://localhost:8080/travel-routes/v1/routes
curl -X POST -i -d '{ "from": "SCL", "to": "ORL", "cost": 20}' -H 'Content-Type: application/json' http://localhost:8080/travel-routes/v1/routes
```
# Get best travel route curl example
```
curl -i -H "Content-Type: application/json" --request GET  http://localhost:8080/travel-routes/v1/routes/from/GRU/to/CDG
```