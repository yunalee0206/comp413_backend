package com.backend.owlfinance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.backend.owlfinance.Portfolio.PortfolioController;
import org.springframework.transaction.annotation.Transactional;

@Component
public class Transact2PortfolioAdapter {

    @Autowired
    private PortfolioController portfolioController;
   
    public Transact2PortfolioAdapter(PortfolioController portfolioController) {
        this.portfolioController = portfolioController;
    }

    public boolean checkBalance(Long buyerId, double amount) {
        return portfolioController.checkBalance(buyerId, amount);
    }

    public boolean checkShare(Long sellerId, String ticker, int amount) {
        return portfolioController.checkShare(sellerId, ticker, amount);
    }

    @Transactional
    public String updatePortfolio(Long buyerId, Long sellerId, Double amount, String ticker, Integer shares) {
        return portfolioController.update(buyerId, sellerId, amount, ticker, shares);
    }

}
