package com.backend.owlfinance.Transaction;

// import jakarta.persistence.Entity;
// import jakarta.persistence.Id;
// import jakarta.persistence.Table;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
import java.util.Date;

public class Order {

    private String id;
    private String username;    
    private String symbol;
    private int quantity;
    private double price;
    private String type;
    private Date timestamp;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String userId) {
        this.username = userId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id != null && id.equals(order.id);
    }

    @Override
    public String toString() {
        return "Order Info:" + this.id + " " + this.username + " " + this.symbol + " " + this.quantity + " " + this.price + " " + this.type + " " + this.timestamp;
    }

}