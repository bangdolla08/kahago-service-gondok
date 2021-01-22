package com.kahago.kahagoservice.validation;

import com.kahago.kahagoservice.validation.validator.TutorialStepperExistValidator;

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
@Constraint(validatedBy = TutorialStepperExistValidator.class)
public @interface TutorialStepperIsExist {

    String message() default "step is exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] path() default {};

    interface OnProcess {
        Integer getStep();

        Integer getTypeOfTutorial();
    }

}
