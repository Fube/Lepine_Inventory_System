package com.lepine.transfers.integration.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lepine.transfers.config.AuthConfig;
import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.controllers.confirmation.ConfirmationController;
import com.lepine.transfers.data.confirmation.Confirmation;
import com.lepine.transfers.data.confirmation.ConfirmationUuidLessDTO;
import com.lepine.transfers.exceptions.transfer.QuantityExceededException;
import com.lepine.transfers.services.confirmation.ConfirmationService;
import com.lepine.transfers.utils.MessageSourceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static com.lepine.transfers.utils.MessageSourceUtils.wrapperFor;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = { ConfirmationController.class })
@ContextConfiguration(classes = { MapperConfig.class, ValidationConfig.class, AuthConfig.class, })
@ActiveProfiles("test")
public class ControllerHttpTests {

    private final static UUID
            VALID_CONFIRMATION_UUID = UUID.randomUUID(),
            VALID_TRANSFER_UUID = UUID.randomUUID(),
            EXCEEDING_TRANSFER_UUID = UUID.randomUUID();

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

    private String
            TRANSFER_UUID_NOT_NULL_MESSAGE,
            TRANSFER_MIN_MESSAGE;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ConfirmationController confirmationController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;

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

        final MessageSourceUtils.ForLocaleWrapper wrapper = wrapperFor(messageSource);
        TRANSFER_UUID_NOT_NULL_MESSAGE = wrapper.getMessage("transfer.uuid.not_null");
        TRANSFER_MIN_MESSAGE = wrapper.getMessage("transfer.quantity.min");

        given(confirmationService.confirm(VALID_TRANSFER_UUID, VALID_QUANTITY))
                .willReturn(VALID_CONFIRMATION);

        given(confirmationService.confirm(EXCEEDING_TRANSFER_UUID, EXCEEDING_QUANTITY))
                .willThrow(new QuantityExceededException(VALID_QUANTITY, EXCEEDING_QUANTITY));
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
                .andExpect(jsonPath("$.errors.quantity").isArray())
                .andExpect(jsonPath("$.errors.quantity[*]", containsInAnyOrder(TRANSFER_MIN_MESSAGE)));
    }
}
