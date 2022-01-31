package com.lepine.transfers.unit.controllers;

import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.controllers.confirmation.ConfirmationController;
import com.lepine.transfers.services.confirmation.ConfirmationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {
        ConfirmationController.class,
        MapperConfig.class,
        ValidationConfig.class,
})
@ActiveProfiles({"test"})
public class ConfirmationControllerTests {

    @Autowired
    private ConfirmationController confirmationController;

    @MockBean
    private ConfirmationService confirmationService;

    @Test
    void contextLoads(){}
}
