package com.backend.owlfinance.Transaction;

import java.util.PriorityQueue;
import java.util.Comparator;


class OrderBook {

    private OrderOperation orderOperation;
    private PriorityQueue<Order> buyOrders;
    private PriorityQueue<Order> sellOrders;


    public OrderBook(OrderOperation orderOperation) {
        this.orderOperation = orderOperation;
        buyOrders = new PriorityQueue<>(new BuyOrderComparator());
        sellOrders = new PriorityQueue<>(new SellOrderComparator());
    }

    public Order addOrder(Order order) {
        Order o = orderOperation.updateOrder(order);
        if (o.getType().equals("buy")) {
            buyOrders.add(o);
            System.out.println("Buy Order addded: " + o.getId() + " Ticker: " + o.getSymbol() + " Price: " + o.getPrice() + " Quantity: " + o.getQuantity());
        } else if (o.getType().equals("sell")) {
            sellOrders.add(o);
            System.out.println("Sell Order addded: " + o.getId() + " Ticker: " + o.getSymbol() + " Price: " + o.getPrice() + " Quantity: " + o.getQuantity());
        }
        return o;
    }

    public Order updateOrder(Long orderId, Order order) {
        Order o = orderOperation.findOrder(orderId);
        order = orderOperation.updateOrder(order);
        if (o.getType().equals("buy")) {
            buyOrders.remove(o);
            buyOrders.add(order);
            System.out.println("Buy Order updated: " + order.getId() + " Ticker: " + order.getSymbol() + " Price: " + order.getPrice() + " Quantity: " + order.getQuantity());
        } else if (o.getType().equals("sell")) {
            sellOrders.remove(o);
            sellOrders.add(order);
            System.out.println("Sell Order updated: " + order.getId() + " Ticker: " + order.getSymbol() + " Price: " + order.getPrice() + " Quantity: " + order.getQuantity());
        }
        return order;
    }

    public void matchOrders() {
        while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            Order buyOrder = buyOrders.peek();
            Order sellOrder = sellOrders.peek();
            int price;

            if (buyOrder.getPrice() >= sellOrder.getPrice()) {
                executeTransaction(buyOrder, sellOrder);
            } else {
                break;
            }
        }
    }

    public void removeOrder(Order order) {
        orderOperation.deleteOrder(order);
        if (order.getType().equals("buy")) {
            buyOrders.remove(order);
        } else if (order.getType().equals("sell")) {
            sellOrders.remove(order);
        }

    }

    // TODO: Trasaction logic
    private void executeTransaction(Order buy, Order sell) {
        double price = buy.getTimestamp().compareTo(sell.getTimestamp()) > 0 ? sell.getPrice() : buy.getPrice();
        int quantity = Math.min(buy.getQuantity(), sell.getQuantity());

        System.out.printf("Executing trasaction... Ticker: %s, Quantity: %d, Buy: %d, Sell: %d, Price: %f \n",
                         buy.getSymbol(), quantity, buy.getId(), sell.getId(), price);

        buy.setQuantity(buy.getQuantity() - quantity);
        sell.setQuantity(sell.getQuantity() - quantity);
        if (buy.getQuantity() == 0) {
            System.out.println("Buy Order filled: " + buy.getId()+ " " + buy.getSymbol());
            removeOrder(buy);
        }
        if (sell.getQuantity() == 0) {
            System.out.println("Sell Order filled: " + sell.getId()+ " " + sell.getSymbol());
            removeOrder(sell);
        }

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

