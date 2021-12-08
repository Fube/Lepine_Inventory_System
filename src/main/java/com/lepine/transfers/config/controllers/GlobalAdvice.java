package com.lepine.transfers.config.controllers;

import com.lepine.transfers.exceptions.NotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static java.time.ZoneOffset.UTC;
import static java.time.ZonedDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_INSTANT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class GlobalAdvice {

    @Data
    @RequiredArgsConstructor
    public class HTTPErrorMessage {

        private final int statusCode;
        private String timestamp = now( UTC ).format( ISO_INSTANT );
        private final String message;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value = NOT_FOUND)
    public HTTPErrorMessage handleNotFoundException(NotFoundException e) {
        return new HTTPErrorMessage(NOT_FOUND.value(), e.getMessage());
    }
}
