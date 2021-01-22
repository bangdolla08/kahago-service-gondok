package com.kahago.kahagoservice.validation.validator;

import com.kahago.kahagoservice.validation.PickCourierOnWarehouseMustMatch;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Hendro yuwono
 */
public class PickCourierOnWarehouseMustMatchValidator extends Validator implements ConstraintValidator<PickCourierOnWarehouseMustMatch, PickCourierOnWarehouseMustMatch.PickupStatusOnWarehouse> {

    @Override
    public boolean isValid(PickCourierOnWarehouseMustMatch.PickupStatusOnWarehouse value, ConstraintValidatorContext context) {

        boolean isStatusMatchInConstant = value.getStatus().equals("CANCEL_TO_WAREHOUSE");
        if (!isStatusMatchInConstant) {
            constraintContext(context, "status " + value.getStatus() + " not valid");
            return false;
        }

        return true;
    }

}
