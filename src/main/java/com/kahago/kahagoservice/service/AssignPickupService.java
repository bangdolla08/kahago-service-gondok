package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.component.FirebaseComponent;
import com.kahago.kahagoservice.entity.*;
import com.kahago.kahagoservice.enummodel.*;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.projection.PickupAddress;
import com.kahago.kahagoservice.model.request.CreateDetailManifestReq;
import com.kahago.kahagoservice.model.request.CreateManifestReq;
import com.kahago.kahagoservice.model.response.*;
import com.kahago.kahagoservice.repository.*;
import com.kahago.kahagoservice.util.DateTimeUtil;

import lombok.SneakyThrows;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * @author bangd ON 17/12/2019
 * @project com.kahago.kahagoservice.service
 */
@Service
public class AssignPickupService {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PickupService pickupService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private MUserRepo mUserRepo;
    @Autowired
    private RequestPickUpService requestPickUpService;
    @Autowired
    private MPickupTimeRepo mastPickupTimeRepo;
    @Autowired
    private TPickupDetailRepo tPickupDetailRepo;
    @Autowired
    private TPickupRepo tPickupRepo;
    @Autowired
    private FirebaseComponent firebase;
    @Autowired
    private TPickupOrderRequestDetailRepo tReqDetailRepo;
    @Autowired
    private HistoryTransactionService historyTransactionService;
    @Autowired
    private TPickupOrderRequestRepo tPickupOrderRequestRepo;
    @Autowired
    private TPickupOrderRequestDetailRepo orderRequestDetailRepo;
    @Autowired
    private PickupAddressService pickupAddressService;
    @Autowired
    private TPaymentHistoryRepo tPaymentHistoryRepo;
    @Autowired
    private TPickupAddressRepo tPickupAddressRepo;

    @Autowired
    private TCourierPickupRepo courierPickupRepo;

    private static final Logger log = LoggerFactory.getLogger(AssignPickupService.class);

    public List<ResPickupTime> getActiveTimePickup() {
        return pickupService.findPickupTimeAsaign();
    }

    public List<BookDataResponse> getDataNeedToAssignPickup(String userId, String date, Integer pickupTimeId, Integer areaKotaId) throws ParseException {
        List<BookDataResponse> bookDataResponses = paymentService.getPaymentsNeedPickup(DateTimeUtil.getDateFrom(date), pickupTimeId, areaKotaId).stream().map(this::generateBookDataResponse).collect(Collectors.toList());
        bookDataResponses.addAll(requestPickUpService.getPickupOrderList(userId, DateTimeUtil.getDateFrom(date), pickupTimeId, RequestPickupEnum.REQUEST, areaKotaId).stream().map(this::generateBookDataResponse).collect(Collectors.toList()));
        return bookDataResponses;
    }


    public ValidateTimeToAsiggn getTimeStatus(String date, Integer pickup_time_id, String code) throws ParseException {
        LocalDate localDate = DateTimeUtil.getDateFrom(date);
        return getTimeStatus(localDate, pickup_time_id, code);
    }

