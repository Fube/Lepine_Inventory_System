package com.lepine.transfers.data.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class StockUuidLessItemUuidWarehouseUuid {

    private int quantity;

    @NotNull(message = "item.uuid.not_null")
    private UUID itemUuid;
    private UUID warehouseUuid;
}
