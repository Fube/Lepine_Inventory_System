package com.lepine.transfers.services.warehouse;

import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.data.warehouse.WarehouseActiveLessUUIDLessDTO;
import com.lepine.transfers.data.warehouse.WarehouseUUIDLessDTO;

import javax.validation.Valid;
import java.util.UUID;

public interface WarehouseService {
    Warehouse create(@Valid WarehouseActiveLessUUIDLessDTO warehouse);

    void delete(UUID uuid);

    void update(UUID uuid, @Valid WarehouseUUIDLessDTO toUpdate);
}
