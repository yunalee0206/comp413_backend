package com.backend.owlfinance.service;

import com.backend.owlfinance.dto.TradeDto;
import com.backend.owlfinance.entity.TradeEntity;
import com.backend.owlfinance.repository.TradeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TradeService {

    @Autowired
    private TradeRepository tradeRepository;

    public boolean insertTrade(TradeDto tradeDto) {
        // TODO: Create a new trade recorde to DB
        log.info("TradeService - insertTrade() ...");

        TradeEntity tradeEntity = new TradeEntity();
        tradeEntity.setTradeId(tradeDto.getTradeId());
        tradeEntity.setTradeType(tradeDto.getTradeType());

        tradeRepository.save(tradeEntity);

        return true; // set it to true for now
    }

    public TradeDto getTradeById(String tradeId) {
        TradeEntity tradeEntity = tradeRepository.findById(tradeId).get();

        TradeDto tradeDto = new TradeDto();
        tradeDto.setTradeId(tradeEntity.getTradeId());
//        tradeDto.set
        return tradeDto;
    }
}
