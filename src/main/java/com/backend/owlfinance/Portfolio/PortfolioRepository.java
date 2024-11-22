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
        if (portfolioRows.isEmpty()) {
            return Optional.empty();
        }

        // Convert BigTable Portfolio records to UserPortfolio
        Map<String, Integer> stocks = new HashMap<>();
        double totalBalance = 0.0;

        for (Portfolio row : portfolioRows) {
            stocks.merge(row.stockSymbol(), row.numShares(), Integer::sum);
            totalBalance += (row.sharePrice() * row.numShares());
        }

        UserPortfolio userPortfolio = new UserPortfolio();
        userPortfolio.setUsername(username);
        userPortfolio.setStocks(stocks);
        userPortfolio.setBalance(totalBalance);

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
