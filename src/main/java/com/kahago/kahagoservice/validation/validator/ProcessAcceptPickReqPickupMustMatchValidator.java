package com.kahago.kahagoservice.validation.validator;

import com.kahago.kahagoservice.model.projection.ItemPickup;
import com.kahago.kahagoservice.model.projection.PickupCourier;
import com.kahago.kahagoservice.model.projection.PiecesOfItem;
import com.kahago.kahagoservice.repository.TBookRepo;
import com.kahago.kahagoservice.repository.TCourierPickupRepo;
import com.kahago.kahagoservice.repository.TPickupDetailRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestDetailRepo;
import com.kahago.kahagoservice.validation.ProcessAcceptPickReqPickupMustMatch;
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
public class ProcessAcceptPickReqPickupMustMatchValidator extends Validator
        implements ConstraintValidator<ProcessAcceptPickReqPickupMustMatch, ProcessAcceptPickReqPickupMustMatch.OnAccept> {

    @Autowired
    private TCourierPickupRepo courierPickupRepo;

    @Autowired
    private TPickupDetailRepo pickupDetailRepo;

    @Autowired
    private TPickupOrderRequestDetailRepo pickupOrderRequestDetailRepo;

    @Autowired
    private TBookRepo bookRepo;

    @Override
    public boolean isValid(ProcessAcceptPickReqPickupMustMatch.OnAccept value, ConstraintValidatorContext context) {
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

        if (value.getPiecesId() != null) {
            List<PiecesOfItem> byPickupOrderId = pickupOrderRequestDetailRepo.findByPickupOrderId(value.getBookId());

            boolean exist = byPickupOrderId.stream().anyMatch(v -> v.getId().equals(value.getPiecesId()));
            if (!exist) {
                constraintContext(context, "pieces id " + value.getPiecesId() + " and booking id " + value.getBookId() + " not found");
                return false;
            }
        }

        boolean existsQrCodeReqPickup = pickupOrderRequestDetailRepo.existsByQrcodeExt(value.getQrCode());
        boolean existsQrCodeNormalBook = bookRepo.existsByQrCode(value.getQrCode());
        if (existsQrCodeNormalBook || existsQrCodeReqPickup) {
            constraintContext(context, "QrCode telah digunakan");
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
