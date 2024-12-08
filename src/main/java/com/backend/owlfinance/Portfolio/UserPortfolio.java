package com.backend.owlfinance.Portfolio;

import java.util.HashMap;
import java.util.Map;

public class UserPortfolio {
    private String username;
    private Double balance;
    private Map<String, StockPosition> stocks;

    public UserPortfolio() {
        this.balance = 0.0;
        this.stocks = new HashMap<>();
    }

    // Inner class to hold stock position details
    public static class StockPosition {
        private int shares;
        private double avgBuyPrice;
        private double currentPrice;
        private double totalReturn;
        private double returnPercentage;
        private double currentValue;
        // Constructor
        public StockPosition(int shares, double avgBuyPrice, double currentPrice, double totalReturn) {
            this.shares = shares;
            this.avgBuyPrice = avgBuyPrice;
            this.currentPrice = currentPrice;
            this.totalReturn = totalReturn;
            this.currentValue = shares * currentPrice;
            this.returnPercentage = avgBuyPrice != 0 ? (totalReturn / (shares * avgBuyPrice)) * 100 : 0;
        }

        // Getters
        public int getShares() { return shares; }
        public double getAvgBuyPrice() { return avgBuyPrice; }
        public double getCurrentPrice() { return currentPrice; }
        public double getTotalReturn() { return totalReturn; }
        public double getReturnPercentage() { return returnPercentage; }
        public double getCurrentValue() { return currentValue; }
    }

    // Updated getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }
    public Map<String, StockPosition> getStocks() { return stocks; }
    public void setStocks(Map<String, StockPosition> stocks) { this.stocks = stocks; }
} 