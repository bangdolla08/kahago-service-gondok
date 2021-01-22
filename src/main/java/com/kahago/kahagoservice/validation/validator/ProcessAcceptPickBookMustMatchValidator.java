package com.kahago.kahagoservice.validation.validator;

import com.kahago.kahagoservice.entity.TPickupDetailEntity;
import com.kahago.kahagoservice.enummodel.PickupDetailEnum;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.projection.ItemPickup;
import com.kahago.kahagoservice.model.projection.PickupCourier;
import com.kahago.kahagoservice.repository.*;
import com.kahago.kahagoservice.validation.ProcessAcceptPickBookMustMatch;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Hendro yuwono
 */
public class ProcessAcceptPickBookMustMatchValidator extends Validator
        implements ConstraintValidator<ProcessAcceptPickBookMustMatch, ProcessAcceptPickBookMustMatch.OnAccept> {

    @Autowired
    private TCourierPickupRepo courierPickupRepo;

    @Autowired
    private TPickupDetailRepo pickupDetailRepo;

    @Autowired
    private TPickupOrderRequestDetailRepo pickupOrderRequestDetailRepo;

    @Autowired
    private TBookRepo bookRepo;

    @Autowired
    private TPaymentRepo paymentRepo;

    @Override
    public boolean isValid(ProcessAcceptPickBookMustMatch.OnAccept value, ConstraintValidatorContext context) {
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

        if (!isRequestPickup(itemPickups)) {
            if (Objects.isNull(value.getPartId())) {
                constraintContext(context, "part id can not be null");
                return false;
            } else {
                boolean existPart = bookRepo.existsByBookingCodeAndSeqid(value.getBookId(), value.getPartId());
                if (!existPart) {
                    constraintContext(context, "part id " + value.getPartId() + " and booking id " + value.getBookId() + "is not match");
                    return false;
                }
            }
        }

        boolean existsQrCodeReqPickup = pickupOrderRequestDetailRepo.existsByQrcodeExt(value.getQrCode());
        boolean existsQrCodeNormalBook = bookRepo.existsByQrCode(value.getQrCode());
        if (existsQrCodeNormalBook || existsQrCodeReqPickup) {
            constraintContext(context, "QrCode telah digunakan");
            return false;
        }

        TPickupDetailEntity pickupDetail = pickupDetailRepo.findByBookIdBookingCode(value.getBookId()).orElseThrow(NotFoundException::new);
        List<Integer> listOfStatus = Arrays.asList(PickupDetailEnum.ASSIGN_PICKUP.getValue(), PickupDetailEnum.IN_COURIER.getValue(), PickupDetailEnum.REJECTED_PICKUP.getValue());
        if (!listOfStatus.contains(pickupDetail.getStatus())) {
            constraintContext(context, "data telah di prosess sebelum nya");
            return false;
        }

        return true;
    }

    private boolean isRequestPickup(List<ItemPickup> itemPickups) {
        return itemPickups.stream().anyMatch(ItemPickup::isRequestPickup);
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
