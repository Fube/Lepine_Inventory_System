package com.lepine.transfers.services;

import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.data.warehouse.WarehouseActiveLessUUIDLessDTO;
import com.lepine.transfers.data.warehouse.WarehouseRepo;
import com.lepine.transfers.services.warehouse.WarehouseService;
import com.lepine.transfers.services.warehouse.WarehouseServiceImpl;
import com.lepine.transfers.utils.ConstraintViolationExceptionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolationException;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = { MapperConfig.class, ValidationConfig.class, WarehouseServiceImpl.class })
@ActiveProfiles({"test"})
public class WarehouseServiceTests {

    private final static String
        VALID_CITY = "City",
        VALID_ZIP = "A1B2C3",
        VALID_PROVINCE = "Province";
    private final static UUID
        VALID_UUID = UUID.randomUUID();

    private final static String
            ERROR_MESSAGE_CITY_NOT_NULL = "City must not be null",
            ERROR_MESSAGE_CITY_NOT_BLANK = "City must not be blank";

    @Autowired
    private WarehouseService warehouseService;

    @MockBean
    private WarehouseRepo warehouseRepo;

    @Test
    void contextLoads(){}

    @Test
    @DisplayName("LHLspyjPmV: Given valid warehouse dto when create, then return Warehouse")
    void create_ValidWarehouseActiveLessUUIDLessDTO(){

        // Arrange
        final WarehouseActiveLessUUIDLessDTO toSave = WarehouseActiveLessUUIDLessDTO.builder()
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

        given(warehouseRepo.save(argThat(n -> n.getZipCode().equals(VALID_ZIP))))
                .willReturn(expected);

        // Act
        final Warehouse saved = warehouseService.create(toSave);

        // Assert
        assertThat(saved).isNotNull();
        assertThat(saved.getUuid()).isEqualTo(VALID_UUID);
        assertThat(saved.getCity()).isEqualTo(VALID_CITY);
        assertThat(saved.getZipCode()).isEqualTo(VALID_ZIP);
        assertThat(saved.getProvince()).isEqualTo(VALID_PROVINCE);
        verify(warehouseRepo, atMostOnce()).save(expected);
    }

    @Test
    @DisplayName("uuMEUbMZnG: Given null city when create, then throw ConstraintViolationException")
    void create_NullCityActiveLessUUIDLessDTO(){

        // Arrange
        final WarehouseActiveLessUUIDLessDTO toSave = WarehouseActiveLessUUIDLessDTO.builder()
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();

        // Act & Assert
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> warehouseService.create(toSave));

        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactly(ERROR_MESSAGE_CITY_NOT_NULL);

        verify(warehouseRepo, never()).save(any());
    }

    @Test
    @DisplayName("kNsjRSVhyX: Given blank city when create, then throw ConstraintViolationException")
    void create_BlankCityActiveLessUUIDLessDTO(){

        // Arrange
        final WarehouseActiveLessUUIDLessDTO toSave = WarehouseActiveLessUUIDLessDTO.builder()
                .city("")
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();

        // Act & Assert
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> warehouseService.create(toSave));

        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactly(ERROR_MESSAGE_CITY_NOT_BLANK);

        verify(warehouseRepo, never()).save(any());
    }
}
