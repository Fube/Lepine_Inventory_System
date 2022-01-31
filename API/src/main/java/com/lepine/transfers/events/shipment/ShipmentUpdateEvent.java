package com.lepine.transfers.events.shipment;

import com.lepine.transfers.data.shipment.Shipment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class ShipmentUpdateEvent extends ApplicationEvent {


    @Getter
    private final Shipment old;

    @Getter
    private final Shipment updated;

    public ShipmentUpdateEvent(Object source, Shipment old, Shipment updated) {
        super(source);
        this.old = old;
        this.updated = updated;
    }
}
