package com.lepine.transfers.integration.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lepine.transfers.config.JWTConfig;
import com.lepine.transfers.config.MapperConfig;
import com.lepine.transfers.config.SecurityConfig;
import com.lepine.transfers.config.ValidationConfig;
import com.lepine.transfers.config.controllers.GlobalAdvice;
import com.lepine.transfers.controllers.auth.AuthController;
import com.lepine.transfers.data.auth.Role;
import com.lepine.transfers.data.auth.UserLogin;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserRepo;
import com.lepine.transfers.exceptions.auth.InvalidLoginException;
import com.lepine.transfers.filters.ExceptionHandlerFilter;
import com.lepine.transfers.filters.auth.JWTFilter;
import com.lepine.transfers.services.auth.AuthService;
import com.lepine.transfers.utils.auth.JWTUtil;
import com.lepine.transfers.utils.auth.UserJWTUtilImpl;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.core.StringContains;
import org.javatuples.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.servlet.http.Cookie;
import java.util.Locale;
import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@ContextConfiguration(classes = {
        AuthController.class,
        MapperConfig.class,
        ValidationConfig.class,
        JWTConfig.class,
        UserJWTUtilImpl.class,
        SecurityConfig.class,
        GlobalAdvice.class,
        JWTFilter.class,
        ExceptionHandlerFilter.class,
})
@ActiveProfiles("test")
public class AuthHttpTests {

    private static final String VALID_EMAIL = "foo@bar.com";
    private static final String INVALID_EMAIL = "";
    private static final String VALID_PASSWORD = "S0meP@ssw0rd";
    private static final String INVALID_PASSWORD = "";
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

    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;

    @SpyBean
    private JWTUtil<User> jwtUtil;

    @SpyBean
    private AuthController authController;

    @SpyBean
    private JWTFilter jwtFilter;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserRepo userRepo;

    @Test
    public void contextLoads() {
    }

