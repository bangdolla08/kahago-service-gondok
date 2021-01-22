package com.kahago.kahagoservice.validation.validator;

import com.kahago.kahagoservice.model.projection.IncomingOfGood;
import com.kahago.kahagoservice.repository.MAreaRepo;
import com.kahago.kahagoservice.repository.MOfficeRepo;
import com.kahago.kahagoservice.repository.TBookRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestDetailRepo;
import com.kahago.kahagoservice.service.OfficeCodeService;
import com.kahago.kahagoservice.service.WarehouseService;
import com.kahago.kahagoservice.validation.IncomingMustMatch;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.*;

/**
 * @author Hendro yuwono
 */
public class IncomingMustMatchValidator extends Validator implements ConstraintValidator<IncomingMustMatch, IncomingMustMatch.Incoming> {

    @Autowired
    private MOfficeRepo officeRepo;

    @Autowired
    private MAreaRepo areaRepo;

    @Autowired
    private OfficeCodeService officeCodeService;

    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private TBookRepo bookRepo;

    @Autowired
    private TPickupOrderRequestDetailRepo pickupOrderRequestDetailRepo;

    @Override
    public boolean isValid(IncomingMustMatch.Incoming incoming, ConstraintValidatorContext context) {

        boolean existOffice = officeRepo.existsById(incoming.getOfficeCode());
        if (!existOffice) {
            constraintContext(context, "office code is not exists");
            return false;
        }


        boolean existQrCode = existingQrCode(incoming.getOfficeCode(), incoming.getQrCode());
        if (!existQrCode) {
            constraintContext(context, "qr_code is not exists");
            return false;
        }

        return true;
    }

    private boolean existingQrCode(String officeCode, String qrCode) {
        Set<Integer> cityId = officeCodeService.transformToCityIds(officeCode);

        IncomingOfGood pieces = bookRepo.findByStatusAndCityIdsAndQrCode(3, cityId, qrCode);
        if (Objects.isNull(pieces)) {
            pieces = pickupOrderRequestDetailRepo.findByStatusAndCityIdsAndQrCode(2, cityId, qrCode);
        }

        return !Objects.isNull(pieces);
    }
}
