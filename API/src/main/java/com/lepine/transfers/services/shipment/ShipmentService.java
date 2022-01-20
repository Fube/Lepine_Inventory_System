package com.lepine.transfers.services.shipment;

import com.lepine.transfers.data.shipment.Shipment;
import com.lepine.transfers.data.shipment.ShipmentStatusLessUuidLessDTO;

public interface ShipmentService {
    Shipment create(ShipmentStatusLessUuidLessDTO shipmentStatusLessUUIDLessDTO);
}
