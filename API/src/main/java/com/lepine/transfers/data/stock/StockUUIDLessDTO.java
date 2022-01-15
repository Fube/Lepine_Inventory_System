package com.lepine.transfers.data.stock;


import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.warehouse.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class StockUUIDLessDTO {

    @NotNull(message = "{stock.item.notnull}")
    @NotBlank(message = "{stock.item.notblank}")
    private Item item;

    @NotNull(message = "{stock.warehouse.notnull}")
    @NotBlank(message = "{stock.warehouse.notblank}")
    private Warehouse warehouse;

    @NotNull(message = "{stock.quantity.notnull}")
    @NotBlank(message = "{stock.quantity.notblank}")
    private Integer quantity;
}
