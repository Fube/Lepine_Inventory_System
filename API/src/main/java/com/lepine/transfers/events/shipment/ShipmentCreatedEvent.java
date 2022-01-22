package com.lepine.transfers.events.shipment;

import com.lepine.transfers.data.shipment.Shipment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class ShipmentCreatedEvent extends ApplicationEvent {
    @Getter
    private Shipment shipment;

    public ShipmentCreatedEvent(Object source, Shipment shipment) {
        super(source);
        this.shipment = shipment;
    }
}
