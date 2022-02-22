package com.lepine.transfers.exceptions;

import org.springframework.context.MessageSource;

import java.util.Locale;

public interface I18nAble {
    String getLocalizedMessage(final MessageSource messageSource, final Locale locale);
}
