package com.lepine.transfers.controllers.warehouse;

import com.lepine.transfers.data.OneIndexedPageAdapter;
import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.data.warehouse.WarehouseActiveLessUUIDLessDTO;
import com.lepine.transfers.data.warehouse.WarehouseUUIDLessDTO;
import com.lepine.transfers.exceptions.warehouse.WarehouseNotFoundException;
import com.lepine.transfers.services.warehouse.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/warehouses")
@RequiredArgsConstructor
@Slf4j
@Validated
@CrossOrigin(origins = "${cors.origin}")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    @ResponseStatus(value = CREATED)
    public Warehouse create(@RequestBody @Valid WarehouseActiveLessUUIDLessDTO warehouseActiveLessUUIDLessDTO) {
        log.info("Creating a new warehouse {}", warehouseActiveLessUUIDLessDTO);
        final Warehouse warehouse = warehouseService.create(warehouseActiveLessUUIDLessDTO);
        log.info("Warehouse created {}", warehouse);

        return warehouse;
    }

    @GetMapping
    public Page<Warehouse> getAll(
            @RequestParam(required = false, defaultValue = "1") @Min(value = 1, message = "{pagination.page.min}") int page,
            @RequestParam(required = false, defaultValue = "1") @Min(value = 1, message = "{pagination.size.min}") int size) {
        log.info("Getting all warehouses with pageNumber {} and pageSize {}", page, size);
        final Page<Warehouse> all = warehouseService.findAll(PageRequest.of(page, size));
        log.info("Warehouses found {}", all);

        return OneIndexedPageAdapter.of(all);
    }

    @GetMapping("/{uuid}")
    public Warehouse getByUuid(@PathVariable UUID uuid) {
        log.info("Getting warehouse with uuid {}", uuid);
        final Warehouse warehouse = warehouseService.findByUuid(uuid)
                .orElseThrow(() -> new WarehouseNotFoundException(uuid));
        log.info("Warehouse found {}", warehouse);

        return warehouse;
    }

    @DeleteMapping("/{uuid}")
    public void deleteByUuid(@PathVariable UUID uuid) {
        log.info("Deleting warehouse with uuid {}", uuid);
        warehouseService.delete(uuid);
    }

    @PutMapping("/{uuid}")
    public Warehouse update(
            @PathVariable UUID uuid,
            @RequestBody @Valid WarehouseUUIDLessDTO warehouseUUIDLessDTO) {
        log.info("Updating warehouse with uuid {}", uuid);
        return warehouseService.update(uuid, warehouseUUIDLessDTO);
    }
}
