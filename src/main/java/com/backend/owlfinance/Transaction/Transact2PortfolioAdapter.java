package com.backend.owlfinance.Transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.backend.owlfinance.Portfolio.PortfolioController;

@Component
public class Transact2PortfolioAdapter {

    @Autowired
    private PortfolioController portfolioController;
   
    public Transact2PortfolioAdapter(PortfolioController portfolioController) {
        this.portfolioController = portfolioController;
    }

    public boolean checkBalance(String buyer, double amount) {
        // return portfolioController.checkBalance(buyer, amount);
        return true;
    }

    public boolean checkShare(String seller, String ticker, int amount) {
        // return portfolioController.checkShare(seller, ticker, amount);
        return true;
    }

    public void buy(Long buyerId, double price, String ticker, int quantity) {
        
    }

    public void sell(Long sellerId, double price, String ticker, int quantity) {
        
    }

}
