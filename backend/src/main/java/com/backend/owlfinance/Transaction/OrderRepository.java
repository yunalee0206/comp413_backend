package com.backend.owlfinance.Transaction;

import com.backend.owlfinance.Transaction.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// @Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}