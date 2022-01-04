package com.lepine.transfers.exceptions.warehouse;

import static java.lang.String.format;

public class DuplicateZipCodeException extends RuntimeException {
    public DuplicateZipCodeException(String zipCode) {
        super(format("Zipcode %s already in use", zipCode));
    }
}
