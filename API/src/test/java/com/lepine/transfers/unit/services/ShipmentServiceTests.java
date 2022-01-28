package com.lepine.transfers.unit.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lepine.transfers.config.JacksonConfig;
import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.shipment.Shipment;
import com.lepine.transfers.data.shipment.ShipmentRepo;
import com.lepine.transfers.data.shipment.ShipmentStatus;
import com.lepine.transfers.data.shipment.ShipmentStatusLessUuidLessDTO;
import com.lepine.transfers.data.stock.Stock;
import com.lepine.transfers.data.transfer.TransferUuidLessDTO;
import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.exceptions.shipment.ShipmentNotFoundException;
import com.lepine.transfers.exceptions.shipment.ShipmentNotPendingException;
import com.lepine.transfers.exceptions.transfer.SameWarehouseException;
import com.lepine.transfers.exceptions.warehouse.WarehouseNotFoundException;
import com.lepine.transfers.services.shipment.ShipmentService;
import com.lepine.transfers.services.shipment.ShipmentServiceImpl;
import com.lepine.transfers.services.stock.StockService;
import com.lepine.transfers.services.warehouse.WarehouseService;
import com.lepine.transfers.utils.ConstraintViolationExceptionUtils;
import com.lepine.transfers.utils.MessageSourceUtils;
import com.lepine.transfers.utils.date.ZonedDateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import javax.json.JsonException;
import javax.json.JsonPatch;
import javax.validation.ConstraintViolationException;
import java.time.ZonedDateTime;
import java.util.*;

import static com.lepine.transfers.utils.MessageSourceUtils.wrapperFor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {
        ShipmentServiceImpl.class,
        MapperConfig.class,
        ValidationConfig.class,
        JacksonAutoConfiguration.class,
        JacksonConfig.class,
})
public class ShipmentServiceTests {

    public final static UUID
            VALID_TARGET_WAREHOUSE_UUID = UUID.randomUUID(),
            VALID_SHIPMENT_UUID = UUID.randomUUID(),
            VALID_STOCK_UUID = UUID.randomUUID();

    private final static int VALID_STOCK_QUANTITY = 10;
    private final static String VALID_SHIPMENT_ORDER_NUMBER = "Some Order Number";
    private final static String SHIPMENT_EXPECTED_DATE_TOO_EARLY_ERROR_MESSAGE_LOCATOR = "shipment.expected.date.too.early";

    private final static ZonedDateTime VALID_SHIPMENT_EXPECTED_DATE = ZonedDateUtils.businessDaysFromNow(4);

    private final static TransferUuidLessDTO VALID_TRANSFER_UUID_LESS_DTO = TransferUuidLessDTO.builder()
            .stockUuid(VALID_STOCK_UUID)
            .quantity(VALID_STOCK_QUANTITY)
            .build();


    private final static ShipmentStatusLessUuidLessDTO VALID_SHIPMENT_STATUS_LESS_UUID_LESS_DTO = ShipmentStatusLessUuidLessDTO.builder()
            .expectedDate(VALID_SHIPMENT_EXPECTED_DATE)
            .orderNumber(VALID_SHIPMENT_ORDER_NUMBER)
            .transfers(List.of(VALID_TRANSFER_UUID_LESS_DTO))
            .to(VALID_TARGET_WAREHOUSE_UUID)
            .build();

    private final static Shipment VALID_SHIPMENT = Shipment.builder()
            .uuid(VALID_SHIPMENT_UUID)
            .expectedDate(VALID_SHIPMENT_EXPECTED_DATE)
            .orderNumber(VALID_SHIPMENT_ORDER_NUMBER)
            .transfers(List.of())
            .to(VALID_TARGET_WAREHOUSE_UUID)
            .build();

    private final static Item VALID_ITEM = Item.builder()
            .uuid(UUID.randomUUID())
            .name("Item Name")
            .description("Item Description")
            .sku("Item SKU")
            .build();

    private final static Warehouse VALID_WAREHOUSE = Warehouse.builder()
            .uuid(VALID_TARGET_WAREHOUSE_UUID)
            .zipCode("A1B2C3")
            .city("City")
            .province("Province")
            .build();

