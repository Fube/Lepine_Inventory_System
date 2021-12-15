package com.lepine.transfers.controllers;

import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.controllers.user.UserController;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserMapper;
import com.lepine.transfers.data.user.UserPasswordLessDTO;
import com.lepine.transfers.data.user.UserUUIDLessDTO;
import com.lepine.transfers.services.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {ValidationConfig.class, MapperConfig.class, UserController.class})
@ActiveProfiles({"test"})
public class UserControllerTests {

    private static final String VALID_EMAIL = "some@domain.com";
    private static final String INVALID_EMAIL = "oogabooga";
    private static final String VALID_PASSWORD = "S0m3P@ssw0rd";
    private static final String INVALID_PASSWORD = "invalidpassword";
    private static final String VALID_ROLE = "SOME_ROLE";

    private static final UserUUIDLessDTO VALID_USER_DTO = UserUUIDLessDTO.builder()
            .email(VALID_EMAIL)
            .password(VALID_PASSWORD)
            .role(VALID_ROLE)
            .build();

    private static List<User> generateUsers(int num) {
        return IntStream.range(0, num)
                .mapToObj(i -> User.builder()
                        .uuid(UUID.randomUUID())
                        .email(i+VALID_EMAIL)
                        .password(VALID_PASSWORD)
                        .build()
                )
                .collect(Collectors.toList());
    }

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
    @DisplayName("Given user with valid data, then register user")
    void registerUser() {

        // Arrange
        User createdUser = userMapper.toEntity(VALID_USER_DTO);

        given(userService.create(VALID_USER_DTO))
                .willReturn(createdUser);

        // Act
        UserPasswordLessDTO got = userController.create(VALID_USER_DTO);

        // Assert
        assertEquals(createdUser.getUuid(), got.getUuid());
        verify(userService, times(1)).create(VALID_USER_DTO);
    }

    @Test
    @DisplayName("Given user with empty password, then throw ConstrainViolationException")
    void registerUser_emptyPassword() {

        // Arrange
        UserUUIDLessDTO userUUIDLessDTO = VALID_USER_DTO.toBuilder()
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
    @DisplayName("Given user with null password, then throw ConstrainViolationException")
    void registerUser_nullPassword() {

        // Arrange
        UserUUIDLessDTO userUUIDLessDTO = VALID_USER_DTO.toBuilder()
                .email(VALID_EMAIL)
                .password(null)
                .role(VALID_ROLE)
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
    @DisplayName("Given user with invalid password, then throw ConstrainViolationException")
    void registerUser_invalidPassword() {

        // Arrange
        UserUUIDLessDTO userUUIDLessDTO = VALID_USER_DTO.toBuilder()
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
    @DisplayName("Given user with empty email, then throw ConstrainViolationException")
    void registerUser_emptyEmail() {

        // Arrange
        UserUUIDLessDTO userUUIDLessDTO = VALID_USER_DTO.toBuilder()
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
    @DisplayName("Given user with null email, then throw ConstrainViolationException")
    void registerUser_nullEmail() {

        // Arrange
        UserUUIDLessDTO userUUIDLessDTO = VALID_USER_DTO.toBuilder()
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
    @DisplayName("Given user with invalid email, then throw ConstrainViolationException")
    void registerUser_invalidEmail() {

        // Arrange
        UserUUIDLessDTO userUUIDLessDTO = VALID_USER_DTO.toBuilder()
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
        UserUUIDLessDTO userUUIDLessDTO = VALID_USER_DTO.toBuilder()
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
        UserUUIDLessDTO userUUIDLessDTO = VALID_USER_DTO.toBuilder()
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
        UserUUIDLessDTO userUUIDLessDTO = VALID_USER_DTO.toBuilder()
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

    @Test
    @DisplayName("Given negative page number, then throw ConstraintViolationException")
    void retrieveAllUsers_negativePageNumber() {

        // Arrange
        final int
                page = -1,
                size = 10;

        // Act
        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> userController.getAll(page, size));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(1, constraintViolations.size());

        final Set<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        assertTrue(
                collect.contains("Page number cannot be less than 1"));

        verify(userService, times(0)).findAll(any());
    }

    @Test
    @DisplayName("Given negative size, then throw ConstraintViolationException")
    void retrieveAllUsers_negativeLimit() {

        // Arrange
        final int
                page = 1,
                size = -1;

        // Act
        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> userController.getAll(page, size));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(1, constraintViolations.size());

        final Set<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        assertTrue(
                collect.contains("Page size cannot be less than 1"));

        verify(userService, times(0)).findAll(any());
    }

    @Test
    @DisplayName("Given negative page number and negative size, then throw ConstraintViolationException")
    void retrieveAllUsers_negativePageNumberAndNegativeSize() {

        // Arrange
        final int
                page = -1,
                size = -1;

        // Act
        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> userController.getAll(page, size));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(2, constraintViolations.size());

        final Set<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        assertTrue(
                collect.containsAll(List.of(
                        "Page number cannot be less than 1",
                        "Page size cannot be less than 1")));

        verify(userService, times(0)).findAll(any());
    }
}
