package com.backend.owlfinance.Portfolio;

public class PortfolioNotFoundException extends RuntimeException {
    public PortfolioNotFoundException(Long id) {
        super("Could not find portfolio for user " + id);
    }
}