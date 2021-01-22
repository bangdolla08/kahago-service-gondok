package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.entity.*;
import com.kahago.kahagoservice.enummodel.*;
import com.kahago.kahagoservice.exception.ConflictException;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.projection.ItemPickup;
import com.kahago.kahagoservice.model.projection.PickupCourier;
import com.kahago.kahagoservice.model.projection.PiecesOfItem;
import com.kahago.kahagoservice.model.response.PickupItemDetailResponse;
import com.kahago.kahagoservice.model.response.PickupItemResponse;
import com.kahago.kahagoservice.model.response.PickupResponse;
import com.kahago.kahagoservice.model.response.PiecesResponse;
import com.kahago.kahagoservice.model.validate.*;
import com.kahago.kahagoservice.repository.*;
import com.kahago.kahagoservice.util.Common;
import com.kahago.kahagoservice.util.ImageConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Hendro yuwono
 */
@Service
@Validated
public class PickupOnGoingService {

    @Value("${kahago.image.pickup}")
    private String imagePickup;

    @Value("${kahago.host}")
    private String host;

    @Autowired
    private TCourierPickupRepo courierPickupRepo;

    @Autowired
    private TPickupDetailRepo pickupDetailRepo;

    @Autowired
    private TPickupRepo pickupRepo;

    @Autowired
    private TBookRepo bookRepo;

    @Autowired
    private TPickupOrderRequestDetailRepo pickupOrderRequestDetailRepo;

    @Autowired
    private TPickupOrderRequestRepo pickupOrderRequestRepo;

    @Autowired
    private TPaymentRepo paymentRepo;

    @Autowired
    private HistoryTransactionService historyTransactionService;

