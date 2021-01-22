package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.entity.*;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.PickupDetailEnum;
import com.kahago.kahagoservice.enummodel.RequestPickupEnum;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.projection.ItemPickup;
import com.kahago.kahagoservice.model.projection.PickupCourier;
import com.kahago.kahagoservice.model.response.NewPickupResponse;
import com.kahago.kahagoservice.model.response.PickupItemDetailResponse;
import com.kahago.kahagoservice.model.response.PickupItemResponse;
import com.kahago.kahagoservice.model.response.PickupTitipanResponse;
import com.kahago.kahagoservice.model.validate.*;
import com.kahago.kahagoservice.repository.*;
import com.kahago.kahagoservice.util.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Hendro yuwono
 */
@Service
@Validated
public class PickupTitipanService {

    @Value("${kahago.host}")
    private String host;

    @Autowired
    private TPickupOrderRequestRepo pickupOrderRequestRepo;

    @Autowired
    private TCourierPickupRepo courierPickupRepo;

    @Autowired
    private TPickupAddressRepo pickupAddressRepo;

    @Autowired
    private TPickupRepo pickupRepo;

    @Autowired
    private RequestPickUpService requestPickUpService;

    @Autowired
    private TPickupDetailRepo pickupDetailRepo;

    @Autowired
    private TPaymentRepo paymentRepo;

    @Autowired
    private TBookRepo bookRepo;

    @Autowired
    private PaymentService payService;

    @Autowired
    private HistoryTransactionService historyTransactionService;

    @Autowired
    private PickupOnGoingService pickupOnGoingService;

    @Transactional
    public NewPickupResponse newPickup(@Valid OnHeaderTitipanRequest request) {
        PickupCourier pickupCourierEn = courierPickupRepo.findByIds(request.getId());
        TPickupEntity pickupEn = pickupRepo.findById(pickupCourierEn.getPickupId()).orElseThrow(NotFoundException::new);
        TPickupAddressEntity pickAddrEn = pickupAddressRepo.findById(pickupCourierEn.getPickupAddressId()).orElseThrow(NotFoundException::new);

        TPickupOrderRequestEntity por = saveNewPickupOrder(request, pickupEn, pickAddrEn);
        saveNewPickupDetail(request, pickupEn, pickAddrEn, por);
        saveNewHistoryTransaction(por, request);

        return NewPickupResponse.builder().idBook(por.getPickupOrderId()).build();
    }

    public PickupTitipanResponse getDataBook(Integer id) {
        String userId = courierPickupRepo.findCustomerIdById(id);
        PickupCourier pickupCourier = courierPickupRepo.findByIds(id);

        List<Integer> statusOfPayments = Collections.singletonList(PaymentEnum.REQUEST.getCode());
        List<ItemPickup> listOfItem = paymentRepo.findByStatusAndUserId(statusOfPayments, userId);

        List<PickupTitipanResponse.TimePickup> timePickups = new ArrayList<>();
        if (listOfItem.size() != 0) {
            timePickups.addAll(groupItemByPickupTime(listOfItem));
        }

        String URL_REPORT_MANIFEST = "api/report/manifest/pickup?id=";
        return PickupTitipanResponse.builder()
                .nameOfAccount(pickupCourier.getName())
                .totalItem(listOfItem.size())
                .phone(pickupCourier.getPhone())
                .reportManifest(host + URL_REPORT_MANIFEST + pickupCourier.getManifest())
                .pickups(timePickups)
                .build();
    }


    private List<PickupTitipanResponse.TimePickup> groupItemByPickupTime(List<ItemPickup> listOfItem) {
        Map<String, List<PickupItemResponse.Detail>> listOfDetail = listOfItem
                .stream().map(this::toDetailPickupResponse)
                .collect(Collectors.groupingBy(PickupItemResponse.Detail::getTimePickup));

        return toTimePickupResponse(listOfDetail);
    }

