package com.lepine.transfers.controllers.auth;

import com.lepine.transfers.data.auth.UserLogin;
import com.lepine.transfers.data.user.User;
import com.lepine.transfers.data.user.UserMapper;
import com.lepine.transfers.data.user.UserPasswordLessDTO;
import com.lepine.transfers.services.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;

    public UserPasswordLessDTO login(@Valid UserLogin userLogin) {
        log.info("Logging in user {}", userLogin.getEmail());
        final User login = authService.login(userLogin);
        log.info("User {} logged in", login.getUsername());

        return userMapper.toPasswordLessDTO(login);
    }
}
