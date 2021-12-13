package com.lepine.transfers.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.lepine.transfers.controllers.user.UserController;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserMapper;
import com.lepine.transfers.data.user.UserPasswordLessDTO;
import com.lepine.transfers.data.user.UserUUIDLessDTO;
import com.lepine.transfers.services.Config;
import com.lepine.transfers.services.user.UserService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@SpringBootTest(classes = {Config.class})
@ActiveProfiles({"test"})
public class UserControllerTests {

    private static final String VALID_EMAIL = "some@domain.com";
    private static final String INVALID_EMAIL = "oogabooga";
    private static final String VALID_PASSWORD = "S0m3P@ssw0rd";
    private static final String INVALID_PASSWORD = "invalidpassword";

    @Autowired
    private UserController userController;

    @Autowired
    private UserMapper userMapper;

    @MockBean
    private UserService userService;

    @Test
    void contextLoads(){
        reset(userService);
    }

    @Test
    @DisplayName("Given user with valid email and password, then register user")
    void registerUser() {

        // Arrange
        UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD)
                .build();

        User createdUser = userMapper.toEntity(userUUIDLessDTO);

        given(userService.create(userUUIDLessDTO))
                .willReturn(createdUser);

        // Act
        UserPasswordLessDTO got = userController.create(userUUIDLessDTO);

        // Assert
        assertEquals(createdUser.getUuid(), got.getUuid());
        verify(userService, times(1)).create(userUUIDLessDTO);
    }

    @Test
    @DisplayName("Given user with valid email but empty password, then throw ConstrainViolationException")
    void registerUser_emptyPassword() {

        // Arrange
        UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email(VALID_EMAIL)
                .password(INVALID_PASSWORD)
                .build();

        // Act
        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> userController.create(userUUIDLessDTO));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(1, constraintViolations.size());
        assertEquals("Password must be at least 8 characters long, include a number, include a capital letter, include a special character",
                constraintViolations.iterator().next().getMessage());

        verify(userService, times(0)).create(userUUIDLessDTO);
    }

    @Test
    @DisplayName("Given user with valid email but null password, then throw ConstrainViolationException")
    void registerUser_nullPassword() {

        // Arrange
        UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email(VALID_EMAIL)
                .password(null)
                .build();

        // Act
        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> userController.create(userUUIDLessDTO));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(1, constraintViolations.size());
        assertEquals("Password must not be blank",
                constraintViolations.iterator().next().getMessage());

        verify(userService, times(0)).create(userUUIDLessDTO);
    }

    @Test
    @DisplayName("Given user with valid email but invalid password, then throw ConstrainViolationException")
    void registerUser_invalidPassword() {

        // Arrange
        UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email(VALID_EMAIL)
                .password("")
                .build();

        // Act
        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> userController.create(userUUIDLessDTO));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(2, constraintViolations.size());

        final Set<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        assertTrue(
                collect.containsAll(List.of(
                        "Password must be at least 8 characters long, include a number, include a capital letter, include a special character",
                        "Password must not be blank")));

        verify(userService, times(0)).create(userUUIDLessDTO);
    }

    @Test
    @DisplayName("Given user with valid password but empty email, then throw ConstrainViolationException")
    void registerUser_emptyEmail() {

        // Arrange
        UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email("")
                .password(VALID_PASSWORD)
                .build();

        // Act
        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> userController.create(userUUIDLessDTO));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(1, constraintViolations.size());
        assertEquals("Email must not be blank",
                constraintViolations.iterator().next().getMessage());

        verify(userService, times(0)).create(userUUIDLessDTO);
    }

    @Test
    @DisplayName("Given user with valid password but null email, then throw ConstrainViolationException")
    void registerUser_nullEmail() {

        // Arrange
        UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email(null)
                .password(VALID_PASSWORD)
                .build();

        // Act
        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> userController.create(userUUIDLessDTO));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(1, constraintViolations.size());
        assertEquals("Email must not be blank",
                constraintViolations.iterator().next().getMessage());

        verify(userService, times(0)).create(userUUIDLessDTO);
    }

    @Test
    @DisplayName("Given user with valid password but invalid email, then throw ConstrainViolationException")
    void registerUser_invalidEmail() {

        // Arrange
        UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email(INVALID_EMAIL)
                .password(VALID_PASSWORD)
                .build();

        // Act
        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> userController.create(userUUIDLessDTO));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(1, constraintViolations.size());
        assertEquals("Email must be a valid email address",
                constraintViolations.iterator().next().getMessage());

        verify(userService, times(0)).create(userUUIDLessDTO);
    }

    @Test
    @DisplayName("Given invalid email and invalid password, then throw ConstrainViolationException")
    void registerUser_invalidEmailAndInvalidPassword() {

        // Arrange
        UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email(INVALID_EMAIL)
                .password(INVALID_PASSWORD)
                .build();

        // Act
        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> userController.create(userUUIDLessDTO));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(2, constraintViolations.size());

        final Set<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        assertTrue(
                collect.containsAll(List.of(
                        "Email must be a valid email address",
                        "Password must be at least 8 characters long, include a number, include a capital letter, include a special character")));

        verify(userService, times(0)).create(userUUIDLessDTO);
    }

    @Test
    @DisplayName("Given empty email and empty password, then throw ConstrainViolationException")
    void registerUser_emptyEmailAndEmptyPassword() {

        // Arrange
        UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email("")
                .password("")
                .build();

        // Act
        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> userController.create(userUUIDLessDTO));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(3, constraintViolations.size());

        final Set<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        assertTrue(
                collect.containsAll(List.of(
                        "Email must not be blank",
                        "Password must not be blank",
                        "Password must be at least 8 characters long, include a number, include a capital letter, include a special character")));

        verify(userService, times(0)).create(userUUIDLessDTO);
    }

    @Test
    @DisplayName("Given null email and null password, then throw ConstrainViolationException")
    void registerUser_nullEmailAndNullPassword() {

        // Arrange
        UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email(null)
                .password(null)
                .build();

        // Act
        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> userController.create(userUUIDLessDTO));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(2, constraintViolations.size());

        final Set<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        assertTrue(
                collect.containsAll(List.of(
                        "Email must not be blank",
                        "Password must not be blank")));

        verify(userService, times(0)).create(userUUIDLessDTO);
    }
}
