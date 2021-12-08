package com.lepine.transfers.exceptions.item;

import com.lepine.transfers.exceptions.NotFoundException;

import java.util.UUID;

import static java.lang.String.format;

public class ItemNotFoundException extends NotFoundException {
    private static final String UUID_FORMAT = "Item with uuid %s not found";
    public ItemNotFoundException(UUID uuid) {
        super(format(UUID_FORMAT, uuid));
    }
}
