package com.lepine.transfers.exceptions.auth;

import com.lepine.transfers.exceptions.I18nAble;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class DefaultLoginNotAllowedException extends RuntimeException implements I18nAble {
    private final static String MESSAGE = "Default login is not allowed for this operation";
    private final static String CODE = "default.login.not_allowed";

    public DefaultLoginNotAllowedException() {
        super(MESSAGE);
    }

    @Override
    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(CODE, null, locale);
    }
}
