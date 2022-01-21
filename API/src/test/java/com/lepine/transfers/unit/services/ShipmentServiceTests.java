package com.lepine.transfers.unit.services;

import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.data.shipment.Shipment;
import com.lepine.transfers.data.shipment.ShipmentRepo;
import com.lepine.transfers.data.shipment.ShipmentStatusLessUuidLessDTO;
import com.lepine.transfers.services.shipment.ShipmentService;
import com.lepine.transfers.services.shipment.ShipmentServiceImpl;
import com.lepine.transfers.services.stock.StockService;
import com.lepine.transfers.utils.ConstraintViolationExceptionUtils;
import com.lepine.transfers.utils.date.LocalDateUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {
        ShipmentServiceImpl.class,
        MapperConfig.class,
        ValidationConfig.class,
})
public class ShipmentServiceTests {

    public final static UUID VALID_SHIPMENT_UUID = UUID.randomUUID();

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

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;

    @MockBean
    private ShipmentRepo shipmentRepo;

    @MockBean
    private StockService stockService;

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
}
