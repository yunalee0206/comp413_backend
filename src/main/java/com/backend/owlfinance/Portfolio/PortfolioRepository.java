package com.backend.owlfinance.Portfolio;

import com.backend.owlfinance.database.bigtable.BigTableManager;
import com.backend.owlfinance.database.obj.Portfolio;
import org.springframework.stereotype.Repository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Repository
public class PortfolioRepository {
    private final BigTableManager bigTableManager;

    public PortfolioRepository() {        
        String projectId = "rice-comp-539-spring-2022";
        String instanceId = "comp-539-bigtable";
        try {
            BigTableManager bt = new BigTableManager(projectId, instanceId);
            this.bigTableManager = bt;
        } catch (IOException e) {
            // Log the error and rethrow as a runtime exception since this is a critical initialization error
            System.err.println("Failed to initialize BigTableManager: " + e.getMessage());
            throw new RuntimeException("Failed to initialize BigTableManager", e);
        }
    }

    public Optional<UserPortfolio> findByUsername(String username) {
        List<Portfolio> portfolioRows = bigTableManager.getPortfolioRowsByUser(username);
        double cashBalance = bigTableManager.getUserCashBalance(username);

        // Create empty portfolio with cash balance if no stock records exist
        if (portfolioRows.isEmpty()) {
            UserPortfolio emptyPortfolio = new UserPortfolio();
            emptyPortfolio.setUsername(username);
            emptyPortfolio.setStocks(new HashMap<>());
            emptyPortfolio.setBalance(cashBalance);
            return Optional.of(emptyPortfolio);
        }

        // Convert BigTable Portfolio records to UserPortfolio
        Map<String, Integer> stocks = new HashMap<>();
        for (Portfolio row : portfolioRows) {
            stocks.merge(row.stockSymbol(), row.numShares(), Integer::sum);
        }

        UserPortfolio userPortfolio = new UserPortfolio();
        userPortfolio.setUsername(username);
        userPortfolio.setStocks(stocks);
        userPortfolio.setBalance(cashBalance);

        return Optional.of(userPortfolio);
    }

    public UserPortfolio save(UserPortfolio portfolio) {
        // Create a new portfolio record
        Portfolio newRecord = new Portfolio(
            portfolio.getUsername(),
            "", // stock symbol will be set for each stock entry
            0,  // shares will be set for each stock entry
            portfolio.getBalance(),
            java.time.Instant.now().toString()
        );

        // Save the portfolio record
        for (Map.Entry<String, Integer> stockEntry : portfolio.getStocks().entrySet()) {
            Portfolio stockRecord = new Portfolio(
                portfolio.getUsername(),
                stockEntry.getKey(),
                stockEntry.getValue(),
                portfolio.getBalance(),
                java.time.Instant.now().toString()
            );
            bigTableManager.createPortfolioRow(stockRecord);
        }

        return portfolio;
    }

    public void addStocks(String username, String symbol, int shares, double price, String timestamp) {
        // Get existing portfolio rows for this user and stock
        List<Portfolio> existingRows = bigTableManager.getPortfolioRowsByUserAndStock(username, symbol);
        
        if (!existingRows.isEmpty()) {
            // Calculate total shares and weighted average price
            int totalShares = existingRows.stream()
                .mapToInt(Portfolio::numShares)
                .sum() + shares;
            
            double weightedPrice = (existingRows.stream()
                .mapToDouble(p -> p.numShares() * p.sharePrice())
                .sum() + (shares * price)) / totalShares;
                
            // Create updated portfolio record
            Portfolio updatedRecord = new Portfolio(
                username,
                symbol,
                totalShares,
                weightedPrice,
                timestamp
            );
            bigTableManager.createPortfolioRow(updatedRecord);
        } else {
            // Create new portfolio record
            Portfolio newRecord = new Portfolio(
                username,
                symbol,
                shares,
                price,
                timestamp
            );
            bigTableManager.createPortfolioRow(newRecord);
        }
    }

    public void removeStocks(String username, String symbol, int sharesToRemove, String timestamp) {
        // Get existing portfolio rows for this user and stock
        List<Portfolio> existingRows = bigTableManager.getPortfolioRowsByUserAndStock(username, symbol);
        
        if (existingRows.isEmpty()) {
            throw new IllegalStateException("No shares found for symbol: " + symbol);
        }
        
        // Calculate total existing shares
        int totalExistingShares = existingRows.stream()
            .mapToInt(Portfolio::numShares)
            .sum();
            
        if (totalExistingShares < sharesToRemove) {
            throw new IllegalStateException(
                "Insufficient shares to remove. Requested: " + sharesToRemove + 
                ", Available: " + totalExistingShares);
        }
        
        // Calculate remaining shares and maintain the same weighted average price
        int remainingShares = totalExistingShares - sharesToRemove;
        if (remainingShares > 0) {
            double currentPrice = existingRows.get(0).sharePrice(); // Maintain existing price
            
            Portfolio updatedRecord = new Portfolio(
                username,
                symbol,
                remainingShares,
                currentPrice,
                timestamp
            );
            bigTableManager.createPortfolioRow(updatedRecord);
        }
        // If remainingShares == 0, we don't create a new record, effectively removing the position
    }

    public int getCashBalance(String username) {
        return bigTableManager.getUserCashBalance(username);
    }

    public void setCashBalance(String username, double newBalance) {
        bigTableManager.setUserCashBalance(username, (int)newBalance);
    }

    public List<UserPortfolio> findAll() {
        return null;
    }
}
