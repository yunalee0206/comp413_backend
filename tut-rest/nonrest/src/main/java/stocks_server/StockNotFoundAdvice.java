package stock_server;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class StockNotFoundAdvice {

  @ExceptionHandler(StockNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  String stockNotFoundHandler(StockNotFoundException ex) {
    return ex.getMessage();
  }
}