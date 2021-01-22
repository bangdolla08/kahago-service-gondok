package com.kahago.kahagoservice.validation.validator;

import com.kahago.kahagoservice.enummodel.PickupCourierEnum;
import com.kahago.kahagoservice.model.projection.PickupCourier;
import com.kahago.kahagoservice.repository.TCourierPickupRepo;
import com.kahago.kahagoservice.validation.PickCourierOnGoingMustMatch;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Hendro yuwono
 */
public class PickCourierOnGoingMustMatchValidator extends Validator
        implements ConstraintValidator<PickCourierOnGoingMustMatch, PickCourierOnGoingMustMatch.PickupStatusOnGoing> {

    @Autowired
    private TCourierPickupRepo courierPickupRepo;

    private final List<String> stPickups = Arrays.asList("READY_PICKUP", "OTW_TO_CUSTOMER", "PROCESS_PICKUP");

    @Override
    public boolean isValid(PickCourierOnGoingMustMatch.PickupStatusOnGoing value, ConstraintValidatorContext context) {
        boolean isExistInCourier = courierPickupRepo.existsByIdAndCourierId(value.getId(), value.getCourierId());
        if (!isExistInCourier) {
            constraintContext(context, "id " + value.getId() + " and courier " + value.getCourierId() + " not match");
            return false;
        }

        boolean isMatchInList = stPickups.contains(value.getStatus());
        if (!isMatchInList) {
            constraintContext(context, "status " + value.getStatus() + " not valid");
            return false;
        }

        PickupCourier data = courierPickupRepo.findByIdPickupCourier(value.getId());
        if (data == null) {
            constraintContext(context, "id " + value.getId() + " not found");
            return false;
        }

        if (!eliminatedPickupCourierEnum().contains(data.getStatus())) {
            constraintContext(context, "id " + value.getId() + " status is " + PickupCourierEnum.getByValue(data.getStatus()).getKey());
            return false;
        }

        if (value.getStatus().equals("READY_PICKUP") && StringUtils.isEmpty(value.getReason())) {
            constraintContext(context, "reason can't be null or empty");
            return false;
        }

        boolean isStatusSameInTable = data.getStatus().equals(PickupCourierEnum.getByKey(value.getStatus()).getValue());
        if (isStatusSameInTable) {
            constraintContext(context, "status " + value.getStatus() + " in database is same");
            return false;
        }

        PickupCourier existCourierPickup = courierPickupRepo.findByStatusInAndCourierId(PickupCourierEnum.showInProcessPickup(), value.getCourierId());
        if (!Objects.isNull(existCourierPickup)) {
            if (!existCourierPickup.getId().equals(value.getId())) {
                constraintContext(context, "masih ada proses pickup yang belum di selesaikan");
                return false;
            }
        }

        List<Integer> warehouseStatus = Collections.singletonList(PickupCourierEnum.OTW_WAREHOUSE.getValue());
        boolean foundStatusInWarehouse = courierPickupRepo.existsByCourierIdAndStatusIn(value.getCourierId(), warehouseStatus);
        if (foundStatusInWarehouse) {
            constraintContext(context, "tidak dapat diproses, sedang menuju gudang");
            return false;
        }

        return true;
    }

    private List<Integer> eliminatedPickupCourierEnum() {
        List<Integer> values = Arrays.asList(1, 2, 3);

        return PickupCourierEnum.all().stream().map(PickupCourierEnum::getValue).
                filter(values::contains).collect(Collectors.toList());
    }
}
