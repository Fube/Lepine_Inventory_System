package com.lepine.transfers.services.auth;

import com.lepine.transfers.data.auth.UserLogin;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {
    UserDetails login(UserLogin userLogin);
}
