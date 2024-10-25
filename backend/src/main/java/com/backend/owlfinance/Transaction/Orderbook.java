package com.backend.owlfinance.Transaction;

import java.util.concurrent.locks.ReentrantLock;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Map;


class OrderBook {

    private PriorityQueue<Order> buyOrders;
    private PriorityQueue<Order> sellOrders;

    public OrderBook() {
        buyOrders = new PriorityQueue<>(new BuyOrderComparator());
        sellOrders = new PriorityQueue<>(new SellOrderComparator());
    }

    public void addOrder(Order order) {
        if (order.getType().equals("buy")) {
            buyOrders.add(order);
            System.out.println("Buy Order addded: " + order.getId());
        } else if (order.getType().equals("sell")) {
            sellOrders.add(order);
            System.out.println("Sell Order addded: " + order.getId());
        }

    }

    public void removeOrder(Order order) {
        if (order.getType().equals("buy")) {
            buyOrders.remove(order);
        } else if (order.getType().equals("sell")) {
            sellOrders.remove(order);
        }
    }

    // TODO: Partial match?
    public void matchOrders() {
        while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            Order buyOrder = buyOrders.peek();
            Order sellOrder = sellOrders.peek();

            if (buyOrder.getPrice() >= sellOrder.getPrice()) {
                executeTransaction(buyOrder, sellOrder);
                buyOrders.poll();
                sellOrders.poll();
            } else {
                break;
            }
        }
    }

    private void executeTransaction(Order buy, Order sell) {
        System.out.printf("Execute Trasaction %s, %s...\n", buy.getId(), sell.getId());
    }

    private class BuyOrderComparator implements Comparator<Order> {
        @Override
        public int compare(Order o1, Order o2) {
            if (o1.getPrice() == o2.getPrice()) {
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }
            return Double.compare(o2.getPrice(), o1.getPrice());
        }
    }
    
    private class SellOrderComparator implements Comparator<Order> {
        @Override
        public int compare(Order o1, Order o2) {
            if (o1.getPrice() == o2.getPrice()) {
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }
            return Double.compare(o1.getPrice(), o2.getPrice());
        }
    }
}

