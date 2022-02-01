package com.lepine.transfers.integration.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lepine.transfers.config.AuthConfig;
import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.controllers.confirmation.ConfirmationController;
import com.lepine.transfers.data.confirmation.Confirmation;
import com.lepine.transfers.data.confirmation.ConfirmationUuidLessDTO;
import com.lepine.transfers.data.shipment.ShipmentStatus;
import com.lepine.transfers.exceptions.shipment.ShipmentNotAcceptedException;
import com.lepine.transfers.exceptions.transfer.QuantityExceededException;
import com.lepine.transfers.exceptions.transfer.TransferNotFoundException;
import com.lepine.transfers.services.confirmation.ConfirmationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = { ConfirmationController.class })
@ContextConfiguration(classes = { MapperConfig.class, ValidationConfig.class, AuthConfig.class, })
@ActiveProfiles("test")
public class ConfirmationHttpTests {

    private final static UUID
            VALID_CONFIRMATION_UUID = UUID.randomUUID(),
            VALID_TRANSFER_UUID = UUID.randomUUID(),
            EXCEEDING_TRANSFER_UUID = UUID.randomUUID(),
            NON_EXISTING_TRANSFER_UUID = UUID.randomUUID(),
            NON_ACCEPTED_TRANSFER_UUID = UUID.randomUUID();

    private final static int
            VALID_QUANTITY = 10,
            EXCEEDING_QUANTITY = VALID_QUANTITY + 1;

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
    private MockMvc mockMvc;

    @Autowired
    private ConfirmationController confirmationController;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ConfirmationService confirmationService;

    private ResultActions create() throws Exception {
        return create(VALID_CONFIRMATION_UUID_LESS_DTO);
    }

    private ResultActions create(final ConfirmationUuidLessDTO confirmationUuidLessDTO) throws Exception {

        // Arrange
        final String givenAsString = objectMapper.writeValueAsString(confirmationUuidLessDTO);

        // Act
        return mockMvc.perform(post("/confirmations")
                        .content(givenAsString).contentType(MediaType.APPLICATION_JSON));
    }

    private void assertValidCreate(final ResultActions resultActions) throws Exception {
        final String expectedAsString = objectMapper.writeValueAsString(VALID_CONFIRMATION);

         resultActions.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedAsString));

    }

    @BeforeEach
    void setUp() {
        given(confirmationService.confirm(VALID_TRANSFER_UUID, VALID_QUANTITY))
                .willReturn(VALID_CONFIRMATION);

        given(confirmationService.confirm(EXCEEDING_TRANSFER_UUID, EXCEEDING_QUANTITY))
                .willThrow(new QuantityExceededException(VALID_QUANTITY, EXCEEDING_QUANTITY));

        given(confirmationService.confirm(eq(NON_EXISTING_TRANSFER_UUID), anyInt()))
                .willThrow(new TransferNotFoundException(NON_EXISTING_TRANSFER_UUID));
    }

    @Test
    void contextLoads(){}

    @Test
    @DisplayName("xjMcYtXrjW: Given POST on /confirmations, with valid DTO as manager, then return confirmation (201, confirmation)")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void valid_Create_AsManager() throws Exception {
        assertValidCreate(create());
    }

    @Test
    @DisplayName("cVSjDHhmfY: Given POST on /confirmations, with valid DTO as clerk, then return confirmation (201, confirmation)")
    @WithMockUser(username = "some-clerk", roles = "CLERK")
    void valid_Create_AsClerk() throws Exception {
        assertValidCreate(create());
    }

    @Test
    @DisplayName("bXwwRKKRPA: Given POST on /confirmations, with valid DTO as salesperson, then deny access (403, error)")
    @WithMockUser(username = "some-salesperson", roles = "SALESPERSON")
    void valid_Create_AsSalesperson() throws Exception {
        create().andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("gnWMCLBnLm: Given POST on /confirmations, with exceeding quantity, return error (400, error)")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void exceeding_Create() throws Exception {

        // Arrange
        final ConfirmationUuidLessDTO given = ConfirmationUuidLessDTO.builder()
                .transferUuid(EXCEEDING_TRANSFER_UUID)
                .quantity(EXCEEDING_QUANTITY)
                .build();

        // Act & Assert
        create(given).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(new QuantityExceededException(VALID_QUANTITY, EXCEEDING_QUANTITY).getMessage()));
    }

    @Test
    @DisplayName("qNVVLJFLye: Given POST on /confirmations, with transferUuid for non-existing transfer, return not found (404, error)")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void nonExisting_Create() throws Exception {

        // Arrange
        final ConfirmationUuidLessDTO given = ConfirmationUuidLessDTO.builder()
                .transferUuid(NON_EXISTING_TRANSFER_UUID)
                .quantity(VALID_QUANTITY)
                .build();

        // Act & Assert
        create(given).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value(new TransferNotFoundException(NON_EXISTING_TRANSFER_UUID).getMessage()));
    }

    @ParameterizedTest(name = "{displayName} - Status: {0}")
    @DisplayName("PnWmFkHyrc: Given POST on /confirmations, for non ACCEPTED shipment, return error (400, error)")
    @EnumSource(value = ShipmentStatus.class, names = {"DENIED", "PENDING"})
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void nonAccepted_Create(final ShipmentStatus status) throws Exception {

        // Arrange
        final ConfirmationUuidLessDTO given = ConfirmationUuidLessDTO.builder()
                .transferUuid(NON_ACCEPTED_TRANSFER_UUID)
                .quantity(VALID_QUANTITY)
                .build();

        final String name = status.name();
        given(confirmationService.confirm(any(), anyInt()))
                .willThrow(new ShipmentNotAcceptedException(NON_ACCEPTED_TRANSFER_UUID, name));

        // Act & Assert
        create(given).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(new ShipmentNotAcceptedException(NON_ACCEPTED_TRANSFER_UUID, name).getMessage()))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
