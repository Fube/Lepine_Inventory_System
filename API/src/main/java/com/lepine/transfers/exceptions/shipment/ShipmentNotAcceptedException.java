package com.lepine.transfers.exceptions.shipment;

import com.lepine.transfers.exceptions.I18nAble;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.UUID;

public class ShipmentNotAcceptedException extends RuntimeException implements I18nAble {
    private final static String MESSAGE = "Shipment with uuid %s is %s";
    private final static String CODE = "shipment.not_accepted";
    private final UUID uuid;
    private final String status;

    public ShipmentNotAcceptedException(final UUID uuid, final String status) {
        super(String.format(MESSAGE, uuid, status));
        this.uuid = uuid;
        this.status = status;
    }

    @Override
    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(
                CODE,
                new Object[]{uuid, status},
                locale
        );
    }
}
