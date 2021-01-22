package com.kahago.kahagoservice.validation.validator;

import com.kahago.kahagoservice.repository.MUserRepo;
import com.kahago.kahagoservice.validation.EmailIsExist;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Hendro yuwono
 */
public class EmailIsExistValidator implements ConstraintValidator<EmailIsExist, String> {

    @Autowired
    private MUserRepo userRepo;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        return !userRepo.existsById(email);
    }
}
