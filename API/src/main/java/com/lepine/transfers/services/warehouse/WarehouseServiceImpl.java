package com.lepine.transfers.services.warehouse;

import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.data.warehouse.WarehouseActiveLessUUIDLessDTO;
import com.lepine.transfers.data.warehouse.WarehouseMapper;
import com.lepine.transfers.data.warehouse.WarehouseRepo;
import com.lepine.transfers.exceptions.warehouse.DuplicateZipCodeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepo warehouseRepo;
    private final WarehouseMapper warehouseMapper;

    @Override
    public Warehouse create(WarehouseActiveLessUUIDLessDTO warehouse) {

        if(warehouseRepo.findByZipCode(warehouse.getZipCode()).isPresent()) {
            log.error("Warehouse with zip code {} already exists", warehouse.getZipCode());
            throw new DuplicateZipCodeException(warehouse.getZipCode());
        }

        log.info("Creating warehouse with zip {}", warehouse.getZipCode());
        final Warehouse save = warehouseRepo.save(warehouseMapper.toEntity(warehouse));
        log.info("Warehouse created with uuid {}", save.getUuid());

        return save;
    }

    @Override
    public void delete(UUID uuid) {
        log.info("Deleting warehouse with uuid {}", uuid);
        warehouseRepo.deleteByUuid(uuid);
        log.info("Warehouse deleted");
    }
}
