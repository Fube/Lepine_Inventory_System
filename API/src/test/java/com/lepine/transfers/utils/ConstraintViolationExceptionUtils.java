package com.lepine.transfers.utils;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;
import java.util.stream.Collectors;

public class ConstraintViolationExceptionUtils {

    public static Set<String> extractMessages(ConstraintViolationException e) {
        return e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }
}
