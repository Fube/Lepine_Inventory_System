package com.lepine.transfers.data;

import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.data.warehouse.WarehouseRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles({"test"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class WarehouseDataTests {

    private final String
        VALID_ZIP_CODE = "A1B2C3",
        VALID_CITY = "City",
        VALID_PROVINCE = "Province";

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private WarehouseRepo warehouseRepo;

    @BeforeEach
    void setup() {
        warehouseRepo.deleteAll();
    }

    @Test
    void contextLoads(){}

    @Test
    @DisplayName("HmlmnSqPkI: Given valid warehouse when save, then return warehouse")
    void save_Valid() {

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

    @Test
    @DisplayName("mTgimKuiHy: Given warehouse with null zip code when save, then throw PersistenceException")
    void save_NullZipCode() {

        // Arrange
        final Warehouse warehouse = Warehouse.builder()
            .city(VALID_CITY)
            .province(VALID_PROVINCE)
            .build();

        // Act
        final PersistenceException persistenceException =
                assertThrows(PersistenceException.class, () ->  {
                    warehouseRepo.save(warehouse);
                    entityManager.flush();
                });

        // Assert
        assertThat(persistenceException).isNotNull();
        final Throwable rootCause = NestedExceptionUtils.getRootCause(persistenceException);

        assertThat(rootCause).isNotNull();
        assertThat(rootCause.getMessage()).contains("NULL not allowed for column \"zip_code\"");
    }

    @Test
    @DisplayName("RToZDuhamP: Given warehouse with null city when save, then throw PersistenceException")
    void save_NullCity() {

        // Arrange
        final Warehouse warehouse = Warehouse.builder()
            .zipCode(VALID_ZIP_CODE)
            .province(VALID_PROVINCE)
            .build();

        // Act
        final PersistenceException persistenceException =
                assertThrows(PersistenceException.class, () ->  {
                    warehouseRepo.save(warehouse);
                    entityManager.flush();
                });

        // Assert
        assertThat(persistenceException).isNotNull();
        final Throwable rootCause = NestedExceptionUtils.getRootCause(persistenceException);

        assertThat(rootCause).isNotNull();
        assertThat(rootCause.getMessage()).contains("NULL not allowed for column \"city\"");
    }

    @Test
    @DisplayName("UGLZlPULhW: Given warehouse with null province when save, then throw PersistenceException")
    void save_NullProvince() {

        // Arrange
        final Warehouse warehouse = Warehouse.builder()
            .zipCode(VALID_ZIP_CODE)
            .city(VALID_CITY)
            .build();

        // Act
        final PersistenceException persistenceException =
                assertThrows(PersistenceException.class, () ->  {
                    warehouseRepo.save(warehouse);
                    entityManager.flush();
                });

        // Assert
        assertThat(persistenceException).isNotNull();
        final Throwable rootCause = NestedExceptionUtils.getRootCause(persistenceException);

        assertThat(rootCause).isNotNull();
        assertThat(rootCause.getMessage()).contains("NULL not allowed for column \"province\"");
    }

    @Test
    @DisplayName("GCKjMDwBDQ: Given warehouse with duplicate zip code when save, then throw PersistenceException")
    void save_DuplicateZipCode() {

        // Arrange
        final Warehouse warehouse = Warehouse.builder()
            .zipCode(VALID_ZIP_CODE)
            .city(VALID_CITY)
            .province(VALID_PROVINCE)
            .build();

        warehouseRepo.save(warehouse);
        entityManager.flush();

        final Warehouse duplicate = Warehouse.builder()
            .zipCode(VALID_ZIP_CODE)
            .city(VALID_CITY)
            .province(VALID_PROVINCE)
            .build();

        // Act
        final PersistenceException persistenceException =
                assertThrows(PersistenceException.class, () ->  {
                    warehouseRepo.save(duplicate);
                    entityManager.flush();
                });

        // Assert
        assertThat(persistenceException).isNotNull();
        final Throwable rootCause = NestedExceptionUtils.getRootCause(persistenceException);

        assertThat(rootCause).isNotNull();
        // Case-insensitive regex
        assertThat(rootCause.getMessage()).matches(
                Pattern.compile(".*unique index.*violation.*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL));
    }
}
