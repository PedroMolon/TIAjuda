package com.projeto.tiajuda.configuration.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

public class RoleTypeValidator implements ConstraintValidator<ValidRoleType, String> {

    private static final List<String> ALLOWED_ROLES = Arrays.asList("CLIENT", "TECHNICIAN");

    @Override
    public void initialize(ValidRoleType constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        return ALLOWED_ROLES.stream()
                .anyMatch(allowedRole -> allowedRole.equalsIgnoreCase(value.trim()));
    }

}
