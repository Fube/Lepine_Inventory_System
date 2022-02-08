package com.lepine.transfers.validation.validators;

import com.lepine.transfers.validation.InEnum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<InEnum, String> {

    private Class<? extends Enum> value;

    @Override
    public void initialize(InEnum constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.value = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if(value == null) {
            return true;
        }

        try {
            Enum.valueOf(this.value, value);
            return true;
        } catch (Exception ignored){}

        return false;
    }
}
