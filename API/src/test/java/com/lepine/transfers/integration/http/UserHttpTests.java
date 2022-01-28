package com.lepine.transfers.integration.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lepine.transfers.config.AuthConfig;
import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.controllers.user.UserController;
import com.lepine.transfers.data.auth.Role;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserMapper;
import com.lepine.transfers.data.user.UserUUIDLessDTO;
import com.lepine.transfers.exceptions.user.DuplicateEmailException;
import com.lepine.transfers.utils.matchers.UserUUIDLessDTOMatcher;
import com.lepine.transfers.services.user.UserService;
import com.lepine.transfers.utils.MessageSourceUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import java.util.function.Consumer;
import static com.lepine.transfers.utils.PageUtils.createPageFor;
import static com.lepine.transfers.utils.MessageSourceUtils.wrapperFor;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = { UserController.class })
@ContextConfiguration(classes = { MapperConfig.class, ValidationConfig.class, AuthConfig.class })
@ActiveProfiles("test")

@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class UserHttpTests {

    private static final String VALID_EMAIL = "valid@gmail.com";
    private static final String INVALID_EMAIL = "invalid";
    private static final String VALID_PASSWORD = "S0m3P@ssword";
    private static final String INVALID_PASSWORD = "a";
    private static final String VALID_ROLE_NAME = "SOME_ROLE";

    private static final Role VALID_ROLE = Role.builder()
            .uuid(UUID.randomUUID())
            .name(VALID_ROLE_NAME)
            .build();

    private final static UUID
            VALID_UUID = UUID.randomUUID();


    private static final User VALID_USER = User.builder()
            .uuid(UUID.randomUUID())
            .email(VALID_EMAIL)
            .password(VALID_PASSWORD)
            .role(VALID_ROLE)
            .build();

    private static final UserUUIDLessDTO VALID_USER_DTO = UserUUIDLessDTO.builder()
            .email(VALID_EMAIL)
            .password(VALID_PASSWORD)
            .role(VALID_ROLE_NAME)
            .build();

    private static List<User> generateUsers(int num) {
        List<User> users = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            users.add(User.builder()
                    .uuid(UUID.randomUUID())
                    .email(i + VALID_EMAIL)
                    .password(VALID_PASSWORD)
                    .role(VALID_ROLE)
                    .build());
        }
        return users;
    }

    private String
            ERROR_MESSAGE_PASSWORD_NOT_NULL,
            ERROR_MESSAGE_PASSWORD_NOT_BLANK,
            ERROR_MESSAGE_PASSWORD_NOT_VALID;

    @BeforeAll
    void bSetup(){
        final MessageSourceUtils.ForLocaleWrapper w = wrapperFor(messageSource);
        ERROR_MESSAGE_PASSWORD_NOT_NULL = w.getMessage("user.password.not_null");
        ERROR_MESSAGE_PASSWORD_NOT_BLANK = w.getMessage("user.password.not_blank");
        ERROR_MESSAGE_PASSWORD_NOT_VALID = w.getMessage("user.password.not_valid");
    }

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;

    @SpyBean
    private UserController userController;

    @MockBean
    private UserService userService;

    @BeforeEach
    public void setUp() {
        reset(userService);
    }


    private ResultActions updateWith(final UUID uuid, final UserUUIDLessDTO given, final User expected) throws Exception {
        return updateWith(uuid, given, stubbing -> stubbing.willReturn(expected));
    }
    private ResultActions updateWith(final UUID uuid, final UserUUIDLessDTO given, Consumer<BDDMockito.BDDMyOngoingStubbing<User>> arrangement) throws Exception {

        // Arrange
        final String asString = objectMapper.writeValueAsString(given);

        arrangement.accept(given(userService.update(uuid, given)));

        // Act
        return mvc.perform(put("/users/" + uuid)
                .contentType("application/json")
                .content(asString));
    }

    @Test
    void contextLoads(){}

    @Test
    @DisplayName("qbGyHUhESg: Given POST on /users with valid data as manager, then return 201")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void create_AsManager() throws Exception {

        // Arrange
        final UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD)
                .role(VALID_ROLE_NAME)
                .build();

        final UUID uuid = UUID.randomUUID();
        final User asEntity = userMapper.toEntity(userUUIDLessDTO)
                .toBuilder()
                .uuid(uuid)
                .build();

        final UserUUIDLessDTOMatcher userUUIDLessDTOMatcher = new UserUUIDLessDTOMatcher(userUUIDLessDTO);
        given(userService.create(argThat(userUUIDLessDTOMatcher)))
            .willReturn(asEntity);

        // Act
        final ResultActions resultActions = mvc.perform(
                post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUUIDLessDTO)));

        // Assert
        resultActions.andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.uuid").value(uuid.toString()))
                .andExpect(jsonPath("$.email").value(VALID_EMAIL))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(userController, times(1)).create(argThat(userUUIDLessDTOMatcher));
    }

    @Test
    @DisplayName("RmptPIrsWe: Given POST on /users with valid email but blank password as manager, then return 400")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void create_AsManager_BlankPassword() throws Exception {

        // Arrange
        final UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email(VALID_EMAIL)
                .password("")
                .build();
        final UserUUIDLessDTOMatcher userUUIDLessDTOMatcher = new UserUUIDLessDTOMatcher(userUUIDLessDTO);
        // Act
        final ResultActions resultActions = mvc.perform(
                post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUUIDLessDTO)));

        // Assert
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Invalid request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.email").doesNotExist())
                .andExpect(jsonPath("$.errors.password.length()").value(2))
                .andExpect(jsonPath("$.errors.password[*]")
                    .value(containsInAnyOrder(
                            messageSource.getMessage("user.password.not_blank", null, Locale.getDefault()),
                            messageSource.getMessage("user.password.not_valid", null, Locale.getDefault()))));


        verify(userController, times(0)).create(argThat(userUUIDLessDTOMatcher));
    }

    @Test
    @DisplayName("frIhksLSfi: Given POST on /users with valid email but null password as manager , then return 400")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void create_AsManager_NullPassword() throws Exception {

        // Arrange
        final UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email(VALID_EMAIL)
                .password(null)
                .build();

        // Act
        final ResultActions resultActions = mvc.perform(
                post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUUIDLessDTO)));

        // Assert
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Invalid request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.email").doesNotExist())
                .andExpect(jsonPath("$.errors.password.length()").value(1))
                .andExpect(jsonPath("$.errors.password[0]")
                    .value(messageSource.getMessage("user.password.not_blank", null, Locale.getDefault())));

        verify(userController, times(0)).create(argThat(new UserUUIDLessDTOMatcher(userUUIDLessDTO)));
    }

    @Test
    @DisplayName("vLrHOobZKR: Given POST on /users with valid email but invalid password as manager, then return 400")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void create_AsManager_InvalidPassword() throws Exception {

        // Arrange
        final UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email(VALID_EMAIL)
                .password(INVALID_PASSWORD)
                .build();
        // Act
        final ResultActions resultActions = mvc.perform(
                post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUUIDLessDTO)));

        // Assert
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Invalid request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.email").doesNotExist())
                .andExpect(jsonPath("$.errors.password.length()").value(1))
                .andExpect(jsonPath("$.errors.password[*]")
                    .value(containsInAnyOrder(
                            messageSource.getMessage("user.password.not_valid", null, Locale.getDefault()))));

        verify(userController, times(0)).create(argThat(new UserUUIDLessDTOMatcher(userUUIDLessDTO)));
    }

    @Test
    @DisplayName("DdpYDUZUha: Given POST on /users with valid password but invalid email as manager, then return 400")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void create_AsManager_InvalidEmail() throws Exception {

        // Arrange
        final UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email(INVALID_EMAIL)
                .password(VALID_PASSWORD)
                .build();
        // Act
        final ResultActions resultActions = mvc.perform(
                post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUUIDLessDTO)));

        // Assert
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Invalid request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.email.length()").value(1))
                .andExpect(jsonPath("$.errors.email[0]")
                        .value(messageSource.getMessage("user.email.not_valid", null, Locale.getDefault())))
                .andExpect(jsonPath("$.errors.password").doesNotExist());

        verify(userController, times(0)).create(argThat(new UserUUIDLessDTOMatcher(userUUIDLessDTO)));
    }

    @Test
    @DisplayName("nNVdAKxiyG: Given POST on /users with valid password but blank email as manager, then return 400")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void create_AsManager_BlankEmail() throws Exception {

        // Arrange
        final UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email("")
                .password(VALID_PASSWORD)
                .build();
        // Act
        final ResultActions resultActions = mvc.perform(
                post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUUIDLessDTO)));

        // Assert
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Invalid request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.email.length()").value(1))
                .andExpect(jsonPath("$.errors.email[0]")
                        .value(messageSource.getMessage("user.email.not_blank", null, Locale.getDefault())))
                .andExpect(jsonPath("$.errors.password").doesNotExist());

        verify(userController, times(0)).create(argThat(new UserUUIDLessDTOMatcher(userUUIDLessDTO)));
    }

    @Test
    @DisplayName("VdkByvnZei: Given POST on /users with valid password but null email as manager , then return 400")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void create_AsManager_NullEmail() throws Exception {

        // Arrange
        final UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email(null)
                .password(VALID_PASSWORD)
                .build();
        // Act
        final ResultActions resultActions = mvc.perform(
                post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUUIDLessDTO)));

        // Assert
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Invalid request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.email.length()").value(1))
                .andExpect(jsonPath("$.errors.email[0]")
                        .value(messageSource.getMessage("user.email.not_blank", null, Locale.getDefault())))
                .andExpect(jsonPath("$.errors.password").doesNotExist());
    }

    @Test
    @DisplayName("oUlwJOTeaC: Given POST on /users with invalid password and invalid email as manager, then return 400")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void create_AsManager_InvalidEmailAndPassword() throws Exception {

        // Arrange
        final UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email(INVALID_EMAIL)
                .password(INVALID_PASSWORD)
                .build();
        // Act
        final ResultActions resultActions = mvc.perform(
                post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUUIDLessDTO)));

        // Assert
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Invalid request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.email.length()").value(1))
                .andExpect(jsonPath("$.errors.email[0]")
                        .value(messageSource.getMessage("user.email.not_valid", null, Locale.getDefault())))
                .andExpect(jsonPath("$.errors.password.length()").value(1))
                .andExpect(jsonPath("$.errors.password[0]")
                        .value(messageSource.getMessage("user.password.not_valid", null, Locale.getDefault())));

        verify(userController, times(0)).create(argThat(new UserUUIDLessDTOMatcher(userUUIDLessDTO)));
    }

    @Test
    @DisplayName("EDOfpjxkSB: Given POST on /users with blank password and blank email as manager, then return 400")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void create_AsManager_BlankEmailAndPassword() throws Exception {

        // Arrange
        final UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email("")
                .password("")
                .build();
        // Act
        final ResultActions resultActions = mvc.perform(
                post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUUIDLessDTO)));

        // Assert
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Invalid request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.email.length()").value(1))
                .andExpect(jsonPath("$.errors.email[0]")
                        .value(messageSource.getMessage("user.email.not_blank", null, Locale.getDefault())))
                .andExpect(jsonPath("$.errors.password.length()").value(2))
                .andExpect(jsonPath("$.errors.password[*]")
                        .value(containsInAnyOrder(
                                messageSource.getMessage("user.password.not_blank", null, Locale.getDefault()),
                                messageSource.getMessage("user.password.not_valid", null, Locale.getDefault()))));

        verify(userController, times(0)).create(argThat(new UserUUIDLessDTOMatcher(userUUIDLessDTO)));

    }

    @Test
    @DisplayName("ElzAqJduRe: Given POST on /users with null password and null email as manager, then return 400")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void create_AsManager_NullEmailAndPassword() throws Exception {

        // Arrange
        final UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email(null)
                .password(null)
                .build();
        // Act
        final ResultActions resultActions = mvc.perform(
                post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUUIDLessDTO)));

        // Assert
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Invalid request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.email.length()").value(1))
                .andExpect(jsonPath("$.errors.email[0]")
                        .value(messageSource.getMessage("user.email.not_blank", null, Locale.getDefault())))
                .andExpect(jsonPath("$.errors.password.length()").value(1))
                .andExpect(jsonPath("$.errors.password[0]")
                        .value(messageSource.getMessage("user.password.not_blank", null, Locale.getDefault())));

        verify(userController, times(0)).create(argThat(new UserUUIDLessDTOMatcher(userUUIDLessDTO)));
    }

    @Test
    @DisplayName("mCvMOXPrQz: Given POST on /users with valid data as clerk, then return 403")
    @WithMockUser(username = "some-user", roles = "CLERK")
    void create_AsClerk() throws Exception {

        // Arrange
        final UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD)
                .build();
        final UserUUIDLessDTOMatcher userUUIDLessDTOMatcher = new UserUUIDLessDTOMatcher(userUUIDLessDTO);

        // Act
        final ResultActions resultActions = mvc.perform(
                post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUUIDLessDTO)));

        // Assert
        resultActions.andExpect(status().isForbidden());

        verify(userController, times(0)).create(argThat(userUUIDLessDTOMatcher));
    }

    @Test
    @DisplayName("mwAxkIoods: Given POST on /users with valid data as salesperson, then return 403")
    @WithMockUser(username = "some-salesperson", roles = "SALESPERSON")
    void create_AsSalesperson() throws Exception {

        // Arrange
        final UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD)
                .build();
        final UserUUIDLessDTOMatcher userUUIDLessDTOMatcher = new UserUUIDLessDTOMatcher(userUUIDLessDTO);

        // Act
        final ResultActions resultActions = mvc.perform(
                post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUUIDLessDTO)));

        // Assert
        resultActions.andExpect(status().isForbidden());

        verify(userController, times(0)).create(argThat(userUUIDLessDTOMatcher));
    }

    @Test
    @DisplayName("drXdaKKHXS: Given GET on /users with no size or page as manager, then return 200 and first Page of Users")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void getAll_AsManager() throws Exception {

        // Arrange
        final int
                num = 100,
                page = 0,
                size = 10;
        final Page<User> pageFor = createPageFor(generateUsers(num));
        given(userService.findAll(PageRequest.of(page, size)))
                .willReturn(pageFor);

        // Act
        final ResultActions resultActions = mvc.perform(
                get("/users")
                        .contentType(APPLICATION_JSON));

        // Assert
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.content[*].uuid").exists())
                .andExpect(jsonPath("$.content[*].email").exists())
                .andExpect(jsonPath("$.content[*].password").doesNotExist())
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false))
                .andExpect(jsonPath("$.totalPages").value(10))
                .andExpect(jsonPath("$.totalElements").value(num))
                .andExpect(jsonPath("$.number").value(1));

        verify(userService, times(1)).findAll(PageRequest.of(page, size));
    }

    @Test
    @DisplayName("OLBKyFJRIY: Given GET on /users with no size or page as clerk, then return 403")
    @WithMockUser(username = "some-clerk", roles = "CLERK")
    void getAll_AsClerk() throws Exception {

        // Arrange
        final int
                num = 100,
                page = 0,
                size = 10;
        final Page<User> pageFor = createPageFor(generateUsers(num));
        given(userService.findAll(PageRequest.of(page, size)))
                .willReturn(pageFor);

        // Act
        final ResultActions resultActions = mvc.perform(
                get("/users")
                        .contentType(APPLICATION_JSON));

        // Assert
        resultActions.andExpect(status().isForbidden());

        verify(userService, times(0)).findAll(PageRequest.of(page, size));
    }

    @Test
    @DisplayName("hytaoIMBZR: Given GET on /users with no size or page as salesperson, then return 403")
    @WithMockUser(username = "some-salesperson", roles = "SALESPERSON")
    void getAll_AsSalesperson() throws Exception {

        // Arrange
        final int
                num = 100,
                page = 0,
                size = 10;
        final Page<User> pageFor = createPageFor(generateUsers(num));
        given(userService.findAll(PageRequest.of(page, size)))
                .willReturn(pageFor);

        // Act
        final ResultActions resultActions = mvc.perform(
                get("/users")
                        .contentType(APPLICATION_JSON));

        // Assert
        resultActions.andExpect(status().isForbidden());

        verify(userService, times(0)).findAll(PageRequest.of(page, size));
    }

    @Test
    @DisplayName("xRlpVlNRbL: Given POST on /users with duplicate email, then return 400")
    @WithMockUser(username = "some-salesperson", roles = "MANAGER")
    void create_DuplicateEmail() throws Exception {

        // Arrange
        final UserUUIDLessDTOMatcher userUUIDLessDTOMatcher = new UserUUIDLessDTOMatcher(VALID_USER_DTO);
        final DuplicateEmailException duplicateEmailException = new DuplicateEmailException(VALID_USER_DTO.getEmail());
        given(userService.create(argThat(userUUIDLessDTOMatcher)))
                .willThrow(duplicateEmailException);

        // Act
        final ResultActions resultActions = mvc.perform(
                post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_USER_DTO)));

        // Assert
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(duplicateEmailException.getMessage()))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));

        verify(userService, times(1)).create(argThat(userUUIDLessDTOMatcher));
    }


    @Test
    @DisplayName("IZwUZRjxye: Given Put on /users/{uuid} with valid password, then return 200")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void update_AsManager() throws Exception {
        // Arrange
        final UserUUIDLessDTO userUUIDLessDTO = VALID_USER_DTO;
        final User expected = userMapper.toEntity(userUUIDLessDTO);

        updateWith(VALID_UUID, userUUIDLessDTO, expected)
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));

        verify(userService, times(1)).update(VALID_UUID, userUUIDLessDTO);

   }
    @Test
    @DisplayName("glqabkOIZD: Given Put on /users/{uuid} with blank password, then return 400")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void update_AsManager_BlankPassword() throws Exception{
        // Arrange
        final UserUUIDLessDTO userUUIDLessDTO = VALID_USER_DTO.toBuilder()
                .password("")
                .build();
        updateWith(VALID_UUID, userUUIDLessDTO, (User) null)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request"));

        verify(userService, never()).update(VALID_UUID, userUUIDLessDTO);
    }

    @Test
    @DisplayName("rZAmHNxZFE: Given Put on /users/{uuid} with invalid password, then return 400")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void update_AsManager_InvalidPassword() throws Exception {

        final UserUUIDLessDTO userUUIDLessDTO = VALID_USER_DTO.toBuilder().password("a").build();
        updateWith(VALID_UUID, userUUIDLessDTO, (User) null)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request"));

        verify(userService, never()).update(VALID_UUID, userUUIDLessDTO);
    }
    @Test
    @DisplayName("cxvatXCacZ: Given Put on /users/{uuid} with null password, then return 400")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void update_AsManager_NullPassword() throws Exception {

        final UserUUIDLessDTO userUUIDLessDTO = VALID_USER_DTO.toBuilder().password(null).build();
        updateWith(VALID_UUID, userUUIDLessDTO, (User) null)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request"));

        verify(userService, never()).update(VALID_UUID, userUUIDLessDTO);
    }
}


