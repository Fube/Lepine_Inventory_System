package com.lepine.transfers.exceptions.item;

import com.lepine.transfers.exceptions.DuplicateResourceException;
import com.lepine.transfers.exceptions.I18nAble;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static java.lang.String.format;

public class DuplicateSkuException extends DuplicateResourceException implements I18nAble {
    private final static String MESSAGE = "Item with SKU %s already exists";
    private final static String CODE = "item.duplicate.sku";
    private final String sku;


    public DuplicateSkuException(String sku) {
        super(format(MESSAGE, sku));
        this.sku = sku;
    }


    @Override
    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(CODE, new Object[]{ sku }, locale);
    }
}
