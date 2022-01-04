package com.lepine.transfers.controllers;

import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.controllers.warehouse.WarehouseController;
import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.data.warehouse.WarehouseActiveLessUUIDLessDTO;
import com.lepine.transfers.services.warehouse.WarehouseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {ValidationConfig.class, WarehouseController.class })
@ActiveProfiles({"test"})
public class WarehouseControllerTests {

    private final static String
            VALID_CITY = "City",
            VALID_ZIP = "A1B2C3",
            VALID_PROVINCE = "Province",
            ERROR_FORMAT_MESSAGE_DUPLICATE_ZIP = "Zipcode %s already in use",
            ERROR_FORMAT_MESSAGE_WAREHOUSE_NOT_FOUND = "Warehouse with uuid %s not found";
    private final static UUID
            VALID_UUID = UUID.randomUUID();

    @Autowired
    private WarehouseController warehouseController;

    @MockBean
    private WarehouseService warehouseService;

    @Test
    void contextLoads() {
    }

    @Test
    @DisplayName("vPYsISyrKh: Given valid warehouse dto when create, then return warehouse")
    void create_ValidWarehouse_ReturnWarehouse() {

        // Arrange
        final WarehouseActiveLessUUIDLessDTO given = WarehouseActiveLessUUIDLessDTO.builder()
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();

        final Warehouse expected = Warehouse.builder()
                .uuid(VALID_UUID)
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();
        given(warehouseService.create(given)).willReturn(expected);

        // Act
        Warehouse gotten = warehouseController.create(given);

        // Assert
        assertThat(gotten).isEqualTo(expected);

        verify(warehouseService, atMostOnce()).create(given);
    }
}
