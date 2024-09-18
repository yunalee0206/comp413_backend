package stock_server

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LoadDatabase {

  private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

  @Bean
  CommandLineRunner initDatabase(StockRepository repository) {

    Date newDate = new Date()

    return args -> {
      log.info("Preloading " + repository.save(new Stock("AAPL", 10, newDate)));
      log.info("Preloading " + repository.save(new Stock("MSFT", 8, newDate)));
    };
  }
}