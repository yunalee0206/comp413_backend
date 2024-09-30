package com.backend.owlfinance.controller;

import com.backend.owlfinance.dto.DummyDto;
import com.backend.owlfinance.dto.DummyResponseDto;
import com.backend.owlfinance.service.DummyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class DummyController {

    @Autowired
    private DummyService dummyService;

    @GetMapping("/dummy")
    public String dummy() {
        log.info("dummy");
        return "hello this is dummy";
    }

    @GetMapping("/dummies")
    public String getDummy(@RequestParam("dummyId") String userId) {
        log.info("dummyId: {}", userId);
        return "getDummy ok";
    }

    // OR
    @GetMapping("/dummies2/{id}")
    public String getDummy2(@PathVariable("id") String id) {
        log.info("id: {}", id);
        return "getDummy2 ok";
    }

    @PostMapping("/dummyItems")
    public DummyResponseDto registerDummyItem(@RequestBody DummyDto dummyItem) {
        log.info("item: {}", dummyItem);

        boolean hasRegistered = dummyService.registerDummyItem(dummyItem);
        if (hasRegistered) {
            DummyResponseDto responseDummyDto = new DummyResponseDto();
            responseDummyDto.setResponseMsg("ok");
            return responseDummyDto;
        }

        DummyResponseDto responseDummyDto = new DummyResponseDto();
        responseDummyDto.setResponseMsg("fail");
        return responseDummyDto;
    }
    @GetMapping("/dummyItems")
    public DummyDto getDummyItem(@RequestParam("dummyId") String dummyId) {
        DummyDto res = dummyService.getDummyItemById(dummyId);
        return res;
    }
}
