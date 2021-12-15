package com.lepine.transfers.controllers.auth;

import com.lepine.transfers.data.auth.UserLogin;
import com.lepine.transfers.data.user.UserMapper;
import com.lepine.transfers.data.user.UserPasswordLessDTO;
import com.lepine.transfers.services.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;

    public UserPasswordLessDTO login(UserLogin userLogin) {
        log.info("Logging in user {}", userLogin.getEmail());
        final UserDetails login = authService.login(userLogin);
        log.info("User {} logged in", login.getUsername());

        return userMapper.toPasswordLessDTO(login);
    }
}
