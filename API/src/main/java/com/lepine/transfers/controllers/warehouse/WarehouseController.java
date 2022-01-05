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
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class WarehouseController {

    private final WarehouseService warehouseService;

    public Warehouse create(@Valid WarehouseActiveLessUUIDLessDTO warehouseActiveLessUUIDLessDTO) {
        log.info("Creating a new warehouse {}", warehouseActiveLessUUIDLessDTO);
        final Warehouse warehouse = warehouseService.create(warehouseActiveLessUUIDLessDTO);
        log.info("Warehouse created {}", warehouse);

        return warehouse;
    }

    public Page<Warehouse> getAll(
            @Min(value = 1, message = "{pagination.page.min}") int pageNumber,
            @Min(value = 1, message = "{pagination.size.min}") int pageSize) {
        log.info("Getting all warehouses with pageNumber {} and pageSize {}", pageNumber, pageSize);
        final Page<Warehouse> all = warehouseService.findAll(PageRequest.of(pageNumber, pageSize));
        log.info("Warehouses found {}", all);

        return OneIndexedPageAdapter.of(all);
    }

    public Warehouse getByUuid(UUID uuid) {
        log.info("Getting warehouse with uuid {}", uuid);
        final Warehouse warehouse = warehouseService.findByUuid(uuid)
                .orElseThrow(() -> new WarehouseNotFoundException(uuid));
        log.info("Warehouse found {}", warehouse);

        return warehouse;
    }

    public void deleteByUuid(UUID uuid) {
        log.info("Deleting warehouse with uuid {}", uuid);
        warehouseService.delete(uuid);
    }

    public Warehouse update(UUID uuid, @Valid WarehouseUUIDLessDTO warehouseUUIDLessDTO) {
        log.info("Updating warehouse with uuid {}", uuid);
        return warehouseService.update(uuid, warehouseUUIDLessDTO);
    }
}
