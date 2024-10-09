package main.java.com.backend.owlfinance.Transaction;

import com.backend.owlfinance.Transaction.Order;
import com.backend.owlfinance.Transaction.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestParam String type, @RequestBody Order order) {
        Order placedOrder = orderService.placeOrder(type, order);
        return ResponseEntity.ok(placedOrder);
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long orderId, @RequestBody Order order) {
        Order updatedOrder = orderService.updateOrder(orderId, order);
        return ResponseEntity.ok(updatedOrder);
    }
}