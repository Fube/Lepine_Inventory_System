package com.lepine.transfers.exceptions.transfer;

import com.lepine.transfers.exceptions.I18nAble;
import com.lepine.transfers.exceptions.NotFoundException;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.UUID;

public class TransferNotFoundException extends NotFoundException implements I18nAble {
    private final static String MESSAGE = "Transfer with uuid %s not found";
    private final static String CODE = "transfer.not_found";
    private final UUID uuid;

    public TransferNotFoundException(final UUID uuid) {
        super(String.format(MESSAGE, uuid));
        this.uuid = uuid;
    }

    @Override
    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(CODE, new Object[]{uuid}, locale);
    }
}
