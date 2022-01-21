package com.lepine.transfers.unit.services;

import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.data.shipment.Shipment;
import com.lepine.transfers.data.shipment.ShipmentRepo;
import com.lepine.transfers.data.shipment.ShipmentStatusLessUuidLessDTO;
import com.lepine.transfers.data.transfer.TransferUuidLessDTO;
import com.lepine.transfers.exceptions.warehouse.WarehouseNotFoundException;
import com.lepine.transfers.services.shipment.ShipmentService;
import com.lepine.transfers.services.shipment.ShipmentServiceImpl;
import com.lepine.transfers.services.stock.StockService;
import com.lepine.transfers.services.warehouse.WarehouseService;
import com.lepine.transfers.utils.ConstraintViolationExceptionUtils;
import com.lepine.transfers.utils.MessageSourceUtils;
import com.lepine.transfers.utils.date.LocalDateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.*;

import static com.lepine.transfers.utils.MessageSourceUtils.wrapperFor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@SpringBootTest(classes = {
        ShipmentServiceImpl.class,
        MapperConfig.class,
        ValidationConfig.class,
})
public class ShipmentServiceTests {

    public final static UUID
            VALID_SHIPMENT_UUID = UUID.randomUUID(),
            VALID_STOCK_UUID = UUID.randomUUID();

    private final static int VALID_STOCK_QUANTITY = 10;
    private final static String VALID_SHIPMENT_ORDER_NUMBER = "Some Order Number";
    private final static String SHIPMENT_EXPECTED_DATE_TOO_EARLY_ERROR_MESSAGE_LOCATOR = "shipment.expected.date.too.early";

    private final static LocalDate VALID_SHIPMENT_EXPECTED_DATE = LocalDateUtils.businessDaysFromNow(3);

    private final static ShipmentStatusLessUuidLessDTO VALID_SHIPMENT_STATUS_LESS_UUID_LESS_DTO = ShipmentStatusLessUuidLessDTO.builder()
            .expectedDate(VALID_SHIPMENT_EXPECTED_DATE)
            .orderNumber(VALID_SHIPMENT_ORDER_NUMBER)
            .transfers(List.of())
            .build();

    private final static Shipment VALID_SHIPMENT = Shipment.builder()
            .uuid(VALID_SHIPMENT_UUID)
            .expectedDate(VALID_SHIPMENT_EXPECTED_DATE)
            .orderNumber(VALID_SHIPMENT_ORDER_NUMBER)
            .transfers(List.of())
            .build();

    private final TransferUuidLessDTO VALID_TRANSFER_UUID_LESS_DTO = TransferUuidLessDTO.builder()
            .stockUuid(VALID_STOCK_UUID)
            .quantity(VALID_STOCK_QUANTITY)
            .build();

    private String SHIPMENT_TRANSFER_QUANTITY_LESS_THAN_OR_EQUAL_TO_ZERO_ERROR_MESSAGE;

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;

    @MockBean
    private ShipmentRepo shipmentRepo;

    @MockBean
    private StockService stockService;

    @MockBean
    private WarehouseService warehouseService;

    @BeforeEach
    void setUp() {
        final MessageSourceUtils.ForLocaleWrapper w = wrapperFor(messageSource);
        SHIPMENT_TRANSFER_QUANTITY_LESS_THAN_OR_EQUAL_TO_ZERO_ERROR_MESSAGE = w.getMessage("transfer.quantity.min");
    }

    @Test
    void contextLoads() {}

    @Test
    @DisplayName("tVFtvgHnSY: Given DTO with date that is not at least 3 business days from now when create, then throw ConstraintViolationException")
    void invalid_Create_ExpectedDate_NotAtLeast3BusinessDaysFromNow() {

        // Arrange
        ShipmentStatusLessUuidLessDTO invalidDTO = VALID_SHIPMENT_STATUS_LESS_UUID_LESS_DTO.toBuilder()
                .expectedDate(LocalDate.now())
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
    @DisplayName("pagination.size.min: Given DTO with quantity <= 0 when create, then throw ConstraintViolationException")
    void invalid_Create_Quantity_LessThanOrEqualToZero() {

        // Arrange
        ShipmentStatusLessUuidLessDTO invalidDTO = VALID_SHIPMENT_STATUS_LESS_UUID_LESS_DTO.toBuilder()
                .transfers(List.of(VALID_TRANSFER_UUID_LESS_DTO))
                .build();

        // Act & Assert
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> shipmentService.create(invalidDTO));
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);

        assertThat(collect).containsExactly(SHIPMENT_TRANSFER_QUANTITY_LESS_THAN_OR_EQUAL_TO_ZERO_ERROR_MESSAGE);
    }
}