    private final static Stock VALID_STOCK = Stock.builder()
            .uuid(VALID_STOCK_UUID)
            .warehouse(VALID_WAREHOUSE)
            .item(VALID_ITEM)
            .quantity(VALID_STOCK_QUANTITY)
            .build();

    private String
            SHIPMENT_TRANSFER_QUANTITY_LESS_THAN_OR_EQUAL_TO_ZERO_ERROR_MESSAGE,
            SHIPMENT_TRANSFERS_SIZE_LESS_THAN_OR_EQUAL_TO_ZERO_ERROR_MESSAGE,
            SHIPMENT_PATCH_DTO_STATUS_INVALID_MESSAGE,
            ERROR_MESSAGE_SHIPMENT_TRANSFERS_NULL;

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShipmentRepo shipmentRepo;

    @MockBean
    private StockService stockService;

    @MockBean
    private WarehouseService warehouseService;

    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;

    @BeforeEach
    void setUp() {
        final MessageSourceUtils.ForLocaleWrapper w = wrapperFor(messageSource);
        SHIPMENT_TRANSFER_QUANTITY_LESS_THAN_OR_EQUAL_TO_ZERO_ERROR_MESSAGE = w.getMessage("transfer.quantity.min");
        SHIPMENT_TRANSFERS_SIZE_LESS_THAN_OR_EQUAL_TO_ZERO_ERROR_MESSAGE = w.getMessage("shipment.transfers.size.min");
        SHIPMENT_PATCH_DTO_STATUS_INVALID_MESSAGE = w.getMessage("shipment.patch.status.in_enum");
        ERROR_MESSAGE_SHIPMENT_TRANSFERS_NULL = w.getMessage("shipment.transfers.not_null");
    }

    @Test
    void contextLoads() {}

