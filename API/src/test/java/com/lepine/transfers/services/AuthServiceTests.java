package com.lepine.transfers.services;

import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.data.auth.Role;
import com.lepine.transfers.data.auth.UserLogin;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserRepo;
import com.lepine.transfers.services.auth.AuthService;
import com.lepine.transfers.services.user.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = { MapperConfig.class, UserServiceImpl.class })
@ActiveProfiles({ "test" })
public class AuthServiceTests {

    private static final String VALID_EMAIL = "foo@bar.com";
    private static final String VALID_PASSWORD = "S0meP@ssw0rd";
    private static final String VALID_ROLE_NAME = "SOME_ROLE";
    private static final Role VALID_ROLE = Role.builder()
            .uuid(UUID.randomUUID())
            .name(VALID_ROLE_NAME)
            .build();

    private static final User VALID_USER = User.builder()
            .uuid(UUID.randomUUID())
            .email(VALID_EMAIL)
            .password(VALID_PASSWORD)
            .role(VALID_ROLE)
            .build();

    @Autowired
    private AuthService authService;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    public void contextLoads() {
    }

    @AfterEach
    public void tearDown() {
        reset(userRepo);
        reset(passwordEncoder);
    }

    @Test
    @DisplayName("Given login with valid user data, then return User")
    public void login_Valid() {

        // Arrange
        final UserLogin userLogin = UserLogin.builder()
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD)
                .build();

        given(userRepo.findByEmail(userLogin.getEmail()))
                .willReturn(Optional.of(VALID_USER));

        given(passwordEncoder.matches(userLogin.getPassword(), VALID_PASSWORD))
                .willReturn(true);

        // Act
        final User login = authService.login(userLogin);

        // Assert
        assertThat(login).isEqualTo(VALID_USER);

        verify(userRepo, times(1)).findByEmail(VALID_EMAIL);
        verify(passwordEncoder, times(1)).matches(VALID_PASSWORD, VALID_PASSWORD);
    }
}
