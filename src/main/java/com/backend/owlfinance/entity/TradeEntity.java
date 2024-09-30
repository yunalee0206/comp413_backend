package com.backend.owlfinance.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class TradeEntity {
    @Id
    private String tradeId;
    private String tradeType;

    public String getTradeId() {
        return tradeId;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }
}
