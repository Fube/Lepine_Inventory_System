package com.lepine.transfers.exceptions.user;

import com.lepine.transfers.exceptions.I18nAble;
import com.lepine.transfers.exceptions.NotFoundException;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static java.lang.String.format;

public class RoleNotFoundException extends NotFoundException implements I18nAble {
    private static final String MESSAGE = "Role %s not found";
    private static final String CODE = "role.not_found";
    private final String role;

    public RoleNotFoundException(String role) {
        super(format(MESSAGE, role));
        this.role = role;
    }

    @Override
    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(CODE, new Object[]{role}, locale);
    }
}
