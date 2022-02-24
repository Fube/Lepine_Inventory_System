package com.lepine.transfers.unit.services;

import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.data.confirmation.Confirmation;
import com.lepine.transfers.data.confirmation.ConfirmationRepo;
import com.lepine.transfers.data.item.Item;
import com.lepine.transfers.data.shipment.Shipment;
import com.lepine.transfers.data.shipment.ShipmentRepo;
import com.lepine.transfers.data.shipment.ShipmentStatus;
import com.lepine.transfers.data.stock.Stock;
import com.lepine.transfers.data.stock.StockMapper;
import com.lepine.transfers.data.stock.StockRepo;
import com.lepine.transfers.data.stock.StockSearchDTO;
import com.lepine.transfers.data.transfer.Transfer;
import com.lepine.transfers.data.transfer.TransferRepo;
import com.lepine.transfers.exceptions.shipment.ShipmentNotAcceptedException;
import com.lepine.transfers.exceptions.shipment.ShipmentNotFoundException;
import com.lepine.transfers.exceptions.transfer.QuantityExceededException;
import com.lepine.transfers.exceptions.transfer.TransferNotFoundException;
import com.lepine.transfers.services.confirmation.ConfirmationService;
import com.lepine.transfers.services.confirmation.ConfirmationServiceImpl;
import com.lepine.transfers.services.search.SearchService;
import com.lepine.transfers.utils.ConstraintViolationExceptionUtils;
import com.lepine.transfers.utils.MessageSourceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import javax.validation.ConstraintViolationException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static com.lepine.transfers.utils.MessageSourceUtils.wrapperFor;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {
        ConfirmationServiceImpl.class,
        ValidationConfig.class,
})
public class ConfirmationServiceTests {

    private final static UUID
        VALID_TRANSFER_UUID = UUID.randomUUID(),
        NOT_ACCEPTED_SHIPMENT_UUID = UUID.randomUUID(),
        VALID_TO_UUID = UUID.randomUUID(),
        VALID_ITEM_UUID = UUID.randomUUID(),
        NOT_FOUND_SHIPMENT_UUID = UUID.randomUUID();

    private final static int VALID_QUANTITY = 10;

    private final static Stock VALID_STOCK = Stock.builder()
            .item(Item.builder().uuid(VALID_ITEM_UUID).build())
            .build();

    private final static Transfer VALID_TRANSFER = Transfer.builder()
            .uuid(VALID_TRANSFER_UUID)
            .quantity(VALID_QUANTITY)
            .stock(VALID_STOCK)
            .build();

    private String
            TRANSFER_UUID_NOT_NULL_MESSAGE,
            TRANSFER_MIN_MESSAGE;

    @Autowired
    private ConfirmationService confirmationService;

    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;

    @MockBean
    private ConfirmationRepo confirmationRepo;

    @MockBean
    private TransferRepo transferRepo;

    @MockBean
    private ShipmentRepo shipmentRepo;

    @MockBean
    private StockRepo stockRepo;

    @MockBean
    private SearchService<StockSearchDTO, UUID> searchService;

    @MockBean
    private StockMapper stockMapper;

