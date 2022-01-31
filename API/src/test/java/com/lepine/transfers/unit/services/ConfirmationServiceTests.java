package com.lepine.transfers.unit.services;

import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.data.confirmation.Confirmation;
import com.lepine.transfers.data.confirmation.ConfirmationRepo;
import com.lepine.transfers.data.stock.Stock;
import com.lepine.transfers.data.transfer.Transfer;
import com.lepine.transfers.data.transfer.TransferRepo;
import com.lepine.transfers.exceptions.transfer.TransferNotFoundException;
import com.lepine.transfers.services.confirmation.ConfirmationService;
import com.lepine.transfers.services.confirmation.ConfirmationServiceImpl;
import com.lepine.transfers.utils.ConstraintViolationExceptionUtils;
import com.lepine.transfers.utils.MessageSourceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import javax.validation.ConstraintViolationException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.lepine.transfers.utils.MessageSourceUtils.wrapperFor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest(classes = {
        ConfirmationServiceImpl.class,
        ValidationConfig.class,
})
public class ConfirmationServiceTests {

    private final static UUID
        VALID_TRANSFER_UUID = UUID.randomUUID();

    private final static int VALID_QUANTITY = 10;

    private final static Stock VALID_STOCK = new Stock();

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
    }

    @Test
    void contextLoads(){}

    @Test
    @DisplayName("XJDnsjBgMl: Given UUID of existing transfer and valid quantity when cofirm, then return confirmation")
    void valid_Confirm() {

        // Arrange
        final int toConfirm = VALID_QUANTITY / 2;

        // Act
        final Confirmation confirmation = confirmationService.confirm(VALID_TRANSFER_UUID, toConfirm);

        // Assert
        assertThat(confirmation).isNotNull();
        assertThat(confirmation.getTransferUuid()).isEqualTo(VALID_TRANSFER_UUID);
        assertThat(confirmation.getQuantity()).isEqualTo(VALID_QUANTITY - toConfirm);
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
    @DisplayName("zqzYlkxLwC: Given null transfer UUID when confirm, then throw ConstractViolationException")
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
    @DisplayName("StAtWXDfCo: Given quantity > transger quantity when confirm, then throw QuantityExceededException")
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
        assertThat(quantityExceededException).hasMessage(new QuantityExceededException(VALID_QUANTITY).getMessage());
    }
}
