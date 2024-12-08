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
import java.util.stream.Collectors;
import com.backend.owlfinance.database.obj.StockPrice;

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
        System.out.println("portfolioRows: " + portfolioRows);
        double cashBalance = bigTableManager.getUserCashBalance(username);
        
        // Set default cash balance of 1000 if no balance exists
        if (cashBalance == -1) {
            cashBalance = 1000.0;
            bigTableManager.createUserCashBalance(username, cashBalance);
        }

        // Create empty portfolio with cash balance if no stock records exist
        if (portfolioRows.isEmpty()) {
            UserPortfolio emptyPortfolio = new UserPortfolio();
            emptyPortfolio.setUsername(username);
            emptyPortfolio.setStocks(new HashMap<>());
            emptyPortfolio.setBalance(cashBalance);
            return Optional.of(emptyPortfolio);
        }

        // Group portfolio rows by stock symbol
        Map<String, List<Portfolio>> stockGroups = portfolioRows.stream()
            .collect(Collectors.groupingBy(Portfolio::stockSymbol));

        // Calculate positions for each stock
        Map<String, UserPortfolio.StockPosition> positions = new HashMap<>();
        for (Map.Entry<String, List<Portfolio>> entry : stockGroups.entrySet()) {
            String symbol = entry.getKey();
            List<Portfolio> rows = entry.getValue();
            
            int totalShares = 0;
            double totalCost = 0.0;
            double totalReturn = 0.0;
            
            for (Portfolio row : rows) {
                totalShares += row.numShares();
                totalCost += row.numShares() * row.sharePrice();
                
                // Calculate return for each lot separately
                double currentPrice = getCurrentPrice(symbol);
                totalReturn += (currentPrice - row.sharePrice()) * row.numShares();
            }
            
            double avgBuyPrice = totalShares > 0 ? totalCost / totalShares : 0;
            double currentPrice = getCurrentPrice(symbol);
            
            if (totalShares > 0) {
                positions.put(symbol, new UserPortfolio.StockPosition(
                    totalShares,
                    avgBuyPrice,
                    currentPrice,
                    totalReturn
                ));
            }
        }

        UserPortfolio userPortfolio = new UserPortfolio();
        userPortfolio.setUsername(username);
        userPortfolio.setStocks(positions);
        userPortfolio.setBalance(cashBalance);

        return Optional.of(userPortfolio);
    }

    private double getCurrentPrice(String symbol) {
        String date = "2024-12-04";
        String time = "09:00:00";
        try {
            StockPrice stockPrice = bigTableManager.getStockPrice(symbol + "#" + date + " " + time);
            return stockPrice != null ? stockPrice.open() : 0.0;
        } catch (Exception e) {
            System.err.println("Failed to fetch current price for " + symbol + ": " + e.getMessage());
            return 0.0;
        }
    }

    public PortfolioRow save(PortfolioRow portfolioRow) {
        // Create a new Portfolio record
        Portfolio portfolio = new Portfolio(
            portfolioRow.getUsername(),
            portfolioRow.getSymbol(),
            portfolioRow.getQuantity(),
            portfolioRow.getPurchasePrice(),
            portfolioRow.getPurchaseDate().toString()
        );
        
        // Save to BigTable using BigTableManager
        bigTableManager.createPortfolioRow(portfolio);
        
        return portfolioRow;
    }

    public void addStocks(String username, String symbol, int shares, double price, String timestamp) {
        // Create new portfolio record for the newly added shares
        Portfolio newRecord = new Portfolio(
            username,
            symbol,
            shares,
            price,
            timestamp
        );
        bigTableManager.createPortfolioRow(newRecord);
    }

    public void removeStocks(String username, String symbol, int sharesToRemove, String timestamp) {
        try {
            bigTableManager.sellShares(username, symbol, sharesToRemove);
        } catch (Exception e) {
            // Log the error and rethrow as a runtime exception
            System.err.println("Failed to remove stocks: " + e.getMessage());
            throw new RuntimeException("Failed to remove stocks", e);
        }
    }

    public double getCashBalance(String username) {
        double balance = bigTableManager.getUserCashBalance(username);
        if (balance == -1) {
            balance = 1000.0;
            bigTableManager.createUserCashBalance(username, balance);
        }
        return balance;
    }

    //TODO: deprecate use of setCashBalance, we should only be using updateUserCashBalance
    public void setCashBalance(String username, double balance) {
        bigTableManager.updateUserCashBalance(username, balance);
    }

    public List<UserPortfolio> findAll() {
        return null;
    }
}
