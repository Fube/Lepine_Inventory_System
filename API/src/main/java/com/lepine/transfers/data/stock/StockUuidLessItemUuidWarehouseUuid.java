package com.lepine.transfers.data.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class StockUuidLessItemUuidWarehouseUuid {

    private String stockUuid;
    private String itemUuid;
    private String warehouseUuid;
}
