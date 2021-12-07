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
import javax.validation.constraints.NotNull;
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
        log.info("retrieving all items");
        final Page<Item> all = itemService.findAll();
        log.info("retrieved all items");

        return all;
    }

    @GetMapping(params = { "page", "size" })
    public Page<Item> getAll(
            @RequestParam
            @Min(value = 1, message = "Page number cannot be less than 1") int page,
            @RequestParam
            @Min(value = 1, message = "Page size cannot be less than 1") int size) {
        log.info("retrieving all items");
        final Page<Item> all = itemService.findAll(PageRequest.of(page - 1, size));
        log.info("retrieved all items");

        return all;
    }

    @GetMapping("/{uuid}")
    public Item getByUuid(@PathVariable @NotNull UUID uuid) {
        log.info("retrieving item by uuid {}", uuid);
        final Item item = itemService.findByUuid(uuid);
        log.info("retrieved item by uuid {}", uuid);

        return item;
    }

    @PostMapping
    public Item create(@RequestBody @Valid ItemUUIDLessDTO itemUUIDLessDTO) {
        log.info("creating item");
        final Item mapped = itemMapper.toEntity(itemUUIDLessDTO);
        final Item created = itemService.create(mapped);
        log.info("created item");

        return created;
    }

    @PutMapping("/{uuid}")
    public Item update(
            @PathVariable UUID uuid,
            @RequestBody @Valid ItemUUIDLessDTO itemUUIDLessDTO) {
        log.info("updating item");

        final Item mapped = itemMapper.toEntity(itemUUIDLessDTO);
        mapped.setUuid(uuid);
        final Item updated = itemService.update(mapped);

        log.info("updated item");

        return updated;
    }

    @DeleteMapping("/{uuid}")
    public void delete(@PathVariable  UUID uuid) {
        log.info("deleting item");
        itemService.delete(uuid);
        log.info("deleted item");
    }
}
