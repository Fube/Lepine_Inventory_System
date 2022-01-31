package com.lepine.transfers.services.shipment;

import com.lepine.transfers.data.shipment.Shipment;
import com.lepine.transfers.data.shipment.ShipmentStatusLessUuidLessDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.json.JsonPatch;
import javax.validation.Valid;
import java.util.UUID;

public interface ShipmentService {
    Shipment create(@Valid ShipmentStatusLessUuidLessDTO shipmentStatusLessUUIDLessDTO);
    Page<Shipment> findAll(PageRequest pageRequest);
    Page<Shipment> findAllByUserUuid(UUID userUuid, PageRequest pageRequest);
    Shipment update(UUID uuid, JsonPatch jsonPatch);
}
