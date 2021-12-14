package com.lepine.transfers.http;

import static com.lepine.transfers.helpers.PageHelpers.createPageFor;
import static java.lang.String.format;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.controllers.user.UserController;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserMapper;
import com.lepine.transfers.data.user.UserUUIDLessDTO;
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

@WebMvcTest(controllers = { UserController.class })
@ContextConfiguration(classes = { MapperConfig.class })
@ActiveProfiles("test")
public class UserHttpTests {

    private static final String VALID_EMAIL = "valid@email.com";
    private static final String VALID_PASSWORD = "S0m3P@ssw0rd";
    
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
    @DisplayName("Given POST on /users with valid data, then return 201")
    @WithMockUser(username = "some-manager", authorities = "MANAGER")
    void create() throws Exception {

        // Arrange
        final UserUUIDLessDTO userUUIDLessDTO = UserUUIDLessDTO.builder()
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD)
                .build();
        final User asEntity = userMapper.toEntity(userUUIDLessDTO);

        given(userService.create(any(UserUUIDLessDTO.class)))
            .willReturn(asEntity);

        // Act
        final ResultActions resultActions = mvc.perform(
                post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userUUIDLessDTO)));

        // Assert
        resultActions.andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.uuid").exists())
                .andExpect(jsonPath("$.email").value(VALID_EMAIL))
                .andExpect(jsonPath("$.password").doesNotExist());

    }
}
