package com.lepine.transfers.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.controllers.user.UserController;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserMapper;
import com.lepine.transfers.data.user.UserUUIDLessDTO;
import com.lepine.transfers.helpers.matchers.UserUUIDLessDTOMatcher;
import com.lepine.transfers.services.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Locale;
import java.util.UUID;

import static org.hamcrest.Matchers.contains;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = { UserController.class })
@ContextConfiguration(classes = { MapperConfig.class, ValidationConfig.class})
@ActiveProfiles("test")
public class UserHttpTests {

    private static final String VALID_EMAIL = "valid@gmail.com";
    private static final String INVALID_EMAIL = "invalid";
    private static final String VALID_PASSWORD = "S0m3P@ssword";
    private static final String INVALID_PASSWORD = "a";

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

    @Test
    void contextLoads(){}

    @Test
    @DisplayName("Given POST on /users with valid data as manager, then return 201")
    @WithMockUser(username = "some-manager", roles = "MANAGER")
    void create_AsManager() throws Exception {

        // Arrange
        final UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD)
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
    @DisplayName("Given POST on /users with valid email but blank password as manager, then return 400")
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
                    .value(contains(
                            messageSource.getMessage("user.password.not_blank", null, Locale.getDefault()),
                            messageSource.getMessage("user.password.not_valid", null, Locale.getDefault()))));


        verify(userController, times(0)).create(argThat(userUUIDLessDTOMatcher));
    }

    @Test
    @DisplayName("Given POST on /users with valid email but null password as manager , then return 400")
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
    }

    @Test
    @DisplayName("Given POST on /users with valid email but invalid password as manager, then return 400")
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
                    .value(contains(
                            messageSource.getMessage("user.password.not_valid", null, Locale.getDefault()))));
    }

    @Test
    @DisplayName("Given POST on /users with valid password but invalid email as manager, then return 400")
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
    }

    @Test
    @DisplayName("Given POST on /users with valid password but blank email as manager, then return 400")
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
    }

    @Test
    @DisplayName("Given POST on /users with valid password but null email as manager , then return 400")
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
    @DisplayName("Given POST on /users with invalid password and invalid email as manager, then return 400")
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
    }

    @Test
    @DisplayName("Given POST on /users with blank password and blank email as manager, then return 400")
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
                        .value(contains(
                                messageSource.getMessage("user.password.not_blank", null, Locale.getDefault()),
                                messageSource.getMessage("user.password.not_valid", null, Locale.getDefault()))));
    }

    @Test
    @DisplayName("Given POST on /users with valid data as clerk, then return 403")
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
    @DisplayName("Given POST on /users with valid data as salesperson, then return 403")
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
}
