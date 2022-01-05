package com.lepine.transfers.services;

import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.data.warehouse.WarehouseActiveLessUUIDLessDTO;
import com.lepine.transfers.data.warehouse.WarehouseRepo;
import com.lepine.transfers.data.warehouse.WarehouseUUIDLessDTO;
import com.lepine.transfers.exceptions.warehouse.DuplicateZipCodeException;
import com.lepine.transfers.exceptions.warehouse.WarehouseNotFoundException;
import com.lepine.transfers.services.warehouse.WarehouseService;
import com.lepine.transfers.services.warehouse.WarehouseServiceImpl;
import com.lepine.transfers.utils.ConstraintViolationExceptionUtils;
import com.lepine.transfers.utils.MessageSourceUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        VALID_PROVINCE = "Province",
        ERROR_FORMAT_MESSAGE_DUPLICATE_ZIP = "Zipcode %s already in use",
        ERROR_FORMAT_MESSAGE_WAREHOUSE_NOT_FOUND = "Warehouse with uuid %s not found";
    private final static UUID
        VALID_UUID = UUID.randomUUID();

    private String
            ERROR_MESSAGE_CITY_NOT_NULL,
            ERROR_MESSAGE_CITY_NOT_BLANK,
            ERROR_MESSAGE_ZIP_NOT_NULL,
            ERROR_MESSAGE_ZIP_NOT_BLANK,
            ERROR_MESSAGE_PROVINCE_NOT_NULL,
            ERROR_MESSAGE_PROVINCE_NOT_BLANK;

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

    @AfterEach
    void tearDown() {
        reset(warehouseRepo);
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

    @Test
    @DisplayName("IpgpmsFURI: Given valid WarehouseUUIDLessDTO when update, then update warehouse")
    void update_ValidWarehouseUUIDLessDTO(){

        // Arrange
        final Warehouse warehouse = Warehouse.builder()
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();

        final WarehouseUUIDLessDTO toUpdate = WarehouseUUIDLessDTO.builder()
                .city(VALID_CITY + "2")
                .zipCode(VALID_ZIP.substring(0, VALID_ZIP.length() - 1) + "2")
                .province("SomeOtherProvince")
                .build();

        final Warehouse expectedWarehouse = Warehouse.builder()
                .uuid(warehouse.getUuid())
                .city(toUpdate.getCity())
                .zipCode(toUpdate.getZipCode())
                .province(toUpdate.getProvince())
                .build();

        when(warehouseRepo.findByUuid(warehouse.getUuid()))
                .thenReturn(Optional.of(warehouse));
        when(warehouseRepo.save(argThat(w -> w.getUuid().equals(warehouse.getUuid()))))
                .thenReturn(expectedWarehouse);

        // Act
        final Warehouse updated = warehouseService.update(warehouse.getUuid(), toUpdate);

        // Assert
        assertThat(updated.getUuid()).isEqualTo(warehouse.getUuid());
        assertThat(updated.getCity()).isEqualTo(toUpdate.getCity());
        assertThat(updated.getZipCode()).isEqualTo(toUpdate.getZipCode());
        assertThat(updated.getProvince()).isEqualTo(toUpdate.getProvince());
        assertThat(updated.isActive()).isEqualTo(warehouse.isActive());

        verify(warehouseRepo).findByUuid(warehouse.getUuid());
        verify(warehouseRepo).save(argThat(w -> w.getUuid().equals(warehouse.getUuid())));
    }

    @Test
    @DisplayName("VnvttdRxZP: Given valid UUID of non-existing warehouse when update, then throw WarehouseNotFoundException")
    void update_NonExistingWarehouseUUIDLessDTO(){

        // Arrange
        final UUID uuid = UUID.randomUUID();
        final WarehouseUUIDLessDTO toUpdate = WarehouseUUIDLessDTO.builder()
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();
        given(warehouseRepo.findByUuid(uuid))
                .willReturn(Optional.empty());

        // Act & Assert
        final WarehouseNotFoundException warehouseNotFoundException =
                assertThrows(WarehouseNotFoundException.class, () -> warehouseService.update(uuid, toUpdate));

        assertThat(warehouseNotFoundException.getMessage())
                .isEqualTo(format(ERROR_FORMAT_MESSAGE_WAREHOUSE_NOT_FOUND, uuid));

        verify(warehouseRepo, never()).save(any());
    }

    @Test
    @DisplayName("XNiCkEzPhB: Given valid UUID of existing warehouse with blank zip when update, then throw ConstraintViolationException")
    void update_ExistingWarehouseUUIDLessDTO_BlankZip(){

        // Arrange
        final Warehouse warehouse = Warehouse.builder()
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();

        final WarehouseUUIDLessDTO toUpdate = WarehouseUUIDLessDTO.builder()
                .city(VALID_CITY)
                .zipCode("")
                .province(VALID_PROVINCE)
                .build();

        // Act & Assert
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> warehouseService.update(warehouse.getUuid(), toUpdate));

        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactly(ERROR_MESSAGE_ZIP_NOT_BLANK);

        verify(warehouseRepo, never()).findByUuid(any());
        verify(warehouseRepo, never()).save(any());
    }

    @Test
    @DisplayName("qCkmloUyGy: Given valid UUID of existing warehouse with null zip when update, then throw ConstraintViolationException")
    void update_ExistingWarehouseUUIDLessDTO_NullZip(){

        // Arrange
        final Warehouse warehouse = Warehouse.builder()
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();

        final WarehouseUUIDLessDTO toUpdate = WarehouseUUIDLessDTO.builder()
                .city(VALID_CITY)
                .zipCode(null)
                .province(VALID_PROVINCE)
                .build();

        // Act & Assert
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> warehouseService.update(warehouse.getUuid(), toUpdate));

        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactlyInAnyOrder(ERROR_MESSAGE_ZIP_NOT_NULL, ERROR_MESSAGE_ZIP_NOT_BLANK);

        verify(warehouseRepo, never()).findByUuid(any());
        verify(warehouseRepo, never()).save(any());
    }

    @Test
    @DisplayName("YiEzGmrmGY: Given valid UUID of existing warehouse with duplicate zipcode when update, then throw DuplicateZipCodeException")
    void update_ExistingWarehouseUUIDLessDTO_DuplicateZip(){

        // Arrange
        final Warehouse warehouse = Warehouse.builder()
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();

        final WarehouseUUIDLessDTO toUpdate = WarehouseUUIDLessDTO.builder()
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();

        given(warehouseRepo.findByUuid(warehouse.getUuid()))
                .willReturn(Optional.of(warehouse));
        given(warehouseRepo.findByZipCode(VALID_ZIP))
                .willReturn(Optional.of(warehouse.toBuilder().uuid(UUID.randomUUID()).build()));

        // Act & Assert
        final DuplicateZipCodeException duplicateWarehouseException =
                assertThrows(DuplicateZipCodeException.class, () -> warehouseService.update(warehouse.getUuid(), toUpdate));

        assertThat(duplicateWarehouseException.getMessage())
                .isEqualTo(format(ERROR_FORMAT_MESSAGE_DUPLICATE_ZIP, VALID_ZIP));

        verify(warehouseRepo).findByUuid(warehouse.getUuid());
        verify(warehouseRepo).findByZipCode(VALID_ZIP);
        verify(warehouseRepo, never()).save(any());
    }

    @Test
    @DisplayName("HAOtuUfIso: Given valid UUID of existing warehouse with blank province when update, then throw ConstraintViolationException")
    void update_ExistingWarehouseUUIDLessDTO_BlankProvince(){

        // Arrange
        final Warehouse warehouse = Warehouse.builder()
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();

        final WarehouseUUIDLessDTO toUpdate = WarehouseUUIDLessDTO.builder()
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province("")
                .build();

        // Act & Assert
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> warehouseService.update(warehouse.getUuid(), toUpdate));

        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactly(ERROR_MESSAGE_PROVINCE_NOT_BLANK);

        verify(warehouseRepo, never()).findByUuid(any());
        verify(warehouseRepo, never()).save(any());
    }

    @Test
    @DisplayName("QJabOocKHP: Given valid UUID of existing warehouse with null province when update, then throw ConstraintViolationException")
    void update_ExistingWarehouseUUIDLessDTO_NullProvince(){

        // Arrange
        final Warehouse warehouse = Warehouse.builder()
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();

        final WarehouseUUIDLessDTO toUpdate = WarehouseUUIDLessDTO.builder()
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province(null)
                .build();

        // Act & Assert
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> warehouseService.update(warehouse.getUuid(), toUpdate));

        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactlyInAnyOrder(ERROR_MESSAGE_PROVINCE_NOT_NULL, ERROR_MESSAGE_PROVINCE_NOT_BLANK);

        verify(warehouseRepo, never()).findByUuid(any());
        verify(warehouseRepo, never()).save(any());
    }

    @Test
    @DisplayName("pQDYgoMlxF: Given valid UUID of existing warehouse with blank city when update, then throw ConstraintViolationException")
    void update_ExistingWarehouseUUIDLessDTO_BlankCity(){

        // Arrange
        final Warehouse warehouse = Warehouse.builder()
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();

        final WarehouseUUIDLessDTO toUpdate = WarehouseUUIDLessDTO.builder()
                .city("")
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();

        // Act & Assert
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> warehouseService.update(warehouse.getUuid(), toUpdate));

        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactly(ERROR_MESSAGE_CITY_NOT_BLANK);

        verify(warehouseRepo, never()).findByUuid(any());
        verify(warehouseRepo, never()).save(any());
    }

    @Test
    @DisplayName("EurotOZVRn: Given valid UUID of existing warehouse with null city when update, then throw ConstraintViolationException")
    void update_ExistingWarehouseUUIDLessDTO_NullCity(){

        // Arrange
        final Warehouse warehouse = Warehouse.builder()
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();

        final WarehouseUUIDLessDTO toUpdate = WarehouseUUIDLessDTO.builder()
                .city(null)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();

        // Act & Assert
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> warehouseService.update(warehouse.getUuid(), toUpdate));

        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactlyInAnyOrder(ERROR_MESSAGE_CITY_NOT_NULL, ERROR_MESSAGE_CITY_NOT_BLANK);

        verify(warehouseRepo, never()).findByUuid(any());
        verify(warehouseRepo, never()).save(any());
    }

    @Test
    @DisplayName("iRoKlSuZwS: Given nothing when findAll, then return all warehouses paginated")
    void findAll_NoArguments_ReturnAllPaginated(){

        // Arrange
        final Pageable pageable = PageRequest.of(0, 10);
        final Page<Warehouse> expected = Page.empty(pageable);
        when(warehouseRepo.findAll(pageable)).thenReturn(expected);

        // Act
        final Page<Warehouse> result = warehouseService.findAll();

        // Assert
        assertThat(result).isEqualTo(expected);
        verify(warehouseRepo).findAll(pageable);
        verify(warehouseRepo, never()).findAll();
    }

    @Test
    @DisplayName("xxuFJXKpgT: Given page request when findAll, then return all warehouses paginated")
    void findAll_PageRequest_ReturnAllPaginated(){

        // Arrange
        final PageRequest pageRequest = PageRequest.of(1, 5);
        final Page<Warehouse> expected = Page.empty(pageRequest);
        when(warehouseRepo.findAll(pageRequest)).thenReturn(expected);

        // Act
        final Page<Warehouse> result = warehouseService.findAll(pageRequest);

        // Assert
        assertThat(result).isEqualTo(expected);
        verify(warehouseRepo).findAll(pageRequest);
        verify(warehouseRepo, never()).findAll();
    }

    @Test
    @DisplayName("SQOcHArIfE: Given valid UUID of existing warehouse when findByUuid, then return warehouse")
    void findByUuid_ExistingWarehouseUUID_ReturnWarehouse(){

        // Arrange
        final Warehouse expected = Warehouse.builder()
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();

        when(warehouseRepo.findByUuid(any()))
                .thenReturn(Optional.of(expected));

        // Act
        final Optional<Warehouse> gotten = warehouseService.findByUuid(expected.getUuid());

        // Assert
        assertThat(gotten).isPresent();
        assertThat(gotten.get()).isEqualTo(expected);

        verify(warehouseRepo, atMostOnce()).findByUuid(expected.getUuid());
        verify(warehouseRepo, never()).findAll();
    }
}
