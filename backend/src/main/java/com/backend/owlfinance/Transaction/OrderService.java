package main.java.com.backend.owlfinance.Transaction;

import com.example.tradeengine.model.Order;

public interface OrderService {
    Order placeOrder(String type, Order order);
    Order updateOrder(Long orderId, Order order);
}