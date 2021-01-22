package com.kahago.kahagoservice.validation.validator;

import com.kahago.kahagoservice.enummodel.PickupCourierEnum;
import com.kahago.kahagoservice.model.projection.PickupCourier;
import com.kahago.kahagoservice.repository.TCourierPickupRepo;
import com.kahago.kahagoservice.validation.PickTitipanNonBookingMustMatch;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Hendro yuwono
 */
public class PickTitipanNonBookingMustMatchValidator extends Validator
        implements ConstraintValidator<PickTitipanNonBookingMustMatch, PickTitipanNonBookingMustMatch.OnAccept> {

    @Autowired
    private TCourierPickupRepo courierPickupRepo;

    @Override
    public boolean isValid(PickTitipanNonBookingMustMatch.OnAccept value, ConstraintValidatorContext context) {
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

        if (!eliminatedPickupCourierEnum().contains(data.getStatus())) {
            constraintContext(context, "id " + value.getId() + " status is " + PickupCourierEnum.getByValue(data.getStatus()).getKey());
            return false;
        }

        return true;
    }

    private List<Integer> eliminatedPickupCourierEnum() {
        List<Integer> values = Arrays.asList(4, 6);

        return PickupCourierEnum.all().stream().map(PickupCourierEnum::getValue).
                filter(values::contains).collect(Collectors.toList());
    }
}
