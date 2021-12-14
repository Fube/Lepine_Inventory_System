package com.lepine.transfers.controllers;

import com.lepine.transfers.controllers.user.UserController;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserMapper;
import com.lepine.transfers.data.user.UserPasswordLessDTO;
import com.lepine.transfers.data.user.UserUUIDLessDTO;
import com.lepine.transfers.services.user.UserService;
import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.lepine.transfers.helpers.PageHelpers.createPageFor;
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

    private static List<User> seedUsers(int num) {
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

    @Test
    @DisplayName("Given no page number or size, then retrieve first page of Users")
    void retrieveAllUsers_noPageRequest() {

        // Arrange
        final int
                total = 100,
                page = 0,
                size = 10;
        final Page<User> pageFor = createPageFor(seedUsers(total));
        given(userService.getAll(page, size)).willReturn(pageFor);

        // Act
        Page<UserUUIDLessDTO> got = userController.getAll(pageable);

        // Assert
        assertEquals(0, got.getNumber());
        assertEquals(10, got.getSize());
        assertEquals(1, got.getTotalPages());
        assertEquals(1, got.getTotalElements());
        assertEquals(0, got.getNumberOfElements());
        assertEquals(0, got.getContent().size());
    }

    @Test
    @DisplayName("Given negative page number and valid size, then throw ConstraintViolationException")
    void retrieveAllUsers_negativePageNumber() {

        // Arrange
        final int
                total = 100,
                page = -1,
                size = 10;
        final Page<User> pageFor = createPageFor(seedUsers(total));
        given(userService.getAll(page, size)).willReturn(pageFor);

        // Act
        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> userController.getAll(pageable));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(1, constraintViolations.size());

        final Set<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        assertTrue(
                collect.contains("Page number must be greater than or equal to 0"));

        verify(userService, times(0)).getAll(page, size);
    }

    @Test
    @DisplayName("Given valid page but negative size, then throw ConstraintViolationException")
    void retrieveAllUsers_negativeLimit() {

        // Arrange
        final int
                total = 100,
                page = 0,
                size = -1;
        final Page<User> pageFor = createPageFor(seedUsers(total));
        given(userService.getAll(page, size)).willReturn(pageFor);

        // Act
        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> userController.getAll(pageable));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(1, constraintViolations.size());

        final Set<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        assertTrue(
                collect.contains("Page size must be greater than or equal to 1"));

        verify(userService, times(0)).getAll(page, size);
    }

    @Test
    @DisplayName("Given negative page number and negative size, then throw ConstraintViolationException")
    void retrieveAllUsers_negativePageNumberAndNegativeSize() {

        // Arrange
        final int
                total = 100,
                page = -1,
                size = -1;
        final Page<User> pageFor = createPageFor(seedUsers(total));
        given(userService.getAll(page, size)).willReturn(pageFor);

        // Act
        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> userController.getAll(pageable));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(2, constraintViolations.size());

        final Set<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        assertTrue(
                collect.containsAll(List.of(
                        "Page number must be greater than or equal to 0",
                        "Page size must be greater than or equal to 1")));

        verify(userService, times(0)).getAll(page, size);
    }
}
