package com.lepine.transfers.config.controllers;

import com.lepine.transfers.exceptions.DuplicateResourceException;
import com.lepine.transfers.exceptions.I18nAble;
import com.lepine.transfers.exceptions.NotFoundException;
import com.lepine.transfers.exceptions.auth.DefaultLoginNotAllowedException;
import com.lepine.transfers.exceptions.shipment.ShipmentNotAcceptedException;
import com.lepine.transfers.exceptions.stock.StockTooLowException;
import com.lepine.transfers.exceptions.transfer.QuantityExceededException;
import com.lepine.transfers.exceptions.transfer.SameWarehouseException;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.time.ZoneOffset.UTC;
import static java.time.ZonedDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_INSTANT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalAdvice {

    private final ReloadableResourceBundleMessageSource messageSource;

    @Data
    @RequiredArgsConstructor
    public static class HTTPErrorMessage {

        private final int status;
        private String timestamp = now(UTC).format(ISO_INSTANT);
        private final String message;

        public HTTPErrorMessage(MessageSource messageSource, I18nAble i18nAble, Locale locale, int status) {
            this(status,i18nAble.getLocalizedMessage(messageSource, locale));
        }
    }

    public static class HTTPConstraintViolationError extends HTTPErrorMessage {

        @Getter
        private final Map<String, List<String>> errors;

        public HTTPConstraintViolationError(BindingResult bindingResult) {
            super(BAD_REQUEST.value(), "Invalid request");

            errors = new HashMap<>();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errors
                        .computeIfAbsent(fieldError.getField(), k -> new java.util.ArrayList<>())
                        .add(fieldError.getDefaultMessage());
            }
        }
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value = NOT_FOUND)
    public HTTPErrorMessage handleNotFoundException(NotFoundException e) {
        return new HTTPErrorMessage(NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = BAD_REQUEST)
    public HTTPConstraintViolationError handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new HTTPConstraintViolationError(e.getBindingResult());
    }

    @ExceptionHandler({
            DuplicateResourceException.class,
            SameWarehouseException.class,
            StockTooLowException.class,
            DefaultLoginNotAllowedException.class,
            QuantityExceededException.class,
            ShipmentNotAcceptedException.class,
    })
    @ResponseStatus(value = BAD_REQUEST)
    public HTTPErrorMessage handleGenericBusinessLogicRuntimeException(WebRequest req, RuntimeException e) {

        if(e instanceof I18nAble) {
            final HTTPErrorMessage debug = new HTTPErrorMessage(messageSource, (I18nAble) e, req.getLocale(), BAD_REQUEST.value());
            return debug;
        }

        return new HTTPErrorMessage(BAD_REQUEST.value(), e.getMessage());
    }
}