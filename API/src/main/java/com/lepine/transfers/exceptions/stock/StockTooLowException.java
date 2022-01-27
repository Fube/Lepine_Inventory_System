package com.lepine.transfers.exceptions.stock;

import lombok.Getter;

import java.util.UUID;

import static java.lang.String.format;

@Getter
public class StockTooLowException extends RuntimeException {

    private final static String MESSAGE = "Stock for %s is too low, wanted %d, have %d";

    private final int given;
    private final int wanted;
    private final UUID uuid;

    public StockTooLowException(UUID uuid, int given, int wanted) {
        super(format(MESSAGE, uuid, wanted, given));
        this.uuid = uuid;
        this.given = given;
        this.wanted = wanted;
    }
}
