package com.lepine.transfers.exceptions.user;

import com.lepine.transfers.exceptions.NotFoundException;

import static java.lang.String.format;

public class RoleNotFoundException extends NotFoundException {
    private static final String ROLE_FORMAT = "Role with email %s not found";
    public RoleNotFoundException(String role) {
        super(format(ROLE_FORMAT, role));
    }
}
