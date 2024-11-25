package com.backend.owlfinance.Portfolio;

public class InsufficientSharesException extends RuntimeException {

    public InsufficientSharesException(String message) {
        super(message);
    }

}