    public ValidateTimeToAsiggn getTimeStatus(LocalDate date, Integer pickup_time_id, String code) {
        LocalDate localDate = LocalDate.now();
        TPickupEntity pickup = tPickupRepo.findByCode(code);
        MPickupTimeEntity pickupTimeEntity = mastPickupTimeRepo.findByIdPickupTime(pickup_time_id);
        LocalTime localTimeNow = LocalTime.now();
        LocalTime pickupTime = pickupTimeEntity.getTimeTo().plusMinutes(30);
        String timeNow = localTimeNow.format(DateTimeFormatter.ofPattern("HH.mm"));
        String timePickup = pickupTime.format(DateTimeFormatter.ofPattern("HH.mm"));
        Boolean canAssign = false;
        Boolean canDraft = false;
        if (localTimeNow.isAfter(pickupTime) && Double.valueOf(timeNow) > Double.valueOf(timePickup)) {
            canAssign = false;
            canAssign = false;
        } else if (localTimeNow.compareTo(pickupTimeEntity.getTimeFrom()) >= 0 && localTimeNow.compareTo(pickupTimeEntity.getTimeTo()) <= 0 && localDate.isEqual(date)) {
            canAssign = true;
            if (pickup != null) {
                if (pickup.getStatus().equals(PickupEnum.ASSIGN_PICKUP.getValue())
                        || pickup.getStatus().equals(PickupEnum.DRAFT.getValue())) {
                    canAssign = true;
                } else {
                    canAssign = false;
                }
            }
        } else if ((localTimeNow.compareTo(pickupTimeEntity.getTimeFrom()) < 0 && localDate.isEqual(date)) || date.isAfter(localDate)) {
            canDraft = true;
            if (pickup != null) {
                if (pickup.getStatus().equals(PickupEnum.ASSIGN_PICKUP.getValue())
                        || pickup.getStatus().equals(PickupEnum.DRAFT.getValue())) {
                    canDraft = true;
                } else {
                    canDraft = false;
                }
            }
        }
        if (pickup != null) {
            if (!pickup.getPickupDate().equals(localDate)) {
                canAssign = false;
                canDraft = false;
            }else {
            	if((pickup.getStatus().equals(PickupEnum.DRAFT.getValue())||pickup.getStatus().equals(PickupEnum.ASSIGN_PICKUP.getValue()))) {
            		if(localTimeNow.compareTo(pickup.getTimePickupId().getTimeTo()) <= 0) {
            			canAssign = true;
                        canDraft = true;
            		}
            	}
            }
            
        }
        
        // Boolean canAssign=localTimeNow.isBefore(localTimeFrom)&&localTimeNow.isAfter(pickupTimeEntity.getTimeFrom())&&localDate.isEqual(date);
        //Boolean canDraft=(localTimeNow.isBefore(localTimeFrom)&&localDate.isEqual(date))||date.isAfter(localDate);

        return ValidateTimeToAsiggn.builder()
                .canAssign(canAssign)
                .canDraft(canDraft)
                .canSearch(canDraft || canAssign)
                .build();
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public SaveResponse savePickupData(CreateManifestReq createManifestReq) {
        SaveResponse saveResponse = SaveResponse
                .builder()
                .saveInformation("Save Suksess")
                .saveStatus(1)
                .build();
        TPickupEntity pickupEntity = new TPickupEntity();
        MUserEntity userEntity = new MUserEntity();
        MPickupTimeEntity pickupTimeEntity = new MPickupTimeEntity();
        if (createManifestReq.getManifestCode() != null) {
            pickupEntity = tPickupRepo.findByCode(createManifestReq.getManifestCode());
            userEntity = pickupEntity.getCourierId();
            pickupTimeEntity = pickupEntity.getTimePickupId();
        } else {
        	Integer origin = getOrigin(createManifestReq.getDetail().stream().findAny().get());
            userEntity = mUserRepo.getMUserEntitiesBy(createManifestReq.getUserIdCorier());
            pickupTimeEntity = pickupService.getPickupTimeEntity(createManifestReq.getTimePickupId());
//            pickupEntity = tPickupRepo.findByPickupTimeIdAndCourierId(pickupTimeEntity.getIdPickupTime(), userEntity.getUserId(), LocalDate.now());
            pickupEntity = tPickupRepo.findByPickupTimeIdAndCourierIdAndOrigin(pickupTimeEntity.getIdPickupTime(), userEntity.getUserId(), LocalDate.now(),origin).stream().findAny().orElse(null);
           
        }
        
        //if(createManifestReq.getSaveSendStatusEnum()==SaveSendStatusEnum.SAVE){
        PickupEnum pickupEnum = PickupEnum.DRAFT;
        PaymentEnum paymentEnum = PaymentEnum.DRAFT_PICKUP;
        RequestPickupEnum requestPickupEnum = RequestPickupEnum.DRAFT_PICKUP;
        LocalTime localTime = LocalTime.now();
        LocalTime addPicku = pickupTimeEntity.getTimeTo().plusMinutes(30);
        String timenow = localTime.format(DateTimeFormatter.ofPattern("HH.mm"));
        String pickupTime = addPicku.format(DateTimeFormatter.ofPattern("HH.mm"));
        //check time now apakah sudah lewat lebih dari 30 menit timepickupTo
        

        //menentukan status DRAF atau ASSIGN
        if( pickupEntity == null) {
        	pickupEntity = new TPickupEntity();
        	if (localTime.isAfter(addPicku) && Double.valueOf(timenow) > Double.valueOf(pickupTime)) {
                saveResponse.setSaveInformation("Dilarang Save waktu Tidak Tepat Harap baca Peraturan!!");
                saveResponse.setSaveStatus(0);
                throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, saveResponse.getSaveInformation());
            }
        	if ((localTime.compareTo(pickupTimeEntity.getTimeFrom()) >= 0 && localTime.compareTo(pickupTimeEntity.getTimeTo()) <= 0)) {
                pickupEnum = PickupEnum.ASSIGN_PICKUP;
                paymentEnum = PaymentEnum.ASSIGN_PICKUP;
                requestPickupEnum = RequestPickupEnum.ASSIGN_PICKUP;
            } else if (localTime.compareTo(pickupTimeEntity.getTimeTo()) > 0 && Double.valueOf(timenow) <= Double.valueOf(pickupTime)) {
                pickupEnum = PickupEnum.ASSIGN_PICKUP;
                paymentEnum = PaymentEnum.ASSIGN_PICKUP;
                requestPickupEnum = RequestPickupEnum.ASSIGN_PICKUP;
            } else if (localTime.compareTo(pickupTimeEntity.getTimeFrom()) < 0) {
                pickupEnum = PickupEnum.DRAFT;
                paymentEnum = PaymentEnum.DRAFT_PICKUP;
                requestPickupEnum = RequestPickupEnum.DRAFT_PICKUP;
                if (createManifestReq.getManifestCode() != null && createManifestReq.getTimePickupId() == null) {
                    saveResponse.setSaveInformation("Dilarang Save waktu Tidak Tepat Harap baca Peraturan!!");
                    saveResponse.setSaveStatus(0);
                    throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, saveResponse.getSaveInformation());
                }
            }
        }else {
        	if(pickupEntity.getStatus().equals(PickupEnum.DRAFT.getValue()) && createManifestReq.getManifestCode()==null) {
        		log.info("Status Manifest DRAFT");
        		pickupEnum = PickupEnum.DRAFT;
                paymentEnum = PaymentEnum.DRAFT_PICKUP;
                requestPickupEnum = RequestPickupEnum.DRAFT_PICKUP;
        	}else {
        		log.info("Status Manifest ASSIGN");
        		pickupEnum = PickupEnum.ASSIGN_PICKUP;
                paymentEnum = PaymentEnum.ASSIGN_PICKUP;
                requestPickupEnum = RequestPickupEnum.ASSIGN_PICKUP;
        	}
        	
        }
        

        // TODO: 18/12/2019 Create Manifest harus dengan flow


        if (pickupEntity.getIdPickup() == null) {
            pickupEntity = TPickupEntity.builder()
                    .code(pickupService.createCodePickup())
                    .courierId(userEntity)
                    .createAt(LocalDateTime.now())
                    .createBy(createManifestReq.getUserId())
                    .timePickupId(pickupTimeEntity)
                    .pickupDate(LocalDate.now())
                    .timePickupFrom(pickupTimeEntity.getTimeFrom())
                    .timePickupTo(pickupTimeEntity.getTimeTo())
                    .status(pickupEnum.getValue())
                    .build();
        } else {
            pickupEntity.setStatus(pickupEnum.getValue());
            pickupEntity.setModifyAt(LocalDateTime.now());
            pickupEntity.setModifyBy(createManifestReq.getUserId());
        }
        pickupEntity = pickupService.savePickupEntity(pickupEntity);

