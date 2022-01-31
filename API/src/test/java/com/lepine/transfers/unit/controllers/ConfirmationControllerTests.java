package com.lepine.transfers.unit.controllers;

import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.controllers.confirmation.ConfirmationController;
import com.lepine.transfers.data.confirmation.Confirmation;
import com.lepine.transfers.data.confirmation.ConfirmationUuidLessDTO;
import com.lepine.transfers.services.confirmation.ConfirmationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Autowired
    private ConfirmationController confirmationController;

    @MockBean
    private ConfirmationService confirmationService;

    @BeforeEach
    void setUp() {
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
        final Confirmation gotten = confirmationController.create(invalidDTO);

        // Assert
        assertThat(gotten).isNull();
    }
}
