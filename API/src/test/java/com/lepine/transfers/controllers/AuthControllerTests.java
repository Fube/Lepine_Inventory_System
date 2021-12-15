package com.lepine.transfers.controllers;

import com.lepine.transfers.controllers.auth.AuthController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = { AuthController.class })
@ActiveProfiles({"test"})
public class AuthControllerTests {

    @Autowired
    private AuthController authController;

    @Test
    void contextLoads() {
    }
}
