package com.lepine.transfers.exceptions.item;

import com.lepine.transfers.exceptions.DuplicateResourceException;

import static java.lang.String.format;

public class DuplicateSkuException extends DuplicateResourceException {
    private final static String ERROR_MESSAGE_FORMAT = "Item with SKU %s already exists";
    public DuplicateSkuException(String sku) {
        super(format(ERROR_MESSAGE_FORMAT, sku));
    }
}
