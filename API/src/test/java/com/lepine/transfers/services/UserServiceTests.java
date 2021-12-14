package com.lepine.transfers.services;

import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserMapper;
import com.lepine.transfers.data.user.UserUUIDLessDTO;
import com.lepine.transfers.services.user.UserService;
import com.lepine.transfers.services.user.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {MapperConfig.class, UserServiceImpl.class })
@ActiveProfiles("test")
public class UserServiceTests {

    private final static String VALID_EMAIL = "some@email.com";
    private final static String VALID_PASSWORD = "S0m3P@ssw0rd";

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @MockBean
    private UserRepo userRepo;

    @Test
    void contextLoads() {}

    @Test
    @DisplayName("Given a UserUUIDLessDTO, then create a User with a hashed password")
    void createUser() {

        // Arrange
        final UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD)
                .build();

        // Act
        final User user = userService.create(userUUIDLessDTO);

        // Assert
        assertNotNull(user);
        assertEquals(VALID_EMAIL, user.getEmail());
        assertNotNull(user.getPassword());
        assertNotEquals(VALID_PASSWORD, user.getPassword());

        verify(userRepo, times(1)).save(user);
    }

    @Test
    @DisplayName("Given existing email on create, then throw DuplicateEmailException")
    void createUser_DuplicateEmail() {

        // Arrange
        final UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD)
                .build();
        final User user = userMapper.toEntity(userUUIDLessDTO);
        given(userRepo.save(user))
                .willThrow(new DataIntegrityViolationException("Duplicate email or something"));

        // Act
        final DuplicateEmailException dex = assertThrows(DuplicateEmailException.class, () -> userService.create(userUUIDLessDTO));

        // Assert
        assertEquals(format("Email %s already in use", VALID_EMAIL), dex.getMessage());
        verify(userRepo, times(1)).save(user);
    }
}
