package com.kahago.kahagoservice.validation.validator;

import com.kahago.kahagoservice.repository.MUserRepo;
import com.kahago.kahagoservice.validation.UserMustExist;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Hendro yuwono
 */
public class UserMustExistValidator implements ConstraintValidator<UserMustExist, String> {

    @Autowired
    private MUserRepo userRepo;

    @Override
    public boolean isValid(String userId, ConstraintValidatorContext constraintValidatorContext) {
        return userRepo.existsById(userId);
    }
}
