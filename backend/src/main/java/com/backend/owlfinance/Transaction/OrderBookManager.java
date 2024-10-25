package com.backend.owlfinance.Transaction;

import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrderBookManager {
    private final ConcurrentHashMap<String, OrderBook> orderBooks = new ConcurrentHashMap<>();

    public void addOrder(Order order) {
        OrderBook orderBook = orderBooks.computeIfAbsent(order.getSymbol(), s -> new OrderBook());
        orderBook.addOrder(order);
    }

    public void removeOrder(Order order) {
        OrderBook orderBook = orderBooks.get(order.getSymbol());
        if (orderBook != null) {
            orderBook.removeOrder(order);
        }
    }

    public void matchOrders() {
        for (OrderBook o: orderBooks.values()) {
            o.matchOrders();
        }
    }

    public OrderBook getOrderBook(String symbol) {
        return orderBooks.get(symbol);
    }
}