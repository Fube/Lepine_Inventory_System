package com.lepine.transfers.exceptions.stock;

import com.lepine.transfers.data.stock.Stock;
import com.lepine.transfers.exceptions.DuplicateResourceException;
import com.lepine.transfers.exceptions.I18nAble;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class StockAlreadyExistsException extends DuplicateResourceException implements I18nAble {
    public final static String MESSAGE = "Stock already exists";
    public final static String CODE = "stock.duplicate";

    public StockAlreadyExistsException(final Stock stock) {
        super(MESSAGE);
    }

    @Override
    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(CODE, null, locale);
    }
}
