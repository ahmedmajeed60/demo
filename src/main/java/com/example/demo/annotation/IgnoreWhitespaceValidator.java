package com.example.demo.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IgnoreWhitespaceValidator implements ConstraintValidator<IgnoreWhitespace, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Trim the input to ignore leading and trailing whitespace
        return value == null || value.trim().equals(value);
    }
}
