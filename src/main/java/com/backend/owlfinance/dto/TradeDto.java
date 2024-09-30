package com.backend.owlfinance.dto;

public class TradeDto {
//    private String username;
//    private String tradeType;
//    private String symbol;
//    private int numShares;
//    private float sharePrice;
    // TODO: timestamp

    private String tradeId;
    private String tradeType;

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getTradeId() {
        return tradeId;
    }

    public String getTradeType() {
        return tradeType;
    }
}
