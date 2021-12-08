package com.lepine.transfers.config.controllers;

import com.lepine.transfers.exceptions.NotFoundException;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static java.time.ZoneOffset.UTC;
import static java.time.ZonedDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_INSTANT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class GlobalAdvice {

    @Data
    @RequiredArgsConstructor
    public static class HTTPErrorMessage {

        private final int status;
        private String timestamp = now( UTC ).format( ISO_INSTANT );
        private final String message;
    }

    public static class HTTPConstraintViolationError extends HTTPErrorMessage {

        @Getter
        private final Map<String, String> errors;

        public HTTPConstraintViolationError(BindingResult bindingResult) {
            super(BAD_REQUEST.value(), "Invalid request");

            errors = new HashMap<>();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
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
}