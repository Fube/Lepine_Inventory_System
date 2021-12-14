package com.lepine.transfers.services;

import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.SecurityConfig;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserMapper;
import com.lepine.transfers.data.user.UserRepo;
import com.lepine.transfers.data.user.UserUUIDLessDTO;
import com.lepine.transfers.exceptions.user.DuplicateEmailException;
import com.lepine.transfers.services.user.UserService;
import com.lepine.transfers.services.user.UserServiceImpl;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.lepine.transfers.helpers.PageHelpers.createPageFor;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = { MapperConfig.class, UserServiceImpl.class })
@ActiveProfiles("test")
public class UserServiceTests {

    private final static String VALID_EMAIL = "some@email.com";
    private final static String VALID_PASSWORD = "S0m3P@ssw0rd";
    private final static String VALID_HASHED_PASSWORD = "some.hashed.password.or.something";

    private static List<User> generateUsers(int num) {
        List<User> items = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            items.add(User.builder()
                    .email(i+VALID_EMAIL)
                    .password(VALID_PASSWORD)
                    .build());
        }
        return items;
    }

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepo userRepo;

    @BeforeEach
    void setup() {
        reset(userRepo);
        reset(passwordEncoder);
    }

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
        final User asEntity = userMapper.toEntity(userUUIDLessDTO);
        given(userRepo.save(any())).willReturn(asEntity);
        given(passwordEncoder.encode(VALID_PASSWORD)).willReturn(VALID_HASHED_PASSWORD);

        // Act
        final User user = userService.create(userUUIDLessDTO);

        // Assert
        assertNotNull(user);
        assertEquals(VALID_EMAIL, user.getEmail());
        assertNotNull(user.getPassword());
        assertEquals(VALID_HASHED_PASSWORD, user.getPassword());

        verify(userRepo, times(1)).findByEmail(VALID_EMAIL);
        verify(userRepo, times(0)).save(argThat(u -> u.getPassword().equals(VALID_PASSWORD)));
        verify(userRepo, times(1)).save(user);
        verify(passwordEncoder, times(1)).encode(VALID_PASSWORD);
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

        given(userRepo.findByEmail(VALID_EMAIL))
                .willReturn(Optional.of(user));

        // Act
        final DuplicateEmailException dex = assertThrows(DuplicateEmailException.class, () -> userService.create(userUUIDLessDTO));

        // Assert
        assertEquals(format("Email %s already in use", VALID_EMAIL), dex.getMessage());

        verify(userRepo, times(1)).findByEmail(VALID_EMAIL);
        verify(userRepo, times(0)).save(any());
    }

    @Test
    @DisplayName("Given no PageRequest, then return first page of Users")
    void findAll() {

        // Arrange
        final int num = 100;
        final Page<User> pageFor = createPageFor(generateUsers(num), PageRequest.of(0, 10));
        given(userRepo.findAll(any(PageRequest.class))).willReturn(pageFor);

        // Act
        final Page<User> all = userService.findAll();

        // Assert
        assertEquals(pageFor, all);
        verify(userRepo, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    @DisplayName("Given findAll with a PageRequest, then return the appropriate page")
    void findAll_WithPageRequest() {


        // Arrange
        final int
                num = 100,
                size = 15,
                page = 2;
        PageRequest pageRequest = PageRequest.of(page, size);

        final Page<User> pageFor = createPageFor(generateUsers(num), pageRequest);
        given(userRepo.findAll(pageRequest)).willReturn(pageFor);

        // Act
        Page<User> all = userService.findAll(pageRequest);

        // Assert
        assertEquals(pageFor, all);

        verify(userRepo, times(1)).findAll(pageRequest);
    }
}
