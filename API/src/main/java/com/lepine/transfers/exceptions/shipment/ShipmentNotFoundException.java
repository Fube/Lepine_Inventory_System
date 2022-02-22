package com.lepine.transfers.exceptions.shipment;

import com.lepine.transfers.exceptions.I18nAble;
import com.lepine.transfers.exceptions.NotFoundException;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.UUID;

public class ShipmentNotFoundException extends NotFoundException implements I18nAble {
    private static final String MESSAGE = "Shipment with uuid %s not found";
    private static final String MESSAGE_NO_UUID = "Shipment not found";
    private static final String CODE = "shipment.not_found";
    private static final String CODE_NO_UUID = "shipment.not_found.no_uuid";
    private final UUID uuid;

    public ShipmentNotFoundException(UUID uuid) {
        super(String.format(MESSAGE, uuid));
        this.uuid = uuid;
    }

    public ShipmentNotFoundException() {
        super(MESSAGE_NO_UUID);
        this.uuid = null;
    }

    @Override
    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        if(uuid == null) {
            return messageSource.getMessage(CODE_NO_UUID, null, locale);
        }
        return messageSource.getMessage(CODE, new Object[]{uuid}, locale);
    }
}
