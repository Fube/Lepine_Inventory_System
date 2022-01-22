package com.lepine.transfers.controllers.shipment;

import com.lepine.transfers.data.shipment.Shipment;
import com.lepine.transfers.data.shipment.ShipmentMapper;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.services.shipment.ShipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;
    private final ShipmentMapper shipmentMapper;

    public Page<Shipment> findAll(@AuthenticationPrincipal User user, PageRequest pageRequest) {
        log.info("Fetching all shipments for user {}", user.getUsername());

        if(user.getRole().getName().equals("MANAGER")) {
            log.info("User is manager, fetching all shipments");
            return shipmentService.findAll(pageRequest);
        }

        log.info("User is not manager, fetching only relevant shipments");
        return shipmentService.findAllByUserUuid(user.getUuid(), pageRequest);
    }
}
