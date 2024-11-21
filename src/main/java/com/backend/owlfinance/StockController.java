package com.backend.owlfinance;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.backend.owlfinance.database.bigtable.BigTableManager;
import com.backend.owlfinance.database.obj.StockPrice;

@RestController
class StockController {

//  private final StockRepository repository;

  StockController(StockRepository repository) {
//    this.repository = repository;
  }

  // Aggregate root
  // tag::get-aggregate-root[]
  @GetMapping("/stocks")
  String all() {
    return BigTableManager.getAllStockPrices();
//    return repository.findAll();
  }
  // end::get-aggregate-root[]

//  @PostMapping("/stocks")
//  Stock newStock(@RequestBody Stock newStock) {
//    Date newDate = new Date();
//
//    Stock newStockWithDate = new Stock();
//
//    newStockWithDate.setId(newStock.getId());
//    newStockWithDate.setTicker(newStock.getTicker());
//    newStockWithDate.setPrice(newStock.getPrice());
//    newStockWithDate.setDate(newDate);
//
//    return repository.save(newStockWithDate);
//  }

  // Single item
  
  @GetMapping("/stocks/{ticker}/{date}/{time}")
  StockPrice one(@PathVariable String ticker, @PathVariable String date, @PathVariable String time) {

    String rowKey = ticker + "#" + date + " " + time;

    return BigTableManager.getStockPrice(rowKey);
    
//    return repository.findById(ticker)
//      .orElseThrow(() -> new StockNotFoundException(ticker));
  }

//  @PutMapping("/stocks/{ticker}")
//  Stock replaceStock(@RequestBody Stock newStock, @PathVariable String ticker) {
//    Date newDate = new Date();
//
//    return repository.findById(ticker)
//      .map(stock -> {
//        stock.setId(newStock.getId());//id or ticker?
//        stock.setPrice(newStock.getPrice());
//        stock.setDate(newDate);
//        return repository.save(stock);
//      })
//      .orElseGet(() -> {
//        return repository.save(newStock);
//      });
//  }
//
//  @DeleteMapping("/stocks/{ticker}")
//  void deleteStock(@PathVariable String ticker) {
//    repository.deleteById(ticker);
//  }
}