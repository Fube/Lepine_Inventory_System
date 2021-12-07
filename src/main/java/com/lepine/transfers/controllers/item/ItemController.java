package com.lepine.transfers.controllers.item;

import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.item.ItemMapper;
import com.lepine.transfers.data.item.ItemUUIDLessDTO;
import com.lepine.transfers.services.item.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.UUID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemService itemService;
    private final ItemMapper itemMapper;

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
        final Page<Item> all = itemService.findAll(PageRequest.of(page - 1, size));
        log.info("ItemController::getAll retrieved all items");

        return all;
    }

    @PostMapping
    public Item create(@RequestBody @Valid ItemUUIDLessDTO itemUUIDLessDTO) {
        log.info("ItemController::create creating item");
        final Item mapped = itemMapper.toEntity(itemUUIDLessDTO);
        final Item created = itemService.create(mapped);
        log.info("ItemController::create created item");

        return created;
    }

    @PutMapping("/{uuid}")
    public Item update(@RequestBody @Valid ItemUUIDLessDTO itemUUIDLessDTO) {
        log.info("ItemController::update updating item");
        final Item mapped = itemMapper.toEntity(itemUUIDLessDTO);
        final Item updated = itemService.update(mapped);
        log.info("ItemController::update updated item");

        return updated;
    }

    @DeleteMapping("/{uuid}")
    public void delete(@PathVariable  UUID uuid) {
        log.info("ItemController::delete deleting item");
        itemService.delete(uuid);
        log.info("ItemController::delete deleted item");
    }
}
