package com.lepine.transfers.exceptions.user;

import com.lepine.transfers.exceptions.NotFoundException;

import java.util.UUID;

import static java.lang.String.format;

public class UserNotFoundException extends NotFoundException {
    private static final String EMAIL_FORMAT = "User with email %s not found";
    private static final String UUID_FORMAT = "User with uuid %s not found";
    public UserNotFoundException(String email) {
        super(format(EMAIL_FORMAT, email));
    }
    public UserNotFoundException(UUID uuid){
        super(format(UUID_FORMAT,uuid));
    }
}