    private List<PickupTitipanResponse.TimePickup> toTimePickupResponse(Map<String, List<PickupItemResponse.Detail>> listOfDetail) {
        List<PickupTitipanResponse.TimePickup> timePickups = new ArrayList<>();
        listOfDetail.forEach((key, value) -> {
            PickupTitipanResponse.TimePickup listOfPickup = PickupTitipanResponse.TimePickup.builder()
                    .pickupTime(key)
                    .item(value)
                    .build();

            timePickups.add(listOfPickup);
        });
        return timePickups;
    }

    private PickupItemResponse.Detail toDetailPickupResponse(ItemPickup ip) {
        return PickupItemResponse.Detail.builder()
                .idBook(ip.getIdBooking())
                .sender(ip.getSender())
                .receiver(ip.getReceiver())
                .receiverAddress(ip.getReceiverAddress())
                .totalItem(ip.getTotalItem())
                .weight(ip.getWeight())
                .imageVendor(ip.getPathImage())
                .isRequestPickup(false)
                .timePickup(ip.getPickupTime())
                .build();
    }

    private void saveNewPickupDetail(OnHeaderTitipanRequest request, TPickupEntity pickupEn, TPickupAddressEntity pickAddrEn, TPickupOrderRequestEntity por) {
        TPickupDetailEntity pd = TPickupDetailEntity.builder()
                .pickupId(pickupEn)
                .pickupOrderRequestEntity(por)
                .pickupAddrId(pickAddrEn)
                .status(PickupDetailEnum.IN_COURIER.getValue())
                .createBy(request.getCourierId())
                .createDate(LocalDateTime.now())
                .build();

        pickupDetailRepo.save(pd);
    }

    private void saveNewHistoryTransaction(TPickupOrderRequestEntity por, OnHeaderTitipanRequest request) {
        historyTransactionService.historyRequestPickup(por, null, RequestPickupEnum.IN_COURIER.getValue(), request.getCourierId(), "");
    }

    private TPickupOrderRequestEntity saveNewPickupOrder(OnHeaderTitipanRequest request, TPickupEntity pickupEn, TPickupAddressEntity pickAddrEn) {
        TPickupOrderRequestEntity por = TPickupOrderRequestEntity.builder()
                .pickupOrderId(requestPickUpService.createOrderNumb())
                .userEntity(pickAddrEn.getUserId())
                .pickupAddressEntity(pickAddrEn)
                .createBy(request.getCourierId())
                .createDate(LocalDateTime.now())
                .orderDate(LocalDate.now())
                .pickupTimeEntity(pickupEn.getTimePickupId())
                .qty(0)
                .status(RequestPickupEnum.IN_COURIER.getValue())
                .build();

        return pickupOrderRequestRepo.save(por);
    }

    @Transactional
    public void deletePickup(@Valid OnDeleteHeaderTitipanRequest request) {
        TPickupOrderRequestEntity por = pickupOrderRequestRepo.findById(request.getBookId()).orElseThrow(NotFoundException::new);
        TPickupDetailEntity pd = pickupDetailRepo.findByPickupOrderRequestEntity(por);

        pickupOrderRequestRepo.delete(por);
        pickupDetailRepo.delete(pd);
    }

    @Transactional
    public void addDetailPickup(@Valid OnDetailTitipanRequest request) {
        PickupCourier data = courierPickupRepo.findByIds(request.getId());
        String path = pickupOnGoingService.extractImageToFileSystem(request.getImage(), request.getBookId(), data);
        TPickupOrderRequestEntity por = pickupOrderRequestRepo.findById(request.getBookId()).orElseThrow(NotFoundException::new);

        createPickupOrderReqDetail(request, path, por);
    }

