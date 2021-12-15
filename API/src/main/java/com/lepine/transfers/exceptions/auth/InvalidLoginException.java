package com.lepine.transfers.exceptions.auth;

public class InvalidLoginException extends RuntimeException {
    public InvalidLoginException() {
        super("Invalid login");
    }
}
