// package com.example.tradeengine.service;

// import com.example.tradeengine.model.Order;
// import com.example.tradeengine.model.OrderBook;
// import org.springframework.stereotype.Component;

// import java.util.concurrent.ConcurrentHashMap;

// @Component
// public class OrderBookManager {
//     private final ConcurrentHashMap<String, OrderBook> orderBooks = new ConcurrentHashMap<>();

//     public void addOrder(Order order) {
//         OrderBook orderBook = orderBooks.computeIfAbsent(order.getSymbol(), OrderBook::new);
//         orderBook.addOrder(order);
//     }

//     public void removeOrder(Order order) {
//         OrderBook orderBook = orderBooks.get(order.getSymbol());
//         if (orderBook != null) {
//             orderBook.removeOrder(order);
//         }
//     }

//     public OrderBook getOrderBook(String symbol) {
//         return orderBooks.get(symbol);
//     }
// }