    @Test
    @DisplayName("icawrdfeWI: Given POST /auth/login with a valid UserLogin, then return UserPasswordLessDTO and JWT in header as HTTP-Only cookie")
    public void login_ValidUser() throws Exception {

        // Arrange
        given(authService.login(VALID_USER_LOGIN))
                .willReturn(Pair.with(VALID_USER, VALID_JWT));

        // Act
        final ResultActions resultActions = mvc.perform(
                post("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(VALID_USER_LOGIN))
        );

        // Assert
        final Matcher<String> jwtMatcher = StringContains.containsStringIgnoringCase(VALID_JWT);
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(VALID_USER.getUuid().toString()))
                .andExpect(jsonPath("$.role.uuid").doesNotExist())
                .andExpect(jsonPath("$.role.name").doesNotExist())
                .andExpect(jsonPath("$.role").value(VALID_ROLE_NAME))
                .andExpect(jsonPath("$.email").value(VALID_EMAIL))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, jwtMatcher));


        verify(authController, times(1)).login(argThat(n -> n.getEmail().equals(VALID_EMAIL)));
        verify(userRepo, times(0)).findByEmail(VALID_EMAIL); // Ensure the filter was not called
    }

    @Test
    @DisplayName("zSgFUaVqxB: Given POST /auth/login with an invalid UserLogin, then return HTTP 400")
    public void login_InvalidUser() throws Exception {

        // Arrange
        final UserLogin invalidUserLogin = VALID_USER_LOGIN.toBuilder()
                .email(INVALID_EMAIL)
                .password(INVALID_PASSWORD)
                .build();

        // Act
        final ResultActions resultActions = mvc.perform(
                post("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidUserLogin))
        );

        // Assert
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors.email.length()").value(1))
                .andExpect(jsonPath("$.errors.email[*]",
                        containsInAnyOrder(messageSource.getMessage("user.email.not_blank", null, Locale.getDefault()))
                ))
                .andExpect(jsonPath("$.errors.password.length()").value(1))
                .andExpect(jsonPath("$.errors.password[*]",
                        containsInAnyOrder(messageSource.getMessage("user.password.not_blank", null, Locale.getDefault()))
                ));

        verify(authController, times(0)).login(argThat(n -> n.getEmail().equals(VALID_EMAIL)));
        verify(authService, times(0)).login(argThat(n -> n.getEmail().equals(VALID_EMAIL)));
        verify(userRepo, times(0)).findByEmail(VALID_EMAIL); // Ensure the filter was not called
    }

    @Test
    @DisplayName("KhCRyxyOqH: Given POST /auth/login with an invalid match, then return HTTP 401")
    public void login_InvalidMatch() throws Exception {

        // Arrange
        final UserLogin invalidUserLogin = VALID_USER_LOGIN.toBuilder()
                .email(VALID_EMAIL)
                .password(VALID_PASSWORD)
                .build();

        final InvalidLoginException invalidLoginException = new InvalidLoginException();
        given(authService.login(invalidUserLogin))
                .willThrow(invalidLoginException);

        // Act
        final ResultActions resultActions = mvc.perform(
                post("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidUserLogin))
        );

        // Assert
        resultActions
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value(invalidLoginException.getMessage()));

        verify(authService, times(1)).login(argThat(n -> n.getEmail().equals(VALID_EMAIL)));
        verify(authController, times(1)).login(argThat(n -> n.getEmail().equals(VALID_EMAIL)));
        verify(jwtFilter, atLeastOnce()).doFilter(any(), any(), any());
        verify(userRepo, times(0)).findByEmail(VALID_EMAIL); // Ensure the filter was not called
    }

    @Test
    @DisplayName("sxpmAjeMZa: Given request with JWT, trigger JWT filter")
    public void jwtFilter_ValidJWT() throws Exception {

        // Arrange
        final String jwt = jwtUtil.encode(VALID_USER);

        // Act
        mvc.perform(
                get("/some/protected/path")
                        .cookie(new Cookie("token", jwt))
        );

        // Assert
        verify(jwtFilter, atLeastOnce()).doFilter(any(), any(), any());
        verify(jwtUtil, times(1)).decode(jwt);
    }

    @Test
    @DisplayName("iyYluellAl: Given request with JWT, trigger JWT filter with invalid JWT")
    public void jwtFilter_InvalidJWT() throws Exception {

        // Arrange
        final String jwt = "invalidJWT";

        // Act
        final ResultActions resultActions = mvc.perform(
                get("/some/protected/path")
                        .cookie(new Cookie("token", jwt))
        );

        // Assert
        verify(jwtFilter, atLeastOnce()).doFilter(any(), any(), any());
        verify(jwtUtil, times(1)).decode(jwt);

        resultActions.andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("uSKbUCaWdE: Given logout request, return HTTP 204 and clear JWT cookie")
    public void logout_ValidRequest() throws Exception {

        // Arrange
        final String jwt = jwtUtil.encode(VALID_USER);

        // Act
        final ResultActions resultActions = mvc.perform(
                head("/auth/logout")
                        .cookie(new Cookie("token", jwt))
        );

        // Assert
        resultActions
                .andExpect(status().isNoContent())
                .andExpect(cookie().maxAge("token", 0))
                .andExpect(cookie().value("token", Matchers.blankOrNullString()));

        verify(jwtUtil, times(1)).decode(jwt);
    }

    @Test
    @DisplayName("txnqWMGwNt: Given HEAD on /logout, regardless of authentication or authorization, return HTTP 204 and clear JWT cookie")
    public void logout_AnyoneHeadRequest() throws Exception {

        // Arrange

        // Act
        final ResultActions resultActions = mvc.perform(
                head("/auth/logout")
        );

        // Assert
        resultActions
                .andExpect(status().isNoContent())
                .andExpect(cookie().maxAge("token", 0))
                .andExpect(cookie().value("token", Matchers.blankOrNullString()));

        verify(jwtUtil, times(0)).decode(any());
    }
}
