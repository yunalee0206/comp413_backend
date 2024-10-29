package com.backend.owlfinance.Portfolio;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.backend.owlfinance.exception.InvalidAmountException;
import com.backend.owlfinance.exception.InsufficientFundsException;
import com.backend.owlfinance.exception.PortfolioNotFoundException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/portfolios")
class PortfolioController {

  private final PortfolioRepository repository;

  PortfolioController(PortfolioRepository repository) {
    this.repository = repository;
  }

  @GetMapping
  List<Portfolio> all() {
    return repository.findAll();
  }

  @GetMapping("/{userId}")
  ResponseEntity<Portfolio> getPortfolioByUserId(@PathVariable Long userId) {
    Portfolio portfolio = repository.findByUserId(userId)
        .orElseThrow(() -> new PortfolioNotFoundException(userId));
    return ResponseEntity.ok(portfolio);
  }

  @GetMapping("/{userId}/balance")
  ResponseEntity<Double> getUserBalance(@PathVariable Long userId) {
    Portfolio portfolio = repository.findByUserId(userId)
        .orElseThrow(() -> new PortfolioNotFoundException(userId));
    return ResponseEntity.ok(portfolio.getBalance());
  }

  @GetMapping("/{userId}/stocks")
  ResponseEntity<Map<String, Integer>> getUserStocks(@PathVariable Long userId) {
    Portfolio portfolio = repository.findByUserId(userId)
        .orElseThrow(() -> new PortfolioNotFoundException(userId));
    return ResponseEntity.ok(portfolio.getStocks());
  }

  @PostMapping("/{userId}/deposit")
  ResponseEntity<Portfolio> deposit(@PathVariable Long userId, @RequestBody Double amount) {
    // Example JSON body:
    // {
    //   "amount": 1000.00
    // }
    if (amount <= 0) {
      throw new InvalidAmountException("Deposit amount must be positive");
    }
    Portfolio portfolio = repository.findByUserId(userId)
        .orElseThrow(() -> new PortfolioNotFoundException(userId));
    portfolio.setBalance(portfolio.getBalance() + amount);
    return ResponseEntity.ok(repository.save(portfolio));
  }

  @PostMapping("/{userId}/withdraw")
  ResponseEntity<Portfolio> withdraw(@PathVariable Long userId, @RequestBody Double amount) {
    // Example JSON body:
    // {
    //   "amount": 500.00
    // }
    if (amount <= 0) {
      throw new InvalidAmountException("Withdrawal amount must be positive");
    }
    Portfolio portfolio = repository.findByUserId(userId)
        .orElseThrow(() -> new PortfolioNotFoundException(userId));
    if (portfolio.getBalance() >= amount) {
      portfolio.setBalance(portfolio.getBalance() - amount);
    } else {
      throw new InsufficientFundsException("Insufficient funds for withdrawal");
    }
    return ResponseEntity.ok(repository.save(portfolio));
  }

  @PostMapping("/{userId}/stocks/add")
  ResponseEntity<Portfolio> addStocks(@PathVariable Long userId, @RequestBody Map<String, Integer> stocksToAdd) {
    // Example JSON body:
    // {
    //   "AAPL": 10,
    //   "GOOGL": 5,
    //   "MSFT": 8
    // }
    Portfolio portfolio = repository.findByUserId(userId)
        .orElseThrow(() -> new PortfolioNotFoundException(userId));
    
    Map<String, Integer> currentStocks = portfolio.getStocks();
    
    for (Map.Entry<String, Integer> entry : stocksToAdd.entrySet()) {
      String symbol = entry.getKey();
      Integer quantity = entry.getValue();
      
      if (quantity <= 0) {
        throw new InvalidAmountException("Stock quantity must be positive");
      }
      
      currentStocks.merge(symbol, quantity, Integer::sum);
    }
    
    portfolio.setStocks(currentStocks);
    return ResponseEntity.ok(repository.save(portfolio));
  }

  @PostMapping("/{userId}/stocks/remove")
  ResponseEntity<Portfolio> removeStocks(@PathVariable Long userId, @RequestBody Map<String, Integer> stocksToRemove) {
    // Example JSON body:
    // {
    //   "AAPL": 3,
    //   "GOOGL": 2
    // }
    Portfolio portfolio = repository.findByUserId(userId)
        .orElseThrow(() -> new PortfolioNotFoundException(userId));
    
    Map<String, Integer> currentStocks = portfolio.getStocks();
    
    for (Map.Entry<String, Integer> entry : stocksToRemove.entrySet()) {
      String symbol = entry.getKey();
      Integer quantity = entry.getValue();
      
      if (quantity <= 0) {
        throw new InvalidAmountException("Stock quantity must be positive");
      }
      
      Integer currentQuantity = currentStocks.getOrDefault(symbol, 0);
      if (currentQuantity < quantity) {
        throw new InsufficientFundsException("Insufficient stocks for removal");
      }
      
      int newQuantity = currentQuantity - quantity;
      if (newQuantity == 0) {
        currentStocks.remove(symbol);
      } else {
        currentStocks.put(symbol, newQuantity);
      }
    }
    
    portfolio.setStocks(currentStocks);
    return ResponseEntity.ok(repository.save(portfolio));
  }
}