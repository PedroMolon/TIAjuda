package com.projeto.tiajuda.configuration.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RoleTypeValidator.class)
@Documented
public @interface ValidRoleType {

    String message() default "Tipo de perfil inválido. Use 'CLIENT' ou 'TECHNICIAN'.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
