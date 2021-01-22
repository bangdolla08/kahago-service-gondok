package com.kahago.kahagoservice.validation;

import com.kahago.kahagoservice.validation.validator.PickCourierOnItemDetailMustMatchValidator;

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
        PickCourierOnItemDetailMustMatchValidator.class
})
public @interface PickCourierOnItemDetailMustMatch {
    String message() default "your parameter or data not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] path() default {};

    interface ItemDetail {
        Integer getId();

        String getBookId();

        String getCourierId();
    }
}
