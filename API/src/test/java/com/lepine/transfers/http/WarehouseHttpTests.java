package com.lepine.transfers.http;

import com.lepine.transfers.config.AuthConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.controllers.warehouse.WarehouseController;
import com.lepine.transfers.services.warehouse.WarehouseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@WebMvcTest(controllers = { WarehouseController.class })
@ContextConfiguration(classes = { ValidationConfig.class, AuthConfig.class })
@ActiveProfiles("test")
public class WarehouseHttpTests {

    @Autowired
    private WarehouseController warehouseController;

    @MockBean
    private WarehouseService warehouseService;

    @Test
    void contextLoads() {}
}
