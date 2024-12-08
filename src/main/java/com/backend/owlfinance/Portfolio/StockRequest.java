package com.backend.owlfinance.Portfolio;

public class StockRequest {
    private String symbol;
    private int shares;
    private double price;
    private String timestamp;

    public String getSymbol() { return symbol; }

    public int getShares() { return shares; }

    public double getPrice() { return price; }

    public String getTimestamp() { return timestamp; }

    public void setSymbol(String symbol) { this.symbol = symbol; }

    public void setShares(int shares) { this.shares = shares; }

    public void setPrice(double price) { this.price = price; }

    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
