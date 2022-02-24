package com.lepine.transfers.exceptions.user;

import com.lepine.transfers.exceptions.I18nAble;
import com.lepine.transfers.exceptions.NotFoundException;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.UUID;

import static java.lang.String.format;

public class UserNotFoundException extends NotFoundException implements I18nAble {
    private static final String EMAIL_MESSAGE = "User with email %s not found";
    private static final String EMAIL_CODE = "user.email.not_found";
    private final String email;

    private static final String UUID_MESSAGE = "User with uuid %s not found";
    private static final String UUID_CODE = "user.uuid.not_found";
    private final UUID uuid;

    public UserNotFoundException(String email) {
        super(format(EMAIL_MESSAGE, email));
        this.email = email;
        this.uuid = null;
    }

    public UserNotFoundException(UUID uuid){
        super(format(UUID_MESSAGE, uuid));
        this.uuid = uuid;
        this.email = null;
    }

    @Override
    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        if (email != null) {
            return messageSource.getMessage(EMAIL_CODE, new Object[]{email}, locale);
        }

        return messageSource.getMessage(UUID_CODE, new Object[]{uuid}, locale);
    }
}