    private void createPickupOrderReqDetail(OnDetailTitipanRequest request, String path, TPickupOrderRequestEntity por) {
        TPickupOrderRequestDetailEntity detailEntity = TPickupOrderRequestDetailEntity.builder()
                .qty(1)
                .orderRequestEntity(por)
                .qrcodeExt(request.getQrCode())
                .status(RequestPickupEnum.IN_COURIER.getValue())
                .updateDate(LocalDateTime.now())
                .updateBy(request.getCourierId())
                .createDate(LocalDateTime.now())
                .createBy(request.getCourierId())
                .qrCode(Common.gerQrCode())
                .amount(BigDecimal.ZERO)
                .isPay(0)
                .pathPic(path)
                .build();

        por.getPickupOrderRequestDetails().add(detailEntity);
        por.setQty(por.getPickupOrderRequestDetails().size());

        TPickupOrderRequestDetailEntity temp = pickupOrderRequestRepo.save(por)
                .getPickupOrderRequestDetails()
                .stream().reduce((first, second) -> second)
                .orElseThrow(NotFoundException::new);

        historyTransactionService.historyRequestPickup(por, temp, RequestPickupEnum.IN_COURIER.getValue(), request.getCourierId(), "");
    }

    public List<PickupItemDetailResponse> itemDetails(String bookId) {
        boolean bookExist = bookRepo.existsByBookingCode(bookId);
        if (!bookExist) {
            return new ArrayList<>();
        } else {
            return bookRepo.findByBookingCode(bookId).stream().map(PickupOnGoingService::toPickupItemDetailResponse).collect(Collectors.toList());
        }
    }

    private void findByBookingAndSavePieces(OnBookingTitipanRequest request, String path) {
        TBookEntity book = bookRepo.findById(request.getPartId()).orElseThrow(NotFoundException::new);
        book.setQrCode(request.getQrCode());
        book.setImages(path);
        book.setStatus(PaymentEnum.PICKUP_BY_KURIR.getCode());

        bookRepo.save(book);
    }

    @Transactional
    public void doUpdatePickup(@Valid OnBookingTitipanRequest request) {
        PickupCourier pickupCourier = courierPickupRepo.findByIds(request.getId());
        TPickupEntity pickup = pickupRepo.findById(pickupCourier.getPickupId()).orElseThrow(NotFoundException::new);
        TPaymentEntity payment = updateAndSavePayment(request, pickup);
        String pathFileDestination = pickupOnGoingService.extractImageToFileSystem(request.getImage(), payment.getBookingCode(), pickupCourier);

        findByBookingAndSavePieces(request, pathFileDestination);
        savePickupDetailAndHistory(request, pickup, payment);
    }

    private void savePickupDetailAndHistory(OnBookingTitipanRequest request, TPickupEntity pickup, TPaymentEntity payment) {
        TPickupDetailEntity pickupDetail = TPickupDetailEntity.builder()
                .bookId(payment)
                .createBy(request.getCourierId())
                .createDate(LocalDateTime.now())
                .pickupAddrId(payment.getPickupAddrId())
                .pickupId(pickup)
                .status(PickupDetailEnum.IN_COURIER.getValue())
                .build();

        pickupDetailRepo.save(pickupDetail);

        String reason = "Barang titipan dengan kode booking " + payment.getBookingCode() + "telah diambil oleh courier " + request.getCourierId();
        payService.createHistory(payment, PaymentEnum.getPaymentEnum(payment.getStatus()), PaymentEnum.ASSIGN_PICKUP, reason);
    }

    private TPaymentEntity updateAndSavePayment(OnBookingTitipanRequest request, TPickupEntity pickup) {
        MPickupTimeEntity pickupTime = pickup.getTimePickupId();
        TPaymentEntity book = paymentRepo.findById(request.getBookId()).orElseThrow(NotFoundException::new);
        book.setStatus(PaymentEnum.PICKUP_BY_KURIR.getCode());
        book.setPickupTimeId(pickupTime);
        book.setPickupDate(LocalDate.now());
        book.setPickupTime(pickupTime.getTimeFrom().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " - " + pickupTime.getTimeTo().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        paymentRepo.save(book);
        return book;
    }
}
