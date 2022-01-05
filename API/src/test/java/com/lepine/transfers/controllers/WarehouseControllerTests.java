package com.lepine.transfers.controllers;

import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.controllers.warehouse.WarehouseController;
import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.data.warehouse.WarehouseActiveLessUUIDLessDTO;
import com.lepine.transfers.exceptions.warehouse.DuplicateZipCodeException;
import com.lepine.transfers.exceptions.warehouse.WarehouseNotFoundException;
import com.lepine.transfers.services.warehouse.WarehouseService;
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

@SpringBootTest(classes = { ValidationConfig.class, WarehouseController.class })
@ActiveProfiles({"test"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WarehouseControllerTests {

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
            ERROR_MESSAGE_PROVINCE_NOT_BLANK,
            ERROR_MESSAGE_PAGINATION_PAGE_MIN,
            ERROR_MESSAGE_PAGINATION_SIZE_MIN;

    @BeforeAll
    void bSetup(){
        final MessageSourceUtils.ForLocaleWrapper w = wrapperFor(messageSource);
        ERROR_MESSAGE_CITY_NOT_NULL = w.getMessage("warehouse.city.not_null");
        ERROR_MESSAGE_CITY_NOT_BLANK = w.getMessage("warehouse.city.not_blank");
        ERROR_MESSAGE_ZIP_NOT_NULL = w.getMessage("warehouse.zipcode.not_null");
        ERROR_MESSAGE_ZIP_NOT_BLANK = w.getMessage("warehouse.zipcode.not_blank");
        ERROR_MESSAGE_PROVINCE_NOT_NULL = w.getMessage("warehouse.province.not_null");
        ERROR_MESSAGE_PROVINCE_NOT_BLANK = w.getMessage("warehouse.province.not_blank");
        ERROR_MESSAGE_PAGINATION_PAGE_MIN = w.getMessage("pagination.page.min");
        ERROR_MESSAGE_PAGINATION_SIZE_MIN = w.getMessage("pagination.size.min");
    }

    @AfterEach
    void tearDown(){
        reset(warehouseService);
    }

    @Autowired
    private WarehouseController warehouseController;

    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;

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

    @Test
    @DisplayName("hKqgNkfEDG: Given blank zipcode when create, then throw ConstraintViolationException")
    void create_BlankZipCode_ThrowConstraintViolationException() {

        // Arrange
        final WarehouseActiveLessUUIDLessDTO given = WarehouseActiveLessUUIDLessDTO.builder()
                .city(VALID_CITY)
                .zipCode("")
                .province(VALID_PROVINCE)
                .build();

        // Act
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> warehouseController.create(given));

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactly(ERROR_MESSAGE_ZIP_NOT_BLANK);

        verify(warehouseService, never()).create(any());
    }

    @Test
    @DisplayName("TDyevgcMtv: Given null zipcode when create, then throw ConstraintViolationException")
    void create_NullZipCode_ThrowConstraintViolationException() {

        // Arrange
        final WarehouseActiveLessUUIDLessDTO given = WarehouseActiveLessUUIDLessDTO.builder()
                .city(VALID_CITY)
                .zipCode(null)
                .province(VALID_PROVINCE)
                .build();

        // Act
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> warehouseController.create(given));

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactly(ERROR_MESSAGE_ZIP_NOT_NULL, ERROR_MESSAGE_ZIP_NOT_BLANK);

        verify(warehouseService, never()).create(any());
    }

    @Test
    @DisplayName("uvCOaMjFNg: Given duplicate zipcode when create, then throw DuplicateZipCodeException")
    void create_DuplicateZipCode_ThrowDuplicateZipCodeException() {

        // Arrange
        final WarehouseActiveLessUUIDLessDTO given = WarehouseActiveLessUUIDLessDTO.builder()
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();
        given(warehouseService.create(any()))
                .willThrow(new DuplicateZipCodeException(VALID_ZIP));

        final DuplicateZipCodeException duplicateZipCodeException =
                assertThrows(DuplicateZipCodeException.class, () -> warehouseController.create(given));

        // Assert
        assertThat(duplicateZipCodeException.getMessage())
                .isEqualTo(format(ERROR_FORMAT_MESSAGE_DUPLICATE_ZIP, VALID_ZIP));

        verify(warehouseService, atMostOnce()).create(argThat(w -> w.getZipCode().equals(given.getZipCode())));
    }

    @Test
    @DisplayName("XFPuMEAYAY: Given blank city when create, then throw ConstraintViolationException")
    void create_BlankCity_ThrowConstraintViolationException() {

        // Arrange
        final WarehouseActiveLessUUIDLessDTO given = WarehouseActiveLessUUIDLessDTO.builder()
                .city("")
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();

        // Act
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> warehouseController.create(given));

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactly(ERROR_MESSAGE_CITY_NOT_BLANK);

        verify(warehouseService, never()).create(any());
    }

    @Test
    @DisplayName("gqIiJwAZWW: Given null city when create, then throw ConstraintViolationException")
    void create_NullCity_ThrowConstraintViolationException() {

        // Arrange
        final WarehouseActiveLessUUIDLessDTO given = WarehouseActiveLessUUIDLessDTO.builder()
                .city(null)
                .zipCode(VALID_ZIP)
                .province(VALID_PROVINCE)
                .build();

        // Act
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> warehouseController.create(given));

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactlyInAnyOrder(ERROR_MESSAGE_CITY_NOT_NULL, ERROR_MESSAGE_CITY_NOT_BLANK);

        verify(warehouseService, never()).create(any());
    }

    @Test
    @DisplayName("rVNBybBJcv: Given blank province when create, then throw ConstraintViolationException")
    void create_BlankProvince_ThrowConstraintViolationException() {

        // Arrange
        final WarehouseActiveLessUUIDLessDTO given = WarehouseActiveLessUUIDLessDTO.builder()
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province("")
                .build();

        // Act
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> warehouseController.create(given));

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactly(ERROR_MESSAGE_PROVINCE_NOT_BLANK);

        verify(warehouseService, never()).create(any());
    }

    @Test
    @DisplayName("owUKZjHnXR: Given null province when create, then throw ConstraintViolationException")
    void create_NullProvince_ThrowConstraintViolationException() {

        // Arrange
        final WarehouseActiveLessUUIDLessDTO given = WarehouseActiveLessUUIDLessDTO.builder()
                .city(VALID_CITY)
                .zipCode(VALID_ZIP)
                .province(null)
                .build();

        // Act
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> warehouseController.create(given));

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactlyInAnyOrder(ERROR_MESSAGE_PROVINCE_NOT_NULL, ERROR_MESSAGE_PROVINCE_NOT_BLANK);

        verify(warehouseService, never()).create(any());
    }

    @Test
    @DisplayName("uvCOaMjFNg: Given page less than 1 when getAll, then throw ConstraintViolationException")
    void getAll_PageLessThan1_ThrowConstraintViolationException() {

        // Arrange
        final int pageNumber = 0, pageSize = 10;

        // Act
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> warehouseController.getAll(pageNumber, pageSize));

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactly(ERROR_MESSAGE_PAGINATION_PAGE_MIN);

        verify(warehouseService, never()).findAll(any());
    }

    @Test
    @DisplayName("znLPozjllS: Given page size less than 1 when getAll, then throw ConstraintViolationException")
    void getAll_PageSizeLessThan1_ThrowConstraintViolationException() {

        // Arrange
        final int pageNumber = 1, pageSize = 0;

        // Act
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> warehouseController.getAll(pageNumber, pageSize));

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactly(ERROR_MESSAGE_PAGINATION_SIZE_MIN);

        verify(warehouseService, never()).findAll(any());
    }

    @Test
    @DisplayName("ipIXNstdae: Given page and size less than 1 when getAll, then throw ConstraintViolationException")
    void getAll_PageAndSizeLessThan1_ThrowConstraintViolationException() {

        // Arrange
        final int pageNumber = 0, pageSize = 0;

        // Act
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> warehouseController.getAll(pageNumber, pageSize));

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactlyInAnyOrder(ERROR_MESSAGE_PAGINATION_PAGE_MIN, ERROR_MESSAGE_PAGINATION_SIZE_MIN);

        verify(warehouseService, never()).findAll(any());
    }

    @Test
    @DisplayName("zopZsHMFJb: Given valid page and size when getAll, then return all warehouses paginated and offset by 1")
    void getAll_ValidPageAndSize_ReturnAllWarehousesPaginated() {

        // Arrange
        final int pageNumber = 1, pageSize = 10;
        final PageRequest pageable = PageRequest.of(pageNumber, pageSize);
        final Page<Warehouse> expected = Page.empty();

        when(warehouseService.findAll(pageable)).thenReturn(expected);

        // Act
        final Page<Warehouse> actual = warehouseController.getAll(pageNumber, pageSize);

        // Assert
        assertThat(actual.getTotalPages()).isEqualTo(expected.getTotalPages());
        assertThat(actual.getNumber()).isEqualTo(expected.getNumber() + 1);
        assertThat(actual.getTotalElements()).isEqualTo(expected.getTotalElements());
        assertThat(actual.getContent()).isEqualTo(expected.getContent());

        verify(warehouseService).findAll(pageable);
    }

    @Test
    @DisplayName("arHsLSCkzs: Given warehouse uuid of existing warehouse when get, then return warehouse")
    void get_ValidUuid_ReturnWarehouse() {

        // Arrange
        final Warehouse expected = Warehouse.builder()
                .uuid(VALID_UUID)
                .city(VALID_CITY)
                .province(VALID_PROVINCE)
                .zipCode(VALID_ZIP)
                .build();

        when(warehouseService.findByUuid(VALID_UUID))
                .thenReturn(Optional.of(expected));

        // Act
        final Warehouse actual = warehouseController.getByUuid(expected.getUuid());

        // Assert
        assertThat(actual).isEqualTo(expected);

        verify(warehouseService, atMostOnce()).findByUuid(expected.getUuid());
    }

    @Test
    @DisplayName("pAaEuqyNLh: Given warehouse uuid of non-existing warehouse when get, then throw WarehouseNotFoundException")
    void get_InvalidUuid_ThrowWarehouseNotFoundException() {

        // Arrange
        when(warehouseService.findByUuid(VALID_UUID))
                .thenReturn(Optional.empty());

        // Act
        final WarehouseNotFoundException warehouseNotFoundException =
                assertThrows(WarehouseNotFoundException.class, () -> warehouseController.getByUuid(VALID_UUID));

        // Assert
        final String message = warehouseNotFoundException.getMessage();
        assertThat(message).isEqualTo(format(ERROR_FORMAT_MESSAGE_WAREHOUSE_NOT_FOUND, VALID_UUID));

        verify(warehouseService, atMostOnce()).findByUuid(VALID_UUID);
    }

    @Test
    @DisplayName("qXrzvvDGIg: Given warehouse uuid of existing warehouse when delete, then delete warehouse")
    void delete_ValidUuid_DeleteWarehouse() {

        // Arrange
        final Warehouse expected = Warehouse.builder()
                .uuid(VALID_UUID)
                .city(VALID_CITY)
                .province(VALID_PROVINCE)
                .zipCode(VALID_ZIP)
                .build();

        // Act
        warehouseController.deleteByUuid(expected.getUuid());

        // Assert
        verify(warehouseService, atMostOnce()).delete(expected.getUuid());
    }

    @Test
    @DisplayName("gwHVqEbnzj: Given warehouse uuid of non-existing warehouse when delete, then do nothing")
    void delete_InvalidUuid_DoNothing() {

        // Act
        warehouseController.deleteByUuid(VALID_UUID);

        // Assert
        verify(warehouseService, atMostOnce()).delete(VALID_UUID);
    }

}
