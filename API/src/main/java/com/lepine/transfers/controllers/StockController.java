package com.lepine.transfers.controllers;

import com.lepine.transfers.data.OneIndexedPageAdapter;
import com.lepine.transfers.data.stock.Stock;
import com.lepine.transfers.data.stock.StockUuidLessItemLessWarehouseLess;
import com.lepine.transfers.data.stock.StockUuidLessItemUuidWarehouseUuid;
import com.lepine.transfers.services.stock.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
@Slf4j
@Validated
@CrossOrigin(origins = "${cors.origin}")
public class StockController {

    private final StockService stockService;

    @PostMapping
    @ResponseStatus(value = CREATED)
    public Stock create(@RequestBody StockUuidLessItemUuidWarehouseUuid dto) {
        log.info("Create stock {}", dto);
        return stockService.create(dto);
    }

    @GetMapping
    public Page<Stock> getAll(
            @RequestParam(required = false, defaultValue = "1") @Min(value = 1, message = "{pagination.page.min}") int page,
            @RequestParam(required = false, defaultValue = "10") @Min(value = 1, message = "{pagination.size.min}") int size
    ){
        log.info("Getting all stocks");
        return OneIndexedPageAdapter.of(stockService.findAll(PageRequest.of(page - 1, size)));
    }

    @PutMapping("/{uuid}")
    public Stock update(@PathVariable("uuid") UUID uuid, @RequestBody StockUuidLessItemLessWarehouseLess dto) {
        log.info("Update stock {}", dto);
        return stockService.update(uuid, dto);
    }

    @DeleteMapping("/{uuid}")
    public void delete(@PathVariable("uuid") UUID uuid) {
        log.info("Delete stock {}", uuid);
        stockService.delete(uuid);
    }
}
