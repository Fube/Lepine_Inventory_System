package com.lepine.transfers.events.shipment;

import org.springframework.context.event.EventListener;

public interface ShipmentCreateHandler {

    @EventListener(ShipmentCreateEvent.class)
    void onShipmentCreate(ShipmentCreateEvent event);
}
