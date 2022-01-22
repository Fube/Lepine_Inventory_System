package com.lepine.transfers.unit.controllers;

import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.controllers.shipment.ShipmentController;
import com.lepine.transfers.services.shipment.ShipmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = { MapperConfig.class, ValidationConfig.class, ShipmentController.class })
@ActiveProfiles({"test"})
public class ShipmentControllerTests {

    @Autowired
    private ShipmentController shipmentController;

    @MockBean
    private ShipmentService shipmentService;

    @Test
    void contextLoads() {}
}