        List<TPickupDetailEntity> detailEntities = tPickupDetailRepo.findAllByPickupIdAndStatus(pickupEntity,PickupDetailEnum.ASSIGN_PICKUP.getValue());
        if (createManifestReq.getDetail().size() == 0) {
            for (TPickupDetailEntity pd : detailEntities) {
                if (pd.getBookId() != null) {
                    TPaymentEntity pay = pd.getBookId();
                    TPaymentEntity entityPaymentHistory = paymentService.createOldPayment(pay);
                    pay.setStatus(paymentEnum.getValue());
                    paymentService.save(pay);
                    historyTransactionService.createHistory(entityPaymentHistory, pay, createManifestReq.getUserId());
                    TPaymentHistoryEntity pHistory = tPaymentHistoryRepo.findFirstByBookingCodeAndLastStatusOrderByLastUpdateDesc(pay, pay.getStatus());
        			pHistory.setReason("Courier Name : "+pickupEntity.getCourierId().getName());
        			tPaymentHistoryRepo.save(pHistory);
                } else {
                    TPickupOrderRequestEntity order = pd.getPickupOrderRequestEntity();
                    Integer oldStatus = order.getStatus();
                    order.setStatus(requestPickupEnum.getValue());
                    requestPickUpService.savePickupOrderRequestEntity(order);
                    String reason = "Courier Name : "+pickupEntity.getCourierId().getName();
                    historyTransactionService.historyRequestPickup(order, null, oldStatus, createManifestReq.getUserId(),reason);
                }
            }
        } else {
            for (CreateDetailManifestReq createDetailManifestReq : createManifestReq.getDetail()) {
            	detailEntities = tPickupDetailRepo.findByPickupId(pickupEntity);
                TPaymentEntity paymentEntity = paymentService.get(createDetailManifestReq.getBookingCode());
                TPickupOrderRequestEntity orderRequestEntity = requestPickUpService.getPickupOrderRequestEntity(createDetailManifestReq.getBookingCode());
                TPickupAddressEntity pickupAddrId = null;
                List<TPickupOrderRequestDetailEntity> lPickupDtl = tReqDetailRepo.findAllByOrderRequestEntity(orderRequestEntity);

                Boolean flagpay = false;
                Boolean flagreq = false;
                if (paymentEntity != null) {
                    if (!paymentEntity.getStatus().equals(PaymentEnum.CANCEL_BY_USER.getCode())) {
                        TPaymentEntity entityPaymentHistory = paymentService.createOldPayment(paymentEntity);
                        for (TPickupDetailEntity dtl : detailEntities) {
                            if (dtl.getBookId() != null && 
                            		(paymentEntity.getStatus().equals(PaymentEnum.REQUEST.getValue()) ||
                            				paymentEntity.getStatus().equals(PaymentEnum.DRAFT_PICKUP.getValue()))) {
                                if (dtl.getBookId().equals(paymentEntity)) {
                                    paymentEntity.setStatus(paymentEnum.getValue());
                                    paymentService.save(paymentEntity);
                                    flagpay = true;
                                }
                            }
                        }
                        pickupAddrId = paymentEntity.getPickupAddrId();
                        paymentEntity.setStatus(paymentEnum.getValue());
                        historyTransactionService.createHistory(entityPaymentHistory, paymentEntity, createManifestReq.getUserId());
                        TPaymentHistoryEntity pHistory = tPaymentHistoryRepo.findFirstByBookingCodeAndLastStatusOrderByLastUpdateDesc(paymentEntity, paymentEntity.getStatus());
            			pHistory.setReason("Courier Name : "+pickupEntity.getCourierId().getName());
            			tPaymentHistoryRepo.save(pHistory);
                    }
                }
                if (orderRequestEntity != null) {
                    if (!orderRequestEntity.getStatus().equals(RequestPickupEnum.CANCEL_DETAIL.getValue())) {
                    	  Integer oldStatus = orderRequestEntity.getStatus();
                        for (TPickupDetailEntity dtl : detailEntities) {
                        	if(dtl.getPickupOrderRequestEntity()!=null && 
                        			(orderRequestEntity.getStatus().equals(RequestPickupEnum.REQUEST.getValue()) || 
                        					orderRequestEntity.getStatus().equals(RequestPickupEnum.DRAFT_PICKUP.getValue()) || 
                        					orderRequestEntity.getStatus().equals(RequestPickupEnum.ASSIGN_PICKUP.getValue()))) {
                        		if (orderRequestEntity.equals(dtl.getPickupOrderRequestEntity())) {
                                    orderRequestEntity.setStatus(requestPickupEnum.getValue());
                                    requestPickUpService.savePickupOrderRequestEntity(orderRequestEntity);
                                    flagreq = true;
                                }
                        	}
                        }
                        for (TPickupOrderRequestDetailEntity dtl : lPickupDtl) {
                            dtl.setStatus(requestPickupEnum.getValue());
                            tReqDetailRepo.save(dtl);
                        }
                        pickupAddrId = orderRequestEntity.getPickupAddressEntity();
                        orderRequestEntity.setStatus(requestPickupEnum.getValue());
                        String reason = "Courier Name : "+pickupEntity.getCourierId().getName();
                        historyTransactionService.historyRequestPickup(orderRequestEntity, null, oldStatus, createManifestReq.getUserId(),reason);
                    }

                }
                if (!flagpay && !flagreq) {
                    TPickupDetailEntity pickupDetailEntity = TPickupDetailEntity.builder()
                            .pickupId(pickupEntity)
                            .bookId(paymentEntity)
                            .pickupOrderRequestEntity(orderRequestEntity)
                            .pickupAddrId(pickupAddrId)
                            .status(PickupDetailEnum.ASSIGN_PICKUP.getValue())
                            .createBy(createManifestReq.getUserId())
                            .createDate(LocalDateTime.now())
                            .build();
                    //detailEntities.add(pickupDetailEntity);
//                    tPickupDetailRepo.save(pickupDetailEntity);
                    pickupService.savePickupDetail(pickupDetailEntity);
                }
            }
        }

//        this.pickupService.savePickupDetailEntity(detailEntities);

        //pickupEntity.setEntities(detailEntities);
        pickupEntity = pickupService.savePickupEntity(pickupEntity);

        routingPickAddrForCourier(pickupEntity.getIdPickup());

