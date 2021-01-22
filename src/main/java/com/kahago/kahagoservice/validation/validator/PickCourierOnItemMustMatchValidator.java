package com.kahago.kahagoservice.validation.validator;

import com.kahago.kahagoservice.model.projection.PickupCourier;
import com.kahago.kahagoservice.repository.TCourierPickupRepo;
import com.kahago.kahagoservice.validation.PickCourierOnItemMustMatch;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Hendro yuwono
 */
public class PickCourierOnItemMustMatchValidator extends Validator
        implements ConstraintValidator<PickCourierOnItemMustMatch, PickCourierOnItemMustMatch.ItemPickup> {

    @Autowired
    private TCourierPickupRepo courierPickupRepo;

    @Override
    public boolean isValid(PickCourierOnItemMustMatch.ItemPickup value, ConstraintValidatorContext context) {
        boolean isExistInCourier = courierPickupRepo.existsByIdAndCourierId(value.getId(), value.getCourierId());
        if (!isExistInCourier) {
            constraintContext(context, "id " + value.getId() + " and courier " + value.getCourierId() + " not match");
            return false;
        }

        PickupCourier data = courierPickupRepo.findByIdPickupCourier(value.getId());
        if (data == null) {
            constraintContext(context, "id " + value.getId() + " not found");
            return false;
        }
        return true;
    }
}
