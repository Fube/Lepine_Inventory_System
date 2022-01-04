package com.lepine.transfers.services.warehouse;

import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.data.warehouse.WarehouseActiveLessUUIDLessDTO;

public interface WarehouseService {
    Warehouse create(WarehouseActiveLessUUIDLessDTO warehouse);
}
