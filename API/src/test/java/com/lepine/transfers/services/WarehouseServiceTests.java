package com.lepine.transfers.services;

import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.data.warehouse.WarehouseActiveLessUUIDLessDTO;
import com.lepine.transfers.data.warehouse.WarehouseRepo;
import com.lepine.transfers.exceptions.warehouse.DuplicateZipCodeException;
import com.lepine.transfers.services.warehouse.WarehouseService;
import com.lepine.transfers.services.warehouse.WarehouseServiceImpl;
import com.lepine.transfers.utils.ConstraintViolationExceptionUtils;
import com.lepine.transfers.utils.MessageSourceUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolationException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.lepine.transfers.utils.MessageSourceUtils.wrapperFor;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {
        MapperConfig.class,
        ValidationConfig.class,
        WarehouseServiceImpl.class,
})
@ActiveProfiles({"test"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WarehouseServiceTests {

    private final static String
        VALID_CITY = "City",
        VALID_ZIP = "A1B2C3",
        VALID_PROVINCE = "Province";
    private final static UUID
        VALID_UUID = UUID.randomUUID();

    private String
            ERROR_MESSAGE_CITY_NOT_NULL,
            ERROR_MESSAGE_CITY_NOT_BLANK,
            ERROR_MESSAGE_ZIP_NOT_NULL,
            ERROR_MESSAGE_ZIP_NOT_BLANK,
            ERROR_MESSAGE_PROVINCE_NOT_NULL,
            ERROR_MESSAGE_PROVINCE_NOT_BLANK,
            ERROR_FORMAT_MESSAGE_DUPLICATE_ZIP = "Zipcode %s already in use";

    @BeforeAll
    void bSetup(){
        final MessageSourceUtils.ForLocaleWrapper w = wrapperFor(messageSource);
        ERROR_MESSAGE_CITY_NOT_NULL = w.getMessage("warehouse.city.not_null");
        ERROR_MESSAGE_CITY_NOT_BLANK = w.getMessage("warehouse.city.not_blank");
        ERROR_MESSAGE_ZIP_NOT_NULL = w.getMessage("warehouse.zipcode.not_null");
        ERROR_MESSAGE_ZIP_NOT_BLANK = w.getMessage("warehouse.zipcode.not_blank");
        ERROR_MESSAGE_PROVINCE_NOT_NULL = w.getMessage("warehouse.province.not_null");
        ERROR_MESSAGE_PROVINCE_NOT_BLANK = w.getMessage("warehouse.province.not_blank");
    }

    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;

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
        assertThat(collect).containsExactlyInAnyOrder(ERROR_MESSAGE_CITY_NOT_NULL, ERROR_MESSAGE_CITY_NOT_BLANK);

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

    @Test
    @DisplayName("mFDxulFTNI: Given null zip when create, then throw ConstraintViolationException")
    void create_NullZipActiveLessUUIDLessDTO(){

        // Arrange
        final WarehouseActiveLessUUIDLessDTO toSave = WarehouseActiveLessUUIDLessDTO.builder()
                .city(VALID_CITY)
                .province(VALID_PROVINCE)
                .build();

        // Act & Assert
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> warehouseService.create(toSave));

        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactlyInAnyOrder(ERROR_MESSAGE_ZIP_NOT_NULL, ERROR_MESSAGE_ZIP_NOT_BLANK);

        verify(warehouseRepo, never()).save(any());
    }

    @Test
    @DisplayName("nJXSQnUzxV: Given blank zip when create, then throw ConstraintViolationException")
    void create_BlankZipActiveLessUUIDLessDTO(){

        // Arrange
        final WarehouseActiveLessUUIDLessDTO toSave = WarehouseActiveLessUUIDLessDTO.builder()
                .city(VALID_CITY)
                .zipCode("")
                .province(VALID_PROVINCE)
                .build();

        // Act & Assert
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> warehouseService.create(toSave));

        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactly(ERROR_MESSAGE_ZIP_NOT_BLANK);

        verify(warehouseRepo, never()).save(any());
    }

    @Test
    @DisplayName("ivHDXJGayt: Given null province when create, then throw ConstraintViolationException")
    void create_NullProvinceActiveLessUUIDLessDTO(){

        // Arrange
        final WarehouseActiveLessUUIDLessDTO toSave = WarehouseActiveLessUUIDLessDTO.builder()
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .build();

        // Act & Assert
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> warehouseService.create(toSave));

        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactlyInAnyOrder(ERROR_MESSAGE_PROVINCE_NOT_NULL, ERROR_MESSAGE_PROVINCE_NOT_BLANK);

        verify(warehouseRepo, never()).save(any());
    }

    @Test
    @DisplayName("SPxtXyKTxQ: Given blank province when create, then throw ConstraintViolationException")
    void create_BlankProvinceActiveLessUUIDLessDTO(){

        // Arrange
        final WarehouseActiveLessUUIDLessDTO toSave = WarehouseActiveLessUUIDLessDTO.builder()
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province("")
                .build();

        // Act & Assert
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> warehouseService.create(toSave));

        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactly(ERROR_MESSAGE_PROVINCE_NOT_BLANK);

        verify(warehouseRepo, never()).save(any());
    }

    @Test
    @DisplayName("BbxucsXQSf: Given duplicate zipcode when create, then throw DuplicateZipCodeException")
    void create_DuplicateZipCodeActiveLessUUIDLessDTO(){

        // Arrange
        final WarehouseActiveLessUUIDLessDTO toSave = WarehouseActiveLessUUIDLessDTO.builder()
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();

        final Warehouse warehouse = Warehouse.builder()
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();

        when(warehouseRepo.findByZipCode(VALID_ZIP))
                .thenReturn(Optional.of(warehouse));

        // Act & Assert
        final DuplicateZipCodeException duplicateZipCodeException =
                assertThrows(DuplicateZipCodeException.class, () -> warehouseService.create(toSave));

        assertThat(duplicateZipCodeException.getMessage())
                .isEqualTo(format(ERROR_FORMAT_MESSAGE_DUPLICATE_ZIP, VALID_ZIP));

        verify(warehouseRepo, never()).save(any());
        verify(warehouseRepo).findByZipCode(VALID_ZIP);
    }

    @Test
    @DisplayName("NhzdRhdvIW: Given UUID when delete, then try to delete warehouse regardless of its existence")
    void delete_UUID(){

        // Arrange
        final UUID uuid = UUID.randomUUID();

        // Act
        warehouseService.delete(uuid);

        // Assert
        verify(warehouseRepo).deleteByUuid(uuid);
    }
}
