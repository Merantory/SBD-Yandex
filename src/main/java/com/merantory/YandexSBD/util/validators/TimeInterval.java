package com.merantory.YandexSBD.util.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Constraint(validatedBy = TimeIntervalValidator.class)
@Documented
public @interface TimeInterval {
    String message() default "Invalid format. Use follow format: 00:00-23:59";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
