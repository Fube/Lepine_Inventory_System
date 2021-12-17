package com.lepine.transfers.exceptions.user;

import com.lepine.transfers.exceptions.NotFoundException;

import static java.lang.String.format;

public class UserNotFoundException extends NotFoundException {
    private static final String UUID_FORMAT = "User with email %s not found";
    public UserNotFoundException(String email) {
        super(format(UUID_FORMAT, email));
    }
}
