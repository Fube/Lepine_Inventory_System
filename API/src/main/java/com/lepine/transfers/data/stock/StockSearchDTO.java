package com.lepine.transfers.data.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class StockSearchDTO {

    private UUID objectID;
    private int quantity;

    private UUID itemUuid;
    private String name;
    private String sku;
    private String description;

    private UUID warehouseUuid;
    private String zipCode;
}
