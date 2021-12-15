package com.lepine.transfers.controllers;

import com.lepine.transfers.controllers.auth.AuthController;
import com.lepine.transfers.data.auth.UserLogin;
import com.lepine.transfers.data.user.UserPasswordLessDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolationException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = { AuthController.class })
@ActiveProfiles({"test"})
public class AuthControllerTests {

    private static final String VALID_EMAIL = "foo@bar.com";
    private static final String VALID_PASSWORD = "S0meP@ssw0rd";

    private static final UserPasswordLessDTO VALID_USER = UserPasswordLessDTO.builder()
            .uuid(UUID.randomUUID())
            .email(VALID_EMAIL)
            .build();

    @Autowired
    private AuthController authController;

    @Test
    void contextLoads() {
    }

    @Test
    @DisplayName("Given fully valid UserLogin, then return UserPasswordLessDTO")
    void login_Valid() {

        // Arrange
        final UserLogin userLogin = UserLogin.builder()
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD)
                .build();

        given(authService.login(userLogin))
                .willReturn(VALID_USER);

        // Act
        final UserPasswordLessDTO user = authController.login(userLogin);

        // Assert
        assertEquals(VALID_USER, user);

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
