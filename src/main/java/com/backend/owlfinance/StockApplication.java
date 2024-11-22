package com.backend.owlfinance;

import com.backend.owlfinance.database.obj.Transaction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.backend.owlfinance.database.bigtable.BigTableManager;
import com.backend.owlfinance.external.StockFetcher;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
public class StockApplication {

  public static void main(String... args) throws IOException {
    String projectId = "rice-comp-539-spring-2022";
    String instanceId = "comp-539-bigtable";

    System.out.println("Setting up BigTable...");
    BigTableManager bt = new BigTableManager(projectId, instanceId);
    Transaction test = new Transaction("Slim", "Buy", "APPL", 10, 200.0, "dbejbhedbc");
    String testRowKey = bt.createTransaction(test);
    System.out.println("New transaction made: " + testRowKey);
    Transaction found = bt.getTransaction(testRowKey);
    System.out.println("Transaction (" + testRowKey + ") found: " + found.toString());

    System.out.println("Trying to find incorrect row key: ");
    Transaction not = bt.getTransaction(testRowKey + "slim");

    // TODO BEFORE UNCOMMENTING: MAKE SURE ALPHA VANTAGE API LINK IN 
    // EXTERNAL STOCK FETCHER IS POPULATED
    
    // Add daily data
//    try {
//      String stockCode = "AAPL";
//
//      // Fetch and save minute-by-minute data for a specific date
//      String targetDate = "2024-11-15"; // Replace with your target date in YYYY-MM-DD format
//      StockFetcher.getPriceDaily(stockCode, targetDate);
//
//    } catch (Exception e) {
//      e.printStackTrace();
//    }


    SpringApplication.run(StockApplication.class, args);
  }
}