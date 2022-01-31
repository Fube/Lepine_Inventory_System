package com.lepine.transfers.exceptions.transfer;

import com.lepine.transfers.exceptions.NotFoundException;

import java.util.UUID;

public class TransferNotFoundException extends NotFoundException {

    private final static String MESSAGE = "Transfer with uuid %s not found";

    public TransferNotFoundException(final UUID uuid) {
        super(String.format(MESSAGE, uuid));
    }
}