    @Test
    @DisplayName("tVFtvgHnSY: Given DTO with date that is not at least 3 business days from now when create, then throw ConstraintViolationException")
    void invalid_Create_ExpectedDate_NotAtLeast3BusinessDaysFromNow() {

        // Arrange
        ShipmentStatusLessUuidLessDTO invalidDTO = VALID_SHIPMENT_STATUS_LESS_UUID_LESS_DTO.toBuilder()
                .expectedDate(ZonedDateTime.now())
                .build();

        // Act & Assert
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> shipmentService.create(invalidDTO));
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);

        final String interpolatedMessage = messageSource.getMessage(SHIPMENT_EXPECTED_DATE_TOO_EARLY_ERROR_MESSAGE_LOCATOR, null, Locale.getDefault())
                .replace("{days}", "3");
        assertThat(collect).containsExactly(interpolatedMessage);
    }

    @Test
    @DisplayName("msClKIXpFT: Given DTO with 'to' warehouse that does not exist when create, then throw WarehouseNotFoundException")
    void invalid_Create_ToWarehouse_NotExist() {

        // Arrange
        final UUID to = UUID.randomUUID();
        ShipmentStatusLessUuidLessDTO invalidDTO = VALID_SHIPMENT_STATUS_LESS_UUID_LESS_DTO.toBuilder()
                .to(to)
                .build();

        given(warehouseService.findByUuid(to)).willReturn(Optional.empty());

        // Act
        final WarehouseNotFoundException warehouseNotFoundException =
                catchThrowableOfType(() -> shipmentService.create(invalidDTO), WarehouseNotFoundException.class);

        // Assert
        assertThat(warehouseNotFoundException.getMessage())
                .isEqualTo(new WarehouseNotFoundException(to).getMessage());
    }

    @Test
    @DisplayName("VGnZujdvFJ: Given DTO with quantity <= 0 when create, then throw ConstraintViolationException")
    void invalid_Create_Quantity_LessThanOrEqualToZero() {

        // Arrange
        ShipmentStatusLessUuidLessDTO invalidDTO = VALID_SHIPMENT_STATUS_LESS_UUID_LESS_DTO.toBuilder()
                .transfers(List.of(VALID_TRANSFER_UUID_LESS_DTO.toBuilder().quantity(0).build()))
                .build();

        // Act & Assert
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> shipmentService.create(invalidDTO));
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);

        assertThat(collect).containsExactly(SHIPMENT_TRANSFER_QUANTITY_LESS_THAN_OR_EQUAL_TO_ZERO_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("EdWjTaarDF: Given DTO with no transfers when create, then throw ConstraintViolationException")
    void invalid_Create_NoTransfers() {

        // Arrange
        ShipmentStatusLessUuidLessDTO invalidDTO = VALID_SHIPMENT_STATUS_LESS_UUID_LESS_DTO.toBuilder()
                .transfers(List.of())
                .build();

        // Act & Assert
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> shipmentService.create(invalidDTO));
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);

        assertThat(collect).containsExactly(SHIPMENT_TRANSFERS_SIZE_LESS_THAN_OR_EQUAL_TO_ZERO_ERROR_MESSAGE);
        verify(warehouseService, never()).findByUuid(any());
    }

    @Test
    @DisplayName("eiTJSLljFY: Given DTO with transfer to same warehouse when create, then throw SameWarehouseException")
    void invalid_Create_TransferToSameWarehouse() {

        // Arrange
        ShipmentStatusLessUuidLessDTO invalidDTO = VALID_SHIPMENT_STATUS_LESS_UUID_LESS_DTO.toBuilder()
                .transfers(List.of(VALID_TRANSFER_UUID_LESS_DTO))
                .build();

        given(warehouseService.findByUuid(VALID_TARGET_WAREHOUSE_UUID)).willReturn(Optional.of(VALID_WAREHOUSE));
        given(stockService.findByUuidIn(any())).willReturn(Set.of(VALID_STOCK));

        // Act
        final SameWarehouseException sameWarehouseException =
                catchThrowableOfType(() -> shipmentService.create(invalidDTO), SameWarehouseException.class);

        // Assert
        assertThat(sameWarehouseException.getMessage())
                .isEqualTo(new SameWarehouseException(VALID_STOCK, VALID_TARGET_WAREHOUSE_UUID).getMessage());
    }

    @Test
    @DisplayName("hhIwgajnXJ: Given DTO with null transfers when create, then throw ConstraintViolationException")
    void invalid_Create_NullTransfers() {

        // Arrange
        ShipmentStatusLessUuidLessDTO invalidDTO = VALID_SHIPMENT_STATUS_LESS_UUID_LESS_DTO.toBuilder()
                .transfers(null)
                .build();

        // Act & Assert
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> shipmentService.create(invalidDTO));
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);

        assertThat(collect).containsExactly(ERROR_MESSAGE_SHIPMENT_TRANSFERS_NULL);
        verify(warehouseService, never()).findByUuid(any());
    }

    @Test
    @DisplayName("bsfviHubzM: Given JsonPatch when update, then update and return updated entity")
    void valid_Update_JsonPatch() {

        // Arrange
        final Shipment expected = VALID_SHIPMENT.toBuilder().status(ShipmentStatus.ACCEPTED).build();

        final Map<String, Object> patchAsMap = Map.of(
                "value", ShipmentStatus.ACCEPTED.toString(),
                "path", "/status",
                "op", "replace"
        );
        final JsonPatch jsonPatch = objectMapper.convertValue(List.of(patchAsMap), JsonPatch.class);

        given(shipmentRepo.save(any())).willAnswer(invocation -> invocation.getArgument(0)); // Return as is
        given(shipmentRepo.findById(VALID_SHIPMENT_UUID)).willReturn(Optional.of(VALID_SHIPMENT));

        // Act
        final Shipment updatedShipment = shipmentService.update(VALID_SHIPMENT_UUID, jsonPatch);

        // Assert
        assertThat(updatedShipment).usingRecursiveComparison().isEqualTo(expected);
        verify(shipmentRepo, times(1)).save(refEq(expected));
        verify(shipmentRepo, times(1)).findById(VALID_SHIPMENT_UUID);
        verify(applicationEventPublisher, times(1)).publishEvent(
                refEq(new ShipmentUpdatedEvent(shipmentService, VALID_SHIPMENT, expected)));
        );
    }

    @Test
    @DisplayName("RVIhyPdeLK: Given non-existing entity when update, then throw ShipmentNotFoundException")
    void invalid_Update_NonExistingEntity() {

        // Arrange
        final Map<String, Object> patchAsMap = Map.of(
                "value", ShipmentStatus.ACCEPTED.toString(),
                "path", "/status",
                "op", "replace"
        );
        final JsonPatch jsonPatch = objectMapper.convertValue(List.of(patchAsMap), JsonPatch.class);

        given(shipmentRepo.findById(VALID_SHIPMENT_UUID)).willReturn(Optional.empty());

        // Act & Assert
        final ShipmentNotFoundException shipmentNotFoundException =
                catchThrowableOfType(
                        () -> shipmentService.update(VALID_SHIPMENT_UUID, jsonPatch), ShipmentNotFoundException.class);

        assertThat(shipmentNotFoundException.getMessage())
                .isEqualTo(new ShipmentNotFoundException(VALID_SHIPMENT_UUID).getMessage());
        verify(shipmentRepo, never()).save(any());
        verify(shipmentRepo, times(1)).findById(VALID_SHIPMENT_UUID);
    }

    @Test
    @DisplayName("gQZVHDBSPg: Given JsonPatch with invalid path when update, then throw JsonException")
    void invalid_Update_InvalidPath() {

        // Arrange
        final Map<String, Object> patchAsMap = Map.of(
                "value", ShipmentStatus.ACCEPTED.toString(),
                "path", "/invalid",
                "op", "replace"
        );
        final JsonPatch jsonPatch = objectMapper.convertValue(List.of(patchAsMap), JsonPatch.class);

        given(shipmentRepo.findById(VALID_SHIPMENT_UUID)).willReturn(Optional.of(VALID_SHIPMENT));

        // Act & Assert
        final JsonException jsonException =
                catchThrowableOfType(() -> shipmentService.update(VALID_SHIPMENT_UUID, jsonPatch), JsonException.class);

        assertThat(jsonException)
                .hasMessageContaining("contains no value for name 'invalid'");
        verify(shipmentRepo, never()).save(any());
        verify(shipmentRepo, times(1)).findById(VALID_SHIPMENT_UUID);
    }

    @Test
    @DisplayName("vtKrSaYebx: Given JsonPatch with invalid value when update, then throw ConstraintViolationException")
    void invalid_Update_InvalidValue() {

        // Arrange
        final Map<String, Object> patchAsMap = Map.of(
                "value", "invalid",
                "path", "/status",
                "op", "replace"
        );
        final JsonPatch jsonPatch = objectMapper.convertValue(List.of(patchAsMap), JsonPatch.class);

        given(shipmentRepo.findById(VALID_SHIPMENT_UUID)).willReturn(Optional.of(VALID_SHIPMENT));

        // Act
        final ConstraintViolationException constraintViolationException =
                catchThrowableOfType(() -> shipmentService.update(VALID_SHIPMENT_UUID, jsonPatch), ConstraintViolationException.class);

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);

        assertThat(collect).containsExactlyInAnyOrder(SHIPMENT_PATCH_DTO_STATUS_INVALID_MESSAGE);

        verify(shipmentRepo, never()).save(any());
        verify(shipmentRepo, times(1)).findById(VALID_SHIPMENT_UUID);
    }

    @Test
    @DisplayName("AqfTQwkxYA: Given non-pending shipment when update, then throw ShipmentNotPendingException")
    void invalid_Update_NonPendingShipment() {

        // Arrange
        final Map<String, Object> patchAsMap = Map.of(
                "value", ShipmentStatus.ACCEPTED.toString(),
                "path", "/status",
                "op", "replace"
        );
        final JsonPatch jsonPatch = objectMapper.convertValue(List.of(patchAsMap), JsonPatch.class);

        given(shipmentRepo.findById(VALID_SHIPMENT_UUID)).willReturn(Optional.of(VALID_SHIPMENT.toBuilder()
                .status(ShipmentStatus.ACCEPTED)
                .build()));

        // Act & Assert
        final ShipmentNotPendingException shipmentNotPendingException =
                catchThrowableOfType(
                        () -> shipmentService.update(VALID_SHIPMENT_UUID, jsonPatch), ShipmentNotPendingException.class);

        assertThat(shipmentNotPendingException)
                .hasMessage(new ShipmentNotPendingException(VALID_SHIPMENT_UUID).getMessage());
        verify(shipmentRepo, never()).save(any());
        verify(shipmentRepo, times(1)).findById(VALID_SHIPMENT_UUID);
    }
}
