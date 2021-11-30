package com.lepine.transfers.controllers;

import com.lepine.transfers.data.Item;
import com.lepine.transfers.data.ItemRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/items")
@RequiredArgsConstructor
@Slf4j()
public class ItemController {

    private final ItemRepo itemRepo;

    @GetMapping
    public Page<Item> getAll() {
        log.info("ItemController::getAll retrieving all items");
        final Page<Item> all = itemRepo.findAll(PageRequest.of(0, 10));
        log.info("ItemController::getAll retrieved all items");

        return all;
    }

    @GetMapping(params = { "page" })
    public Page<Item> getAll(@RequestParam int page) {
        log.info("ItemController::getAll retrieving all items");
        final Page<Item> all = itemRepo.findAll(PageRequest.of(page, 10));
        log.info("ItemController::getAll retrieved all items");

        return all;
    }
}
