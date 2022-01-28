package com.lepine.transfers.events.shipment;

import org.springframework.context.event.EventListener;

public interface ShipmentUpdateHandler {

    @EventListener(ShipmentUpdateEvent.class)
    void onShipmentCreate(ShipmentUpdateEvent event);
}
