package com.kahago.kahagoservice.validation;

import com.kahago.kahagoservice.validation.validator.IncomingMustMatchValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Hendro yuwono
 */

@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(value = RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {
        IncomingMustMatchValidator.class
})
public @interface IncomingMustMatch {
    String message() default "your parameter or data not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] path() default {};

    interface Incoming {
        String getOfficeCode();

        String getQrCode();
    }
}
