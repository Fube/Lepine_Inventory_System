package com.lepine.transfers.exceptions.transfer;

import com.lepine.transfers.data.stock.Stock;
import lombok.Getter;

import java.util.UUID;

public class SameWarehouseException extends RuntimeException {

    private final static String MESSAGE = "Stock with UUID %s is already in warehouse with UUID %s";

    @Getter
    private final Stock stock;

    public SameWarehouseException(Stock stock, UUID warehouseUuid) {
        super(String.format(MESSAGE, stock.getUuid(), warehouseUuid));
        this.stock = stock;
    }
}
