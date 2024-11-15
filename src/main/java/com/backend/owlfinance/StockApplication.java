package com.backend.owlfinance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
//import com.bigtable.BigTableManager;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
public class StockApplication {

  public static void main(String... args) throws IOException {
//    String projectId = "rice-comp-539-spring-2022";
//    String instanceId = "comp-539-bigtable";
//
//    BigTableManager bt = new BigTableManager(projectId, instanceId);
    SpringApplication.run(StockApplication.class, args);
  }
}