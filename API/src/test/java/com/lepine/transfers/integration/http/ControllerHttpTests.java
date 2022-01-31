package com.lepine.transfers.integration.http;

import com.lepine.transfers.config.AuthConfig;
import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.controllers.confirmation.ConfirmationController;
import com.lepine.transfers.data.confirmation.Confirmation;
import com.lepine.transfers.data.confirmation.ConfirmationUuidLessDTO;
import com.lepine.transfers.services.confirmation.ConfirmationService;
import com.lepine.transfers.utils.MessageSourceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static com.lepine.transfers.utils.MessageSourceUtils.wrapperFor;
import static org.mockito.BDDMockito.given;

@WebMvcTest(controllers = { ConfirmationController.class })
@ContextConfiguration(classes = { MapperConfig.class, ValidationConfig.class, AuthConfig.class})
@ActiveProfiles("test")
public class ControllerHttpTests {

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
}
