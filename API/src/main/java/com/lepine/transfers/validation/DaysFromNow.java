package com.lepine.transfers.validation;

import com.lepine.transfers.validation.validators.DaysFromNowValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DaysFromNowValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DaysFromNow {
    String message() default "Date must be %s days from now";
    long days();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
