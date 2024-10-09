package com.backend.owlfinance.Transaction;

public interface OrderService {
    Order placeOrder(String type, Order order);
    Order updateOrder(Long orderId, Order order);
    void matchOrders();
}