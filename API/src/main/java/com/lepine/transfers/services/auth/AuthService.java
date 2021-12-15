package com.lepine.transfers.services.auth;

import com.lepine.transfers.data.auth.UserLogin;
import com.lepine.transfers.data.user.User;

public interface AuthService {
    User login(UserLogin userLogin);
}
