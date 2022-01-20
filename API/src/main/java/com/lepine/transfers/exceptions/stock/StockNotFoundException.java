package com.lepine.transfers.exceptions.stock;

import com.lepine.transfers.exceptions.NotFoundException;

import java.util.UUID;

import static java.lang.String.format;

public class StockNotFoundException extends NotFoundException {
    private final static String FORMAT = "Stock with uuid %s not found";

    public StockNotFoundException(UUID uuid) {
        super(format(FORMAT, uuid));
    }
}
