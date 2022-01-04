package com.lepine.transfers.services.warehouse;

import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.data.warehouse.WarehouseActiveLessUUIDLessDTO;
import com.lepine.transfers.data.warehouse.WarehouseMapper;
import com.lepine.transfers.data.warehouse.WarehouseRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepo warehouseRepo;
    private final WarehouseMapper warehouseMapper;

    @Override
    public Warehouse create(WarehouseActiveLessUUIDLessDTO warehouse) {
        log.info("Creating warehouse with zip {}", warehouse.getZipCode());
        final Warehouse save = warehouseRepo.save(warehouseMapper.toEntity(warehouse));
        log.info("Warehouse created with uuid {}", save.getUuid());

        return save;
    }
}
