package com.lepine.transfers.config;

import com.lepine.transfers.data.user.User;
import com.lepine.transfers.utils.auth.JWTUtil;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
public class AuthConfig {

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JWTUtil<User> jwtUtil;
}
