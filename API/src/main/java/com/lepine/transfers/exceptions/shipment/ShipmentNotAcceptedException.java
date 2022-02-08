package com.lepine.transfers.exceptions.shipment;

import java.util.UUID;

public class ShipmentNotAcceptedException extends RuntimeException{
    private final static String MESSAGE = "Shipment with uuid %s is %s";

    public ShipmentNotAcceptedException(final UUID uuid, final String status) {
        super(String.format(MESSAGE, uuid, status));
    }
}
