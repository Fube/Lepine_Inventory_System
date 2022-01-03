package com.lepine.transfers.services;

import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.data.warehouse.WarehouseRepo;
import com.lepine.transfers.services.warehouse.WarehouseService;
import com.lepine.transfers.services.warehouse.WarehouseServiceImpl;
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

@SpringBootTest(classes = {WarehouseServiceImpl.class})
@ActiveProfiles({"test"})
public class WarehouseServiceTests {

    private final static String
        VALID_CITY = "City",
        VALID_ZIP = "A1B2C3",
        VALID_PROVINCE = "Province";
    private final static UUID
        VALID_UUID = UUID.randomUUID();

    @Autowired
    private WarehouseService warehouseService;

    @MockBean
    private WarehouseRepo warehouseRepo;

    @Test
    void contextLoads(){}

    @Test
    @DisplayName("LHLspyjPmV: Given valid warehouse when create, then return Warehouse")
    void create_ValidWarehouseActiveLessUUIDLessDTO(){

        // Arrange
        final Warehouse toSave = Warehouse.builder()
            .city(VALID_CITY)
            .zipCode(VALID_ZIP)
            .province(VALID_PROVINCE)
            .build();

        given(warehouseRepo.save(toSave)).willReturn(toSave.toBuilder().uuid(VALID_UUID).build());

        // Act
        final Warehouse saved = warehouseService.create(toSave);

        // Assert
        assertThat(saved).isNotNull();
        assertThat(saved.getUuid()).isEqualTo(VALID_UUID);
        assertThat(saved.getCity()).isEqualTo(VALID_CITY);
        assertThat(saved.getZipCode()).isEqualTo(VALID_ZIP);
        assertThat(saved.getProvince()).isEqualTo(VALID_PROVINCE);
        assertThat(saved.isActive()).isTrue();
        verify(warehouseRepo, atMostOnce()).save(toSave);
    }
}
