To run the app, open the terminal and type these commands:

cd into the nonrest directory:
`cd tut-rest/nonrest`

Build the code using maven:
`mvn clean spring-boot:run`

This runs the app at `https://localhost:8080`.

From a separate terminal, make curl commands to call the API:

Get all stocks:
`curl -v localhost:8080/stocks`

Get a specific stock by ID:
`curl -v localhost:8080/stocks/<id_num>`

Post a new stock record:
`curl -X POST localhost:8080/stocks -H 'Content-type:application/json' -d '{"ticker": <ticker-symbol>, "price": <price>}'`