package com.lepine.transfers.unit.controllers;

import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.controllers.shipment.ShipmentController;
import com.lepine.transfers.data.auth.Role;
import com.lepine.transfers.data.shipment.Shipment;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.services.shipment.ShipmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static com.lepine.transfers.utils.PageUtils.createPageFor;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = { MapperConfig.class, ValidationConfig.class, ShipmentController.class })
@ActiveProfiles({"test"})
public class ShipmentControllerTests {

    private final static UUID
        VALID_USER_UUID = UUID.randomUUID(),
        VALID_ROLE_UUID = UUID.randomUUID();

    private final static String
        VALID_USER_EMAIL = "a@b.c",
        VALID_USER_PASSWORD = "noneofyourbusiness",
        VALID_CLERK_ROLE_NAME = "CLERK",
        VALID_MANAGER_ROLE_NAME = "MANAGER";

    private final static Role VALID_CLERK_ROLE = Role.builder()
        .uuid(VALID_ROLE_UUID)
        .name(VALID_CLERK_ROLE_NAME)
        .build();

    @Autowired
    private ShipmentController shipmentController;

    @MockBean
    private ShipmentService shipmentService;

    @Test
    void contextLoads() {}

    @Test
    @DisplayName("UIHcfxYzbs: Given page and size when get, then return page of Shipments")
    void valid_findAll() {

        // Arrange
        final int
                page = 1, // One-indexed
                size = 10;
        final User givenUser = User.builder()
                .uuid(VALID_USER_UUID)
                .email(VALID_USER_EMAIL)
                .password(VALID_USER_PASSWORD)
                .role(VALID_CLERK_ROLE)
                .build();
        final PageRequest givenPageRequest = PageRequest.of(page, size);
        final Page<Shipment> givenShipments = Page.empty();

        // Act
        final Page<Shipment> response = shipmentController.findAll(givenUser, givenPageRequest);

        // Assert
        assertThat(response).isEqualTo(givenShipments);
    }
}
