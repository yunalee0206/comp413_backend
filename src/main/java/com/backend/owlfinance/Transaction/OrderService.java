package com.backend.owlfinance.Transaction;

import java.util.List;
import com.backend.owlfinance.database.obj.Transaction;

public interface OrderService {
    Order placeOrder(String type, Order order);
    // Order updateOrder(Long orderId, Order order);
    void matchOrders();

    List<Transaction> getLastMatchedOrders();

    List<Order> getUserOrders(String username);

    boolean cancelOrder(String username, String orderId);
}