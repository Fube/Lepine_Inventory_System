package com.lepine.transfers.controllers.auth;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;

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
                .maxAge(60 * 60 * 24)
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtAsCookie.toString())
                .body(userPasswordLessDTO);
    }
}
