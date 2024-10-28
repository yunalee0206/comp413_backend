package com.backend.owlfinance.Transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderOperation orderOp;
    private final ConcurrentHashMap<String, OrderBook> orderBooks = new ConcurrentHashMap<>();

    @Override
    public Order placeOrder(String type, Order order) {
        order.setType(type);
        OrderBook orderBook = orderBooks.computeIfAbsent(order.getSymbol(), s -> new OrderBook(this.orderOp));
        return orderBook.addOrder(order);
    }

    @Override
    public Order updateOrder(Long orderId, Order order) {
        OrderBook orderBook = orderBooks.get(order.getSymbol());
        if (orderBook != null) {
            return orderBook.updateOrder(orderId, order);
        }
        return null;
    }

    @Override
    public void matchOrders() {
        for (OrderBook o: orderBooks.values()) {
            o.matchOrders();
        }
    }
}