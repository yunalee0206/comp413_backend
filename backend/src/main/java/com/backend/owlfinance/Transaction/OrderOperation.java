package com.backend.owlfinance.Transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderOperation {
    
    @Autowired
    private OrderRepository orderRepository;

    public Order updateOrder(Order order) {
        return orderRepository.save(order);
    }

    public void deleteOrder(Order order) {
        System.out.println(order.getType() + " Order deleted from database: " + order.getId());
        orderRepository.delete(order);
    }

    public Order findOrder(Long orderId) {
        Order o = orderRepository.findById(orderId)
        .orElseThrow(() -> new RuntimeException("Order not found"));
        return o;
    }
}