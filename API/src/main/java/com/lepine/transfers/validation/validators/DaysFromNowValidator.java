package com.lepine.transfers.validation.validators;

import com.lepine.transfers.utils.date.LocalDateUtils;
import com.lepine.transfers.validation.DaysFromNow;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DaysFromNowValidator implements ConstraintValidator<DaysFromNow, LocalDate> {

    private long days;

    @Override
    public void initialize(DaysFromNow constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        days = constraintAnnotation.days();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        // Check if X business days from now
        final LocalDate nDaysAhead = LocalDateUtils.businessDaysFromNow(days);
        return value.isAfter(nDaysAhead) || value.isEqual(nDaysAhead);
    }
}
