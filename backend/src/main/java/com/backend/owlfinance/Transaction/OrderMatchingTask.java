package com.backend.owlfinance.Transaction;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderMatchingTask {

    private final OrderBookManager orderBookManager;

    public OrderMatchingTask(OrderBookManager orderBookManager) {
        this.orderBookManager = orderBookManager;
    }

    @Scheduled(fixedRate = 1000) // Run every 1000 milliseconds
    public void matchOrders() {
        orderBookManager.matchOrders();
    }
}