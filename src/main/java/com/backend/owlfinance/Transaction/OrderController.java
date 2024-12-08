package com.backend.owlfinance.Transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.backend.owlfinance.User.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import com.backend.owlfinance.database.obj.Transaction;;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    private final JwtUtil jwtUtil;

    OrderController(OrderService orderService, JwtUtil jwtUtil) {
        this.orderService = orderService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<Order> placeOrder(
        @RequestParam String type, 
        @RequestBody Order order,
        HttpServletRequest request) {
        try {
            // Extract username from JWT and set it in the order
            String username = jwtUtil.extractUsernameFromHeader(request);
            System.out.println("Username: " + username);
            order.setUsername(username);
            order.setTimestamp(new Date());
            
            Order placedOrder = orderService.placeOrder(type, order);
            return ResponseEntity.ok(placedOrder);
        } catch (Exception e) {
            // Log the error
            System.err.println("Error placing order: " + e.getMessage());
            throw new RuntimeException("Failed to place order: " + e.getMessage());
        }
    }
 

    @GetMapping("/match")
    public ResponseEntity<List<Transaction>> getLastMatchedOrders(HttpServletRequest request) {
        String username = jwtUtil.extractUsernameFromHeader(request);
        List<Transaction> transactions = orderService.getLastMatchedOrders(username);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/ongoing")
    public ResponseEntity<List<Order>> getUserOrders(HttpServletRequest request) {
        String username = jwtUtil.extractUsernameFromHeader(request);
        List<Order> orders = orderService.getUserOrders(username);
        return ResponseEntity.ok(orders);
    }

    @DeleteMapping("/cancel/{orderId}")
    public ResponseEntity<Boolean> cancelOrder(HttpServletRequest request, @PathVariable String orderId) {
        String username = jwtUtil.extractUsernameFromHeader(request);
        boolean success = orderService.cancelOrder(username, orderId);
        return ResponseEntity.ok(success);
    }
}
