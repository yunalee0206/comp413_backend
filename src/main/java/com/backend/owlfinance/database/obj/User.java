package com.backend.owlfinance.database.obj;

public record User(String username, String password, String token, int cash_balance) {

    @Override
    public String toString() {
        return this.username + " " + this.password + " " + this.token + " " + this.cash_balance;
    }
}
