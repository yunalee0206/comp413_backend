package com.backend.owlfinance.Portfolio;

public class PortfolioNotFoundException extends RuntimeException {
    public PortfolioNotFoundException(String username) {
        super("Could not find portfolio for user " + username);
    }
}