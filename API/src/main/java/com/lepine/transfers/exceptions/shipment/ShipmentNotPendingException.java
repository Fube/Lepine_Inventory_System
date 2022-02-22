package com.lepine.transfers.exceptions.shipment;

import com.lepine.transfers.exceptions.I18nAble;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.UUID;

public class ShipmentNotPendingException extends IllegalArgumentException implements I18nAble {
    private final static String MESSAGE = "Shipment with uuid %s is not pending";
    private final static String CODE = "shipment.not_pending";
    private final UUID uuid;

    public ShipmentNotPendingException(UUID uuid) {
        super(String.format(MESSAGE, uuid));
        this.uuid = uuid;
    }

    @Override
    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(CODE, new Object[]{uuid}, locale);
    }
}
