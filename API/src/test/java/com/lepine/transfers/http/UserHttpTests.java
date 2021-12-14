package com.lepine.transfers.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lepine.transfers.config.MapperConfig;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = { UserController.class })
@ContextConfiguration(classes = { MapperConfig.class })
@ActiveProfiles("test")
public class UserHttpTests {

    private static final String VALID_EMAIL = "valid@gmail.com";
    private static final String VALID_PASSWORD = "S0m3P@ssword";
    
    @Autowired
    private MockMvc mvc;

    @SpyBean
    private UserController userController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @MockBean
    private UserService userService;

    @Test
    void contextLoads(){}

    @Test
    @DisplayName("Given POST on /users with valid data as manager, then return 201")
    @WithMockUser(username = "some-manager", authorities = "MANAGER")
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
    @DisplayName("Given POST on /users with valid data as clerk, then return 403")
    @WithMockUser(username = "some-user", authorities = "CLERK")
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
    @WithMockUser(username = "some-salesperson", authorities = "SALESPERSON")
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
