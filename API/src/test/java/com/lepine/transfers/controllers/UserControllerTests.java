package com.lepine.transfers.controllers;

import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.controllers.user.UserController;
import com.lepine.transfers.data.auth.Role;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserMapper;
import com.lepine.transfers.data.user.UserPasswordLessDTO;
import com.lepine.transfers.data.user.UserUUIDLessDTO;
import com.lepine.transfers.data.warehouse.Warehouse;
import com.lepine.transfers.data.warehouse.WarehouseUUIDLessDTO;
import com.lepine.transfers.services.user.UserService;
import com.lepine.transfers.utils.ConstraintViolationExceptionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
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
    private static final String VALID_ROLE_NAME = "SOME_ROLE";
    private final static UUID
            VALID_UUID = UUID.randomUUID();
    private static final UserUUIDLessDTO VALID_USER_DTO = UserUUIDLessDTO.builder()
            .email(VALID_EMAIL)
            .password(VALID_PASSWORD)
            .role(VALID_ROLE_NAME)
            .build();
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
    @DisplayName("zEnzmoaiCu: Given user with valid data, then register user")
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
    @DisplayName("UFXmqrgZTo: Given user with empty password, then throw ConstrainViolationException")
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
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(exception);
        assertThat(collect).containsExactly(
                "Password must be at least 8 characters long, include a number, include a capital letter, include a special character");

        verify(userService, times(0)).create(userUUIDLessDTO);
    }

    @Test
    @DisplayName("YDHAuZwJac: Given user with null password, then throw ConstrainViolationException")
    void registerUser_nullPassword() {

        // Arrange
        UserUUIDLessDTO userUUIDLessDTO = VALID_USER_DTO.toBuilder()
                .email(VALID_EMAIL)
                .password(null)
                .role(VALID_ROLE_NAME)
                .build();

        // Act
        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> userController.create(userUUIDLessDTO));

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(exception);
        assertThat(collect).containsExactly("Password must not be blank");

        verify(userService, times(0)).create(userUUIDLessDTO);
    }

    @Test
    @DisplayName("SuOhbzazUd: Given user with invalid password, then throw ConstrainViolationException")
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
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(exception);
        assertThat(collect).containsExactlyInAnyOrder(
                "Password must be at least 8 characters long, include a number, include a capital letter, include a special character",
                "Password must not be blank");

        verify(userService, times(0)).create(userUUIDLessDTO);
    }

    @Test
    @DisplayName("MHNCOzncRO: Given user with empty email, then throw ConstrainViolationException")
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
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(exception);
        assertThat(collect).containsExactly("Email must not be blank");

        verify(userService, times(0)).create(userUUIDLessDTO);
    }

    @Test
    @DisplayName("CNKLYFCeTg: Given user with null email, then throw ConstrainViolationException")
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
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(exception);
        assertThat(collect).containsExactly("Email must not be blank");

        verify(userService, times(0)).create(userUUIDLessDTO);
    }

    @Test
    @DisplayName("wMhwytjmfd: Given user with invalid email, then throw ConstrainViolationException")
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
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(exception);
        assertThat(collect).containsExactly("Email must be a valid email address");

        verify(userService, times(0)).create(userUUIDLessDTO);
    }

    @Test
    @DisplayName("daHxsQgYKd: Given invalid email and invalid password, then throw ConstrainViolationException")
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
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(exception);
        assertThat(collect).containsExactlyInAnyOrder(
                        "Email must be a valid email address",
                        "Password must be at least 8 characters long, include a number, include a capital letter, include a special character");

        verify(userService, times(0)).create(userUUIDLessDTO);
    }

    @Test
    @DisplayName("ORbiVwNHYb: Given empty email and empty password, then throw ConstrainViolationException")
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
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(exception);
        assertThat(collect).containsExactlyInAnyOrder(
                        "Email must not be blank",
                        "Password must not be blank",
                        "Password must be at least 8 characters long, include a number, include a capital letter, include a special character");

        verify(userService, times(0)).create(userUUIDLessDTO);
    }

    @Test
    @DisplayName("ZMvzBKGuQY: Given null email and null password, then throw ConstrainViolationException")
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
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(exception);
        assertThat(collect).containsExactlyInAnyOrder("Email must not be blank", "Password must not be blank");

        verify(userService, times(0)).create(userUUIDLessDTO);
    }

    @Test
    @DisplayName("sRzGktiLsH: Given negative page number, then throw ConstraintViolationException")
    void retrieveAllUsers_negativePageNumber() {

        // Arrange
        final int
                page = -1,
                size = 10;

        // Act
        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> userController.getAll(page, size));

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(exception);
        assertThat(collect).containsExactly("Page number cannot be less than 1");

        verify(userService, times(0)).findAll(any());
    }

    @Test
    @DisplayName("sKmzcIaBxP: Given negative size, then throw ConstraintViolationException")
    void retrieveAllUsers_negativeLimit() {

        // Arrange
        final int
                page = 1,
                size = -1;

        // Act
        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> userController.getAll(page, size));

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(exception);
        assertThat(collect).containsExactly("Page size cannot be less than 1");

        verify(userService, times(0)).findAll(any());
    }

    @Test
    @DisplayName("fkVKnZglMi: Given negative page number and negative size, then throw ConstraintViolationException")
    void retrieveAllUsers_negativePageNumberAndNegativeSize() {

        // Arrange
        final int
                page = -1,
                size = -1;

        // Act
        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> userController.getAll(page, size));

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(exception);
        assertThat(collect).containsExactlyInAnyOrder(
                "Page number cannot be less than 1", "Page size cannot be less than 1");

        verify(userService, times(0)).findAll(any());
    }
    @Test
    @DisplayName("pEEIjxtJre: Given user with valid password, then update user")
    void updateUser_validPassword() {

        // Arrange
        UserUUIDLessDTO userUUIDLessDTO = VALID_USER_DTO.toBuilder()
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD)
                .role(VALID_ROLE_NAME)
                .build();
        given(userService.update(VALID_UUID, userUUIDLessDTO))
                .willReturn(User.builder()
                        .uuid(VALID_UUID)
                        .email(VALID_EMAIL)
                        .password(VALID_PASSWORD)
                        .role(VALID_ROLE)
                        .build());
        // Act
        final User updated= userController.update(VALID_UUID,userUUIDLessDTO);

        // Assert
        assertThat(updated.getUuid()).isEqualTo(VALID_UUID);
        assertThat(updated.getEmail()).isEqualTo(VALID_EMAIL);
        assertThat(updated.getPassword()).isEqualTo(VALID_PASSWORD);
        assertThat(updated.getRole()).isEqualTo(VALID_ROLE);

        verify(userService, atMostOnce()).update(VALID_UUID, userUUIDLessDTO);
    }
    @Test
    @DisplayName("OLKtEHHQMS: Given user with invalid password, then throw ConstrainViolationException")
    void updateUser_invalidPassword() {

        // Arrange
        final UserUUIDLessDTO user = UserUUIDLessDTO.builder()
                .email(VALID_EMAIL)
                .password(INVALID_PASSWORD)
                .role(VALID_ROLE_NAME)
                .build();

        // Act
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> userController.update(VALID_UUID, user));

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactly("Password must be at least 8 characters long, include a number, include a capital letter, include a special character");

        verify(userService, never()).update(any(), any());
    }
    @Test
    @DisplayName("fKzJIgEwqI: Given user with null password, then throw ConstrainViolationException")
    void updateUser_nullPassword() {

        // Arrange
        final UserUUIDLessDTO user = UserUUIDLessDTO.builder()
                .email(VALID_EMAIL)
                .password(null)
                .role(VALID_ROLE_NAME)
                .build();

        // Act
        final ConstraintViolationException constraintViolationException =
                assertThrows(ConstraintViolationException.class, () -> userController.update(VALID_UUID, user));

        // Assert
        final Set<String> collect = ConstraintViolationExceptionUtils.extractMessages(constraintViolationException);
        assertThat(collect).containsExactly("Password must not be blank");

        verify(userService, never()).update(any(), any());
    }
}
