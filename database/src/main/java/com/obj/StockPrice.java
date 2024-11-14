package com.obj;

public record StockPrice(String stockSymbol, String dateTime, double low, double high, int volume, double open, double close) {

    @Override
    public String toString() {
        return "Stock Price Info:" + this.stockSymbol + " " + this.dateTime + " " + this.low + " " + this.low + " " + this.high + " " + this.volume + " " + this.open + " " +  this.close;
    }
}