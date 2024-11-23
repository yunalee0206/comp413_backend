package com.backend.owlfinance.Transaction;

import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import com.backend.owlfinance.Portfolio.InsufficientFundsException;
import com.backend.owlfinance.Portfolio.InsufficientSharesException;
import com.backend.owlfinance.database.bigtable.BigTableManager;
import com.backend.owlfinance.database.obj.Portfolio;
import com.backend.owlfinance.database.obj.StockPrice;
import com.backend.owlfinance.database.obj.Transaction;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

class OrderBook {
    private String symbol;
    private PriorityQueue<Order> buyOrders;
    private PriorityQueue<Order> sellOrders;
    private BigTableManager database;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:00'Z'");

    public OrderBook(String symbol, BigTableManager database) {
        this.symbol = symbol;
        this.database = database;
        this.buyOrders = new PriorityQueue<>(new BuyOrderComparator());
        this.sellOrders = new PriorityQueue<>(new SellOrderComparator());
    }

    /**
     * Place an order into the orderbook
     * @param order
     * @return the order that was placed with the id
     */
    public Order placeOrder(Order order) {
        if (order.getType().equals("buy")) {
            checkBalance(order.getUsername(), order.getPrice() * order.getQuantity());
            buyOrders.add(order);
            System.out.println("Buy Order addded: " + " Ticker: " + order.getSymbol() + " Price: " + order.getPrice() + " Quantity: " + order.getQuantity());

        } else if (order.getType().equals("sell")) {
            checkShare(order.getUsername(), order.getSymbol(), order.getQuantity());
            sellOrders.add(order);
            System.out.println("Sell Order addded: " + " Ticker: " + order.getSymbol() + " Price: " + order.getPrice() + " Quantity: " + order.getQuantity());
        }
        return order;
    }

    // public Order updateOrder(Long orderId, Order order) {
    //     if (order.getType().equals("buy")) {
    //         buyOrders.remove(order);
    //         buyOrders.add(order);
    //         System.out.println("Buy Order updated: " + order.getId() + " Ticker: " + order.getSymbol() + " Price: " + order.getPrice() + " Quantity: " + order.getQuantity());
    //     } else if (order.getType().equals("sell")) {
    //         sellOrders.remove(order);
    //         sellOrders.add(order);
    //         System.out.println("Sell Order updated: " + order.getId() + " Ticker: " + order.getSymbol() + " Price: " + order.getPrice() + " Quantity: " + order.getQuantity());
    //     }
    //     return order;
    // }

    /**
     * Match the orders in the orderbook
     * @return a list of transactions
     */
    public List<Transaction> matchOrders() {

        // LocalDateTime now = LocalDateTime.now();
        // LocalDateTime previousBusinessDay = getPreviousBusinessDay(now);
        // String currentTimestamp = previousBusinessDay.format(formatter);
        // String rowKey = this.symbol + "#" + currentTimestamp;
        // StockPrice stockPrice = BigTableManager.getStockPrice(rowKey);
        // if (stockPrice == null) {
        //     System.out.println("Stock price not found for " + this.symbol);
        //     return new ArrayList<>();
        // }
        StockPrice stockPrice = new StockPrice(this.symbol, 90.0, 110.0, 200, 100.0, 100.0);

        List<Transaction> transactions = new ArrayList<>();
        while (!buyOrders.isEmpty()) {
            Order buyOrder = buyOrders.peek();
            if (buyOrder.getPrice() > stockPrice.low()) {
                buyOrder = buyOrders.poll();
                transactions.add(execute(buyOrder));
            } else {
                break;
            }
        }

        while (!sellOrders.isEmpty()) {
            Order sellOrder = sellOrders.peek();
            if (sellOrder.getPrice() < stockPrice.high()) {
                sellOrder = sellOrders.poll();
                transactions.add(execute(sellOrder));
            } else {
                break;
            }
        }
        return transactions;
    }

    /**
     * Remove an order from the orderbook
     * @param order
     */
    public void removeOrder(Order order) {
        if (order.getType().equals("buy")) {
            buyOrders.remove(order);
        } else if (order.getType().equals("sell")) {
            sellOrders.remove(order);
        }
    }

    /**
     * Check if the user has enough balance to place an order
     * @param username
     * @param amount
     */
    private void checkBalance(String username, double amount) {
        System.out.println("Checking Balance for " + username + ": " + amount);
        double balance = database.getUserCashBalance(username);
        if (balance < amount) {
            throw new InsufficientFundsException("Insufficient balance for placing order");
        }
    }

    /**
     * Check if the user has enough shares to place an order
     * @param username
     * @param ticker
     * @param amount
     */
    private void checkShare(String username, String ticker, int amount) {
        System.out.println("Checking Share for " + username + " " + ticker + " " + amount);
        List<Portfolio> portfolio = database.getPortfolioRowsByUserAndStock(username, ticker);
        int totalShares = portfolio.stream()
            .mapToInt(Portfolio::numShares)
            .sum();
        if (totalShares < amount) {
            throw new InsufficientSharesException("Insufficient shares for placing order");
        }
    }

    /**
     * Execute an order, update the user's cash balance and stocks
     * @param order
     * @return the transaction
     */
    private Transaction execute(Order order) {
        System.out.println("Executing Order: " + order.toString());
        String uuid = UUID.randomUUID().toString();
        Transaction transaction = new Transaction(
            order.getUsername(),
            order.getType(),
            order.getSymbol(),
            order.getQuantity(),
            order.getPrice(),
            uuid
        );

        database.createTransaction(transaction);
        return transaction;
        // TODO: Update user cash balance and user stocks
    }




    private LocalDateTime getPreviousBusinessDay(LocalDateTime dateTime) {
        switch (dateTime.getDayOfWeek()) {
            case MONDAY:
                return dateTime.minusDays(3); // Roll back to Friday
            case SUNDAY:
                return dateTime.minusDays(2); // Roll back to Friday
            default:
                return dateTime.minusDays(1); // Roll back to the previous day
        }
    }

    private class BuyOrderComparator implements Comparator<Order> {
        @Override
        public int compare(Order o1, Order o2) {
            if (o1.getPrice() == o2.getPrice()) {
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }
            return Double.compare(o2.getPrice(), o1.getPrice());
        }
    }
    
    private class SellOrderComparator implements Comparator<Order> {
        @Override
        public int compare(Order o1, Order o2) {
            if (o1.getPrice() == o2.getPrice()) {
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }
            return Double.compare(o1.getPrice(), o2.getPrice());
        }
    }
}

