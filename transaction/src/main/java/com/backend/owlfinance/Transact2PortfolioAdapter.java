package com.backend.owlfinance;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class Transact2PortfolioAdapter {
   
    private final WebClient webClient;

    @Autowired
    public Transact2PortfolioAdapter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080/portfolios").build();
    }

    @CircuitBreaker(name = "portfolioService", fallbackMethod = "fallbackCheckBalance")
    public boolean checkBalance(Long buyerId, double amount) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/checkbalance")
                .queryParam("userId", buyerId)
                .queryParam("amount", amount)
                .build())
            .retrieve()
            .bodyToMono(Boolean.class)
            .block();
    }

    @CircuitBreaker(name = "portfolioService", fallbackMethod = "fallbackCheckShare")
    public boolean checkShare(Long sellerId, String ticker, int amount) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/checkshare")
                .queryParam("userId", sellerId)
                .queryParam("ticker", ticker)
                .queryParam("amount", amount)
                .build())
            .retrieve()
            .bodyToMono(Boolean.class)
            .block();
    }

    @CircuitBreaker(name = "portfolioService", fallbackMethod = "fallbackUpdatePortfolio")
    public String updatePortfolio(Long buyerId, Long sellerId, Double amount, String ticker, int shares) {

        return webClient.put()
            .uri(uriBuilder -> uriBuilder.path("/update")
                .queryParam("buyerId", buyerId)
                .queryParam("sellerId", sellerId)
                .queryParam("amount", amount)
                .queryParam("ticker", ticker)
                .queryParam("shares", shares)
                .build())
            .retrieve()
            .bodyToMono(String.class)
            .block();
    }

    // Fallback method for checkBalance
    public boolean fallbackCheckBalance(Long userId, double amount, Throwable throwable) {
        System.out.println("Portfolio service is down, falling back to default balance check.");
        return false; // Default behavior for balance check
    }

    // Fallback method for checkBalance
    public boolean fallbackCheckShare(Long sellerId, String ticker, int amount, Throwable throwable) {
        System.out.println("Portfolio service is down, falling back to default share check.");
        return false; // Default behavior for balance check
    }

    // Fallback method for updateAccount
    public String fallbackUpdatePortfolio(Long buyerId, Long sellerId, Double amount, String ticker, int shares, Throwable throwable) {
        System.out.println("Portfolio service is down, falling back to default account update.");
        return "Failed to update account";
    }

    
}
