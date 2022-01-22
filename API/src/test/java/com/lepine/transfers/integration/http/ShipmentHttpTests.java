package com.lepine.transfers.integration.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lepine.transfers.config.AuthConfig;
import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.controllers.shipment.ShipmentController;
import com.lepine.transfers.services.shipment.ShipmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = { ShipmentController.class })
@ContextConfiguration(classes = { MapperConfig.class, ValidationConfig.class, AuthConfig.class })
@ActiveProfiles("test")
public class ShipmentHttpTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShipmentController shipmentController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;

    @MockBean
    private ShipmentService shipmentService;

    @Test
    void contextLoads() {}
}
