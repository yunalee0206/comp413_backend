package com.backend.owlfinance.Portfolio;

class InsufficientFundsException extends RuntimeException {
  InsufficientFundsException() {
    super("Insufficient funds in the portfolio.");
  }
}