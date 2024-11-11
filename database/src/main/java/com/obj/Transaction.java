package com.obj;

public record Transaction(String username, String transactionType, String stockSymbol, int numShares, double sharePrice) {

    @Override
    public String toString() {
        return this.username + " " + this.transactionType + " " + this.stockSymbol + " " + this.numShares + " " + this.sharePrice;
    }
}
