package com.backend.owlfinance;

class InsufficientFundsException extends RuntimeException {
  InsufficientFundsException() {
    super("Insufficient funds in the portfolio.");
  }
}