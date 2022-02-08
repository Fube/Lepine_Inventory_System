package com.lepine.transfers.utils.matchers;

import com.lepine.transfers.data.user.UserUUIDLessDTO;
import org.mockito.ArgumentMatcher;

public class UserUUIDLessDTOMatcher implements ArgumentMatcher<UserUUIDLessDTO> {

    private final UserUUIDLessDTO user;

    public UserUUIDLessDTOMatcher(UserUUIDLessDTO user) {
        this.user = user;
    }

    @Override
    public boolean matches(UserUUIDLessDTO argument) {
        return argument.getEmail().equals(user.getEmail());
    }
}
