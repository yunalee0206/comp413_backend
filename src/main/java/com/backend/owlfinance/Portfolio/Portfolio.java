package com.backend.owlfinance.Portfolio;

import jakarta.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Portfolio {

  private @Id @GeneratedValue Long id;
  private Double balance;
  
  @ElementCollection
  @CollectionTable(name = "portfolio_stocks", joinColumns = @JoinColumn(name = "portfolio_id"))
  @MapKeyColumn(name = "stock_symbol")
  @Column(name = "quantity")
  private Map<String, Integer> stocks;

  private Long userId;
  private String username;

  public Portfolio() {
    this.balance = 0.0;
    this.stocks = new HashMap<>();
    this.username = "";
  }

  public Portfolio(String username) {
    this.balance = 0.0;
    this.stocks = new HashMap<>();
    this.username = username;
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

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}