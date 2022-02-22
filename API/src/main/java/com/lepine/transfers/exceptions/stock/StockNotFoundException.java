package com.lepine.transfers.exceptions.stock;

import com.lepine.transfers.exceptions.I18nAble;
import com.lepine.transfers.exceptions.NotFoundException;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.UUID;

import static java.lang.String.format;

public class StockNotFoundException extends NotFoundException implements I18nAble {
    private final static String FORMAT = "Stock with uuid %s not found";
    private final static String CODE = "stock.not_found";
    private final UUID uuid;

    public StockNotFoundException(UUID uuid) {
        super(format(FORMAT, uuid));
        this.uuid = uuid;
    }

    @Override
    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(CODE, new Object[]{uuid}, locale);
    }
}
