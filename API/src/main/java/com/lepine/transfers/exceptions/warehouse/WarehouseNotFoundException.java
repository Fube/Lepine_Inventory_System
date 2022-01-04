package com.lepine.transfers.exceptions.warehouse;

import com.lepine.transfers.exceptions.NotFoundException;

import static java.lang.String.format;

public class WarehouseNotFoundException extends NotFoundException {
    public WarehouseNotFoundException(String message) {
        super(format("Warehouse with uuid %s not found", message));
    }
}
