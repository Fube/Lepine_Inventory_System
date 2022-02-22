package com.lepine.transfers.exceptions.item;

import com.lepine.transfers.exceptions.I18nAble;
import com.lepine.transfers.exceptions.NotFoundException;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.UUID;

import static java.lang.String.format;

public class ItemNotFoundException extends NotFoundException implements I18nAble {
    private static final String MESSAGE = "Item with uuid %s not found";
    private static final String CODE = "item.not_found";
    private final UUID uuid;

    public ItemNotFoundException(UUID uuid) {
        super(format(MESSAGE, uuid));
        this.uuid = uuid;
    }


    @Override
    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(CODE, new Object[]{ uuid }, locale);
    }
}
