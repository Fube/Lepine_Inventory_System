package com.lepine.transfers.exceptions.shipment;

import java.util.UUID;

public class ShipmentNotPendingException extends IllegalArgumentException{

    private final static String FORMAT = "Shipment with uuid %s is not pending";

    public ShipmentNotPendingException(UUID uuid) {
        super(String.format(FORMAT, uuid));
    }
}
