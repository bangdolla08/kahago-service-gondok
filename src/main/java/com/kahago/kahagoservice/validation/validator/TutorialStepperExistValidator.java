package com.kahago.kahagoservice.validation.validator;

import com.kahago.kahagoservice.repository.MTutorialRepo;
import com.kahago.kahagoservice.repository.MUserRepo;
import com.kahago.kahagoservice.validation.EmailIsExist;
import com.kahago.kahagoservice.validation.TutorialStepperIsExist;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Hendro yuwono
 */
public class TutorialStepperExistValidator implements ConstraintValidator<TutorialStepperIsExist, TutorialStepperIsExist.OnProcess> {

    @Autowired
    private MTutorialRepo tutorialRepo;

    @Override
    public boolean isValid(TutorialStepperIsExist.OnProcess value, ConstraintValidatorContext constraintValidatorContext) {
        return !tutorialRepo.existsByStepAndJenisTutorial(value.getStep(), value.getTypeOfTutorial());
    }
}
