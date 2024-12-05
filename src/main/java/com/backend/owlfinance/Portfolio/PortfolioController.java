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

  @GetMapping("/checkshare")
  public Boolean checkShare(HttpServletRequest request,
                     @RequestParam(value = "ticker") String ticker,
                     @RequestParam(value = "amount") Integer amount) {
    String username = jwtUtil.extractUsernameFromHeader(request);
    UserPortfolio portfolio = repository.findByUsername(username)
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
    // Get current balances
    double buyerBalance = repository.getCashBalance(buyerUsername);
    double sellerBalance = repository.getCashBalance(sellerUsername);
    
    if (buyerBalance == -1 || sellerBalance == -1) {
        throw new PortfolioNotFoundException("User not found");
    }

    // Update balances
    repository.setCashBalance(buyerUsername, buyerBalance - amount);
    repository.setCashBalance(sellerUsername, sellerBalance + amount);

    // Update stocks (using existing portfolio methods)
    UserPortfolio buyerPortfolio = repository.findByUsername(buyerUsername)
        .orElseThrow(() -> new PortfolioNotFoundException(buyerUsername));
    UserPortfolio sellerPortfolio = repository.findByUsername(sellerUsername)
        .orElseThrow(() -> new PortfolioNotFoundException(sellerUsername));
        
    buyerPortfolio.getStocks().merge(ticker, shares, Integer::sum);
    sellerPortfolio.getStocks().merge(ticker, -shares, Integer::sum);
    repository.save(buyerPortfolio);
    repository.save(sellerPortfolio);
    
    return "Success";
  }

  @GetMapping
  ResponseEntity<UserPortfolio> getPortfolio(HttpServletRequest request) {
    String username = jwtUtil.extractUsernameFromHeader(request);
    UserPortfolio portfolio = repository.findByUsername(username)
        .orElseThrow(() -> new PortfolioNotFoundException(username));
    return ResponseEntity.ok(portfolio);
  }

  @GetMapping("/balance")
  ResponseEntity<Double> getUserBalance(HttpServletRequest request) {
    String username = jwtUtil.extractUsernameFromHeader(request);
    double balance = repository.getCashBalance(username);
    if (balance == -1) {
        throw new PortfolioNotFoundException(username);
    }
    return ResponseEntity.ok(balance);
  }

  @GetMapping("/stocks")
  ResponseEntity<Map<String, Integer>> getUserStocks(HttpServletRequest request) {
    String username = jwtUtil.extractUsernameFromHeader(request);
    UserPortfolio portfolio = repository.findByUsername(username)
        .orElseThrow(() -> new PortfolioNotFoundException(username));
    return ResponseEntity.ok(portfolio.getStocks());
  }

  @PostMapping("/deposit")
  ResponseEntity<Double> deposit(HttpServletRequest request, @RequestBody AmountRequest amountRequest) {
    String username = jwtUtil.extractUsernameFromHeader(request);
    Double amount = amountRequest.getAmount();
    if (amount == null || amount <= 0) {
        throw new InvalidAmountException("Deposit amount must be positive");
    }
    
    double currentBalance = repository.getCashBalance(username);
    if (currentBalance == -1) {
        throw new PortfolioNotFoundException(username);
    }
    
    double newBalance = currentBalance + amount;
    repository.setCashBalance(username, newBalance);
    return ResponseEntity.ok(newBalance);
  }

  @PostMapping("/withdraw")
  ResponseEntity<Double> withdraw(HttpServletRequest request, @RequestBody AmountRequest amountRequest) {
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
        double newBalance = currentBalance - amount;
        repository.setCashBalance(username, newBalance);
        return ResponseEntity.ok(newBalance);
    } else {
        throw new InsufficientFundsException("Insufficient funds for withdrawal");
    }
  }

  @PostMapping("/stocks/add")
  ResponseEntity<UserPortfolio> addStocks(HttpServletRequest request, @RequestBody StockRequest stockRequest) {
    String username = jwtUtil.extractUsernameFromHeader(request);
    
    // Validate request
    if (stockRequest.getShares() <= 0) {
        throw new InvalidAmountException("Stock quantity must be positive");
    }
    
    // Add stocks using repository method
    repository.addStocks(
        username,
        stockRequest.getSymbol(),
        stockRequest.getShares(),
        stockRequest.getPrice(),
        stockRequest.getTimestamp()
    );
    
    // Return updated portfolio
    UserPortfolio updatedPortfolio = repository.findByUsername(username)
        .orElseThrow(() -> new PortfolioNotFoundException(username));
    
    return ResponseEntity.ok(updatedPortfolio);
  }

  @PostMapping("/stocks/remove")
  ResponseEntity<UserPortfolio> removeStocks(HttpServletRequest request, @RequestBody StockRemoveRequest stockRequest) {
    String username = jwtUtil.extractUsernameFromHeader(request);

    if (stockRequest.getShares() <= 0) {
      throw new InvalidAmountException("Stock quantity must be positive");
    }

    repository.removeStocks(
      username,
      stockRequest.getSymbol(),
      stockRequest.getShares(),
      stockRequest.getTimestamp()
    );

    UserPortfolio updatedPortfolio = repository.findByUsername(username)
        .orElseThrow(() -> new PortfolioNotFoundException(username));
    
    return ResponseEntity.ok(updatedPortfolio);
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
            
            // Create portfolio with some initial stocks
            UserPortfolio portfolio = new UserPortfolio();
            portfolio.setUsername(testUsers[i]);
            
            Map<String, Integer> initialStocks = new HashMap<>();
            switch (i) {
                case 0: // testUser1 gets some tech stocks
                    initialStocks.put("AAPL", 10);
                    initialStocks.put("GOOGL", 5);
                    initialStocks.put("MSFT", 8);
                    break;
                case 1: // testUser2 gets some finance stocks
                    initialStocks.put("JPM", 15);
                    initialStocks.put("BAC", 20);
                    initialStocks.put("AAPL", 3);
                    break;
                case 2: // testUser3 gets a mix
                    initialStocks.put("AAPL", 2);
                    initialStocks.put("TSLA", 4);
                    initialStocks.put("AMZN", 1);
                    break;
            }
            
            portfolio.setStocks(initialStocks);
            repository.save(portfolio);
        }

        String testUser4 = "testUser4";
        repository.setCashBalance(testUser4, 10000.0);
        
        return ResponseEntity.ok("Test cases created successfully");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Error creating test cases: " + e.getMessage());
    }
  }

  @DeleteMapping("/test/cleanup")
  public ResponseEntity<String> cleanupTestCases() {
    try {
        String[] testUsers = {"testUser1", "testUser2", "testUser3"};
        
        for (String username : testUsers) {
            // Clear portfolio
            UserPortfolio portfolio = new UserPortfolio();
            portfolio.setUsername(username);
            portfolio.setStocks(new HashMap<>());
            repository.save(portfolio);
            
            // Reset balance to 0
            repository.setCashBalance(username, 0);
        }
        
        return ResponseEntity.ok("Test cases cleaned up successfully");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Error cleaning up test cases: " + e.getMessage());
    }
  }

  @GetMapping("/test/verify")
  public ResponseEntity<Map<String, Object>> verifyTestCases() {
    Map<String, Object> status = new HashMap<>();
    String[] testUsers = {"testUser1", "testUser2", "testUser3"};
    
    try {
        for (String username : testUsers) {
            Map<String, Object> userStatus = new HashMap<>();
            
            // Get balance
            double balance = repository.getCashBalance(username);
            userStatus.put("balance", balance);
            
            // Get portfolio
            Optional<UserPortfolio> portfolio = repository.findByUsername(username);
            if (portfolio.isPresent()) {
                userStatus.put("stocks", portfolio.get().getStocks());
            } else {
                userStatus.put("stocks", "No portfolio found");
            }
            
            status.put(username, userStatus);
        }
        
        return ResponseEntity.ok(status);
    } catch (Exception e) {
        status.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(status);
    }
  }
}