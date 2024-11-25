package com.backend.owlfinance.database.obj;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record StockPrice(String stockSymbol, String dateTime, double low, double high, int volume, double open, double close) {


    public StockPrice(String stockSymbol, double low, double high, int volume, double open, double close) {
        this(
            stockSymbol,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            low,
            high,
            volume,
            open,
            close
        );
    }

    @Override
    public String toString() {
        return "Stock Price Info:" + this.stockSymbol + " " + this.dateTime + " " + this.low + " " + this.high + " " + this.volume + " " + this.open + " " +  this.close;
    }
}