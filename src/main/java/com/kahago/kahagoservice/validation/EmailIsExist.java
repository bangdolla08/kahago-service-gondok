package com.kahago.kahagoservice.validation;

/**
 * @author Hendro yuwono
 */

import com.kahago.kahagoservice.validation.validator.EmailIsExistValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(value = RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailIsExistValidator.class)
public @interface EmailIsExist {

    String message() default "Email is exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] path() default {};

}
