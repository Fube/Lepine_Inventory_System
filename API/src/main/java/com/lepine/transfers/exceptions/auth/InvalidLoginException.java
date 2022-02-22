package com.lepine.transfers.exceptions.auth;

import com.lepine.transfers.exceptions.I18nAble;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class InvalidLoginException extends RuntimeException implements I18nAble {
    private final static String MESSAGE = "Invalid login";
    private final static String CODE = "invalid.login";


    public InvalidLoginException() {
        super(MESSAGE);
    }

    @Override
    public String getLocalizedMessage(final MessageSource messageSource, final Locale locale) {
        return messageSource.getMessage(CODE, null, locale);
    }

}
