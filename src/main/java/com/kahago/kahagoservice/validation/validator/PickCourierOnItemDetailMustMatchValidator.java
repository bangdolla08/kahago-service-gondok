package com.kahago.kahagoservice.validation.validator;

import com.kahago.kahagoservice.enummodel.PickupCourierEnum;
import com.kahago.kahagoservice.model.projection.PickupCourier;
import com.kahago.kahagoservice.model.projection.ItemPickup;
import com.kahago.kahagoservice.repository.TCourierPickupRepo;
import com.kahago.kahagoservice.repository.TPickupDetailRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestDetailRepo;
import com.kahago.kahagoservice.validation.PickCourierOnItemDetailMustMatch;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Hendro yuwono
 */
public class PickCourierOnItemDetailMustMatchValidator extends Validator
        implements ConstraintValidator<PickCourierOnItemDetailMustMatch, PickCourierOnItemDetailMustMatch.ItemDetail> {

    @Autowired
    private TCourierPickupRepo courierPickupRepo;

    @Autowired
    private TPickupDetailRepo pickupDetailRepo;

    @Autowired
    private TPickupOrderRequestDetailRepo pickupOrderRequestDetailRepo;

    @Override
    public boolean isValid(PickCourierOnItemDetailMustMatch.ItemDetail value, ConstraintValidatorContext context) {
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

        List<ItemPickup> itemPickups = joiningReqPickupAndBooking(data);
        if (!isPickupContainIdBook(itemPickups, value.getBookId())) {
            constraintContext(context, "book id " + value.getBookId() + " not found in pickup id " + value.getId());
            return false;
        }

        if (!PickupCourierEnum.showInProcessPickup().contains(data.getStatus())) {
            constraintContext(context, "status pickup not valid");
            return false;
        }

        return true;
    }

    private boolean isPickupContainIdBook(List<ItemPickup> itemPickups, String bookId) {
        return itemPickups.stream().anyMatch(v -> v.getIdBooking().equalsIgnoreCase(bookId));
    }

    private List<ItemPickup> joiningReqPickupAndBooking(PickupCourier data) {
        List<ItemPickup> bookDetail = pickupDetailRepo.findBookingByPickIdAndPickAddrId(data.getPickupId(), data.getPickupAddressId());
        List<ItemPickup> reqPickDetail = pickupOrderRequestDetailRepo.findReqPickupByPickIdAndPickAddrId(data.getPickupId(), data.getPickupAddressId());
        return Stream.of(bookDetail, reqPickDetail).flatMap(Collection::stream).collect(Collectors.toList());
    }
}
