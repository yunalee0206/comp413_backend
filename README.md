To run the app, open the terminal and type these commands:

`cd tut-rest/nonrest`
`mvn clean spring-boot:run`

This runs the app at `https://localhost:8080`.

From a separate terminal, make curl commands to call the API:

Get all stocks:
`curl -v localhost:8080/stocks`

Get a specific stock by ID:
`curl -v localhost:8080/stocks/<id_num>`