package com.lepine.transfers.services.shipment;

import com.lepine.transfers.data.shipment.Shipment;
import com.lepine.transfers.data.shipment.ShipmentStatusLessUuidLessDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.validation.Valid;

public interface ShipmentService {
    Shipment create(@Valid ShipmentStatusLessUuidLessDTO shipmentStatusLessUUIDLessDTO);

    Page<Shipment> findAll(PageRequest pageRequest);
}
