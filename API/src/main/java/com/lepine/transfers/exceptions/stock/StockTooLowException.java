package com.lepine.transfers.exceptions.stock;

import com.lepine.transfers.exceptions.I18nAble;
import lombok.Getter;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.UUID;

import static java.lang.String.format;

@Getter
public class StockTooLowException extends RuntimeException implements I18nAble {

    private final static String MESSAGE = "Stock for %s is too low, wanted %d, have %d";
    private final static String CODE = "stock.too.low";

    private final int given;
    private final int wanted;
    private final UUID uuid;

    public StockTooLowException(UUID uuid, int given, int wanted) {
        super(format(MESSAGE, uuid, wanted, given));
        this.uuid = uuid;
        this.given = given;
        this.wanted = wanted;
    }

    @Override
    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(CODE, new Object[]{uuid, wanted, given}, locale);
    }
}
