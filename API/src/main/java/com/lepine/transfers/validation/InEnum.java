package com.lepine.transfers.validation;

import com.lepine.transfers.validation.validators.EnumValidator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EnumValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface InEnum {

    Class<? extends Enum<?>> value();

    String message() default "Invalid value";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
