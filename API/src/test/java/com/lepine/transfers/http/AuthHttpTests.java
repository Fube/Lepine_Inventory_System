package com.lepine.transfers.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.controllers.auth.AuthController;
import com.lepine.transfers.data.auth.Role;
import com.lepine.transfers.data.auth.UserLogin;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.services.auth.AuthService;
import org.javatuples.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static java.lang.String.format;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = { AuthController.class })
@ContextConfiguration(classes = { MapperConfig.class, ValidationConfig.class})
@ActiveProfiles("test")
public class AuthHttpTests {

    private static final String VALID_EMAIL = "foo@bar.com";
    private static final String VALID_PASSWORD = "S0meP@ssw0rd";
    private static final String VALID_ROLE_NAME = "SOME_ROLE";
    private static final String VALID_JWT = "some.valid.jwt";

    private static final Role VALID_ROLE = Role.builder()
            .uuid(UUID.randomUUID())
            .name(VALID_ROLE_NAME)
            .build();
    private static final UserLogin VALID_USER_LOGIN = UserLogin.builder()
            .email(VALID_EMAIL)
            .password(VALID_PASSWORD)
            .build();
    private static final User VALID_USER = User.builder()
            .uuid(UUID.randomUUID())
            .role(VALID_ROLE)
            .email(VALID_EMAIL)
            .build();

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    public void contextLoads() {
    }

    @Test
    @DisplayName("Given POST /login with a valid UserLogin, then return UserPasswordLessDTO and JWT in header as HTTP-Only cookie")
    public void login_ValidUser() throws Exception {

        // Arrange
        given(authService.login(VALID_USER_LOGIN))
                .willReturn(Pair.with(VALID_USER, VALID_JWT));

        // Act
        final ResultActions resultActions = mvc.perform(
                post("/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(VALID_USER_LOGIN)));

        // Assert
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(VALID_USER.getUuid().toString()))
                .andExpect(jsonPath("$.role.uuid").doesNotExist())
                .andExpect(jsonPath("$.role.name").doesNotExist())
                .andExpect(jsonPath("$.role").value(VALID_ROLE_NAME))
                .andExpect(jsonPath("$.email").value(VALID_EMAIL))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, contains(format("token=%s", VALID_JWT))));
    }
}
