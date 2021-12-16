package com.lepine.transfers.services.auth;

import com.lepine.transfers.data.auth.UserLogin;
import com.lepine.transfers.data.user.User;

import javax.validation.Valid;

public interface AuthService {
    User login(@Valid UserLogin userLogin);
}
