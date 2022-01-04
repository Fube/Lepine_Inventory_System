package com.lepine.transfers.services.warehouse;

import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.data.warehouse.WarehouseActiveLessUUIDLessDTO;

import javax.validation.Valid;

public interface WarehouseService {
    Warehouse create(@Valid WarehouseActiveLessUUIDLessDTO warehouse);
}
