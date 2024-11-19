package com.backend.owlfinance.Transaction;

import java.util.PriorityQueue;
import java.util.Comparator;
import com.backend.owlfinance.database.bigtable.BigTableManager;
import com.backend.owlfinance.database.obj.StockPrice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class OrderBook {
    private String symbol;
    // private Transact2PortfolioAdapter t2pAdapter;
    private PriorityQueue<Order> buyOrders;
    private PriorityQueue<Order> sellOrders;
    private BigTableManager database;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:00'Z'");

    public OrderBook(String symbol, BigTableManager database) {
        this.symbol = symbol;
        this.database = database;
        // this.t2pAdapter = t2pAdapter;
        this.buyOrders = new PriorityQueue<>(new BuyOrderComparator());
        this.sellOrders = new PriorityQueue<>(new SellOrderComparator());
    }

    public Order addOrder(Order order) {
        if (order.getType().equals("buy")) {
            boolean isBuyerVerified = checkBalance(order.getUsername(), order.getPrice() * order.getQuantity());
            if (!isBuyerVerified) {
                System.out.println("Failure to add buy order: Insufficient balance");
                return null;
            }
            buyOrders.add(order);
            System.out.println("Buy Order addded: " + order.getId() + " Ticker: " + order.getSymbol() + " Price: " + order.getPrice() + " Quantity: " + order.getQuantity());

        } else if (order.getType().equals("sell")) {
            boolean isSellerVerified = checkShare(order.getUsername(), order.getSymbol(), order.getQuantity());
            if (!isSellerVerified) {
                System.out.println("Failure to add sell order: Insufficient shares");
                return null;
            }
            sellOrders.add(order);
            System.out.println("Sell Order addded: " + order.getId() + " Ticker: " + order.getSymbol() + " Price: " + order.getPrice() + " Quantity: " + order.getQuantity());

        }
        return order;
    }

    public Order updateOrder(Long orderId, Order order) {
        if (order.getType().equals("buy")) {
            buyOrders.remove(order);
            buyOrders.add(order);
            System.out.println("Buy Order updated: " + order.getId() + " Ticker: " + order.getSymbol() + " Price: " + order.getPrice() + " Quantity: " + order.getQuantity());
        } else if (order.getType().equals("sell")) {
            sellOrders.remove(order);
            sellOrders.add(order);
            System.out.println("Sell Order updated: " + order.getId() + " Ticker: " + order.getSymbol() + " Price: " + order.getPrice() + " Quantity: " + order.getQuantity());
        }
        return order;
    }

    public void matchOrders() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime previousBusinessDay = getPreviousBusinessDay(now);
        String currentTimestamp = previousBusinessDay.format(formatter);
        String rowKey = this.symbol + "#" + currentTimestamp;
        StockPrice stockPrice = database.getStockPrice(rowKey);

        while (!buyOrders.isEmpty()) {
            Order buyOrder = buyOrders.peek();
            if (buyOrder.getPrice() > stockPrice.low()) {
                buyOrder = buyOrders.poll();
                executeBuy(buyOrder);
            } else {
                break;
            }
        }

        while (!sellOrders.isEmpty()) {
            Order sellOrder = sellOrders.peek();
            if (sellOrder.getPrice() < stockPrice.high()) {
                sellOrder = sellOrders.poll();
                executeSell(sellOrder);
            } else {
                break;
            }
        }
    }

    public void removeOrder(Order order) {
        if (order.getType().equals("buy")) {
            buyOrders.remove(order);
        } else if (order.getType().equals("sell")) {
            sellOrders.remove(order);
        }
    }

    private boolean checkBalance(String username, double amount) {
        double balance = database.getUserCashBalance(username);
        return balance >= amount;
    }

    private boolean checkShare(String username, String ticker, int amount) {
        // int shares = database.getUserStocks(username, ticker);
        // return shares >= amount;
        return true;
    }

    private void executeBuy(Order buy) {
        // t2pAdapter.buy(buy.getUsername(), buy.getPrice(), buy.getSymbol(), buy.getQuantity());
    }

    private void executeSell(Order sell) {
        // t2pAdapter.sell(sell.getUsername(), sell.getPrice(), sell.getSymbol(), sell.getQuantity());
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

