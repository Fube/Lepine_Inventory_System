package com.lepine.users.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.lepine.transfers.controllers.user.UserController;
import com.lepine.transfers.data.user.UserMapper;
import com.lepine.transfers.data.user.UserPasswordLessDTO;
import com.lepine.transfers.data.user.UserUUIDLessDTO;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {Config.class})
@ActiveProfiles({"test"})
public class UserControllerTests {

    private static final String VALID_EMAIL = "some@domain.com";
    private static final String VALID_PASSWORD = "S0m3P@ssw0rd";

    @Autowired
    private UserController userController;

    @Autowired
    private UserMapper userMapper;

    @MockBean
    private UserService userService;

    @Test
    void contextLoads(){}

    @Test
    @DisplayName("Given user with valid email and password, then register user")
    void registerUser() {

        // Arrange
        UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD)
                .build();
        
        UserPasswordLessDTO userPasswordLessDTO = userMapper.toPasswordLessDTO(userMapper.toEntity(userUUIDLessDTO));

        given(userService.create(userUUIDLessDTO))
            .willReturn(userPasswordLessDTO);

        // Act
        UserPasswordLessDTO got = userController.create(userPasswordLessDTO);

        // Assert
        assertThat(got).isEqualTo(userPasswordLessDTO);
        verify(userService, times(1)).create(userUUIDLessDTO);
    }

    @Test
    @DisplayName("Given user with valid email but invalid password, then throw ConstrainViolationException")
    void registerUser_invalidPassword() {

        // Arrange
        UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email(VALID_EMAIL)
                .password("")
                .build();
        
        UserPasswordLessDTO userPasswordLessDTO = userMapper.toPasswordLessDTO(userMapper.toEntity(userUUIDLessDTO));

        // Act
        ConstraintViolationException exception = 
            assertThrows(ConstraintViolationException.class, () -> userController.create(userPasswordLessDTO));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(1, constraintViolations.size());
        assertEquals("Password must be at least 8 characters long, include a number, inclue a capital letter, inclue a special character",
                constraintViolations.iterator().next().getMessage());

        verify(userService, times(0)).create(userUUIDLessDTO);
    }

    @Test
    @DisplayName("Given user with valid password but invalid email, then throw ConstrainViolationException")
    void registerUser_invalidEmail() {

        // Arrange
        UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email("")
                .password(VALID_PASSWORD)
                .build();
        
        UserPasswordLessDTO userPasswordLessDTO = userMapper.toPasswordLessDTO(userMapper.toEntity(userUUIDLessDTO));

        // Act
        ConstraintViolationException exception = 
            assertThrows(ConstraintViolationException.class, () -> userController.create(userPasswordLessDTO));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(1, constraintViolations.size());
        assertEquals("Email must be a valid email address",
                constraintViolations.iterator().next().getMessage());

        verify(userService, times(0)).create(userUUIDLessDTO);
    }
}