    public static PickupItemDetailResponse toPickupItemDetailResponse(TBookEntity entity) {
        String dimension = entity.getLength() + " x " + entity.getWidth() + " x " + entity.getHeight();
        return PickupItemDetailResponse.builder()
                .partId(entity.getSeqid())
                .bookingCode(entity.getBookingCode())
                .dimension(dimension)
                .grossWeight(entity.getGrossWeight())
                .status(entity.getStatus() == null ? PaymentEnum.getByCode(1).getKey() : PaymentEnum.getByCode(entity.getStatus()).getKey())
                .build();
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

    public List<PickupResponse> getOnProcess(String courierId) {
        List<PickupResponse> listOfData = new ArrayList<>();

        boolean existInWarehouse = courierPickupRepo.existsByStatusAndCourierId(PickupCourierEnum.OTW_WAREHOUSE.getValue(), courierId);

        if (existInWarehouse) {
            PickupResponse response = PickupResponse.builder().status(PickupCourierEnum.OTW_WAREHOUSE.getKey()).build();
            listOfData.add(response);
        } else {
            PickupCourier data = courierPickupRepo.findByStatusInAndCourierId(PickupCourierEnum.showInProcessPickup(), courierId);
            if (data != null) {
                listOfData.add(toPickResponse(data));
            }
        }

        return listOfData;
    }

    @Transactional
    public void updateOnProcessCourier(@Valid OnGoingPickupRequest request) {
        TCourierPickupEntity data = courierPickupRepo.findById(request.getId())
                .orElseThrow(NotFoundException::new);
        PickupCourierEnum statusPickup = PickupCourierEnum.getByKey(request.getStatus());

        if (statusPickup.getValue().equals(PickupCourierEnum.READY_PICKUP.getValue())) {
            data.setStatus(PickupCourierEnum.READY_PICKUP.getValue());
        } else if (statusPickup.getValue().equals(PickupCourierEnum.OTW_CUSTOMER.getValue())) {
            data.setStatus(PickupCourierEnum.OTW_CUSTOMER.getValue());
            data.setToCustomerTime(LocalDateTime.now());
        } else if (statusPickup.getValue().equals(PickupCourierEnum.PROCESS_PICKUP.getValue())) {
            data.setStatus(PickupCourierEnum.PROCESS_PICKUP.getValue());
            data.setPickupActionTime(LocalDateTime.now());
        }

        courierPickupRepo.save(data);
    }

    public PickupItemResponse getItems(@Valid OnItemPickupRequest detailPickupRequest) {
        PickupCourier data = courierPickupRepo.findByIds(detailPickupRequest.getId());

        List<PickupItemResponse.Detail> details = joiningReqPickupAndBooking(data).stream()
                .map(this::toDetailPickupResponse).collect(Collectors.toList());

        long countItemNotPickup = details.stream().filter(v -> v.getStatus().equals(PickupDetailEnum.ASSIGN_PICKUP.getKey())).count();
        PickupItemResponse pickDetResp = toPickupDetailResponse(data, countItemNotPickup);
        pickDetResp.setDetails(sortItemByStatus(details));

        return pickDetResp;
    }

    private List<PickupItemResponse.Detail> sortItemByStatus(List<PickupItemResponse.Detail> details) {
        Map<String, List<PickupItemResponse.Detail>> groupByStatus = details.stream().collect(Collectors.groupingBy(PickupItemResponse.Detail::getStatus));
        List<PickupItemResponse.Detail> itemDetails = new ArrayList<>();

        groupByStatus.forEach((key, value) -> {
            if (key.equals(PickupDetailEnum.ASSIGN_PICKUP.getKey())) {
                itemDetails.addAll(value);
            }
        });

        groupByStatus.forEach((key, value) -> {
            if (key.equals(PickupDetailEnum.REJECTED_PICKUP.getKey())) {
                itemDetails.addAll(value);
            }
        });

        groupByStatus.forEach((key, value) -> {
            if (key.equals(PickupDetailEnum.IN_COURIER.getKey())) {
                itemDetails.addAll(value);
            }
        });

        groupByStatus.forEach((key, value) -> {
            if (!key.equals(PickupDetailEnum.IN_COURIER.getKey()) && !key.equals(PickupDetailEnum.REJECTED_PICKUP.getKey()) && !key.equals(PickupDetailEnum.ASSIGN_PICKUP.getKey())) {
                itemDetails.addAll(value);
            }
        });
        return itemDetails;
    }

    public List<PickupItemDetailResponse> getItemDetails(@Valid OnItemDetailPickupRequest request) {
        PickupCourier data = courierPickupRepo.findByIds(request.getId());
        List<String> bookingIds = joiningReqPickupAndBooking(data).stream()
                .filter(v -> !v.isRequestPickup())
                .map(ItemPickup::getIdBooking)
                .collect(Collectors.toList());

        if (!bookingIds.contains(request.getBookId())) {
            return new ArrayList<>();
        }
        return bookRepo.findByBookingCode(request.getBookId()).stream()
                .map(PickupOnGoingService::toPickupItemDetailResponse).collect(Collectors.toList());
    }

    private List<ItemPickup> joiningReqPickupAndBooking(PickupCourier data) {
        List<ItemPickup> bookDetail = pickupDetailRepo.findBookingByPickIdAndPickAddrId(data.getPickupId(), data.getPickupAddressId())
                .stream().peek(v -> v.setRequestPickup(false)).collect(Collectors.toList());
        List<ItemPickup> reqPickDetail = pickupOrderRequestDetailRepo.findReqPickupByPickIdAndPickAddrId(data.getPickupId(), data.getPickupAddressId())
                .stream().peek(v -> v.setRequestPickup(true)).collect(Collectors.toList());
        return Stream.of(bookDetail, reqPickDetail).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public List<PiecesResponse> getPieces(OnAcceptPickBookRequest request) {
        PickupCourier data = courierPickupRepo.findByIds(request.getId());
        if (!hasRequestPickup(data, request.getBookId())) {
            return new ArrayList<>();
        }
        return pickupOrderRequestDetailRepo.findByPickupOrderId(request.getBookId()).stream()
                .map(this::toPiecesResponse).collect(Collectors.toList());
    }

    private PiecesResponse toPiecesResponse(PiecesOfItem item) {
        return PiecesResponse.builder()
                .piecesId(item.getId())
                .receiver(item.getReceiver())
                .destination(item.getDestination())
                .quantity(item.getQuantity())
                .weight(item.getWeight())
                .qrCodeExt(item.getQrCodeExt())
                .pathImagePieces(ImageConstant.reversePathPickupToUrl(item.getPathImagePieces()))
                .pathImageVendor(ImageConstant.reversePathVendorToUrl(item.getPathImageVendor()))
                .product(item.getProduct())
                .status(item.getKeyStatus())
                .build();
    }

    @Transactional
    public void rejectPickup(@Valid OnCancelPickingRequest request) {
        PickupCourier data = courierPickupRepo.findByIds(request.getId());
        List<ItemPickup> itemPickups = joiningReqPickupAndBooking(data);
        int idPickupDetail = findPickupDetail(request, itemPickups);

        TPickupDetailEntity pickupDetail = pickupDetailRepo.findById(idPickupDetail)
                .orElseThrow(NotFoundException::new);

        boolean statusIsReject = request.getStatus().equals("REJECT_ITEM");
        if (statusIsReject) {
            boolean isReqPickup = isRequestPickup(pickupDetail);

            if (isReqPickup) {
                updateStatusReqPickup(pickupDetail);
            } else {
                updateStatusBooking(pickupDetail);
            }

            updateStatusPickupDetail(request, pickupDetail);

            updateStatusCourierPickup(data);
        }
    }

    private void updateStatusCourierPickup(PickupCourier data) {
        TCourierPickupEntity courierPickup = courierPickupRepo.findById(data.getId()).orElseThrow(NotFoundException::new);
        if (haveNextItemPickup(joiningReqPickupAndBooking(data))) {
            courierPickup.setStatus(PickupCourierEnum.PROCESS_PICKUP.getValue());
        } else {
            courierPickup.setStatus(PickupCourierEnum.ISSUES_IN_ADDRESS.getValue());
        }

        courierPickupRepo.save(courierPickup);
    }

    private void updateStatusPickupDetail(OnCancelPickingRequest request, TPickupDetailEntity pickupDetail) {
        pickupDetail.setStatus(PickupDetailEnum.REJECTED_PICKUP.getValue());
        pickupDetail.setReason(request.getReason());
        pickupDetailRepo.save(pickupDetail);
    }

    private void updateStatusBooking(TPickupDetailEntity pickupDetail) {
        TPaymentEntity payment = paymentRepo.findById(pickupDetail.getBookId().getBookingCode())
                .orElseThrow(NotFoundException::new);
        payment.setStatus(PaymentEnum.REJECT_BY_COURIER.getCode());
        payment.getTbooks().forEach( v -> v.setStatus(PaymentEnum.REJECT_BY_COURIER.getCode()));

        paymentRepo.save(payment);
    }

    private void updateStatusReqPickup(TPickupDetailEntity pickupDetail) {
        TPickupOrderRequestEntity pickupOrderRequest = pickupOrderRequestRepo.findById(pickupDetail.getPickupOrderRequestEntity()
                .getPickupOrderId()).orElseThrow(NotFoundException::new);
        pickupOrderRequest.setStatus(RequestPickupEnum.REJECT_BY_COURIER.getValue());
        pickupOrderRequest.getPickupOrderRequestDetails().forEach( v -> v.setStatus(RequestPickupEnum.REJECT_BY_COURIER.getValue()));
        pickupOrderRequestRepo.save(pickupOrderRequest);
    }

    private boolean isRequestPickup(TPickupDetailEntity tPickupDetail) {
        return Objects.isNull(tPickupDetail.getBookId());
    }

    @Transactional
    public void acceptBooking(@Valid OnAcceptPickBookRequest request) {
        PickupCourier data = courierPickupRepo.findByIds(request.getId());
        if (hasRequestPickup(data, request.getBookId())) {
            throw new ConflictException("Not for request pickup");
        } else {
            String path = extractImageToFileSystem(request.getImage(), request.getBookId(), data);
            savePiecesOfBooking(request, path);
        }
        updateCourierPickup(data);
        updateManifest(data);
    }

    private void updateManifest(PickupCourier data) {
        List<Integer> status = Arrays.asList(PickupCourierEnum.FINISH_PICKUP.getValue(), PickupCourierEnum.ISSUES_IN_ADDRESS.getValue());
        List<TCourierPickupEntity> courierPickups = courierPickupRepo.findByPickupIdPickup(data.getPickupId());
        boolean hasFinish = courierPickups.stream().anyMatch(v -> status.contains(v.getStatus()));

        if (hasFinish) {
            TPickupEntity pickup = pickupRepo.findById(data.getPickupId()).orElseThrow(NotFoundException::new);
            pickup.setStatus(filterStatusCourierPickup(courierPickups).getValue());
            pickupRepo.save(pickup);
        }

    }

    private PickupEnum filterStatusCourierPickup(List<TCourierPickupEntity> courierPickups) {
        PickupEnum pickupEnum;
        boolean inCourier = courierPickups.stream().map(TCourierPickupEntity::getStatus).anyMatch(v -> v.equals(PickupCourierEnum.FINISH_PICKUP.getValue()));

        if (inCourier) {
            pickupEnum = PickupEnum.IN_COURIER;
        } else {
            pickupEnum = PickupEnum.ISSUES_IN_ADDRESS;
        }
        return pickupEnum;
    }

    private void updateCourierPickup(PickupCourier data) {
        List<ItemPickup> itemPickups = joiningReqPickupAndBooking(data);
        PickupCourierEnum pickupCourierEnum = filterStatusPickup(itemPickups);

        TCourierPickupEntity courierPickup = courierPickupRepo.findById(data.getId()).orElseThrow(NotFoundException::new);
        courierPickup.setStatus(pickupCourierEnum.getValue());
        courierPickupRepo.save(courierPickup);

    }

    private PickupCourierEnum filterStatusPickup(List<ItemPickup> itemPickups) {
        PickupCourierEnum pickupCourierEnum;
        boolean rejectPickup = itemPickups.stream().map(ItemPickup::keyOfStatus).anyMatch(s -> s.equals("REJECTED_PICKUP"));
        boolean assignPickup = itemPickups.stream().map(ItemPickup::keyOfStatus).anyMatch(s -> s.equals("ASSIGN_PICKUP"));

        if (rejectPickup) {
            pickupCourierEnum = PickupCourierEnum.ISSUES_IN_ADDRESS;
        } else if (assignPickup) {
            pickupCourierEnum = PickupCourierEnum.PROCESS_PICKUP;
        } else {
            pickupCourierEnum = PickupCourierEnum.FINISH_PICKUP;
        }
        return pickupCourierEnum;
    }

    private void savePiecesOfBooking(@Valid OnAcceptPickBookRequest request, String path) {
        findByBookingAndSavePieces(request, path);
        boolean isAllInCourier = bookRepo.findByBookingCode(request.getBookId())
                .stream().allMatch(this::matchStatusRequest);

        if (isAllInCourier) {
            updatePickupHeader(request);
        }
    }

    private boolean matchStatusRequest(TBookEntity entity) {
        if (Objects.isNull(entity.getStatus())) {
            return false;
        }
        return entity.getStatus().equals(PaymentEnum.PICKUP_BY_KURIR.getCode());
    }

    private void updatePickupHeader(OnAcceptPickBookRequest request) {
        TPaymentEntity payment = paymentRepo.findById(request.getBookId()).orElseThrow(NotFoundException::new);
        payment.setStatus(PaymentEnum.PICKUP_BY_KURIR.getCode());
        paymentRepo.save(payment);

        TPickupDetailEntity pickupDetail = pickupDetailRepo.findByBookIdBookingCode(payment.getBookingCode()).orElseThrow(NotFoundException::new);
        pickupDetail.setStatus(PickupDetailEnum.IN_COURIER.getValue());
        pickupDetailRepo.save(pickupDetail);
    }

    private void findByBookingAndSavePieces(OnAcceptPickBookRequest request, String path) {
        TBookEntity book = bookRepo.findById(request.getPartId()).orElseThrow(NotFoundException::new);
        book.setQrCode(request.getQrCode());
        book.setImages(path);
        book.setStatus(PaymentEnum.PICKUP_BY_KURIR.getCode());

        bookRepo.save(book);
    }

    private boolean hasRequestPickup(PickupCourier data, String bookId) {
        boolean reqPickExist = pickupOrderRequestDetailRepo.findReqPickupByPickIdAndPickAddrId(data.getPickupId(), data.getPickupAddressId())
                .stream().anyMatch(pickup -> pickup.getIdBooking().equalsIgnoreCase(bookId));

        if (!reqPickExist) {
            boolean bookingExist = pickupDetailRepo.findBookingByPickIdAndPickAddrId(data.getPickupId(), data.getPickupAddressId())
                    .stream().anyMatch(pickup -> pickup.getIdBooking().equalsIgnoreCase(bookId));

            if (!bookingExist) {
                throw new NotFoundException("bookId is not found");
            }
            return false;
        }

        return true;
    }

    public String extractImageToFileSystem(MultipartFile image, String bookId, PickupCourier data) {
        Instant instant = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
        String fileName = data.getManifest() + "_" + bookId + "_" + instant.toEpochMilli();
        String extension = Objects.requireNonNull(image.getOriginalFilename())
                .substring(image.getOriginalFilename().lastIndexOf("."));

        File target = new File(imagePickup + File.separator + fileName + extension);
        try {
            image.transferTo(target);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return target.getAbsolutePath();
    }

    private Integer findPickupDetail(OnCancelPickingRequest request, List<ItemPickup> itemPickups) {
        List<ItemPickup> pickValid = itemPickups.stream()
                .filter(pickup -> pickup.getIdBooking().equalsIgnoreCase(request.getBookId()))
                .collect(Collectors.toList());

        return pickValid.get(0).getIdPickupDetail();
    }

    private boolean haveNextItemPickup(List<ItemPickup> itemPickups) {
        return itemPickups.stream()
                .anyMatch(v -> v.getStatusPickDetail().equals(PickupDetailEnum.ASSIGN_PICKUP.getValue()));
    }

    private PickupItemResponse toPickupDetailResponse(PickupCourier pc, Long countItemNotPickup) {
        PickupResponse.Geometry geometry = PickupResponse.Geometry.builder()
                .type("Point")
                .coordinates(pc.getCoordinate())
                .build();

        String URL_REPORT_MANIFEST = "api/report/manifest/pickup?id=";
        return PickupItemResponse.builder()
                .id(pc.getId())
                .status(PickupCourierEnum.getByValue(pc.getStatus()).getKey())
                .dateOfAssign(pc.dateOfAssign())
                .manifest(pc.getManifest())
                .name(pc.getName())
                .phone(pc.getPhone())
                .address(pc.getAddress())
                .description(pc.getDescription())
                .location(geometry)
                .statusDescription("Belum Diambil")
                .firstTimeVisiting(pc.getFirstTimeVisiting())
                .count(countItemNotPickup)
                .reportManifest(host + URL_REPORT_MANIFEST + pc.getManifest())
                .build();
    }

    private PickupItemResponse.Detail toDetailPickupResponse(ItemPickup pd) {
        return PickupItemResponse.Detail.builder()
                .idBook(pd.getIdBooking())
                .sender(pd.getSender())
                .receiver(pd.getReceiver())
                .receiverAddress(pd.getReceiverAddress())
                .totalItem(pd.getTotalItem())
                .weight(pd.getWeight())
                .status(pd.keyOfStatus())
                .imageVendor(pd.getPathImage())
                .isRequestPickup(pd.isRequestPickup())
                .hasPieces(pd.hasPieces())
                .build();
    }

    @Transactional
    public void acceptRequestPickup(@Valid OnAcceptPickReqPickupRequest request) {
        PickupCourier data = courierPickupRepo.findByIds(request.getId());
        if (hasRequestPickup(data, request.getBookId())) {
            saveRequestPickup(request, data);
        } else {
            throw new ConflictException("Not for normal booking");
        }
        updateCourierPickup(data);
        updateManifest(data);
    }

    private void updateHeaderReqPickup(@Valid OnAcceptPickReqPickupRequest request) {
        List<PiecesOfItem> byPickupOrderId = pickupOrderRequestDetailRepo.findByPickupOrderId(request.getBookId());
        boolean hasNext = byPickupOrderId.stream().anyMatch(this::matchStatusPieces);
        if (!hasNext) {
            TPickupDetailEntity pickupDetail = pickupDetailRepo.findByPickupOrderRequestEntityPickupOrderId(request.getBookId()).orElseThrow(NotFoundException::new);
            pickupDetail.setStatus(PickupDetailEnum.IN_COURIER.getValue());
            pickupDetailRepo.save(pickupDetail);
        }
    }

    private void saveRequestPickup(@Valid OnAcceptPickReqPickupRequest request, PickupCourier data) {
        String path = extractImageToFileSystem(request.getImage(), request.getBookId(), data);
        PiecesOfItem pieces = pickupOrderRequestDetailRepo.findByPickupOrderId(request.getBookId()).stream()
                .filter(v -> v.getId().equals(request.getPiecesId()))
                .findAny().orElse(null);

        TPickupOrderRequestEntity por = pickupOrderRequestRepo.findById(request.getBookId()).orElseThrow(NotFoundException::new);

        if (Objects.isNull(pieces)) {
            createPickupOrderReqDetail(request, path, por);
        } else {
            updatePickupOrderReqDetail(pieces, request, path, por);
        }

        updateHeaderReqPickup(request);
    }

    private boolean matchStatusPieces(PiecesOfItem entity) {
        if (Objects.isNull(entity.getStatus())) {
            return false;
        }
        return entity.getKeyStatus().equals(PickupDetailEnum.ASSIGN_PICKUP.getKey());
    }

    private void updatePickupOrderReqDetail(PiecesOfItem pieces, OnAcceptPickReqPickupRequest request, String path, TPickupOrderRequestEntity por) {
        TPickupOrderRequestDetailEntity detailEntity = pickupOrderRequestDetailRepo.findById(pieces.getId())
                .orElseThrow(NotFoundException::new);

        detailEntity.setQrcodeExt(request.getQrCode());
        detailEntity.setStatus(RequestPickupEnum.IN_COURIER.getValue());
        detailEntity.setUpdateDate(LocalDateTime.now());
        detailEntity.setUpdateBy(request.getCourierId());
        detailEntity.setPathPic(path);

        TPickupOrderRequestDetailEntity temp = pickupOrderRequestDetailRepo.save(detailEntity);
        historyTransactionService.historyRequestPickup(por, temp, RequestPickupEnum.IN_COURIER.getValue(), request.getCourierId(), "");
    }

    private void createPickupOrderReqDetail(OnAcceptPickReqPickupRequest request, String path, TPickupOrderRequestEntity por) {
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
}
