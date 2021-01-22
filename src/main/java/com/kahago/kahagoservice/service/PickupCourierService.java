package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.entity.*;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.PickupCourierEnum;
import com.kahago.kahagoservice.enummodel.PickupDetailEnum;
import com.kahago.kahagoservice.exception.InternalServerException;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.projection.PickupCourier;
import com.kahago.kahagoservice.model.request.ManifestPickupRequest;
import com.kahago.kahagoservice.model.response.PickupResponse;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.model.validate.FetchPickupRequest;
import com.kahago.kahagoservice.model.validate.OnFinishPickupRequest;
import com.kahago.kahagoservice.model.validate.OnReadyPickupRequest;
import com.kahago.kahagoservice.model.validate.OnWarehousePickupRequest;
import com.kahago.kahagoservice.repository.*;
import com.kahago.kahagoservice.util.Common;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Hendro yuwono
 */
@Slf4j
@Service
@Validated
public class PickupCourierService {

    @Autowired
    PickupOnGoingService pickupOnGoingService;

    @Value("${file.upload-dir}")
    private String uploadingDir;

    @Autowired
    private TCourierPickupRepo courierPickupRepo;

    @Autowired
    private TBookRepo bookRepo;

    @Autowired
    private PaymentService payService;

    @Autowired
    private TPaymentRepo payRepo;

    @Autowired
    private TPickupDetailRepo pickupDetailRepo;

    @Autowired
    private TPickupRepo pickupRepo;

    @Autowired
    private MUserRepo userRepo;

    public Page<PickupResponse> getPickup(FetchPickupRequest request) {
        Page<PickupCourier> dataPages = fetchByCondition(request);

        List<PickupResponse> data = dataPages.getContent().stream().map(this::toPickResponse).collect(Collectors.toList());
        return new PageImpl<>(data, dataPages.getPageable(), dataPages.getTotalElements());
    }

    private Page<PickupCourier> fetchByCondition(FetchPickupRequest request) {
        if (request.useSearchAndFilter()) {
            if (request.isFilter()) {
                return courierPickupRepo.findByStatusTimePickupAndCourierId(request.getStatusRequest(), request.getTimePickup(), request.getCourierId(), request.getPageable());
            } else {
                return courierPickupRepo.findByStatusCourierIdAndTerm(request.getStatusRequest(), request.getCourierId(), request.getTerm().toUpperCase(), request.getPageable());
            }
        } else {
            return courierPickupRepo.findByStatusInAndCourierId(request.getStatusRequest(), request.getCourierId(), request.getPageable());
        }
    }

    private PickupResponse toPickResponse(PickupCourier pc) {
        PickupResponse.Geometry geometry = PickupResponse.Geometry.builder()
                .type("Point")
                .coordinates(pc.getCoordinate())
                .build();

        return PickupResponse.builder()
                .id(pc.getId())
                .status(PickupCourierEnum.getByValue(pc.getStatus()).getKey())
                .dateOfAssign(pc.dateOfAssign())
                .manifest(pc.getManifest())
                .name(pc.getName())
                .phone(pc.getPhone())
                .address(pc.getAddress())
                .description(pc.getDescription())
                .location(geometry)
                .build();
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @SneakyThrows
    public SaveResponse doUpdatePickup(MultipartFile file, ManifestPickupRequest request, String courierId) {
        log.info("Request Json ==> " + Common.json2String(request));
        TPickupEntity pickup = pickupRepo.findByCode(request.getNoManifest());
        TPickupDetailEntity pickupDetail = pickupDetailRepo.findByBookIdBookingCode(request.getBookingCode()).get();
        if (pickupDetail != null) {
            throw new InternalServerException("Kode Booking sudah di assign");
        }
        String pathFile = null;
        if (file != null) {
            log.info("Request File ==> " + file.getOriginalFilename());
            pathFile = uploadingDir + file.getOriginalFilename();
            File files = new File(pathFile);
            file.transferTo(files);

        }

        MUserEntity userKurir = userRepo.findById(courierId).orElseThrow(() -> new NotFoundException("Kurir tidak Ditemukan"));
        String alasan = "Barang Titipan = " + userKurir.getName();
        TPaymentEntity payment = payRepo.findByBookingCodeIgnoreCaseContaining(request.getBookingCode());
        payService.createHistory(payment, PaymentEnum.getPaymentEnum(payment.getStatus()), PaymentEnum.ASSIGN_PICKUP, alasan);
        payment.setStatus(PaymentEnum.ASSIGN_PICKUP.getCode());
        pickupDetail = TPickupDetailEntity.builder()
                .bookId(payment)
                .createBy(courierId)
                .createDate(LocalDateTime.now())
                .pickupAddrId(payment.getPickupAddrId())
                .pickupId(pickup)
                .pathPic(pathFile)
                .status(PickupDetailEnum.IN_COURIER.getValue())
                .build();
        payRepo.save(payment);
        pickupDetailRepo.save(pickupDetail);
        return SaveResponse.builder()
                .saveStatus(1)
                .build();
    }


    @Transactional
    public void processToWarehouse(@Valid OnFinishPickupRequest request) {
        courierPickupRepo.findByStatusAndCourierId(PickupCourierEnum.FINISH_PICKUP.getValue(), request.getCourierId()).stream()
                .peek(courPickup -> {
                    courPickup.setStatus(PickupCourierEnum.OTW_WAREHOUSE.getValue());
                    courPickup.setToWarehouseTime(LocalDateTime.now());
                })
                .forEach(courierPickupRepo::save);
    }

    @Transactional
    public void cancelToWarehouse(@Valid OnWarehousePickupRequest request) {
        courierPickupRepo.findByStatusAndCourierId(PickupCourierEnum.OTW_WAREHOUSE.getValue(), request.getCourierId()).stream()
                .peek(courPick -> courPick.setStatus(PickupCourierEnum.FINISH_PICKUP.getValue()))
                .forEach(courierPickupRepo::save);
    }

    @Transactional
    public void updateOnGoingCourier(@Valid OnReadyPickupRequest pickupRequest) {
        TCourierPickupEntity data = courierPickupRepo.findById(pickupRequest.getId())
                .orElseThrow(NotFoundException::new);

        data.setStatus(PickupCourierEnum.OTW_CUSTOMER.getValue());
        data.setToCustomerTime(LocalDateTime.now());
        courierPickupRepo.save(data);
    }

}
