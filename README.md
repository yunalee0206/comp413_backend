# COMP 413 Backend

To run the app, open the terminal and type these commands:

cd into the backend directory:
`cd backend`

Build the code using maven:
`mvn clean spring-boot:run`

This runs the app at `https://localhost:8080`.

You can see the frontend for the API at http://localhost:8080/swagger-ui/index.html

From a separate terminal, make curl commands to call the API:

Get all stocks:
`curl -v localhost:8080/stocks`

Get a specific stock by ID:
`curl -v localhost:8080/stocks/<id_num>`

Post a new stock record:
`curl -X POST localhost:8080/stocks -H 'Content-type:application/json' -d '{"ticker": <ticker-symbol>, "price": <price>}'`

Place an buy/sell order: 
Note: Order type is specified in the url and others in the request body  
curl -X POST "http://localhost:8080/orders?type=buy" -H 'Content-Type: application/json' -d '{"symbol": "AAPL", "quantity": 100, "price": 100.00, "timestamp": "2023-06-15T10:30:00Z"}'

curl -X POST "http://localhost:8080/orders?type=sell" -H 'Content-Type: application/json' -d '{"symbol": "AAPL", "quantity": 50, "price": 80.00, "timestamp": "2023-06-16T10:30:00Z"}'

curl -X POST "http://localhost:8080/orders?type=sell" -H 'Content-Type: application/json' -d '{"symbol": "AAPL", "quantity": 50, "price": 120.00, "timestamp": "2023-06-16T10:30:00Z"}'

curl -X POST "http://localhost:8080/orders?type=sell" -H 'Content-Type: application/json' -d '{"symbol": "ABCD", "quantity": 100, "price": 100.00, "timestamp": "2023-06-15T10:30:00Z"}'

