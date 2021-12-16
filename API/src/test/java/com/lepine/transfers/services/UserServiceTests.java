package com.lepine.transfers.services;

import com.lepine.transfers.config.AuthConfig;
import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserMapper;
import com.lepine.transfers.data.user.UserRepo;
import com.lepine.transfers.data.user.UserUUIDLessDTO;
import com.lepine.transfers.exceptions.user.DuplicateEmailException;
import com.lepine.transfers.services.user.UserService;
import com.lepine.transfers.services.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.lepine.transfers.helpers.PageHelpers.createPageFor;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = { MapperConfig.class, ValidationConfig.class, AuthConfig.class, UserServiceImpl.class })
@ActiveProfiles("test")
public class UserServiceTests {

    private final static String VALID_EMAIL = "some@email.com";
    private final static String INVALID_EMAIL = "aaa";
    private final static String VALID_PASSWORD = "S0m3P@ssw0rd";
    private final static String INVALID_PASSWORD = "bad";
    private final static String VALID_HASHED_PASSWORD = "some.hashed.password.or.something";
    private final static String VALID_ROLE_NAME = "SOME_ROLE";

    private final static UserUUIDLessDTO VALID_USER_DTO = UserUUIDLessDTO.builder()
            .email(VALID_EMAIL)
            .password(VALID_PASSWORD)
            .role(VALID_ROLE_NAME)
            .build();

    private final Function<String, String> messageSourceHelper = name ->
            this.messageSource.getMessage(name, null, Locale.getDefault());

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

    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;

    @Autowired
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
        final UserUUIDLessDTO userUUIDLessDTO = VALID_USER_DTO.toBuilder()
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
        final UserUUIDLessDTO userUUIDLessDTO = VALID_USER_DTO.toBuilder()
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

    @Test
    @DisplayName("Given null UserUUIDLessDTO on create, then throw ConstraintViolationException")
    void create_NullUserUUIDLessDTO() {

        // Arrange
        final UserUUIDLessDTO userUUIDLessDTO = null;

        // Act
        final ConstraintViolationException cve = assertThrows(ConstraintViolationException.class, () -> userService.create(userUUIDLessDTO));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = cve.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(1, constraintViolations.size());

        final Set<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        
        assertTrue(collect.contains(
                messageSourceHelper.apply("user.not_null")
        ));

        verify(userRepo, times(0)).save(any());
    }

    @Test
    @DisplayName("Given UserUUIDLessDTO with null email, then throw ConstraintViolationException")
    void create_NullEmail() {

        // Arrange
        final UserUUIDLessDTO userUUIDLessDTO = VALID_USER_DTO.toBuilder()
                .email(null)
                .build();

        // Act
        final ConstraintViolationException cve = assertThrows(ConstraintViolationException.class, () -> userService.create(userUUIDLessDTO));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = cve.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(1, constraintViolations.size());

        final Set<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        
        assertTrue(collect.contains(messageSourceHelper.apply("user.email.not_blank")));

        verify(userRepo, times(0)).save(any());
    }

    @Test
    @DisplayName("Given UserUUIDLessDTO with empty email, then throw ConstraintViolationException")
    void create_EmptyEmail() {

        // Arrange
        final UserUUIDLessDTO userUUIDLessDTO = VALID_USER_DTO.toBuilder()
                .email("")
                .password(VALID_PASSWORD)
                .build();

        // Act
        final ConstraintViolationException cve = assertThrows(ConstraintViolationException.class, () -> userService.create(userUUIDLessDTO));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = cve.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(1, constraintViolations.size());

        final Set<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        
        assertTrue(collect.contains(messageSourceHelper.apply("user.email.not_blank")));

        verify(userRepo, times(0)).save(any());
    }

    @Test
    @DisplayName("Given UserUUIDLessDTO with invalid email, then throw ConstraintViolationException")
    void create_InvalidEmail() {

        // Arrange
        final UserUUIDLessDTO userUUIDLessDTO = VALID_USER_DTO.toBuilder()
                .email(INVALID_EMAIL)
                .password(VALID_PASSWORD)
                .build();

        // Act
        final ConstraintViolationException cve = assertThrows(ConstraintViolationException.class, () -> userService.create(userUUIDLessDTO));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = cve.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(1, constraintViolations.size());

        final Set<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        
        assertTrue(collect.containsAll(List.of("Email must be a valid email address")));

        verify(userRepo, times(0)).save(any());
    }

    @Test
    @DisplayName("Given UserUUIDLessDTO with null password on create, then throw ConstraintViolationException")
    void create_NullPassword() {

        // Arrange
        final UserUUIDLessDTO userUUIDLessDTO = VALID_USER_DTO.toBuilder()
                .email(VALID_EMAIL)
                .password(null)
                .build();

        // Act
        final ConstraintViolationException cve = assertThrows(ConstraintViolationException.class, () -> userService.create(userUUIDLessDTO));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = cve.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(1, constraintViolations.size());

        final Set<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        
        assertTrue(collect.contains(
                messageSourceHelper.apply("user.password.not_blank")
        ));

        verify(userRepo, times(0)).save(any());
    }

    @Test
    @DisplayName("Given UserUUIDLessDTO with empty password on create, then throw ConstraintViolationException")
    void create_EmptyPassword() {

        // Arrange
        final UserUUIDLessDTO userUUIDLessDTO = VALID_USER_DTO.toBuilder()
                .email(VALID_EMAIL)
                .password("")
                .build();

        // Act
        final ConstraintViolationException cve =
                assertThrows(ConstraintViolationException.class, () -> userService.create(userUUIDLessDTO));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = cve.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(2, constraintViolations.size());

        final Set<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());

        assertTrue(collect.containsAll(List.of(
                messageSourceHelper.apply("user.password.not_blank"),
                messageSourceHelper.apply("user.password.not_valid")
        )));

        verify(userRepo, times(0)).save(any());
    }

    @Test
    @DisplayName("Given UserUUIDLessDTO with invalid password on create, then throw ConstraintViolationException")
    void create_InvalidPassword() {

        // Arrange
        final UserUUIDLessDTO userUUIDLessDTO = VALID_USER_DTO.toBuilder()
                .email(VALID_EMAIL)
                .password(INVALID_PASSWORD)
                .build();

        // Act
        final ConstraintViolationException cve = assertThrows(ConstraintViolationException.class, () -> userService.create(userUUIDLessDTO));

        // Assert
        final Set<ConstraintViolation<?>> constraintViolations = cve.getConstraintViolations();
        assertFalse(constraintViolations.isEmpty());
        assertEquals(1, constraintViolations.size());

        final Set<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        
        assertTrue(collect.contains(messageSourceHelper.apply("user.password.not_valid")));

        verify(userRepo, times(0)).save(any());
    }
}
