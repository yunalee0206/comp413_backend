package com.backend.owlfinance.Transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import com.backend.owlfinance.database.obj.Transaction;;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestParam String type, @RequestBody Order order) {
        Order placedOrder = orderService.placeOrder(type, order);
        return ResponseEntity.ok(placedOrder);
    }

    // @PutMapping("/{orderId}")
    // public ResponseEntity<Order> updateOrder(@PathVariable Long orderId, @RequestBody Order order) {
    //     Order updatedOrder = orderService.updateOrder(orderId, order);
    //     return ResponseEntity.ok(updatedOrder);
    // }

 

    @GetMapping("/match")
    public ResponseEntity<List<Transaction>> getLastMatchedOrders() {
        List<Transaction> transactions = orderService.getLastMatchedOrders();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable String username) {
        List<Order> orders = orderService.getUserOrders(username);
        return ResponseEntity.ok(orders);
    }

    @DeleteMapping("/{username}/{orderId}")
    public ResponseEntity<Boolean> cancelOrder(@PathVariable String username, @PathVariable String orderId) {
        boolean success = orderService.cancelOrder(username, orderId);
        return ResponseEntity.ok(success);
    }
}
