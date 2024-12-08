package com.backend.owlfinance.Transaction;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import com.backend.owlfinance.Portfolio.InsufficientFundsException;
import com.backend.owlfinance.database.bigtable.BigTableManager;
import java.util.*;
import com.backend.owlfinance.database.obj.Transaction;

@Service
public class OrderServiceImpl implements OrderService {


    private BigTableManager database;
    private final ConcurrentHashMap<String, OrderBook> orderBooks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Set<Order>> ongoingTransaction = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Set<Transaction>> executedTransaction = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Double> reservedFunds = new ConcurrentHashMap<>();

    public OrderServiceImpl() throws IOException {
        String projectId = "rice-comp-539-spring-2022";
        String instanceId = "comp-539-bigtable";
        this.database = new BigTableManager(projectId, instanceId);
    }

    @Override
    public synchronized Order placeOrder(String type, Order order) {
        order.setType(type);
        order.setId(UUID.randomUUID().toString());
        
        // For buy orders, check and reserve funds
        if (type.equals("buy")) {
            double orderCost = order.getPrice() * order.getQuantity();
            String username = order.getUsername();
            
            // Get current reserved amount
            double currentReserved = reservedFunds.getOrDefault(username, 0.0);
            double currentBalance = database.getUserCashBalance(username);
            
            // Check if user has enough unreserved funds
            if (currentBalance - currentReserved < orderCost) {
                throw new InsufficientFundsException("Insufficient available balance for order");
            }
            
            // Reserve the funds
            reservedFunds.put(username, currentReserved + orderCost);
        }

        ongoingTransaction.computeIfAbsent(order.getUsername(), k -> ConcurrentHashMap.newKeySet())
                 .add(order);
        OrderBook orderBook = orderBooks.computeIfAbsent(order.getSymbol(), s -> new OrderBook(s, this.database));
        return orderBook.placeOrder(order);
    }

    @Override
    public void matchOrders() {
        List<Transaction> transactions = new ArrayList<>();
        for (OrderBook o: orderBooks.values()) {
            transactions.addAll(o.matchOrders());
        }
        for (Transaction t: transactions) {
            Set<Order> userOrderSet = ongoingTransaction.get(t.username());
            if (userOrderSet != null) {
                userOrderSet.removeIf(order -> {
                    boolean matches = order.getId().equals(t.uuid());
                    
                    // Release reserved funds for executed buy orders
                    if (matches && order.getType().equals("buy")) {
                        double orderCost = order.getPrice() * order.getQuantity();
                        reservedFunds.compute(t.username(), (k, v) -> v - orderCost);
                    }
                    
                    return matches;
                });
            }
            executedTransaction.computeIfAbsent(t.username(), k -> ConcurrentHashMap.newKeySet())
                 .add(t);
        }
    }

    @Override
    public List<Transaction> getLastMatchedOrders(String username) {
        if (executedTransaction.containsKey(username)) {
            Set<Transaction> transactions = executedTransaction.remove(username);
            return new ArrayList<>(transactions);
        }
        return new ArrayList<>();
    }

    @Override
    public List<Order> getUserOrders(String username) {
        return new ArrayList<>(ongoingTransaction.getOrDefault(username, Collections.emptySet()));
    }

    @Override
    public boolean cancelOrder(String username, String orderId) {
        Set<Order> orders = ongoingTransaction.get(username);
        if (orders != null) {
            Order orderToCancel = orders.stream()
                .filter(o -> o.getId().equals(orderId))
                .findFirst()
                .orElse(null);

            System.out.println("Order to cancel: " + orderToCancel);    
                
            if (orderToCancel != null) {
                OrderBook orderBook = orderBooks.get(orderToCancel.getSymbol());
                if (orderBook != null) {
                    // Release reserved funds for buy orders
                    if (orderToCancel.getType().equals("buy")) {
                        double orderCost = orderToCancel.getPrice() * orderToCancel.getQuantity();
                        reservedFunds.compute(username, (k, v) -> v - orderCost);
                    }
                    
                    orderBook.removeOrder(orderToCancel);
                    orders.remove(orderToCancel);
                    return true;
                }
            }
        }
        return false;
    }

}