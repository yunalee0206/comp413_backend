package com.backend.owlfinance.Portfolio;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class PortfolioNotFoundAdvice {

  @ExceptionHandler(InsufficientFundsException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  String insufficientFundsHandler(InsufficientFundsException ex) {
    return ex.getMessage();
  }
}