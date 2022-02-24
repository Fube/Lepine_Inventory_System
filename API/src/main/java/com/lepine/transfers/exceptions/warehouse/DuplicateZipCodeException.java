package com.lepine.transfers.exceptions.warehouse;

import com.lepine.transfers.exceptions.DuplicateResourceException;
import com.lepine.transfers.exceptions.I18nAble;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static java.lang.String.format;

public class DuplicateZipCodeException extends DuplicateResourceException implements I18nAble {
    private final static String MESSAGE = "Zipcode %s already in use";
    private final static String CODE = "warehouse.zipcode.duplicate";
    private final String zipCode;

    public DuplicateZipCodeException(String zipCode) {
        super(format(MESSAGE, zipCode));
        this.zipCode = zipCode;
    }

    @Override
    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(CODE, new Object[]{zipCode}, locale);
    }
}
