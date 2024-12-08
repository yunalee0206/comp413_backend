package com.backend.owlfinance.Portfolio;

public class StockRemoveRequest {
    private String symbol;
    private int shares;
    private String timestamp;

    public String getSymbol() { return symbol; }
    public int getShares() { return shares; }
    public String getTimestamp() { return timestamp; }

    public void setSymbol(String symbol) { this.symbol = symbol; }
    public void setShares(int shares) { this.shares = shares; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
