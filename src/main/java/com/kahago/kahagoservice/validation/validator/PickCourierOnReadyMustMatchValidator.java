package com.kahago.kahagoservice.validation.validator;

import com.kahago.kahagoservice.enummodel.PickupCourierEnum;
import com.kahago.kahagoservice.model.projection.PickupCourier;
import com.kahago.kahagoservice.repository.TCourierPickupRepo;
import com.kahago.kahagoservice.validation.PickCourierOnReadyMustMatch;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collections;
import java.util.List;

/**
 * @author Hendro yuwono
 */
public class PickCourierOnReadyMustMatchValidator extends Validator implements ConstraintValidator<PickCourierOnReadyMustMatch, PickCourierOnReadyMustMatch.PickupStatusOnReady> {

    @Autowired
    private TCourierPickupRepo courierPickupRepo;

    @Override
    public boolean isValid(PickCourierOnReadyMustMatch.PickupStatusOnReady value, ConstraintValidatorContext context) {
        boolean isExistInCourier = courierPickupRepo.existsByIdAndCourierId(value.getId(), value.getCourierId());
        if (!isExistInCourier) {
            constraintContext(context, "id " + value.getId() + " and courier " + value.getCourierId() + " not match");
            return false;
        }

        boolean isStatusMatchInConstant = value.getStatus().equals("OTW_CUSTOMER");
        if (!isStatusMatchInConstant) {
            constraintContext(context, value.getStatus() + " not valid");
            return false;
        }

        boolean hasFoundProcessStatusInTable = courierPickupRepo.existsByCourierIdAndStatusIn(value.getCourierId(), PickupCourierEnum.showInProcessPickup());
        if (hasFoundProcessStatusInTable) {
            constraintContext(context, "you have process pickups active");
            return false;
        }

        PickupCourier data = courierPickupRepo.findByIds(value.getId());
        if (!PickupCourierEnum.showInReadyPickup().contains(data.getStatus())) {
            constraintContext(context, "id " + value.getId() + " status is " + PickupCourierEnum.getByValue(data.getStatus()).getKey());
            return false;
        }

        List<Integer> warehouseStatus = Collections.singletonList(PickupCourierEnum.OTW_WAREHOUSE.getValue());
        boolean foundStatusInWarehouse = courierPickupRepo.existsByCourierIdAndStatusIn(value.getCourierId(), warehouseStatus);
        if (foundStatusInWarehouse) {
            constraintContext(context, "tidak dapat diproses, sedang menuju gudang");
            return false;
        }

        return true;
    }
}
