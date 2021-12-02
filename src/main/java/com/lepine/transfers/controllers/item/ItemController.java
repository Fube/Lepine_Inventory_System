package com.lepine.transfers.controllers.item;

import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.services.item.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;

@RestController("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public Page<Item> getAll() {
        log.info("ItemController::getAll retrieving all items");
        final Page<Item> all = itemService.findAll();
        log.info("ItemController::getAll retrieved all items");

        return all;
    }

    @GetMapping(params = { "page", "size" })
    public Page<Item> getAll(
            @RequestParam
            @Min(value = 1, message = "Page number cannot be less than 1") int page,
            @RequestParam
            @Min(value = 1, message = "Page size cannot be less than 1") int size) {
        log.info("ItemController::getAll retrieving all items");
        final Page<Item> all = itemService.findAll(PageRequest.of(page - 1, 10));
        log.info("ItemController::getAll retrieved all items");

        return all;
    }
}
