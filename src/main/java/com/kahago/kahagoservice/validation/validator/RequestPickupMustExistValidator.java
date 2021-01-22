package com.kahago.kahagoservice.validation.validator;

import com.kahago.kahagoservice.entity.TPickupOrderRequestEntity;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.projection.ItemPickup;
import com.kahago.kahagoservice.model.projection.PickupCourier;
import com.kahago.kahagoservice.repository.TCourierPickupRepo;
import com.kahago.kahagoservice.repository.TPickupDetailRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestDetailRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestRepo;
import com.kahago.kahagoservice.validation.RequestPickupMustExist;
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
public class RequestPickupMustExistValidator extends Validator
        implements ConstraintValidator<RequestPickupMustExist, RequestPickupMustExist.OnAccept> {

    @Autowired
    private TCourierPickupRepo courierPickupRepo;

    @Autowired
    private TPickupDetailRepo pickupDetailRepo;

    @Autowired
    private TPickupOrderRequestRepo pickupOrderRequestRepo;

    @Autowired
    private TPickupOrderRequestDetailRepo pickupOrderRequestDetailRepo;

    @Override
    public boolean isValid(RequestPickupMustExist.OnAccept value, ConstraintValidatorContext context) {
        PickupCourier data = courierPickupRepo.findByIdPickupCourier(value.getId());

        List<ItemPickup> itemPickups = joiningReqPickupAndBooking(data);
        if (!isPickupContainIdBook(itemPickups, value.getBookId())) {
            constraintContext(context, "book id " + value.getBookId() + " not found in pickup id " + value.getId());
            return false;
        }

        TPickupOrderRequestEntity orderRequest = pickupOrderRequestRepo.findById(value.getBookId()).orElseThrow(NotFoundException::new);
        if (!orderRequest.getCreateBy().equals(value.getCourierId())) {
            constraintContext(context, "book id " + value.getBookId() + " not authorize for user " + value.getCourierId());
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
