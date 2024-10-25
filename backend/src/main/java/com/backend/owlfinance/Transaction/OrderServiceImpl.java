package com.backend.owlfinance.Transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderBookManager orderBookManager;

    @Override
    public Order placeOrder(String type, Order order) {
        order.setType(type);
        Order o = orderRepository.save(order);
        orderBookManager.addOrder(o);
        return o;
    }

    // TODO: Update order in order book manager
    @Override
    public Order updateOrder(Long orderId, Order order) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Update the existing order with new values
        existingOrder.setSymbol(order.getSymbol());
        existingOrder.setQuantity(order.getQuantity());
        existingOrder.setPrice(order.getPrice());
        
        return orderRepository.save(existingOrder);
    }

    @Override
    public void matchOrders() {}
}