package com.lepine.transfers.controllers.warehouse;

import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.data.warehouse.WarehouseActiveLessUUIDLessDTO;
import com.lepine.transfers.services.warehouse.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class WarehouseController {

    private final WarehouseService warehouseService;

    public Warehouse create(@Valid WarehouseActiveLessUUIDLessDTO warehouseActiveLessUUIDLessDTO) {
        log.info("Attempting to create a new warehouse {}", warehouseActiveLessUUIDLessDTO);
        final Warehouse warehouse = warehouseService.create(warehouseActiveLessUUIDLessDTO);
        log.info("Warehouse created {}", warehouse);

        return warehouse;
    }
}
