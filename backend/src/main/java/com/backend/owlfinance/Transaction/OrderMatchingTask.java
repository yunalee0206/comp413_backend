package com.backend.owlfinance.Transaction;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class OrderMatchingTask {

    @Autowired
    private OrderService orderService;

    // public OrderMatchingTask(OrderBookManager orderBookManager) {
    //     this.orderBookManager = orderBookManager;
    // }

    @Scheduled(fixedRate = 1000) // Run every 1000 milliseconds
    public void matchOrders() {
        orderService.matchOrders();
    }
}