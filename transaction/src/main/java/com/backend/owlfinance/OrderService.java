package com.backend.owlfinance;

public interface OrderService {
    Order placeOrder(String type, Order order);
    Order updateOrder(Long orderId, Order order);
    void matchOrders();
}