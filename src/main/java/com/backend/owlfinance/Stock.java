package com.backend.owlfinance;

import java.util.Objects;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;


@Entity
class Stock {

  private @Id
  @GeneratedValue Long id;
  private String ticker;
  private Double price;
  private Date date;

  Stock() {}

  Stock(String ticker, Double price, Date date) {

    this.ticker = ticker;
    this.price = price;
    this.date = date;
  }

  public Long getId() {
    return this.id;
  }

  public String getTicker() {
    return this.ticker;
  }

  public Double getPrice() {
    return this.price;
  }

  public Date getDate() {
    return this.date;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setTicker(String ticker) {
    this.ticker = ticker;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o)
      return true;
    if (!(o instanceof Stock))
      return false;
    Stock stock = (Stock) o;
    return Objects.equals(this.id, stock.id) && Objects.equals(this.ticker, stock.ticker)
        && Objects.equals(this.price, stock.price) && Objects.equals(this.date, stock.date);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.ticker, this.price, this.date);
  }

  @Override
  public String toString() {
    return "Stock{" + "id=" + this.id + ", ticker='" + this.ticker + '\'' + ", price='" + this.price + '\'' + ", date='" + this.date + '\'' +'}';
  }
}