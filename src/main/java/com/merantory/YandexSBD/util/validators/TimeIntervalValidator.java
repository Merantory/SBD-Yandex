package com.merantory.YandexSBD.util.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TimeIntervalValidator implements ConstraintValidator<TimeInterval, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // If date not equal format: 00:00-23:59
        if (!value.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]-((?!00:00)[01]?[0-9]|2[0-3]):[0-5][0-9]$")) return false;

        String leftDate = value.substring(0, 2) + value.substring(3,5);
        String rightDate = value.substring(6, 8) + value.substring(9, 11);
        int leftDateInt = Integer.parseInt(leftDate);
        int rightDateInt = Integer.parseInt(rightDate);

        return rightDateInt - leftDateInt > 0;
    }
}
