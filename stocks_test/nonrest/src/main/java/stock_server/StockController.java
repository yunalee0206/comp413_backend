package stock_server;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class StockController {

  private final StockRepository repository;

  StockController(StockRepository repository) {
    this.repository = repository;
  }


  // Aggregate root
  // tag::get-aggregate-root[]
  @GetMapping("/stocks")
  List<Stock> all() {
    return repository.findAll();
  }
  // end::get-aggregate-root[]

  @PostMapping("/stocks")
  Stock newStock(@RequestBody Stock newStock) {
    return repository.save(newStock);
  }

  // Single item
  
  @GetMapping("/stocks/{ticker}")
  Employee one(@PathVariable String ticker) {
    
    return repository.findById(ticker)
      .orElseThrow(() -> new StockNotFoundException(ticker));
  }

  @PutMapping("/stocks/{ticker}")
  Stock replaceStock(@RequestBody Stock newStock, @PathVariable String ticker) {
    
    return repository.findById(ticker)
      .map(stock -> {
        stock.setName(newStock.getName());//id or ticker?
        stock.setPrice(newStock.getPrice());
        stock.setDate(newStock.getDate());
        return repository.save(stock);
      })
      .orElseGet(() -> {
        return repository.save(newStock);
      });
  }

  @DeleteMapping("/stocks/{ticker}")
  void deleteStock(@PathVariable String ticker) {
    repository.deleteById(ticker);
  }
}