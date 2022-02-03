package com.lepine.transfers.exceptions.transfer;

public class QuantityExceededException extends RuntimeException {

    private final static String MESSAGE = "Quantity exceeded, maximum %s, got %s";

    public QuantityExceededException(final int maxQuantity, final int quantity) {
        super(String.format(MESSAGE, maxQuantity, quantity));
    }
}

