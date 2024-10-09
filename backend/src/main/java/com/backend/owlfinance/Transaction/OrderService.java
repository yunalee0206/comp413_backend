package com.backend.owlfinance.Transaction;

import com.backend.owlfinance.Transaction.Order;

public interface OrderService {
    Order placeOrder(String type, Order order);
    Order updateOrder(Long orderId, Order order);
}