package com.backend.owlfinance.Portfolio;

import java.time.LocalDateTime;

public class PortfolioRow {
    private String username;
    private String symbol;
    private Integer quantity;
    private Double purchasePrice;
    private LocalDateTime purchaseDate;

    // Default constructor
    public PortfolioRow() {}

    // Constructor with all fields
    public PortfolioRow(String username, String symbol, Integer quantity, Double purchasePrice, LocalDateTime purchaseDate) {
        this.username = username;
        this.symbol = symbol;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
        this.purchaseDate = purchaseDate;
    }

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public Double getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(Double purchasePrice) { this.purchasePrice = purchasePrice; }
    
    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }
} 