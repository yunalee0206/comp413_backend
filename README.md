# COMP 413 Backend

**NOTE: To deploy the app, the code was modified so you no longer need to cd into the backend directory**

To run the app, open the terminal and type these commands:

Build and run the code using maven:
`mvn spring-boot:run`

If that doesn't work, you may need to clean and rebuild:
`mvn clean spring-boot:run`

This runs the app at `https://localhost:8080`.

You can see the frontend for the API at http://localhost:8080/swagger-ui/index.html

From a separate terminal, make curl commands to call the API:

Get all stocks:
`curl -v localhost:8080/stocks`

Get a specific stock by ticker, date, and time:
`curl -v localhost:8080/stocks/{ticker}/{date}/{time}`

Date format example: 2024-11-19

The date should be at least one market day (M-F) before today's date.

Time format example: 13:56:00

The time is by minutes, ending in :00. 

Place an buy/sell order: 
Note: Order type is specified in the url and others in the request body  
curl -X POST "http://localhost:8080/orders?type=buy" -H 'Content-Type: application/json' -d '{"symbol": "AAPL", "quantity": 100, "price": 100.00, "timestamp": "2023-06-15T10:30:00Z"}'

curl -X POST "http://localhost:8080/orders?type=sell" -H 'Content-Type: application/json' -d '{"symbol": "AAPL", "quantity": 50, "price": 80.00, "timestamp": "2023-06-16T10:30:00Z"}'

curl -X POST "http://localhost:8080/orders?type=sell" -H 'Content-Type: application/json' -d '{"symbol": "AAPL", "quantity": 50, "price": 120.00, "timestamp": "2023-06-16T10:30:00Z"}'

curl -X POST "http://localhost:8080/orders?type=sell" -H 'Content-Type: application/json' -d '{"symbol": "ABCD", "quantity": 100, "price": 100.00, "timestamp": "2023-06-15T10:30:00Z"}'

Find the CURL commands for the deployed version [here](https://docs.google.com/document/d/1IbzVE1KdvjlNzx5qLsn4F_ikE0jlvBWcwUGQZISnEcQ/edit?tab=t.0).
