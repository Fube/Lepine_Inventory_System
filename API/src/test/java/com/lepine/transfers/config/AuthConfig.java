package com.lepine.transfers.config;

import com.lepine.transfers.data.role.RoleRepo;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserRepo;
import com.lepine.transfers.utils.auth.JWTUtil;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@TestConfiguration
public class AuthConfig {

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private JWTUtil<User> jwtUtil;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private RoleRepo roleRepo;

    @MockBean
    private AuthenticationManager authenticationManager;
}
