package com.lepine.transfers.exceptions.transfer;

import com.lepine.transfers.exceptions.I18nAble;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class QuantityExceededException extends RuntimeException implements I18nAble {
    private final static String MESSAGE = "Quantity exceeded, maximum %s, got %s";
    private final static String CODE = "transfer.quantity.exceeded";
    private final int maxQuantity;
    private final int quantity;

    public QuantityExceededException(final int maxQuantity, final int quantity) {
        super(String.format(MESSAGE, maxQuantity, quantity));
        this.maxQuantity = maxQuantity;
        this.quantity = quantity;
    }

    @Override
    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(CODE, new Object[]{maxQuantity, quantity}, locale);
    }
}

