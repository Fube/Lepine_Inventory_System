package com.lepine.transfers.exceptions.shipment;

import com.lepine.transfers.exceptions.NotFoundException;

import java.util.UUID;

public class ShipmentNotFoundException extends NotFoundException {

    private static final String FORMAT = "Shipment with uuid %s not found";

    public ShipmentNotFoundException(UUID uuid) {
        super(String.format(FORMAT, uuid));
    }

    public ShipmentNotFoundException(String message) {
        super(message);
    }
}
