package com.lepine.transfers.exceptions.stock;

import com.lepine.transfers.data.stock.Stock;
import com.lepine.transfers.exceptions.DuplicateResourceException;

public class StockAlreadyExistsException extends DuplicateResourceException {

    public final static String MESSAGE = "Stock already exists";

    public StockAlreadyExistsException(final Stock stock) {
        super(MESSAGE);
    }
}
