package com.backend.owlfinance;

class StockNotFoundException extends RuntimeException {

  StockNotFoundException(String ticker) {
    super("Could not find stock " + ticker);
  }
}