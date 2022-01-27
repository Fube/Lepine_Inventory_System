package com.lepine.transfers.utils.matchers;

import com.lepine.transfers.data.user.User;
import org.mockito.ArgumentMatcher;

public class UserMatcher implements ArgumentMatcher<User> {

    private final User user;

    public UserMatcher(User user) {
        this.user = user;
    }

    @Override
    public boolean matches(User argument) {
        return argument.getEmail().equals(user.getEmail());
    }
}
