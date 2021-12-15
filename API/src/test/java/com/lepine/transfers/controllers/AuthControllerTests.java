package com.lepine.transfers.controllers;

import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.controllers.auth.AuthController;
import com.lepine.transfers.data.auth.UserLogin;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserPasswordLessDTO;
import com.lepine.transfers.exceptions.auth.InvalidLoginException;
import com.lepine.transfers.services.auth.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolationException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {MapperConfig.class, AuthController.class })
@ActiveProfiles({"test"})
public class AuthControllerTests {

    private static final String VALID_EMAIL = "foo@bar.com";
    private static final String VALID_PASSWORD = "S0meP@ssw0rd";

    @Autowired
    private AuthController authController;

    @MockBean
    private AuthService authService;

    @Test
    void contextLoads() {
    }

    @Test
    @DisplayName("Given fully valid manager UserLogin, then return UserPasswordLessDTO with manager role")
    void login_ValidManager() {

        // Arrange
        final UserLogin userLogin = UserLogin.builder()
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD)
                .build();

        final User userDetails = User.builder()
                .uuid(UUID.randomUUID())
                .email(VALID_EMAIL)
                .role("MANAGER")
                .build();

        given(authService.login(userLogin))
                .willReturn(userDetails);

        // Act
        final UserPasswordLessDTO user = authController.login(userLogin);

        // Assert
        assertEquals(userDetails.getUuid(), user.getUuid());
        assertEquals(userDetails.getEmail(), user.getEmail());
        assertEquals(userDetails.getRole(), user.getRole());

        verify(authService, times(1)).login(userLogin);
    }

    @Test
    @DisplayName("Given invalid match with valid data UserLogin, then throw InvalidLoginException")
    void login_Invalid() {

        // Arrange
        final UserLogin userLogin = UserLogin.builder()
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD)
                .build();

        given(authService.login(userLogin))
                .willThrow(InvalidLoginException.class);

        // Act
        final InvalidLoginException invalidLoginException =
                assertThrows(InvalidLoginException.class, () -> authController.login(userLogin));

        // Assert
        assertEquals("Invalid login", invalidLoginException.getMessage());

        verify(authService, times(1)).login(userLogin);
    }

    @Test
    @DisplayName("Given fully invalid UserLogin, then throw ConstraintViolationException")
    void login_Invalid_ConstraintViolation() {

        // Arrange
        final UserLogin userLogin = UserLogin.builder()
                .email(null)
                .password(null)
                .build();

        given(authService.login(userLogin))
                .willThrow(ConstraintViolationException.class);

        // Act
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> authController.login(userLogin));

        // Assert
        assertEquals("Invalid login", constraintViolationException.getMessage());

        verify(authService, times(0)).login(userLogin);
    }
}
