package com.lepine.transfers.controllers.auth;

import com.lepine.transfers.config.JWTConfig;
import com.lepine.transfers.data.auth.UserLogin;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserMapper;
import com.lepine.transfers.data.user.UserPasswordLessDTO;
import com.lepine.transfers.exceptions.auth.InvalidLoginException;
import com.lepine.transfers.exceptions.user.UserNotFoundException;
import com.lepine.transfers.services.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;
    private final JWTConfig jwtConfig;

    @PostMapping("/login")
    public ResponseEntity<UserPasswordLessDTO> login(@Valid @RequestBody UserLogin userLogin) {
        log.info("Logging in user {}", userLogin.getEmail());

        Pair<User, String> login;
        try {
            login = authService.login(userLogin);
        } catch (UserNotFoundException e) {
            log.error("User with email {} not found", userLogin.getEmail());
            throw new InvalidLoginException();
        }

        final User principle = login.getValue0();
        final String jwt = login.getValue1();

        log.info("User {} logged in", principle.getUsername());

        final UserPasswordLessDTO userPasswordLessDTO = userMapper.toPasswordLessDTO(principle);
        final ResponseCookie jwtAsCookie = ResponseCookie.from("token", jwt)
                .httpOnly(true)
                .secure(true)
                .maxAge(jwtConfig.getExpiration() / 1000)
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtAsCookie.toString())
                .body(userPasswordLessDTO);
    }
    @RequestMapping(method = {RequestMethod.HEAD}, value = "/logout")
    public ResponseEntity<Void> logout() {
        log.info("Logging out user");
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, ResponseCookie.from("token", "")
                        .httpOnly(true)
                        .secure(true)
                        .maxAge(0)
                        .path("/")
                        .build().toString())
                .build();
    }
}
