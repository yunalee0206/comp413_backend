package com.backend.owlfinance.Transaction;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import com.backend.owlfinance.database.bigtable.BigTableManager;
import java.util.*;
import com.backend.owlfinance.database.obj.Transaction;;

@Service
public class OrderServiceImpl implements OrderService {


    private BigTableManager database;
    private final ConcurrentHashMap<String, OrderBook> orderBooks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Set<Order>> userOrders = new ConcurrentHashMap<>();
    private List<Transaction> lastMatchedTransactions = new ArrayList<>();

    public OrderServiceImpl() throws IOException {
        String projectId = "rice-comp-539-spring-2022";
        String instanceId = "comp-539-bigtable";
        this.database = new BigTableManager(projectId, instanceId);
    }

    @Override
    public Order placeOrder(String type, Order order) {
        order.setType(type);
        order.setId(UUID.randomUUID().toString());
        userOrders.computeIfAbsent(order.getUsername(), k -> ConcurrentHashMap.newKeySet())
                 .add(order);
        OrderBook orderBook = orderBooks.computeIfAbsent(order.getSymbol(), s -> new OrderBook(s, this.database));
        return orderBook.placeOrder(order);
    }

    // @Override
    // public Order updateOrder(Long orderId, Order order) {
    //     OrderBook orderBook = orderBooks.get(order.getSymbol());
    //     if (orderBook != null) {
    //         return orderBook.updateOrder(orderId, order);
    //     }
    //     return null;
    // }

    @Override
    public void matchOrders() {
        List<Transaction> transactions = new ArrayList<>();
        for (OrderBook o: orderBooks.values()) {
            transactions.addAll(o.matchOrders());
        }
        for (Transaction t : transactions) {
            Set<Order> userOrderSet = userOrders.get(t.username());
            if (userOrderSet != null) {
                userOrderSet.removeIf(order -> 
                    order.getSymbol().equals(t.stockSymbol()) &&
                    order.getQuantity() == t.numShares() &&
                    order.getPrice() == t.sharePrice() &&
                    order.getUsername().equals(t.username())
                );
            }
        }
        lastMatchedTransactions.addAll(transactions);
    }

    @Override
    public List<Transaction> getLastMatchedOrders() {
        List<Transaction> transactions = new ArrayList<>(lastMatchedTransactions);
        lastMatchedTransactions.clear();
        return transactions;
    }

    @Override
    public List<Order> getUserOrders(String username) {
        return new ArrayList<>(userOrders.getOrDefault(username, Collections.emptySet()));
    }

    @Override
    public boolean cancelOrder(String username, String orderId) {
        Set<Order> orders = userOrders.get(username);
        if (orders != null) {
            Order orderToCancel = orders.stream()
                .filter(o -> o.getId().equals(orderId))
                .findFirst()
                .orElse(null);
                
            if (orderToCancel != null) {
                OrderBook orderBook = orderBooks.get(orderToCancel.getSymbol());
                if (orderBook != null) {
                    orderBook.removeOrder(orderToCancel);
                    orders.remove(orderToCancel);
                    System.out.println("Remove Order");
                    return true;
                }
            }
        }
        return false;
    }

}