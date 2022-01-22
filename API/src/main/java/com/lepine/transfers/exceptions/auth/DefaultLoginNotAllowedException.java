package com.lepine.transfers.exceptions.auth;

public class DefaultLoginNotAllowedException extends RuntimeException{
    private final static String MESSAGE = "Default login is not allowed for this operation";
    public DefaultLoginNotAllowedException() {
        super(MESSAGE);
    }
}
