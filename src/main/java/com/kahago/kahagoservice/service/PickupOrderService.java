package com.kahago.kahagoservice.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.model.request.RequestBook;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.kahago.kahagoservice.entity.TPickupDetailEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestDetailEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestEntity;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.PickupDetailEnum;
import com.kahago.kahagoservice.enummodel.RequestPickupEnum;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.response.BookDataResponse;
import com.kahago.kahagoservice.model.response.ListBookingCompleteResponse;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.repository.TPickupDetailRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestDetailRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestRepo;

@Service
public class PickupOrderService {
    @Autowired
    private TPickupOrderRequestDetailRepo pickupOrderDetailRepo;
    @Autowired
    private TPickupOrderRequestRepo pickupOrderRepo;
    @Autowired
    private TPickupDetailRepo pickupDetailRepo;
    @Autowired
    private TPaymentRepo paymentRepo;
    @Autowired
    private PaymentService paymentService;

    public List<BookDataResponse> getlistDetailOrder(String code, RequestBook request) {
        TPickupOrderRequestEntity pickup = pickupOrderRepo.findById(code).orElseThrow(() -> new NotFoundException("Pickup Order Id Tidak Ditemukan"));
        List<TPickupOrderRequestDetailEntity> lsDetail = pickupOrderDetailRepo.findAllByOrderRequestEntity(pickup);
        //edit @Ibnu 15/07/2020 bug detail pickup request
        List<TPickupOrderRequestDetailEntity> lsBelomBook = pickupOrderDetailRepo.findByIdAndStatusOrQrCode(request.getStatus(),request.getQrCode(),request.getVendorCode(),pickup,"");
        lsDetail=lsDetail.stream().filter(x->x.getBookCode()!=null&&!x.getBookCode().isEmpty()).collect(Collectors.toList());
        List<BookDataResponse> lsresp = new ArrayList<>();
        if(request.getStatus() == null) {
        	lsresp = lsBelomBook.stream().map(this::orderReqDto).collect(Collectors.toList());
        }        
        if(lsDetail.size() > 0) {
        	List<String> listCodeBooking=lsDetail.stream().map(this::toGetCodeBooking).collect(Collectors.toList());
            List<TPaymentEntity> paymentEntityList=paymentRepo.findAllByUserIdAndStatusInAndBookingCode(
                    request.getUserId(),request.getStatus(),request.getQrCode(),request.getFilter(),
                    null,request.getVendorCode(),request.getOrigin(),listCodeBooking);
            List<BookDataResponse> responseList=paymentEntityList.stream().map(paymentService::toBookDataResponse).collect(Collectors.toList());
            lsresp.addAll(responseList);
        }
        //end
        return lsresp;
    }

    private String toGetCodeBooking(TPickupOrderRequestDetailEntity entity) {
        return entity.getBookCode();
    }

    private BookDataResponse orderReqDto(TPickupOrderRequestDetailEntity entity) {
        return BookDataResponse.builder()
                .userId(entity.getOrderRequestEntity().getUserEntity().getUserId())
                .stt("")
                .bookingCode(entity.getBookCode() == null ? "-" : entity.getBookCode())
                .productName(entity.getProductSwitcherEntity() == null ? "-" : entity.getProductSwitcherEntity().getName() + " " + entity.getProductSwitcherEntity().getSwitcherEntity().getDisplayName())
                .receiverAddress("")
                .receiverName("")
                .shipperName(entity.getOrderRequestEntity().getUserEntity().getName())
                .status(entity.getStatus().toString())
                .statusDesc(RequestPickupEnum.getPaymentEnum(entity.getStatus()).toString())
                .dateTrx(entity.getCreateDate().toLocalDate())
                .timeTrx(entity.getCreateDate().format(DateTimeFormatter.ofPattern("HH:mm")))
                .vendorUrlImage("")
                .vendorName("")
                .destination("")
                .dimension("")
                .qty(entity.getQty())
                .weight(entity.getWeight() == null ? Long.valueOf("0") : entity.getWeight().longValue())
                .volumeWeight(Long.valueOf("0"))
                .isBooking(false)
                .qrcode(entity.getQrCode()==null?"":entity.getQrCode())
                .qrcodeExt(entity.getQrcodeExt()==null?"":entity.getQrcodeExt())
                .goodDesc("")
                .userPhone(entity.getOrderRequestEntity().getUserEntity().getHp())
                .senderPhone(entity.getOrderRequestEntity().getUserEntity().getHp())
                .receiverPhone("")
                .build();
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public SaveResponse doCancelPickup(String code, String reason, String userlogin) {
        List<Integer> lsStatus = Arrays.asList(RequestPickupEnum.REQUEST.getValue(), RequestPickupEnum.ASSIGN_PICKUP.getValue());
        TPickupOrderRequestEntity pickup = pickupOrderRepo.findByPickupOrderIdAndStatusIn(code, lsStatus).orElseThrow(() -> new NotFoundException("Pickup Order Id Tidak Ditemukan"));
        if (RequestPickupEnum.getPaymentEnum(pickup.getStatus()) == RequestPickupEnum.ASSIGN_PICKUP) {
            TPickupDetailEntity pickupDetail = pickupDetailRepo.findByPickupOrderRequestEntity(pickup);
            pickupDetail.setStatus(PickupDetailEnum.HISTORY.getValue());
            pickupDetailRepo.save(pickupDetail);
        }
        pickup.setStatus(RequestPickupEnum.CANCEL_DETAIL.getValue());
        pickup.setReason(reason);
        pickup.setUpdateBy(userlogin);
        pickup.setUpdateDate(LocalDateTime.now());
        List<TPickupOrderRequestDetailEntity> lsDetail = pickupOrderDetailRepo.findAllByOrderRequestEntity(pickup);
        lsDetail.forEach(o -> o.setStatus(RequestPickupEnum.CANCEL_DETAIL.getValue()));
        pickupOrderDetailRepo.saveAll(lsDetail);
        pickupOrderRepo.save(pickup);

        return SaveResponse.builder()
                .saveStatus(1)
                .saveInformation("Berhasil")
                .build();
    }
}
