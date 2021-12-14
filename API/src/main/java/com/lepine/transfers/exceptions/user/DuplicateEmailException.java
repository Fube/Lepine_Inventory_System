package com.lepine.transfers.exceptions.user;

import static java.lang.String.format;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super(format("Email %s already in use", email));
    }
}
