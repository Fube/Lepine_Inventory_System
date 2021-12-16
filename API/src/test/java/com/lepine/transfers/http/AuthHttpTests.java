package com.lepine.transfers.http;

import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.controllers.auth.AuthController;
import com.lepine.transfers.services.auth.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@WebMvcTest(controllers = { AuthController.class })
@ContextConfiguration(classes = { MapperConfig.class, ValidationConfig.class})
@ActiveProfiles("test")
public class AuthHttpTests {

    @MockBean
    private AuthService authService;

    @Test
    public void contextLoads() {
    }
}
