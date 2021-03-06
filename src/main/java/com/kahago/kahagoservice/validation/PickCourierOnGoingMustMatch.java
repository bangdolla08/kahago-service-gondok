package com.kahago.kahagoservice.validation;

import com.kahago.kahagoservice.validation.validator.PickCourierOnGoingMustMatchValidator;

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
        PickCourierOnGoingMustMatchValidator.class
})
public @interface PickCourierOnGoingMustMatch {
    String message() default "your parameter or data not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] path() default {};

    interface PickupStatusOnGoing {
        Integer getId();

        String getStatus();

        String getCourierId();

        String getReason();
    }
}
