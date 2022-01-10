package com.lepine.transfers.exceptions.warehouse;

import com.lepine.transfers.exceptions.NotFoundException;

import java.util.UUID;

import static java.lang.String.format;

public class WarehouseNotFoundException extends NotFoundException {
    public WarehouseNotFoundException(UUID uuid) {
        super(format("Warehouse with uuid %s not found", uuid));
    }
}
