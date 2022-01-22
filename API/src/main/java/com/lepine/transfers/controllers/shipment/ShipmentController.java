package com.lepine.transfers.controllers.shipment;

import com.lepine.transfers.data.shipment.ShipmentMapper;
import com.lepine.transfers.services.shipment.ShipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;
    private final ShipmentMapper shipmentMapper;
}
