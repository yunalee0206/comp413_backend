package com.backend.owlfinance.database.obj;

public record Transaction(String username, String stockSymbol, int numShares, double sharePrice, String dateTime) {

    @Override
    public String toString() {
        return this.username + " " + this.stockSymbol + " " + this.numShares + " " + this.sharePrice + " " + this.dateTime;
    }
}