    @BeforeEach
    void setUp() {

        final MessageSourceUtils.ForLocaleWrapper wrapper = wrapperFor(messageSource);
        TRANSFER_UUID_NOT_NULL_MESSAGE = wrapper.getMessage("transfer.uuid.not_null");
        TRANSFER_MIN_MESSAGE = wrapper.getMessage("transfer.quantity.min");

        given(transferRepo.findById(VALID_TRANSFER_UUID))
                .willReturn(Optional.of(VALID_TRANSFER));

        given(confirmationRepo.save(any()))
                .willAnswer(invocation -> {
                    final Confirmation argument = invocation.getArgument(0);
                    argument.setUuid(UUID.randomUUID());
                    return argument;
                });

        given(transferRepo.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        given(shipmentRepo.findByTransferUuid(VALID_TRANSFER_UUID))
                .willReturn(Optional.of(Shipment.builder()
                        .status(ShipmentStatus.ACCEPTED)
                        .to(VALID_TO_UUID)
                        .build()));
    }

    @Test
    void contextLoads(){}

    @ParameterizedTest(name = "{displayName} - Context: confirmed {0} confirming {1}")
    @DisplayName("XJDnsjBgMl: Given UUID of existing transfer and valid quantity when confirm, then return confirmation")
    @MethodSource("validConfirmations")
    void valid_Confirm(final int alreadyConfirmed, final int confirming) {

        // Arrange
        given(confirmationRepo.sumQuantityByTransferUuid(VALID_TRANSFER_UUID)).willReturn(alreadyConfirmed);
        given(stockRepo.findByWarehouseUuidAndItemUuid(VALID_TO_UUID, VALID_ITEM_UUID))
                .willReturn(Optional.of(VALID_STOCK.toBuilder().quantity(alreadyConfirmed).build()));

        // Act
        final Confirmation confirmation = confirmationService.confirm(VALID_TRANSFER_UUID, confirming);

        // Assert
        assertThat(confirmation).isNotNull();
        assertThat(confirmation.getTransferUuid()).isEqualTo(VALID_TRANSFER_UUID);
        assertThat(confirmation.getQuantity()).isEqualTo(confirming);

        verify(transferRepo, times(1)).findById(VALID_TRANSFER_UUID);
        verify(confirmationRepo, times(1)).sumQuantityByTransferUuid(VALID_TRANSFER_UUID);
        verify(confirmationRepo, times(1)).save(confirmation);
        verify(stockRepo, times(1))
                .findByWarehouseUuidAndItemUuid(VALID_TO_UUID, VALID_ITEM_UUID);
    }

    private static Stream<Arguments> validConfirmations() {
        final Arguments[] arguments = new Arguments[VALID_QUANTITY];

        for (int i = 0; i < VALID_QUANTITY; i++) {
            arguments[i] = Arguments.of(i, VALID_QUANTITY - i);
        }

        return Stream.of(arguments);
    }

    @Test
    @DisplayName("dFgIhpBgzK: Given UUID of non-existing transfer when confirm, then throw TransferNotFoundException")
    void non_existing_transfer_Confirm() {

        // Arrange
        final int toConfirm = VALID_QUANTITY / 2;
        final UUID transferUuid = UUID.randomUUID();

        // Act
        final TransferNotFoundException transferNotFoundException =
                catchThrowableOfType(
                        () -> confirmationService.confirm(transferUuid, toConfirm),
                        TransferNotFoundException.class);

        // Assert
        assertThat(transferNotFoundException).isNotNull();
        assertThat(transferNotFoundException).hasMessage(new TransferNotFoundException(transferUuid).getMessage());
    }

    @Test
    @DisplayName("zqzYlkxLwC: Given null transfer UUID when confirm, then throw ConstraintViolationException")
    void null_transfer_UUID_Confirm() {

        // Arrange
        final int toConfirm = VALID_QUANTITY / 2;

        // Act
        final ConstraintViolationException constraintViolationException =
                catchThrowableOfType(
                        () -> confirmationService.confirm(null, toConfirm),
                        ConstraintViolationException.class);

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactlyInAnyOrder(TRANSFER_UUID_NOT_NULL_MESSAGE);
    }

    @ParameterizedTest(name = "{displayName} - {0}")
    @DisplayName("EEFODQzUOB: Given quantity <= 0 when confirm, then throw ConstraintViolationException")
    @ValueSource(ints = {-1, 0})
    void invalid_quantity_Confirm(final int quantity) {

        // Act
        final ConstraintViolationException constraintViolationException =
                catchThrowableOfType(
                        () -> confirmationService.confirm(VALID_TRANSFER_UUID, quantity),
                        ConstraintViolationException.class);

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactlyInAnyOrder(TRANSFER_MIN_MESSAGE);
    }

    @Test
    @DisplayName("StAtWXDfCo: Given quantity > transfer.quantity when confirm, then throw QuantityExceededException")
    void quantity_exceeded_Confirm() {

        // Arrange
        final int toConfirm = VALID_QUANTITY + 1;

        // Act
        final QuantityExceededException quantityExceededException =
                catchThrowableOfType(
                        () -> confirmationService.confirm(VALID_TRANSFER_UUID, toConfirm),
                        QuantityExceededException.class);

        // Assert
        assertThat(quantityExceededException).isNotNull();
        assertThat(quantityExceededException)
                .hasMessage(new QuantityExceededException(VALID_QUANTITY, toConfirm).getMessage());
    }

    @ParameterizedTest(name = "{displayName} - Status: {0}")
    @DisplayName("xiUSpqwuHx: Given not ACCEPTED transfer when confirm, then throw ShipmentNotAcceptedException")
    @EnumSource(value = ShipmentStatus.class, names = {"DENIED", "PENDING"})
    void not_ACCEPTED_transfer_Confirm(final ShipmentStatus status) {

        // Arrange
        final int toConfirm = VALID_QUANTITY / 2;

        given(transferRepo.findById(NOT_ACCEPTED_SHIPMENT_UUID))
                .willReturn(Optional.ofNullable(VALID_TRANSFER));

        given(shipmentRepo.findByTransferUuid(NOT_ACCEPTED_SHIPMENT_UUID)).willReturn(Optional.of(Shipment.builder()
                .uuid(NOT_ACCEPTED_SHIPMENT_UUID)
                .status(status)
                .build()));


        // Act
        final ShipmentNotAcceptedException shipmentNotAcceptedException =
                catchThrowableOfType(
                        () -> confirmationService.confirm(NOT_ACCEPTED_SHIPMENT_UUID, toConfirm),
                        ShipmentNotAcceptedException.class);

        // Assert
        assertThat(shipmentNotAcceptedException).isNotNull();
        assertThat(shipmentNotAcceptedException)
                .hasMessage(new ShipmentNotAcceptedException(NOT_ACCEPTED_SHIPMENT_UUID, status.name()).getMessage());
    }

    @Test
    @DisplayName("aEPiYSoTwl: Given shipment not found when confirm, then throw ShipmentNotFoundException")
    void shipment_not_found_Confirm() {

        // Arrange
        final int toConfirm = VALID_QUANTITY / 2;

        given(transferRepo.findById(NOT_FOUND_SHIPMENT_UUID))
                .willReturn(Optional.ofNullable(VALID_TRANSFER));

        given(shipmentRepo.findByTransferUuid(NOT_FOUND_SHIPMENT_UUID)).willReturn(Optional.empty());

        // Act
        final ShipmentNotFoundException shipmentNotFoundException =
                catchThrowableOfType(
                        () -> confirmationService.confirm(NOT_FOUND_SHIPMENT_UUID, toConfirm)
                        , ShipmentNotFoundException.class);

        // Assert
        assertThat(shipmentNotFoundException).isNotNull();
        assertThat(shipmentNotFoundException)
                .hasMessage(new ShipmentNotFoundException().getMessage());

        verify(transferRepo, times(1)).findById(NOT_FOUND_SHIPMENT_UUID);
        verify(shipmentRepo, times(1)).findByTransferUuid(NOT_FOUND_SHIPMENT_UUID);
    }

    @Test
    @DisplayName("sOvUBacHTt: Given sumQuantityByTransferUuid is null, set quantity to 0, then return confirmation")
    void sumQuantityByTransferUuid_null_Confirm() {

        // Arrange
        final int toConfirm = VALID_QUANTITY / 2;

        given(transferRepo.findById(VALID_TRANSFER_UUID))
                .willReturn(Optional.ofNullable(VALID_TRANSFER));

        given(shipmentRepo.findByTransferUuid(VALID_TRANSFER_UUID)).willReturn(Optional.of(Shipment.builder()
                .uuid(VALID_TRANSFER_UUID)
                .status(ShipmentStatus.ACCEPTED)
                .build()));

        given(confirmationRepo.sumQuantityByTransferUuid(VALID_TRANSFER_UUID))
                .willReturn(null);

        // Act
        final Confirmation confirmation = confirmationService.confirm(VALID_TRANSFER_UUID, toConfirm);

        // Assert
        assertThat(confirmation).isNotNull();
        assertThat(confirmation.getTransferUuid()).isEqualTo(VALID_TRANSFER_UUID);
        assertThat(confirmation.getQuantity()).isEqualTo(toConfirm);

        verify(transferRepo, times(1)).findById(VALID_TRANSFER_UUID);
        verify(shipmentRepo, times(1)).findByTransferUuid(VALID_TRANSFER_UUID);
        verify(confirmationRepo, times(1))
                .sumQuantityByTransferUuid(VALID_TRANSFER_UUID);
    }
}
