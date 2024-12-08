package com.backend.owlfinance.Portfolio;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;
import com.backend.owlfinance.User.JwtUtil;
import java.util.List;
import java.util.Map;
import com.backend.owlfinance.database.obj.Portfolio;
import java.util.Optional;
import java.util.HashMap;
import java.time.LocalDateTime;
import com.backend.owlfinance.database.bigtable.BigTableManager;
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
  List<UserPortfolio> getAllPortfolios() {
    return repository.findAll();
  }

  @GetMapping("/checkbalance")
  public Boolean checkBalance(HttpServletRequest request,
                       @RequestParam(value = "amount") Double amount) {
    String username = jwtUtil.extractUsernameFromHeader(request);
    double currentBalance = repository.getCashBalance(username);
    if (currentBalance == -1) {
        throw new PortfolioNotFoundException(username);
    }
    System.out.println("Checking balance for user " + username + " amount " + amount);
    return currentBalance >= amount;
  }


  @GetMapping
  ResponseEntity<UserPortfolio> getPortfolio(HttpServletRequest request) {
    String username = jwtUtil.extractUsernameFromHeader(request);
    UserPortfolio portfolio = repository.findByUsername(username)
        .orElseThrow(() -> new PortfolioNotFoundException(username));
    return ResponseEntity.ok(portfolio);
  }

  @GetMapping("/balance")
  ResponseEntity<Map<String, Double>> getUserBalance(HttpServletRequest request) {
    String username = jwtUtil.extractUsernameFromHeader(request);
    double balance = repository.getCashBalance(username);
    if (balance == -1) {
        throw new PortfolioNotFoundException(username);
    }
    Map<String, Double> response = new HashMap<>();
    response.put("balance", balance);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/deposit")
  ResponseEntity<Map<String, Double>> deposit(HttpServletRequest request, @RequestBody AmountRequest amountRequest) {
    String username = jwtUtil.extractUsernameFromHeader(request);
    Double amount = amountRequest.getAmount();
    if (amount == null || amount <= 0) {
        throw new InvalidAmountException("Deposit amount must be positive");
    }
    
    double currentBalance = repository.getCashBalance(username);
    if (currentBalance == -1) {
        throw new PortfolioNotFoundException(username);
    }
    
    repository.setCashBalance(username, amount);
    Map<String, Double> response = new HashMap<>();
    response.put("balance", currentBalance + amount);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/withdraw")
  ResponseEntity<Map<String, Double>> withdraw(HttpServletRequest request, @RequestBody AmountRequest amountRequest) {
    String username = jwtUtil.extractUsernameFromHeader(request);
    Double amount = amountRequest.getAmount();
    if (amount == null || amount <= 0) {
        throw new InvalidAmountException("Withdrawal amount must be positive");
    }
    
    double currentBalance = repository.getCashBalance(username);
    if (currentBalance == -1) {
        throw new PortfolioNotFoundException(username);
    }
    
    if (currentBalance >= amount) {
        repository.setCashBalance(username, -amount);
        Map<String, Double> response = new HashMap<>();
        response.put("balance", currentBalance - amount);
        return ResponseEntity.ok(response);
    } else {
        throw new InsufficientFundsException("Insufficient funds for withdrawal");
    }
  }
  @GetMapping("/balance/history")
  ResponseEntity<List<BigTableManager.BalanceEntry>> getBalanceHistory(HttpServletRequest request) {
    String username = jwtUtil.extractUsernameFromHeader(request);
    List<BigTableManager.BalanceEntry> history = repository.getBalanceHistory(username);
    return ResponseEntity.ok(history);
  }

  @PostMapping("/test/setup")
  public ResponseEntity<String> setupTestCases() {
    try {
        // Create test users with initial balances
        String[] testUsers = {"testUser1", "testUser2", "testUser3"};
        double[] initialBalances = {10000.0, 5000.0, 2000.0};
        
        for (int i = 0; i < testUsers.length; i++) {
            // Set initial cash balance
            repository.setCashBalance(testUsers[i], initialBalances[i]);
            
            // Create portfolio entries for each stock
            LocalDateTime now = LocalDateTime.now();
            
            switch (i) {
                case 0: // testUser1 gets some tech stocks with multiple entries
                    repository.save(new PortfolioRow(testUsers[i], "AAPL", 10, 180.5, now));
                    repository.save(new PortfolioRow(testUsers[i], "AAPL", 5, 175.0, now.minusDays(1)));
                    repository.save(new PortfolioRow(testUsers[i], "GOOG", 5, 140.75, now));
                    repository.save(new PortfolioRow(testUsers[i], "GOOG", 3, 138.50, now.minusDays(2)));
                    repository.save(new PortfolioRow(testUsers[i], "MSFT", 8, 338.2, now));
                    break;
                case 1: // testUser2 gets some finance stocks with multiple entries
                    repository.save(new PortfolioRow(testUsers[i], "JPM", 15, 147.8, now));
                    repository.save(new PortfolioRow(testUsers[i], "JPM", 10, 145.2, now.minusDays(3)));
                    repository.save(new PortfolioRow(testUsers[i], "BAC", 20, 34.5, now));
                    repository.save(new PortfolioRow(testUsers[i], "AAPL", 3, 180.5, now));
                    break;
                case 2: // testUser3 gets a mix with multiple entries
                    repository.save(new PortfolioRow(testUsers[i], "AAPL", 2, 180.5, now));
                    repository.save(new PortfolioRow(testUsers[i], "AAPL", 3, 182.75, now.minusDays(1)));
                    repository.save(new PortfolioRow(testUsers[i], "TSLA", 4, 175.3, now));
                    repository.save(new PortfolioRow(testUsers[i], "TSLA", 2, 170.8, now.minusDays(4)));
                    repository.save(new PortfolioRow(testUsers[i], "AMZN", 1, 178.2, now));
                    break;
            }
        }

        String testUser4 = "testUser4";
        repository.setCashBalance(testUser4, 10000.0);
        
        return ResponseEntity.ok("Test cases created successfully");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Error creating test cases: " + e.getMessage());
    }
  }

  // @PostMapping("/test/cleanup")
  // public ResponseEntity<String> cleanupTestCases() {
  //   try {
  //       String[] testUsers = {"testUser1", "testUser2", "testUser3", "testUser4"};
        
  //       for (String username : testUsers) {
  //           // Delete all portfolio entries for each test user
  //           List<Portfolio> userPortfolio = repository.findAllByUsername(username);
  //           for (Portfolio entry : userPortfolio) {
  //               repository.delete(entry);
  //           }
            
  //           // Reset cash balance to 0 or remove it entirely
  //           repository.setCashBalance(username, 0.0);
  //       }
        
  //       return ResponseEntity.ok("Test cases cleaned up successfully");
  //   } catch (Exception e) {
  //       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
  //           .body("Error cleaning up test cases: " + e.getMessage());
  //   }
  // }

}