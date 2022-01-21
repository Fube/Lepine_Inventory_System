package com.lepine.transfers.unit.services;

import com.lepine.transfers.config.AuthConfig;
import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.data.auth.Role;
import com.lepine.transfers.data.auth.UserLogin;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserRepo;
import com.lepine.transfers.exceptions.auth.InvalidLoginException;
import com.lepine.transfers.services.auth.AuthService;
import com.lepine.transfers.services.user.UserServiceImpl;
import com.lepine.transfers.utils.auth.JWTUtil;
import org.javatuples.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = { MapperConfig.class, ValidationConfig.class, AuthConfig.class, UserServiceImpl.class })
@ActiveProfiles({ "test" })
public class AuthServiceTests {

    private static final String VALID_JWT = "some.valid.jwt";
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
    private static final UserLogin VALID_LOGIN = UserLogin.builder()
            .email(VALID_EMAIL)
            .password(VALID_PASSWORD)
            .build();

    @Autowired
    private AuthService authService;

    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtil<User> jwtUtil;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Test
    public void contextLoads() {
    }

    @AfterEach
    public void tearDown() {
        reset(userRepo);
        reset(passwordEncoder);
    }

    @Test
    @DisplayName("gRWyKHiZyu: Given login with valid user data, then return Pair<User, String> representing user and JWT")
    public void login_Valid() {

        // Arrange
        final UserLogin userLogin = VALID_LOGIN;

        given(jwtUtil.encode(VALID_USER))
                .willReturn(VALID_JWT);

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(new UsernamePasswordAuthenticationToken(VALID_USER, VALID_PASSWORD));

        // Act
        final Pair<User, String> login = authService.login(userLogin);

        // Assert
        assertThat(login.getValue0()).isEqualTo(VALID_USER);
        assertThat(login.getValue1()).isEqualTo(VALID_JWT);

        verify(authenticationManager, atLeastOnce())
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("QLVLqNyvpn: Given login with invalid user data, then throw ConstrainViolationException")
    public void login_Invalid() {

        // Arrange
        final UserLogin userLogin = VALID_LOGIN.toBuilder()
                .email("")
                .password("")
                .build();

        // Act
        final ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> authService.login(userLogin));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        final Set<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());

        assertThat(collect)
                .containsExactlyInAnyOrder(
                messageSource.getMessage("user.email.not_blank", null, Locale.getDefault()),
                messageSource.getMessage("user.password.not_blank", null, Locale.getDefault()));

        verify(userRepo, times(0)).findByEmail(VALID_EMAIL);
        verify(passwordEncoder, times(0)).matches(VALID_PASSWORD, VALID_PASSWORD);
    }

    @Test
    @DisplayName("QljRaiQcqU: Given login with invalid match, then throw InvalidLoginException")
    public void login_InvalidMatch() {

        // Arrange
        final String wrongPassword = "somewrongpassword";
        final UserLogin userLogin = VALID_LOGIN.toBuilder()
                .password(wrongPassword)
                .build();

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(new BadCredentialsException(""));

        // Act
        final InvalidLoginException exception =
                assertThrows(InvalidLoginException.class, () -> authService.login(userLogin));

        // Assert
        assertThat(exception.getMessage()).isEqualTo("Invalid login");

        verify(authenticationManager, atLeastOnce())
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
