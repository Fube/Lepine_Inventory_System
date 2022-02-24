package com.lepine.transfers.exceptions.user;

import com.lepine.transfers.exceptions.DuplicateResourceException;
import com.lepine.transfers.exceptions.I18nAble;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static java.lang.String.format;

public class DuplicateEmailException extends DuplicateResourceException implements I18nAble {
    private final static String MESSAGE = "Email %s already in use";
    private final static String CODE = "email.duplicate";
    private final String email;

    public DuplicateEmailException(String email) {
        super(format(MESSAGE, email));
        this.email = email;
    }

    @Override
    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(CODE, new Object[] {email}, locale);
    }
}
