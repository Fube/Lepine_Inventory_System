package com.lepine.transfers.services.warehouse;

import com.lepine.transfers.data.warehouse.Warehouse;

public interface WarehouseService {
    Warehouse create(WarehouseActiveLessUUIDLessDTO warehouse);
}
