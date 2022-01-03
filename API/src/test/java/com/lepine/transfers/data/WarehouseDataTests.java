package com.lepine.transfers.data;

import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.data.warehouse.WarehouseRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles({"test"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class WarehouseDataTests {

    private final String
        VALID_ZIP_CODE = "A1B2C3",
        VALID_CITY = "City",
        VALID_PROVINCE = "Province";

    @Autowired
    private WarehouseRepo warehouseRepo;

    @BeforeEach
    void setup() {
        warehouseRepo.deleteAll();
    }

    @Test
    void contextLoads(){}

    @Test
    @DisplayName("Given valid warehouse when save then return warehouse")
    void saveValid() {

        // Arrange
        final Warehouse warehouse = Warehouse.builder()
            .zipCode(VALID_ZIP_CODE)
            .city(VALID_CITY)
            .province(VALID_PROVINCE)
            .build();

        // Act
        final Warehouse save = warehouseRepo.save(warehouse);

        // Assert
        assertThat(save).isNotNull();
        assertThat(save.getUuid()).isNotNull();
        assertThat(save.getZipCode()).isEqualTo(VALID_ZIP_CODE);
        assertThat(save.getCity()).isEqualTo(VALID_CITY);
        assertThat(save.getProvince()).isEqualTo(VALID_PROVINCE);
        assertThat(save.isActive()).isTrue();
    }
}
