package com.backend.owlfinance.database.obj;

public record Transaction(String username, String transactionType, String stockSymbol, int numShares, double sharePrice, String timestamp, String uuid) {

    @Override
    public String toString() {
        return this.username + " " + this.transactionType + " " + this.stockSymbol + " " + this.numShares + " " + this.sharePrice + " " + this.timestamp + " " + this.uuid;
    }
}
