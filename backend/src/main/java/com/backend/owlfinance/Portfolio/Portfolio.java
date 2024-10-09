package com.backend.owlfinance;

import jakarta.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
class Portfolio {

  private @Id @GeneratedValue Long id;
  private Double balance;
  
  @ElementCollection
  @CollectionTable(name = "portfolio_stocks", joinColumns = @JoinColumn(name = "portfolio_id"))
  @MapKeyColumn(name = "stock_symbol")
  @Column(name = "quantity")
  private Map<String, Integer> stocks;

  private Long userId;

  Portfolio() {
    this.balance = 0.0;
    this.stocks = new HashMap<>();
  }

  public Long getId() {
    return this.id;
  }

  public Double getBalance() {
    return this.balance;
  }

  public void setBalance(Double balance) {
    this.balance = balance;
  }

  public Map<String, Integer> getStocks() {
    return this.stocks;
  }

  public void setStocks(Map<String, Integer> stocks) {
    this.stocks = stocks;
  }

  public Long getUserId() {
    return this.userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }
}