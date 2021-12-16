package com.lepine.transfers.services;

import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.data.user.UserRepo;
import com.lepine.transfers.services.auth.AuthService;
import com.lepine.transfers.services.user.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = { MapperConfig.class, UserServiceImpl.class })
@ActiveProfiles({ "test" })
public class AuthServiceTests {

    @Autowired
    private AuthService authService;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    public void contextLoads() {
    }
}
