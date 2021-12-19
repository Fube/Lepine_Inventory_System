package com.lepine.transfers.services.auth;

import com.lepine.transfers.data.auth.UserLogin;
import com.lepine.transfers.data.user.User;
import org.javatuples.Pair;

import javax.validation.Valid;

public interface AuthService {
    Pair<User, String> login(@Valid UserLogin userLogin);
}
