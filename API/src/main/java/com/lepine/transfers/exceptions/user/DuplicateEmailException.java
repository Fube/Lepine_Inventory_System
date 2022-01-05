package com.lepine.transfers.exceptions.user;

import com.lepine.transfers.exceptions.DuplicateResourceException;

import static java.lang.String.format;

public class DuplicateEmailException extends DuplicateResourceException {
    public DuplicateEmailException(String email) {
        super(format("Email %s already in use", email));
    }
}
