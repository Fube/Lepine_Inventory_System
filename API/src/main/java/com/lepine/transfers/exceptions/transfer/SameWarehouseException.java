package com.lepine.transfers.exceptions.transfer;

import com.lepine.transfers.data.stock.Stock;
import com.lepine.transfers.exceptions.I18nAble;
import lombok.Getter;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.UUID;

public class SameWarehouseException extends RuntimeException implements I18nAble {
    private final static String MESSAGE = "Stock with UUID %s is already in warehouse with UUID %s";
    private final static String CODE = "transfer.same_warehouse";

    @Getter
    private final Stock stock;
    private final UUID warehouseUuid;

    public SameWarehouseException(Stock stock, UUID warehouseUuid) {
        super(String.format(MESSAGE, stock.getUuid(), warehouseUuid));
        this.warehouseUuid = warehouseUuid;
        this.stock = stock;
    }

    @Override
    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage(CODE, new Object[]{stock.getUuid(), warehouseUuid}, locale);
    }
}
