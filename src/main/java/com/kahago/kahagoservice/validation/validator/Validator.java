package com.kahago.kahagoservice.validation.validator;

import javax.validation.ConstraintValidatorContext;

/**
 * @author Hendro yuwono
 */
public abstract class Validator {

    void constraintContext(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
