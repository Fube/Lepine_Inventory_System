package com.lepine.transfers.controllers.auth;

import com.lepine.transfers.data.auth.UserLogin;
import com.lepine.transfers.data.user.UserPasswordLessDTO;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    public UserPasswordLessDTO login(UserLogin userLogin) {
        return null;
    }
}
