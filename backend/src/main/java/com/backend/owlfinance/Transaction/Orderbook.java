// package com.backend.owlfinance.Transaction;

// import java.util.concurrent.ConcurrentHashMap;
// import java.util.concurrent.ConcurrentMap;

// public class OrderBook {
//     private final ConcurrentMap<String, ConcurrentMap<Long, Order>> buyOrders;
//     private final ConcurrentMap<String, ConcurrentMap<Long, Order>> sellOrders;

//     public OrderBook() {
//         this.buyOrders = new ConcurrentHashMap<>();
//         this.sellOrders = new ConcurrentHashMap<>();
//     }

//     public void addOrder(Order order) {
//         ConcurrentMap<String, ConcurrentMap<Long, Order>> orders = 
//             order.getType().equalsIgnoreCase("buy") ? buyOrders : sellOrders;
        
//         orders.computeIfAbsent(order.getSymbol(), k -> new ConcurrentHashMap<>())
//               .put(order.getId(), order);
//     }

//     public void removeOrder(Order order) {
//         ConcurrentMap<String, ConcurrentMap<Long, Order>> orders = 
//             order.getType().equalsIgnoreCase("buy") ? buyOrders : sellOrders;
        
//         orders.getOrDefault(order.getSymbol(), new ConcurrentHashMap<>())
//               .remove(order.getId());
//     }

//     // Getter methods for buyOrders and sellOrders
//     // ...
// }