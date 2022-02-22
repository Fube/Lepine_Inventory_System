package com.lepine.transfers.exceptions.warehouse;

import com.lepine.transfers.exceptions.I18nAble;
import com.lepine.transfers.exceptions.NotFoundException;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.UUID;

import static java.lang.String.format;

public class WarehouseNotFoundException extends NotFoundException implements I18nAble {
    private final static String MESSAGE = "Warehouse with uuid %s not found";
    private final static String CODE = "warehouse.not_found";
    private final UUID uuid;

    public WarehouseNotFoundException(UUID uuid) {
        super(format(MESSAGE, uuid));
        this.uuid = uuid;
    }

    @Override
    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(CODE, new Object[]{uuid}, locale);
    }
}
