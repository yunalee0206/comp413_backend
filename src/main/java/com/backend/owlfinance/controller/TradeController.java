package com.backend.owlfinance.controller;

import com.backend.owlfinance.dto.TradeDto;
import com.backend.owlfinance.service.TradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class TradeController {

    @Autowired
    private TradeService tradeService;

    @PostMapping("/trades")
    public String insertTrade(@RequestBody TradeDto trade) {
        log.info("trade: {}", trade);
        boolean hasInserted = tradeService.insertTrade(trade);
        if (hasInserted) {
            return "success";
        }
        return "fail";
    }

    @GetMapping("/trades")
    public TradeDto getTrade(@RequestParam("tradeId") String tradeId) {
        TradeDto res = tradeService.getTradeById(tradeId);
        return res;
    }
}
