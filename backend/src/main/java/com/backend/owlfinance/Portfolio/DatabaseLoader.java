package com.backend.owlfinance.Portfolio;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DatabaseLoader {

    @Bean
    CommandLineRunner initPortDatabase(PortfolioRepository repository) {
        return args -> {
            // Create first portfolio
            Portfolio portfolio1 = new Portfolio();
            portfolio1.setUserId(1L);
            portfolio1.setBalance(10000.0);
            Map<String, Integer> stocks1 = new HashMap<>();
            stocks1.put("AAPL", 10);
            stocks1.put("GOOGL", 5);
            portfolio1.setStocks(stocks1);

            // Create second portfolio
            Portfolio portfolio2 = new Portfolio();
            portfolio2.setUserId(2L);
            portfolio2.setBalance(25000.0);
            Map<String, Integer> stocks2 = new HashMap<>();
            stocks2.put("MSFT", 15);
            stocks2.put("TSLA", 8);
            portfolio2.setStocks(stocks2);

            // Create third portfolio
            Portfolio portfolio3 = new Portfolio();
            portfolio3.setUserId(3L);
            portfolio3.setBalance(5000.0);
            Map<String, Integer> stocks3 = new HashMap<>();
            stocks3.put("AMZN", 3);
            stocks3.put("META", 12);
            portfolio3.setStocks(stocks3);

            // Save all portfolios
            repository.save(portfolio1);
            repository.save(portfolio2);
            repository.save(portfolio3);

            System.out.println("Database has been populated with test data!");
        };
    }
}