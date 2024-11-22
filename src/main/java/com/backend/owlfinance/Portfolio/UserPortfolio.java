package com.backend.owlfinance.Portfolio;

import java.util.HashMap;
import java.util.Map;

public class UserPortfolio {
    private String username;
    private Double balance;
    private Map<String, Integer> stocks;

    public UserPortfolio() {
        this.balance = 0.0;
        this.stocks = new HashMap<>();
    }

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }
    public Map<String, Integer> getStocks() { return stocks; }
    public void setStocks(Map<String, Integer> stocks) { this.stocks = stocks; }
} 