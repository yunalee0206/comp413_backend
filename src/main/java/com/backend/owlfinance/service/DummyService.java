package com.backend.owlfinance.service;

import com.backend.owlfinance.dto.DummyDto;
import com.backend.owlfinance.entity.DummyEntity;
import com.backend.owlfinance.repository.DummyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DummyService {

    @Autowired
    private DummyRepository dummyRepository;

    public boolean registerDummyItem(DummyDto dummyDto) {
        // TODO: logic to insert data to DB
        log.info("DummyService... registerDummyItem()... ");

        // JPA Example
        DummyEntity dummyEntity = new DummyEntity();
        dummyEntity.setDummyId(dummyDto.getDummyId());
        dummyEntity.setDummyName(dummyDto.getDummyName());

        dummyRepository.save(dummyEntity); // insert

        return true;
    }


    public DummyDto getDummyItemById(String dummyId) {
        DummyEntity dummyEntity = dummyRepository.findById(dummyId).get();

        DummyDto dummyDto = new DummyDto();
        dummyDto.setDummyId(dummyEntity.getDummyId());
        dummyDto.setDummyName(dummyEntity.getDummyName());

        return dummyDto;
    }


}
