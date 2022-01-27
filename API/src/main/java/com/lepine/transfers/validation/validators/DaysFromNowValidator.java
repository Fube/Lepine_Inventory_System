package com.lepine.transfers.validation.validators;

import com.lepine.transfers.utils.date.ZonedDateUtils;
import com.lepine.transfers.validation.DaysFromNow;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.ZonedDateTime;

public class DaysFromNowValidator implements ConstraintValidator<DaysFromNow, ZonedDateTime> {

    private long days;

    @Override
    public void initialize(DaysFromNow constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        days = constraintAnnotation.days();
    }

    @Override
    public boolean isValid(ZonedDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        // Check if X business days from now
        final ZonedDateTime nDaysAhead = ZonedDateUtils.businessDaysFromNow(days);
        return value.isAfter(nDaysAhead) || value.isEqual(nDaysAhead);
    }
}
