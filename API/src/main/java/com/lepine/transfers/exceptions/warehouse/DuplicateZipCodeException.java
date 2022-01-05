package com.lepine.transfers.exceptions.warehouse;

import com.lepine.transfers.exceptions.DuplicateResourceException;

import static java.lang.String.format;

public class DuplicateZipCodeException extends DuplicateResourceException {
    public DuplicateZipCodeException(String zipCode) {
        super(format("Zipcode %s already in use", zipCode));
    }
}
