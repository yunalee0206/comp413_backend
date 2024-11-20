package com.backend.owlfinance.Portfolio;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;
import com.backend.owlfinance.User.JwtUtil;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/portfolios")
public class PortfolioController {

  private final PortfolioRepository repository;
  private final JwtUtil jwtUtil;

  PortfolioController(PortfolioRepository repository, JwtUtil jwtUtil) {
    this.repository = repository;
    this.jwtUtil = jwtUtil;
  }

  @GetMapping("/all")
  List<Portfolio> getAllPortfolios() {
    return repository.findAll();
  }

  @GetMapping("/checkbalance")
  public Boolean checkBalance(HttpServletRequest request,
                       @RequestParam(value = "amount") Double amount) {
    String username = jwtUtil.extractUsernameFromHeader(request);
    Portfolio portfolio = repository.findByUsername(username)
        .orElseThrow(() -> new PortfolioNotFoundException(username));
    System.out.println("Checking balance for user " + username + " amount " + amount);
    return portfolio.getBalance() >= amount;
  }

  @GetMapping("/checkshare")
  public Boolean checkShare(HttpServletRequest request,
                     @RequestParam(value = "ticker") String ticker,
                     @RequestParam(value = "amount") Integer amount) {
    String username = jwtUtil.extractUsernameFromHeader(request);
    Portfolio portfolio = repository.findByUsername(username)
        .orElseThrow(() -> new PortfolioNotFoundException(username));
    System.out.println("Checking share for user " + username + " ticker " + ticker + " amount " + amount);
    return portfolio.getStocks().getOrDefault(ticker, 0) >= amount;
  }

  @PutMapping("/update")
  public String update(@RequestParam(value = "buyerUsername") String buyerUsername,
                @RequestParam(value = "sellerUsername") String sellerUsername,
                @RequestParam(value = "amount") Double amount,
                @RequestParam(value = "ticker") String ticker,
                @RequestParam(value = "shares") Integer shares) {
    Portfolio buyerPortfolio = repository.findByUsername(buyerUsername)
        .orElseThrow(() -> new PortfolioNotFoundException(buyerUsername));
    Portfolio sellerPortfolio = repository.findByUsername(sellerUsername)
        .orElseThrow(() -> new PortfolioNotFoundException(sellerUsername));
    buyerPortfolio.setBalance(buyerPortfolio.getBalance() - amount);
    sellerPortfolio.setBalance(sellerPortfolio.getBalance() + amount);
    buyerPortfolio.getStocks().merge(ticker, shares, Integer::sum);
    sellerPortfolio.getStocks().merge(ticker, -shares, Integer::sum);
    repository.save(buyerPortfolio);
    repository.save(sellerPortfolio);
    return "Success";
  }

  @GetMapping
  ResponseEntity<Portfolio> getPortfolio(HttpServletRequest request) {
    String username = jwtUtil.extractUsernameFromHeader(request);
    Portfolio portfolio = repository.findByUsername(username)
        .orElseThrow(() -> new PortfolioNotFoundException(username));
    return ResponseEntity.ok(portfolio);
  }

  @GetMapping("/balance")
  ResponseEntity<Double> getUserBalance(HttpServletRequest request) {
    String username = jwtUtil.extractUsernameFromHeader(request);
    Portfolio portfolio = repository.findByUsername(username)
        .orElseThrow(() -> new PortfolioNotFoundException(username));
    return ResponseEntity.ok(portfolio.getBalance());
  }

  @GetMapping("/stocks")
  ResponseEntity<Map<String, Integer>> getUserStocks(HttpServletRequest request) {
    String username = jwtUtil.extractUsernameFromHeader(request);
    Portfolio portfolio = repository.findByUsername(username)
        .orElseThrow(() -> new PortfolioNotFoundException(username));
    return ResponseEntity.ok(portfolio.getStocks());
  }

  @PostMapping("/deposit")
  ResponseEntity<Portfolio> deposit(HttpServletRequest request, @RequestBody AmountRequest amountRequest) {
    String username = jwtUtil.extractUsernameFromHeader(request);
    Double amount = amountRequest.getAmount();
    if (amount == null || amount <= 0) {
        throw new InvalidAmountException("Deposit amount must be positive");
    }
    Portfolio portfolio = repository.findByUsername(username)
        .orElseThrow(() -> new PortfolioNotFoundException(username));
    portfolio.setBalance(portfolio.getBalance() + amount);
    return ResponseEntity.ok(repository.save(portfolio));
  }

  @PostMapping("/withdraw")
  ResponseEntity<Portfolio> withdraw(HttpServletRequest request, @RequestBody AmountRequest amountRequest) {
    String username = jwtUtil.extractUsernameFromHeader(request);
    Double amount = amountRequest.getAmount();
    if (amount == null || amount <= 0) {
        throw new InvalidAmountException("Withdrawal amount must be positive");
    }
    Portfolio portfolio = repository.findByUsername(username)
        .orElseThrow(() -> new PortfolioNotFoundException(username));
    if (portfolio.getBalance() >= amount) {
        portfolio.setBalance(portfolio.getBalance() - amount);
    } else {
        throw new InsufficientFundsException("Insufficient funds for withdrawal");
    }
    return ResponseEntity.ok(repository.save(portfolio));
  }

  @PostMapping("/stocks/add")
  ResponseEntity<Portfolio> addStocks(HttpServletRequest request, @RequestBody Map<String, Integer> stocksToAdd) {
    String username = jwtUtil.extractUsernameFromHeader(request);
    Portfolio portfolio = repository.findByUsername(username)
        .orElseThrow(() -> new PortfolioNotFoundException(username));
    
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

  @PostMapping("/stocks/remove")
  ResponseEntity<Portfolio> removeStocks(HttpServletRequest request, @RequestBody Map<String, Integer> stocksToRemove) {
    String username = jwtUtil.extractUsernameFromHeader(request);
    Portfolio portfolio = repository.findByUsername(username)
        .orElseThrow(() -> new PortfolioNotFoundException(username));
    
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