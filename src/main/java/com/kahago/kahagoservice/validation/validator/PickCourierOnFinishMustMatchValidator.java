package com.kahago.kahagoservice.validation.validator;

import com.kahago.kahagoservice.enummodel.PickupCourierEnum;
import com.kahago.kahagoservice.repository.TCourierPickupRepo;
import com.kahago.kahagoservice.validation.PickCourierOnFinishMustMatch;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collections;
import java.util.List;

/**
 * @author Hendro yuwono
 */
public class PickCourierOnFinishMustMatchValidator extends Validator implements ConstraintValidator<PickCourierOnFinishMustMatch, PickCourierOnFinishMustMatch.PickupStatusOnFinish> {

    @Autowired
    private TCourierPickupRepo courierPickupRepo;

    @Override
    public boolean isValid(PickCourierOnFinishMustMatch.PickupStatusOnFinish value, ConstraintValidatorContext context) {

        boolean isStatusMatchInConstant = value.getStatus().equals("OTW_WAREHOUSE");
        if (!isStatusMatchInConstant) {
            constraintContext(context, "status " + value.getStatus() + " not valid");
            return false;
        }

        List<Integer> listOfFinish = Collections.singletonList(PickupCourierEnum.FINISH_PICKUP.getValue());
        boolean existInWarehouse = courierPickupRepo.existsByCourierIdAndStatusIn(value.getCourierId(), listOfFinish);
        if (!existInWarehouse) {
            constraintContext(context, "alamat pengambilan yang terselesaikan tidak ditemukan");
            return false;
        }

        List<Integer> listOfProcess = PickupCourierEnum.showInProcessPickup();
        boolean existOnProcess = courierPickupRepo.existsByCourierIdAndStatusIn(value.getCourierId(), listOfProcess);
        if (existOnProcess) {
            constraintContext(context, "gagal diproses, terdapat alamat pengambilan yang sedang berjalan");
            return false;
        }

        return true;
    }
}