        if (pickupEntity.getStatus().equals(PickupEnum.ASSIGN_PICKUP.getValue())) {
            getNotif(pickupEntity);
        }
        return saveResponse;
    }

    private void routingPickAddrForCourier(int pickupId) {
        List<PickupAddress> pickupAddressCouriers = tPickupDetailRepo.courierStatusInAssignPickup(pickupId);
        boolean isPickupIdExist = courierPickupRepo.existsByPickupIdPickup(pickupId);

        if (isPickupIdExist) {
            List<PickupAddress> newCandidateCourierPickup = intersectOfCourierPickup(pickupAddressCouriers, pickupId);
            modifyStatusExceptDraftAndReadyPickupInCourierPickup(newCandidateCourierPickup, pickupId);

            newCandidateCourierPickup.forEach(this::savingCourierPickup);
        } else {
            pickupAddressCouriers.forEach(this::savingCourierPickup);
        }

        verifyPickupAddress(pickupAddressCouriers.get(0).getStatusPickup(), pickupId);

    }

    private void verifyPickupAddress(Integer statusPickup, int pickupId) {
        if (statusPickup.equals(PickupEnum.ASSIGN_PICKUP.getValue())) {
            List<TCourierPickupEntity> entity = courierPickupRepo.findByPickupIdPickupAndStatus(pickupId, PickupCourierEnum.DRAFT.getValue());
            entity.forEach(this::updateCourierPickup);
        }
    }

    private List<PickupAddress> intersectOfCourierPickup(List<PickupAddress> courierPickAddr, Integer pickupId) {
        List<Integer> status = Arrays.asList(PickupCourierEnum.DRAFT.getValue(), PickupCourierEnum.READY_PICKUP.getValue());
        Set<Integer> ids = courierPickupRepo.findByPickupIdPickupAndStatusIn(pickupId, status)
                .map(TCourierPickupEntity::getPickupAddress)
                .map(TPickupAddressEntity::getPickupAddrId)
                .collect(Collectors.toSet());

        return courierPickAddr.stream().filter(v -> !ids.contains(v.getPickupAddressId())).collect(Collectors.toList());
    }

    private void modifyStatusExceptDraftAndReadyPickupInCourierPickup(List<PickupAddress> newCandidatePickupAddr, Integer pickupId) {
        if (newCandidatePickupAddr.size() != 0) {
            List<Integer> addressIds = newCandidatePickupAddr.stream().map(PickupAddress::getPickupAddressId).collect(Collectors.toList());

            List<TCourierPickupEntity> statusOrderThanProcess = courierPickupRepo.findByPickupIdPickupAndPickupAddressPickupAddrIdIn(pickupId, addressIds);
            updateTableAndRemoveFromCandidate(newCandidatePickupAddr, statusOrderThanProcess);
        }
    }

    private void updateTableAndRemoveFromCandidate(List<PickupAddress> newCandidatePickupAddr, List<TCourierPickupEntity> statusOrderThanProcess) {
        statusOrderThanProcess.forEach(this::updateCourierPickup);

        List<PickupAddress> temp = new ArrayList<>(newCandidatePickupAddr);
        if (statusOrderThanProcess.size() != 0) {
            for (PickupAddress pickupAddress : temp) {
                for (TCourierPickupEntity spa : statusOrderThanProcess) {
                    if (spa.getPickupAddress().getPickupAddrId().equals(pickupAddress.getPickupAddressId())) {
                        newCandidatePickupAddr.remove(pickupAddress);
                    }
                }
            }
        }
    }

    private void savingCourierPickup(PickupAddress pickupAddress) {
        TPickupEntity tPickup = tPickupRepo.findById(pickupAddress.getPickupId()).orElseThrow(() -> new NotFoundException("pickup not found"));
        TPickupAddressEntity pickupAddressEntity = tPickupAddressRepo.findById(pickupAddress.getPickupAddressId()).orElseThrow(() -> new NotFoundException("pickup address not found"));
        TCourierPickupEntity entity = TCourierPickupEntity.builder()
                .courierId(pickupAddress.getCourierId())
                .pickup(tPickup)
                .pickupAddress(pickupAddressEntity)
                .status(statusCourierPickup(pickupAddress.getStatusPickup()))
                .build();

        courierPickupRepo.save(entity);
    }

    private Integer statusCourierPickup(Integer pickupStatus) {
        if (pickupStatus.equals(PickupEnum.DRAFT.getValue())) {
            return PickupCourierEnum.DRAFT.getValue();
        } else if (pickupStatus.equals(PickupEnum.ASSIGN_PICKUP.getValue())) {
            return PickupCourierEnum.READY_PICKUP.getValue();
        } else {
            return PickupCourierEnum.READY_PICKUP.getValue();
        }
    }

    private void updateCourierPickup(TCourierPickupEntity entity) {
        entity.setStatus(PickupCourierEnum.READY_PICKUP.getValue());
        courierPickupRepo.save(entity);
    }
    private Integer getOrigin(CreateDetailManifestReq createDetailManifestReq) {
		// TODO Auto-generated method stub
    	TPaymentEntity pay = paymentService.get(createDetailManifestReq.getBookingCode());
    	if(pay==null) {
    		TPickupOrderRequestEntity pod= tPickupOrderRequestRepo.findByPickupOrderId(createDetailManifestReq.getBookingCode());
    		return pod.getPickupAddressEntity().getPostalCode().getKecamatanEntity().getKotaEntity().getAreaKotaId();
    	}else {
    		return pay.getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getAreaKotaId();
    	}
	}
    private BookDataResponse generateBookDataResponse(TPaymentEntity paymentEntity) {
        BookDataResponse bookDataResponse = paymentService.toBookDataResponse(paymentEntity);
        bookDataResponse.setPickupAddress(paymentEntity.getPickupAddrId().getAddress().concat(" ").concat(areaService.getFullAddressByPostalCode(paymentEntity.getPickupAddrId().getPostalCode())));
        bookDataResponse.setUserPhone(paymentEntity.getUserId().getHp());
        bookDataResponse.setGoodDesc(paymentEntity.getGoodsDesc());
        bookDataResponse.setPickupPostalCode(paymentEntity.getPickupAddrId().getPostalCode().getPostalCode());
        return bookDataResponse;
    }

    private BookDataResponse generateBookDataResponse(TPickupOrderRequestEntity pickupOrderRequestEntity) {
        return paymentService.getDetailRequestPickup(pickupOrderRequestEntity);
    }

    public void getNotif(TPickupEntity pickup) {
        //TPickupEntity pickup = tPickupRepo.findByCode(code);
        String title = "pickup";
        log.info("===> Notif Assign Pickup <===");
        JSONObject data = new JSONObject();
        Integer totalBook = tPickupDetailRepo.countByPickupIdIdPickup(pickup.getIdPickup());
        try {
            data.put("idTrx", pickup.getCode());
            data.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")));
            data.put("userid", pickup.getCourierId().getUserId());
            data.put("nominal", totalBook.toString());
            data.put("type_trx", "3"); //1. Book, 2. Deposit, 3. Pickup
            data.put("tag", title);
            data.put("status_trx", "1"); //0. failed, 1.Success
            data.put("tittle", "Manifest " + pickup.getCode());
            data.put("body", "Permintaan Pengambilan Barang");
            firebase.notif(title, "Assign Pickup", data, title, pickup.getCourierId().getTokenNotif());
        } catch (JSONException e) {
            // TODO: handle exception
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public List<AssignPickupResponse> getListAssignPickup(String userId, Integer areaDetailId, Integer areaKotaId, Integer pickupTimeId, String pickupDate) {
        List<AssignPickupResponse> lAssign = new ArrayList<>();
        LocalDate pickupDt = LocalDate.now();

        if (pickupDate != null) {
            pickupDt = DateTimeUtil.getDateFrom(pickupDate, "ddMMyyyy");
        }

        List<Integer> addressEntities = paymentService.getTPickupAdrressByPickupDate(userId, pickupTimeId, areaDetailId, areaKotaId, pickupDt);
        if (addressEntities == null) {
            addressEntities = new ArrayList<>();
        }
        List<Integer> addressEntitiesReqPic = requestPickUpService.getListPickupOrderByAddress(userId, pickupTimeId, pickupDt, areaDetailId, areaKotaId, null);
        if (addressEntitiesReqPic == null) {
            addressEntitiesReqPic = new ArrayList<>();
        }
        for (Integer y : addressEntitiesReqPic) {
            Boolean isAvail=false;
            for (Integer x : addressEntities) {

                if (x == y) {
                    isAvail=true;
                    break;
                }
            }
            if(!isAvail){
                addressEntities.add(y);
            }
        }
        addressEntities.forEach(consumeAddress(userId, areaDetailId, areaKotaId, pickupTimeId, lAssign, pickupDt));
        Collections.sort(lAssign, (a,b)->a.getPostalCode().compareToIgnoreCase(b.getPostalCode()));
        return lAssign;
    }

	private Consumer<? super Integer> consumeAddress(String userId, Integer areaDetailId, Integer areaKotaId,
			Integer pickupTimeId, List<AssignPickupResponse> lAssign, LocalDate pickupDt) {
		return x -> {
            List<TPaymentEntity> lpayment = paymentService.getTPaymentByPickupAddressRequest(x,userId,areaDetailId,areaKotaId,pickupTimeId,pickupDt);
            List<TPickupOrderRequestEntity> lPickupOrder = requestPickUpService.getLisPickupOrderByAddress(x,userId,pickupTimeId,pickupDt);
            AssignPickupResponse assRes = getAssignPickup(lpayment, x, checkPayment(lPickupOrder), true);
            lAssign.add(assRes);
        };
	}

    public AssignPickupResponse getAssignPickup(List<TPaymentEntity> lpay, Integer pickupAddresId, List<TPickupOrderRequestEntity> lRequest) {
        return this.getAssignPickup(lpay, pickupAddresId, lRequest, false);
    }

    public AssignPickupResponse getAssignPickup(List<TPaymentEntity> lpay, Integer pickupAddresId, List<TPickupOrderRequestEntity> lRequest, Boolean isCheckStatus) {
        List<BookDataResponse> detail = new ArrayList<>();
        TPaymentEntity payment = new TPaymentEntity();
        TPickupOrderRequestEntity pickup = new TPickupOrderRequestEntity();
        TPickupAddressEntity tAddress = pickupAddressService.getPickupAddressEntity(pickupAddresId);
        Integer totWeight = 0;
        Integer totVolume = 0;
        Integer totBook = 0;
        Integer mostWeight = 0;
        Integer item = 0;
        Integer countStatusCourier = 0;
        Integer countStatusWarehouse = 0;
        Integer countAddress = 0;
        Boolean isHasCount = false;
        /*countStatusCourier = lpay.stream().filter(p -> p.getStatus() == PaymentEnum.PICKUP_BY_KURIR.getCode()).collect(Collectors.toList()).size()
                + lRequest.stream().filter(p -> p.getStatus() == RequestPickupEnum.IN_COURIER.getValue()).collect(Collectors.toList()).size();
        countStatusWarehouse = lpay.stream().filter(p -> p.getStatus() == PaymentEnum.RECEIVE_IN_WAREHOUSE.getCode()).collect(Collectors.toList()).size()
                + lRequest.stream().filter(p -> p.getStatus() == RequestPickupEnum.IN_WAREHOUSE.getValue()).collect(Collectors.toList()).size();*/
        if (isCheckStatus) {

//            x.setTotalBarangTitipan("Terdapat Barang Titipan Sebanyak "+
//                    this.orderRequestDetailRepo.countAllByOrderRequestEntityUserEntityUserId(x.getCustomerId()));
            for (TPaymentEntity pay : lpay) {
                if (pay.getPickupAddrId().getPickupAddrId().equals(pickupAddresId)) {
                    if (!isHasCount) {
                        countAddress = this.orderRequestDetailRepo.countAllByOrderRequestEntityUserEntityUserIdAndStatusEqual(pay.getUserId().getUserId(),RequestPickupEnum.IN_WAREHOUSE.getValue());
                        isHasCount = true;
                    }
                    BookDataResponse br = paymentService.toBookDataResponse(pay);
                    detail.add(br);
                    totWeight = totWeight + pay.getGrossWeight().intValue();
                    totVolume = totVolume + pay.getVolume().intValue();
                    totBook = totBook + 1;
                    item = item + pay.getJumlahLembar();
                    if (mostWeight < pay.getGrossWeight().intValue()) {
                        mostWeight = pay.getGrossWeight().intValue();
                    }
                    if (pay.getStatus().equals(PaymentEnum.PICKUP_BY_KURIR.getCode()))
                        countStatusCourier++;
                    if (!pay.getStatus().equals(PaymentEnum.ASSIGN_PICKUP.getCode()) &&
                            !pay.getStatus().equals(PaymentEnum.PICKUP_BY_KURIR.getCode()) &&
                            !pay.getStatus().equals(PaymentEnum.DRAFT_PICKUP.getCode()))
                        countStatusWarehouse++;
                }
            }
            for (TPickupOrderRequestEntity p : lRequest) {
                if (p.getPickupAddressEntity().getPickupAddrId().equals(pickupAddresId)) {
                    if (!isHasCount) {
                        countAddress = this.orderRequestDetailRepo.countAllByOrderRequestEntityUserEntityUserIdAndStatusEqual(p.getUserEntity().getUserId(),RequestPickupEnum.IN_WAREHOUSE.getValue());
                        isHasCount = true;
                    }
                    if (checkStatusPay(p)) {
                        BookDataResponse br = generateBookDataResponse(p);
                        if (p.getStatus() == RequestPickupEnum.FINISH_BOOK.getValue()) {
                            List<String> paymentBookId = this.orderRequestDetailRepo.findByOrderRequest(p);
                            List<TPaymentEntity> paymentEntityList = this.paymentService.get(paymentBookId);
                            for (TPaymentEntity pay : paymentEntityList) {
                                if (pay.getPickupAddrId().getPickupAddrId().equals(pickupAddresId)) {
                                    if (!isHasCount) {
                                        countAddress = this.orderRequestDetailRepo.countAllByOrderRequestEntityUserEntityUserIdAndStatusEqual(pay.getUserId().getUserId(),RequestPickupEnum.IN_WAREHOUSE.getValue());
                                        isHasCount = true;
                                    }

                                    br = paymentService.toBookDataResponse(pay);
                                    detail.add(br);
                                    totWeight = totWeight + pay.getGrossWeight().intValue();
                                    totVolume = totVolume + pay.getVolume().intValue();
                                    totBook = totBook + 1;
                                    item = item + pay.getJumlahLembar();
                                    if (mostWeight < pay.getGrossWeight().intValue()) {
                                        mostWeight = pay.getGrossWeight().intValue();
                                    }
                                    if (pay.getStatus().equals(PaymentEnum.PICKUP_BY_KURIR.getCode()))
                                        countStatusCourier++;
                                    if (!pay.getStatus().equals(PaymentEnum.ASSIGN_PICKUP.getCode()) &&
                                            !pay.getStatus().equals(PaymentEnum.PICKUP_BY_KURIR.getCode()) &&
                                            !pay.getStatus().equals(PaymentEnum.DRAFT_PICKUP.getCode()))
                                        countStatusWarehouse++;
                                }
                            }
                        } else {
                            detail.add(br);
                            totBook = totBook + 1;
                            item = item + p.getQty();
                            if (p.getStatus() == RequestPickupEnum.IN_COURIER.getValue())
                                countStatusCourier++;
                            if (p.getStatus() != RequestPickupEnum.REQUEST.getValue() && p.getStatus() != RequestPickupEnum.ASSIGN_PICKUP.getValue() && p.getStatus() != RequestPickupEnum.IN_COURIER.getValue())
                                countStatusWarehouse++;
                        }
                       
                    }
                }
            }
        }
        String address = tAddress.getAddress() + "," + tAddress.getPostalCode().getKelurahan() + ","
                + tAddress.getPostalCode().getKecamatanEntity().getKecamatan() + "," +
                tAddress.getPostalCode().getKecamatanEntity().getKotaEntity().getName() + ","
                + tAddress.getPostalCode().getPostalCode();
        Integer statusAddressIni = 0;//0 Belom Di ambil semua 1 masih nyantorl 2 udah di warehouse semua
        if (countStatusCourier == totBook)
            statusAddressIni = 1;
        if (countStatusWarehouse == totBook)
            statusAddressIni = 2;
        String totalBarangTitipan="";
        if(countAddress!=0){
            totalBarangTitipan="Terdapat Barang Titipan Sebanyak " + countAddress;
        }
        return AssignPickupResponse.builder()
                .customerName(tAddress.getUserId().getName())
                .customerId(tAddress.getUserId().getUserId()+" ("+tAddress.getUserId().getName()+")")
                .addressPickup(address)
                .qtyBook(totBook)
                .totalKg(totWeight)
                .totalVolume(totVolume)
                .totalItem(item)
                .mostWeight(mostWeight)
                .statusAddress(statusAddressIni)
                .telp(tAddress.getUserId().getHp())
                .detailBook(detail)
                .totalBarangTitipan(totalBarangTitipan)
                .pickupAddresId(pickupAddresId)
                .postalCode(tAddress.getPostalCode().getPostalCode())
                .build();
    }
    private List<TPickupOrderRequestEntity> checkPayment(List<TPickupOrderRequestEntity> lPickupOrder){
        List<TPickupOrderRequestEntity> result=new ArrayList<>();
        for (TPickupOrderRequestEntity entity: lPickupOrder) {
            Boolean paycheck = true;
            List<TPickupOrderRequestDetailEntity> pickupDtl = tReqDetailRepo.findAllByOrderRequestEntity(entity);
            for (TPickupOrderRequestDetailEntity pEntity : pickupDtl){
                if (pEntity.getIsPay() == null || pEntity.getIsPay().equals(StatusPayEnum.PICKUP_NOT_PAID.getCode())){
                    paycheck=false;
                    break;
                }
            }
            if(paycheck){
                result.add(entity);
            }
        }
//        Boolean result = true;
        return result;
    }
    public Boolean checkStatusPay(TPickupOrderRequestEntity entity) {
//        List<TPickupOrderRequestDetailEntity> pickupDtl = tReqDetailRepo.findAllByOrderRequestEntity(entity);
//        Boolean result = true;
//        for (TPickupOrderRequestDetailEntity pEntity : pickupDtl) {
//            if (pEntity.getIsPay() == null || pEntity.getIsPay().equals(StatusPayEnum.PICKUP_NOT_PAID.getCode())) {
//                result = false;
//                break;
//            }
//        }
        return true;
    }

    @Transactional
    public Response<String> removePickupDetail(String bookingCode, String userId,String userAdmin) {
        TPickupDetailEntity pickupDtl = tPickupDetailRepo.findByPickupOrdeIdOrbookIdAndCourierId(bookingCode, userId, bookingCode);
        if (pickupDtl == null) {
            throw new NotFoundException("Data Tidak Ditemukan !");
        }
        TPickupEntity pickup = pickupDtl.getPickupId();
        if (pickupDtl.getBookId() != null) {
            TPaymentEntity paymentEntity = pickupDtl.getBookId();
            TPaymentEntity oldPayment = paymentService.createOldPayment(paymentEntity);
            paymentEntity.setStatus(PaymentEnum.REQUEST.getValue());
            paymentService.save(paymentEntity);
            historyTransactionService.createHistory(oldPayment, paymentEntity, userAdmin);
        } else {
            TPickupOrderRequestEntity pickups = pickupDtl.getPickupOrderRequestEntity();
            Integer oldStatus = pickups.getStatus();
            pickups.setStatus(RequestPickupEnum.REQUEST.getValue());
            requestPickUpService.savePickupOrderRequestEntity(pickups);
            historyTransactionService.historyRequestPickup(pickups, null, oldStatus, userId, "");
        }
        tPickupDetailRepo.delete(pickupDtl);
        
        List<TPickupDetailEntity> lsPickupDetail = tPickupDetailRepo.findByPickupId(pickup);
        if (lsPickupDetail.size() == 0) {
            pickup.setStatus(PickupEnum.ACCEPT_IN_WAREHOUSE.getValue());
            pickup.setModifyBy(userAdmin);
    		pickup.setModifyAt(LocalDateTime.now());
    		pickupService.savePickupEntity(pickup);
        }else {
        	Integer status = lsPickupDetail.get(0).getStatus();
        	for(TPickupDetailEntity dtl : lsPickupDetail) {
        		if(dtl.getStatus() < status) {
        			status = dtl.getStatus();
        		}
        	}
        	pickup.setStatus(status);
            pickup.setModifyBy(userAdmin);
    		pickup.setModifyAt(LocalDateTime.now());
    		pickupService.savePickupEntity(pickup);
        }
       
        verifyAndRemoveInPickupCourier(pickup.getIdPickup());
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase()
        );
    }

    private void verifyAndRemoveInPickupCourier(Integer pickupId) {
        List<Integer> pickAddrIds = tPickupDetailRepo.courierStatusInAssignPickup(pickupId).stream()
                .map(PickupAddress::getPickupAddressId).collect(Collectors.toList());

        List<TCourierPickupEntity> newCandidateRemove = intersectOfPickupAddress(pickAddrIds, pickupId);
        newCandidateRemove.forEach(v -> courierPickupRepo.delete(v));
        verifyPickupDetail(pickupId);
    }

    private List<TCourierPickupEntity> intersectOfPickupAddress(List<Integer> pickAddrIds, Integer pickupId) {
        return courierPickupRepo.findByPickupIdPickup(pickupId).stream()
                .filter(v -> !pickAddrIds.contains(v.getPickupAddress().getPickupAddrId())).collect(Collectors.toList());
    }

    private void verifyPickupDetail(int pickupId) {
        Set<Integer> pickupAddrIds = tPickupDetailRepo.findByPickupIdIdPickup(pickupId).stream()
                .map(TPickupDetailEntity::getPickupAddrId)
                .map(TPickupAddressEntity::getPickupAddrId)
                .collect(Collectors.toSet());

        pickupAddrIds.forEach(pickupAddrId -> {
            List<TPickupDetailEntity> pickupIdAndPickupAddr = tPickupDetailRepo.findByPickupIdAndPickupAddr(pickupId, pickupAddrId);

            filterStatusOfPickupDetail(pickupId, pickupAddrId, pickupIdAndPickupAddr);
        });
    }

    private void filterStatusOfPickupDetail(int pickupId, int pickupAddrId, List<TPickupDetailEntity> pickupIdAndPickupAddr) {
        boolean isAssignPickup = pickupIdAndPickupAddr.stream().map(TPickupDetailEntity::getStatus).anyMatch(status -> status.equals(PickupDetailEnum.ASSIGN_PICKUP.getValue()));
        boolean isIssueInAddress = pickupIdAndPickupAddr.stream().map(TPickupDetailEntity::getStatus).anyMatch(status -> status.equals(PickupDetailEnum.REJECTED_PICKUP.getValue()));
        boolean isAllInCourier = pickupIdAndPickupAddr.stream().map(TPickupDetailEntity::getStatus).allMatch(status -> status.equals(PickupDetailEnum.IN_COURIER.getValue()));
        boolean isAllInWarehouse = pickupIdAndPickupAddr.stream().map(TPickupDetailEntity::getStatus).allMatch(status -> status.equals(PickupDetailEnum.IN_WAREHOUSE.getValue()));

        if (isIssueInAddress) {
            updateStatusCourierPickup(pickupId, pickupAddrId, PickupCourierEnum.ISSUES_IN_ADDRESS.getValue());
        } else if (isAssignPickup) {
            updateStatusCourierPickup(pickupId, pickupAddrId, PickupCourierEnum.READY_PICKUP.getValue());
        } else if (isAllInCourier) {
            updateStatusCourierPickup(pickupId, pickupAddrId, PickupCourierEnum.FINISH_PICKUP.getValue());
        } else if (isAllInWarehouse) {
            updateStatusCourierPickup(pickupId, pickupAddrId, PickupCourierEnum.FINISH.getValue());
        }
    }

    private void updateStatusCourierPickup(int pickupId, int pickupAddressId, int status) {
        TCourierPickupEntity courierPickup = courierPickupRepo.findByPickupIdAndPickupAddressId(pickupId, pickupAddressId);
        courierPickup.setStatus(status);

        courierPickupRepo.save(courierPickup);
    }

    public Response<String> deleteManifest(String noManifets, String userId) {
        TPickupEntity pickup = tPickupRepo.findByCourierIdUserIdAndCode(userId, noManifets);
        if (pickup == null) {
            throw new NotFoundException("Data tidak Ditemukan !");
        }
        if (!pickup.getStatus().equals(PickupEnum.DRAFT.getValue())) {
            throw new NotFoundException("Manifest tidak dapat dihapus !");
        }
        List<TPickupDetailEntity> lpickupDtl = tPickupDetailRepo.findByPickupId(pickup);
        for (TPickupDetailEntity dtl : lpickupDtl) {
            if (dtl.getBookId() != null) {
                TPaymentEntity paymentEntity = dtl.getBookId();
                TPaymentEntity oldPay = paymentService.createOldPayment(paymentEntity);
                paymentEntity.setStatus(PaymentEnum.REQUEST.getValue());
                paymentService.save(paymentEntity);
                historyTransactionService.createHistory(oldPay, paymentEntity, userId);
            } else {
                TPickupOrderRequestEntity reqpickup = dtl.getPickupOrderRequestEntity();
                Integer oldStatus = reqpickup.getStatus();
                reqpickup.setStatus(RequestPickupEnum.REQUEST.getValue());
                requestPickUpService.savePickupOrderRequestEntity(reqpickup);
                historyTransactionService.historyRequestPickup(reqpickup, null, oldStatus, userId, "");
            }
            tPickupDetailRepo.delete(dtl);
        }
        List<TCourierPickupEntity> candidateRemove = courierPickupRepo.findByPickupIdPickup(pickup.getIdPickup());
        tPickupRepo.delete(pickup);

        removeInListCourierPickup(pickup.getIdPickup(), candidateRemove);
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase()
        );
    }

    private void removeInListCourierPickup(Integer id, List<TCourierPickupEntity> candidateRemove) {
        boolean isPickupExist = tPickupRepo.existsById(id);

        if (!isPickupExist) {
            candidateRemove.forEach(v -> courierPickupRepo.delete(v));
        }
    }

    @Transactional
    public SaveResponse addDetailManifest(CreateManifestReq request,String userLogin) {
        TPickupEntity pickup = tPickupRepo.findByCode(request.getManifestCode());
        if (pickup == null) throw new NotFoundException("Manifest Tidak Ditemukan !");
        if (pickup.getStatus().equals(PickupEnum.ACCEPT_IN_WAREHOUSE.getValue()) || pickup.getStatus().equals(PickupEnum.HOLD_BY_WAREHOUSE.getValue())) {
            throw new NotFoundException("Status Manifest sudah In Warehouse");
        }
        for (CreateDetailManifestReq dtl : request.getDetail()) {
            TPickupDetailEntity entity = tPickupDetailRepo.findByBookIdAndCode(dtl.getBookingCode(), request.getManifestCode());
            TPaymentEntity payment = paymentService.get(dtl.getBookingCode());
            TPickupOrderRequestEntity requestPickup = requestPickUpService.getPickupOrderRequestEntity(dtl.getBookingCode());
            if (entity != null) {
                throw new NotFoundException("Booking Code sudah ada di Manifest lain");
            } else {
                if (payment != null) {
                    if (payment.getStatus().equals(PaymentEnum.PENDING.getCode()))
                        throw new NotFoundException("Pesanan Belum Bayar !");
                    TPaymentEntity entityPaymentHistory = paymentService.createOldPayment(payment);
                    if (pickup.getStatus().equals(PickupEnum.ASSIGN_PICKUP.getValue())) {
                        payment.setStatus(PaymentEnum.ASSIGN_PICKUP.getCode());
                    } else if (pickup.getStatus().equals(PickupEnum.DRAFT.getValue())) {
                        payment.setStatus(PaymentEnum.DRAFT_PICKUP.getCode());
                    }
                    entity = TPickupDetailEntity.builder()
                            .pickupId(pickup)
                            .bookId(payment)
                            .pickupOrderRequestEntity(requestPickup)
                            .pickupAddrId(payment.getPickupAddrId())
                            .status(PickupDetailEnum.ASSIGN_PICKUP.getValue())
                            .createBy(request.getUserId())
                            .createDate(LocalDateTime.now())
                            .build();
                    paymentService.save(payment);
                    historyTransactionService.createHistory(entityPaymentHistory, payment, request.getUserId());
                    TPaymentHistoryEntity pHistory = tPaymentHistoryRepo.findFirstByBookingCodeAndLastStatusOrderByLastUpdateDesc(payment, payment.getStatus());
        			pHistory.setReason("Courier Name : "+pickup.getCourierId().getName());
                } else if (requestPickup != null) {
                	Integer oldStatus = requestPickup.getStatus();
                    List<TPickupOrderRequestDetailEntity> lPickupDtl = tReqDetailRepo.findAllByOrderRequestEntity(requestPickup);
                    if (pickup.getStatus().equals(PickupEnum.ASSIGN_PICKUP.getValue())) {
                        requestPickup.setStatus(RequestPickupEnum.ASSIGN_PICKUP.getValue());
                    } else if (pickup.getStatus().equals(PickupEnum.DRAFT.getValue())) {
                        requestPickup.setStatus(RequestPickupEnum.DRAFT_PICKUP.getValue());
                    }
                    entity = TPickupDetailEntity.builder()
                            .pickupId(pickup)
                            .bookId(payment)
                            .pickupOrderRequestEntity(requestPickup)
                            .pickupAddrId(requestPickup.getPickupAddressEntity())
                            .status(PickupDetailEnum.ASSIGN_PICKUP.getValue())
                            .createBy(request.getUserId())
                            .createDate(LocalDateTime.now())
                            .build();
                    for (TPickupOrderRequestDetailEntity rdtl : lPickupDtl) {
                        rdtl.setStatus(requestPickup.getStatus());
                        tReqDetailRepo.save(rdtl);
                    }
                    requestPickUpService.savePickupOrderRequestEntity(requestPickup);
                    String reason = "Courier Name : "+pickup.getCourierId().getName();
                    historyTransactionService.historyRequestPickup(requestPickup, null, oldStatus, userLogin,reason);
                } else {
                    throw new NotFoundException("Data Tidak Ditemukan !");
                }
                tPickupDetailRepo.save(entity);
            }
        }

        routingPickAddrForCourier(pickup.getIdPickup());

        return SaveResponse.builder()
                .saveStatus(1)
                .saveInformation("Berhasil Tambah Detail Manifest")
                .build();
    }
}
