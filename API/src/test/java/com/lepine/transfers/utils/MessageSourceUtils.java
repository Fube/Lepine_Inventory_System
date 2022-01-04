package com.lepine.transfers.utils;

import org.springframework.context.MessageSource;

import java.util.Locale;

public class MessageSourceUtils {
    public static class ForLocaleWrapper {
        private MessageSource messageSource;
        private Locale locale;

        ForLocaleWrapper(MessageSource messageSource, Locale locale) {
            this.messageSource = messageSource;
            this.locale = locale;
        }

        ForLocaleWrapper(MessageSource messageSource) {
            this(messageSource, Locale.getDefault());
        }

        public String getMessage(String code) {
            return messageSource.getMessage(code, null, locale);
        }
    }

    public static ForLocaleWrapper wrapperFor(MessageSource messageSource) {
        return new ForLocaleWrapper(messageSource);
    }
}
