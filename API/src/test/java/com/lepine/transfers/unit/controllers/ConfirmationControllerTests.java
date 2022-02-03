package com.lepine.transfers.unit.controllers;

import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.controllers.confirmation.ConfirmationController;
import com.lepine.transfers.data.confirmation.Confirmation;
import com.lepine.transfers.data.confirmation.ConfirmationUuidLessDTO;
import com.lepine.transfers.services.confirmation.ConfirmationService;
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
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolationException;
import java.util.Set;
import java.util.UUID;

import static com.lepine.transfers.utils.MessageSourceUtils.wrapperFor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.given;

@SpringBootTest(classes = {
        ConfirmationController.class,
        MapperConfig.class,
        ValidationConfig.class,
})
@ActiveProfiles({"test"})
public class ConfirmationControllerTests {

    private final static UUID
            VALID_CONFIRMATION_UUID = UUID.randomUUID(),
            VALID_TRANSFER_UUID = UUID.randomUUID();

    private final static int VALID_QUANTITY = 10;

    private final static Confirmation VALID_CONFIRMATION = Confirmation.builder()
            .uuid(VALID_CONFIRMATION_UUID)
            .quantity(VALID_QUANTITY)
            .transferUuid(VALID_TRANSFER_UUID)
            .build();

    private final static ConfirmationUuidLessDTO VALID_CONFIRMATION_UUID_LESS_DTO = ConfirmationUuidLessDTO.builder()
            .transferUuid(VALID_TRANSFER_UUID)
            .quantity(VALID_QUANTITY)
            .build();

    private String
            TRANSFER_UUID_NOT_NULL_MESSAGE,
            TRANSFER_MIN_MESSAGE;

    @Autowired
    private ConfirmationController confirmationController;

    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;

    @MockBean
    private ConfirmationService confirmationService;

    @BeforeEach
    void setUp() {

        final MessageSourceUtils.ForLocaleWrapper wrapper = wrapperFor(messageSource);
        TRANSFER_UUID_NOT_NULL_MESSAGE = wrapper.getMessage("transfer.uuid.not_null");
        TRANSFER_MIN_MESSAGE = wrapper.getMessage("transfer.quantity.min");

        given(confirmationService.confirm(VALID_TRANSFER_UUID, VALID_QUANTITY))
                .willReturn(VALID_CONFIRMATION);
    }

    @Test
    void contextLoads(){}

    @Test
    @DisplayName("ZSdEdDwCnP: Given confirm with valid DTO, then return created confirmation")
    void valid_Create() {

        // Act
        final Confirmation gotten = confirmationController.create(VALID_CONFIRMATION_UUID_LESS_DTO);

        // Assert
        assertThat(gotten).isEqualTo(VALID_CONFIRMATION);
    }

    @ParameterizedTest(name = "{displayName} - {0}")
    @DisplayName("bysvsSIdkN: Given confirm with quantity <= 0, then throw ConstraintViolationException")
    @ValueSource(ints = {0, -1})
    void invalid_Create_quantity_less_than_zero(int quantity) {

        // Arrange
        final ConfirmationUuidLessDTO invalidDTO = VALID_CONFIRMATION_UUID_LESS_DTO.toBuilder()
                .quantity(quantity)
                .build();

        // Act
        final ConstraintViolationException constraintViolationException =
                catchThrowableOfType(
                        () -> confirmationController.create(invalidDTO),
                        ConstraintViolationException.class);

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactlyInAnyOrder(TRANSFER_MIN_MESSAGE);
    }

    @Test
    @DisplayName("oHhTveeFeE: Given null transfer UUID, then throw ConstraintViolationException")
    void invalid_Create_transfer_uuid_null() {

        // Arrange
        final ConfirmationUuidLessDTO invalidDTO = VALID_CONFIRMATION_UUID_LESS_DTO.toBuilder()
                .transferUuid(null)
                .build();

        // Act
        final ConstraintViolationException constraintViolationException =
                catchThrowableOfType(
                        () -> confirmationController.create(invalidDTO),
                        ConstraintViolationException.class);

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactlyInAnyOrder(TRANSFER_UUID_NOT_NULL_MESSAGE);
    }
}
