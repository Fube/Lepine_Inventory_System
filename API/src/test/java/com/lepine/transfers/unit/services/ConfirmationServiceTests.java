package com.lepine.transfers.unit.services;

import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.data.confirmation.ConfirmationRepo;
import com.lepine.transfers.data.transfer.TransferRepo;
import com.lepine.transfers.services.confirmation.ConfirmationService;
import com.lepine.transfers.services.confirmation.ConfirmationServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@SpringBootTest(classes = {
        ConfirmationServiceImpl.class,
        ValidationConfig.class,
})
public class ConfirmationServiceTests {

    @Autowired
    private ConfirmationService confirmationService;

    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;

    @MockBean
    private ConfirmationRepo confirmationRepo;

    @MockBean
    private TransferRepo transferRepo;

    @Test
    void contextLoads(){}
}
