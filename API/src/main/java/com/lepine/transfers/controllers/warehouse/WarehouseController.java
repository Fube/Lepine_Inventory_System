package com.lepine.transfers.controllers.warehouse;

import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.data.warehouse.WarehouseActiveLessUUIDLessDTO;
import com.lepine.transfers.services.warehouse.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;

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

        return all;
    }
}
