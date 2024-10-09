package main.java.com.backend.owlfinance.Transaction;

import com.example.tradeengine.model.Order;
import com.example.tradeengine.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Order placeOrder(String type, Order order) {
        order.setType(type);
        return orderRepository.save(order);
    }

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
}