package com.kahago.kahagoservice.validation.validator;

import com.kahago.kahagoservice.model.projection.PickupCourier;
import com.kahago.kahagoservice.model.projection.ItemPickup;
import com.kahago.kahagoservice.repository.TCourierPickupRepo;
import com.kahago.kahagoservice.repository.TPickupDetailRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestDetailRepo;
import com.kahago.kahagoservice.validation.ProcessCancelPickMustMatch;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Hendro yuwono
 */
public class ProcessCancelPickMustMatchValidator extends Validator
        implements ConstraintValidator<ProcessCancelPickMustMatch, ProcessCancelPickMustMatch.OnCancel> {

    @Autowired
    private TCourierPickupRepo courierPickupRepo;

    @Autowired
    private TPickupDetailRepo pickupDetailRepo;

    @Autowired
    private TPickupOrderRequestDetailRepo pickupOrderRequestDetailRepo;

    private List<String> status = Collections.singletonList("REJECT_ITEM");

    @Override
    public boolean isValid(ProcessCancelPickMustMatch.OnCancel value, ConstraintValidatorContext context) {
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

        if (!status.contains(value.getStatus().toUpperCase())) {
            constraintContext(context, "status not valid");
            return false;
        }

        if (value.getStatus().equals("REJECT_ITEM") && Strings.isEmpty(value.getReason())) {
            constraintContext(context, "reason can not be empty");
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
