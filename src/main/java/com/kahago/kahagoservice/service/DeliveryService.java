package com.kahago.kahagoservice.service;
/**
 * @author Ibnu Wasis
 */

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestDetailEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestEntity;
import com.kahago.kahagoservice.enummodel.FilterBookingEnum;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.RequestPickupEnum;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.request.DeliveryReq;
import com.kahago.kahagoservice.model.response.DeliveryResponse;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestDetailRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestRepo;
import com.kahago.kahagoservice.util.Common;
import com.kahago.kahagoservice.util.DateTimeUtil;
@Service
public class DeliveryService {
	@Autowired
	private TPaymentRepo tPaymentRepo;
	@Autowired
	private TPickupOrderRequestDetailRepo tOrderRequestDetailRepo;
	@Autowired
	private TPickupOrderRequestRepo tOrderRequestRepo;
	
	private static final int limit = 10;
	public List<DeliveryResponse> getAssignPickup(DeliveryReq req){
		List<DeliveryResponse> listdelivery = new ArrayList<DeliveryResponse>();
		List<TPaymentEntity> listpayment = new ArrayList<>();
		List<TPickupOrderRequestDetailEntity> lpickupDetail = new ArrayList<>();
		List<TPickupOrderRequestEntity> lpickupRequest = new ArrayList<>();
		List<Integer> statusCode = new ArrayList<>();
		List<Integer> statusRequest = new ArrayList<>();
		if(req.getFilterby() == null)req.setFilterby(0);
		if(req.getCari()==null)req.setCari("");
		if(req.getPage()==null)req.setPage(0);
		int seq;
		switch(req.getStatus()) {
		case "pending":
			statusCode.add(PaymentEnum.REQUEST.getCode());
			statusCode.add(PaymentEnum.ASSIGN_PICKUP.getCode());
			statusCode.add(PaymentEnum.DRAFT_PICKUP.getCode());
			statusRequest.add(RequestPickupEnum.REQUEST.getValue());
			statusRequest.add(RequestPickupEnum.ASSIGN_PICKUP.getValue());
			//if(req.getPage().equals(0)) {
				//listpayment = tPaymentRepo.findByStatusAndUserIdReceiverIdSenderId(statusCode,req.getUserId());
				lpickupRequest = tOrderRequestRepo.findAllByUserIdAndStatus(req.getUserId(), statusRequest);
				seq = 0;
				
				/*for(TPaymentEntity tp : listpayment) {
					seq = seq+1;
					if(req.getCari().isEmpty()) {
						tp = getPaymentByUserId(req.getFilterby(), req.getUserId(), tp, req.getIdSearch());
						if(tp.getBookingCode() != null) {
							listdelivery.add(setDeliveryResponse(tp, seq));
						}
					}else {
						if(tp.getSenderName().toLowerCase().contains(req.getCari().toLowerCase()) || 
								tp.getReceiverName().toLowerCase().contains(req.getCari().toLowerCase()) ||
								tp.getBookingCode().toLowerCase().contains(req.getCari().toLowerCase()) || 
								tp.getStt().toLowerCase().contains(req.getCari().toLowerCase())) {
							tp = getPaymentByUserId(req.getFilterby(), req.getUserId(), tp, req.getIdSearch());
							if(tp.getBookingCode()!= null) {
								listdelivery.add(setDeliveryResponse(tp, seq));
							}
						}
					}
				}*/
				//list request pickup
				for(TPickupOrderRequestEntity or : lpickupRequest) {
					lpickupDetail = tOrderRequestDetailRepo.findAllByOrderRequestEntityAndStatus(or, statusRequest);
					if(req.getCari().isEmpty()) {
						//or = getPaymentByUserId(req.getFilterby(), req.getUserId(), or, req.getIdSearch());
						if(lpickupDetail.size() > 0) {
							for(TPickupOrderRequestDetailEntity et:lpickupDetail) {
								if(et.getIsPay()==null) et.setIsPay(0);
								if(et.getIsPay().equals(0)|| et.getIsPay().equals(1)) {
									seq = seq+1;
									listdelivery.add(setDeliveryResponse(or, seq, et));
								}								
							}
						}else {
							seq = seq+1;
							listdelivery.add(setDeliveryResponse(or, seq, null));
						}
					}else {
						if(or.getUserEntity().getName().toLowerCase().contains(req.getCari().toLowerCase())
								|| or.getPickupOrderId().toLowerCase().contains(req.getCari().toLowerCase())) {
							//or = getPaymentByUserId(req.getFilterby(), req.getUserId(), or, req.getIdSearch());
							if(lpickupDetail.size() > 0) {
								for(TPickupOrderRequestDetailEntity et:lpickupDetail) {
									seq = seq+1;
									listdelivery.add(setDeliveryResponse(or, seq, et));
								}
							}else {
								seq = seq+1;
								listdelivery.add(setDeliveryResponse(or, seq, null));
							}
						}
					}
				}
				
		break;
		case "final":
			statusCode.add(PaymentEnum.PICKUP_BY_KURIR.getCode());
			statusCode.add(PaymentEnum.ACCEPT_IN_WAREHOUSE.getCode());
			statusCode.add(PaymentEnum.BAGGING.getCode());
			statusCode.add(PaymentEnum.SEND_TO_VENDOR.getCode());
			statusCode.add(PaymentEnum.PICK_BY_VENDOR.getCode());
			statusCode.add(PaymentEnum.ACCEPT_BY_VENDOR.getCode());
			statusCode.add(PaymentEnum.RECEIVE.getCode());
			statusCode.add(PaymentEnum.HOLD_BY_WAREHOUSE.getCode());
			statusCode.add(PaymentEnum.RECEIVE_IN_WAREHOUSE.getCode());
			statusCode.add(PaymentEnum.FINISH_INPUT_AND_PAID.getCode());
			statusRequest.add(RequestPickupEnum.IN_COURIER.getValue());
			statusRequest.add(RequestPickupEnum.IN_WAREHOUSE.getValue());
			//if(req.getPage().equals(0)) {
				//listpayment = tPaymentRepo.findByStatusAndUserIdReceiverIdSenderId(statusCode,req.getUserId());
				lpickupRequest = tOrderRequestRepo.findAllByUserIdAndStatus(req.getUserId(), statusRequest);
				seq = 0;
				/*for(TPaymentEntity tp : listpayment) {
					seq = seq+1;
					if(req.getCari().isEmpty()) {
						tp = getPaymentByUserId(req.getFilterby(), req.getUserId(), tp, req.getIdSearch());
						if(tp.getBookingCode() != null) {
							listdelivery.add(setDeliveryResponse(tp, seq));
						}
					}else {
						if(tp.getSenderName().toLowerCase().contains(req.getCari().toLowerCase()) || 
								tp.getReceiverName().toLowerCase().contains(req.getCari().toLowerCase()) ||
								tp.getBookingCode().toLowerCase().contains(req.getCari().toLowerCase()) || 
								tp.getStt().toLowerCase().contains(req.getCari().toLowerCase())) {
							tp = getPaymentByUserId(req.getFilterby(), req.getUserId(), tp, req.getIdSearch());
							if(tp.getBookingCode()!= null) {
								listdelivery.add(setDeliveryResponse(tp, seq));
							}
						}
					}
				}*/
				for(TPickupOrderRequestEntity or : lpickupRequest) {
					
					lpickupDetail = tOrderRequestDetailRepo.findAllByOrderRequestEntityAndStatus(or, statusRequest);
					if(req.getCari().isEmpty()) {
						//or = getPaymentByUserId(req.getFilterby(), req.getUserId(), or, req.getIdSearch());
						if(lpickupDetail.size() > 0) {
							for(TPickupOrderRequestDetailEntity et:lpickupDetail) {
								if(et.getIsPay()==null) et.setIsPay(0);
								if(et.getIsPay().equals(0)|| et.getIsPay().equals(1)) {
									seq = seq+1;
									listdelivery.add(setDeliveryResponse(or, seq, et));
								}	
							}
						}else {
							seq = seq+1;
							listdelivery.add(setDeliveryResponse(or, seq, null));
						}
					}else {
						if(or.getUserEntity().getName().toLowerCase().contains(req.getCari().toLowerCase())
								|| or.getPickupOrderId().toLowerCase().contains(req.getCari().toLowerCase())) {
							//or = getPaymentByUserId(req.getFilterby(), req.getUserId(), or, req.getIdSearch());
							if(lpickupDetail.size() > 0) {
								for(TPickupOrderRequestDetailEntity et:lpickupDetail) {
									seq = seq+1;
									listdelivery.add(setDeliveryResponse(or, seq, et));
								}
							}else {
								seq = seq+1;
								listdelivery.add(setDeliveryResponse(or, seq, null));
							}
						}
					}
				}
			
			break;
		default:
			break;
		
		}
		Comparator<DeliveryResponse> dateSorter = (a,b)->a.getTrxDate().compareToIgnoreCase(b.getTrxDate());
		Collections.sort(listdelivery, dateSorter.reversed());
		return listdelivery;
	}
	
	private List<TPaymentEntity> getPaymentByUserId(Integer filter,String UserId,String idSearch,String cari,Pageable pageable,List<Integer> status, String action) {
		FilterBookingEnum filterBookingEnum = FilterBookingEnum.getFilterBookingEnum(filter);
		TPaymentEntity pay = new TPaymentEntity();
		List<TPaymentEntity> lPayment = new ArrayList<>();
		Page<TPaymentEntity> PagePayment = null;
		switch(filterBookingEnum) {
		case ID_USER:
			if(action.equals("final"))
				PagePayment = tPaymentRepo.findByStatusAndUserIdReceiverIdSenderId(status, UserId, cari, idSearch, null, null, pageable);
			else
				lPayment = tPaymentRepo.findByStatusAndUserIdReceiverIdSenderIdNoPage(status, UserId, cari, idSearch, null, null);
			break;
		case SELF_SENDER:
			if(action.equals("final"))
				PagePayment = tPaymentRepo.findByStatusAndUserIdReceiverIdSenderId(status, UserId, cari, null, UserId, null, pageable);
			else
				lPayment = tPaymentRepo.findByStatusAndUserIdReceiverIdSenderIdNoPage(status, UserId, cari, null, UserId, null);
			break;
		case SELF_RECEIVER:
			if(action.equals("final"))
				PagePayment = tPaymentRepo.findByStatusAndUserIdReceiverIdSenderId(status, UserId, cari, null, null, UserId, pageable);
			else
				lPayment = tPaymentRepo.findByStatusAndUserIdReceiverIdSenderIdNoPage(status, UserId, cari, null, null, UserId);
			break;
		default:
			if(action.equals("final"))
				PagePayment = tPaymentRepo.findByStatusAndUserIdReceiverIdSenderId(status, UserId, cari, null, null, null, pageable);
			else
				lPayment = tPaymentRepo.findByStatusAndUserIdReceiverIdSenderIdNoPage(status, UserId, cari, null, null, null);
			break;
		}
		if(PagePayment != null) {
			lPayment.addAll(PagePayment.getContent());
		}		
		return lPayment;
	}
	private List<TPickupOrderRequestEntity> getPaymentByUserId(Integer filter,String UserId,List<Integer> status,String idSearch,String cari,Pageable pageable,String action) {
		FilterBookingEnum filterBookingEnum = FilterBookingEnum.getFilterBookingEnum(filter);
		List<TPickupOrderRequestEntity> lorder = new ArrayList<>();
		Page<TPickupOrderRequestEntity> pageOrder = null;
		switch(filterBookingEnum) {
		case ID_USER:
			if(action.equals("final"))
				pageOrder = tOrderRequestRepo.findByStatusAndUserId(status, UserId, cari, idSearch, pageable);
			else
				lorder = tOrderRequestRepo.findByStatusAndUserIdNoPage(status, UserId, cari, idSearch);
			break;
		case SELF_SENDER:
			if(action.equals("final"))
				pageOrder = tOrderRequestRepo.findByStatusAndUserId(status, UserId, cari, null, pageable);
			else 
				lorder = tOrderRequestRepo.findByStatusAndUserIdNoPage(status, UserId, cari, null);
			break;
		
		default:
			if(action.equals("final"))
				pageOrder = tOrderRequestRepo.findByStatusAndUserId(status, UserId, cari, null, pageable);
			else 
				lorder = tOrderRequestRepo.findByStatusAndUserIdNoPage(status, UserId, cari, null);
			break;
		}
		if(pageOrder != null) {
			lorder.addAll(pageOrder.getContent());
		}
		return lorder;
	}
	
 private DeliveryResponse setDeliveryResponse(TPaymentEntity payment, Integer seq) {
	 DeliveryResponse dr = new DeliveryResponse();
	 String tourl="";
	 LocalDate date = LocalDate.now();
	 if(payment.getInsufficientFund() == null) {
		 payment.setInsufficientFund(BigDecimal.ZERO);
	 }
	 dr.setBookingCode(payment.getBookingCode());
	 dr.setReceiverName(payment.getReceiverName());
	 dr.setSenderName(payment.getSenderName());
	 dr.setStt(payment.getStt());
	 dr.setAmount(payment.getAmount().subtract(payment.getInsufficientFund()));
	 dr.setQrcode(payment.getQrcode());
	 dr.setTrxDate(payment.getTrxDate().toString()+" "+DateTimeUtil.getTime(payment.getTrxTime()));
	 dr.setIsBooking(true);
	 tourl = "api/resi/kahago?bookingcode="+payment.getBookingCode()+"&userid="+payment.getUserId().getUserId();
 	 dr.setUrlresi(tourl);
	 if(dr.getQrcode() != null && dr.getQrcode().equals("-") && payment.getStatus() < 3) {
		 dr.setQrcode(Common.gerQrCode());
		 payment.setQrcode(dr.getQrcode());
		 payment.setQrcodeDate(date);
		 tPaymentRepo.save(payment);
	 }
	 return dr;
 }
 private DeliveryResponse setDeliveryResponse(TPickupOrderRequestEntity entity, Integer seq,TPickupOrderRequestDetailEntity et) {
	 String tourl="-";
	 DeliveryResponse dr = new DeliveryResponse();
	 DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	 if(et != null) {		 	 
			 dr.setBookingCode(et.getOrderRequestEntity().getPickupOrderId());
			 dr.setAmount(et.getAmount()==null?BigDecimal.ZERO:et.getAmount());
			 dr.setQrcode(et.getQrCode()==null?"-":et.getQrCode());
			 dr.setReceiverName(et.getNamaPenerima()==null?"-":et.getNamaPenerima());
			 dr.setSenderName(et.getOrderRequestEntity().getUserEntity().getName());
			 dr.setStt("-");
		 	 dr.setUrlresi(tourl);
		 	 dr.setTrxDate(entity.getCreateDate()==null?"-":entity.getCreateDate().format(format));
		 	 dr.setIsBooking(false);
	 }else {
		 dr.setBookingCode(entity.getPickupOrderId());
		 dr.setAmount(BigDecimal.ZERO);
		 dr.setQrcode("-");
		 dr.setReceiverName("-");
		 dr.setSenderName(entity.getUserEntity().getName());
		 dr.setStt("-");
	 	 dr.setUrlresi(tourl);
	 	 dr.setTrxDate(entity.getCreateDate()==null?"-":entity.getCreateDate().format(format));
	 	 dr.setIsBooking(false);
	 }
	 return dr;
 }
 
 public List<DeliveryResponse> getPayment(DeliveryReq req, Pageable pageable){
	 List<DeliveryResponse> result = new ArrayList<>();
	 List<TPaymentEntity> lPayment= new ArrayList<>();
	 List<TPickupOrderRequestEntity> lOrder= new ArrayList<>();
	 if(req.getFilterby() == null)req.setFilterby(0);
	 if(req.getCari()==null)req.setCari("");
	// if(req.getPage()==null)req.setPage(0);
	// if(req.getPage()==null)req.setPage(1);

	 List<Integer> statusCode = new ArrayList<>();
	 List<Integer> statusRequest = new ArrayList<>();
	 switch(req.getStatus()) {
		case "pending":
			statusCode.add(PaymentEnum.REQUEST.getCode());
			statusCode.add(PaymentEnum.ASSIGN_PICKUP.getCode());
			statusCode.add(PaymentEnum.DRAFT_PICKUP.getCode());
			statusRequest.add(RequestPickupEnum.REQUEST.getValue());
			statusRequest.add(RequestPickupEnum.ASSIGN_PICKUP.getValue());
			if(req.getCari().isEmpty()) {
				lPayment = getPaymentByUserId(req.getFilterby(), req.getUserId(), req.getIdSearch(), null, pageable, statusCode,req.getStatus());
				lOrder = getPaymentByUserId(req.getFilterby(), req.getUserId(), statusRequest, req.getIdSearch(), null, pageable,req.getStatus());
			}else {
				lPayment = getPaymentByUserId(req.getFilterby(), req.getUserId(), req.getIdSearch(), req.getCari(), pageable, statusCode,req.getStatus());
				lOrder = getPaymentByUserId(req.getFilterby(), req.getUserId(), statusRequest, req.getIdSearch(), req.getCari(), pageable,req.getStatus());
			}
			break;
		case "final":
			statusCode.add(PaymentEnum.PICKUP_BY_KURIR.getCode());
			statusCode.add(PaymentEnum.ACCEPT_IN_WAREHOUSE.getCode());
			statusCode.add(PaymentEnum.BAGGING.getCode());
			statusCode.add(PaymentEnum.SEND_TO_VENDOR.getCode());
			statusCode.add(PaymentEnum.PICK_BY_VENDOR.getCode());
			statusCode.add(PaymentEnum.ACCEPT_BY_VENDOR.getCode());
			statusCode.add(PaymentEnum.RECEIVE.getCode());
			statusCode.add(PaymentEnum.HOLD_BY_WAREHOUSE.getCode());
			statusCode.add(PaymentEnum.RECEIVE_IN_WAREHOUSE.getCode());
			statusCode.add(PaymentEnum.FINISH_INPUT_AND_PAID.getCode());
			statusCode.add(PaymentEnum.HOLD_BY_ADMIN.getCode());
			statusCode.add(PaymentEnum.RECEIVE_IN_COUNTER.getCode());
			statusCode.add(PaymentEnum.APPROVE_BY_COUNTER.getCode());
			statusCode.add(PaymentEnum.OUTGOING_BY_COUNTER.getCode());
			statusCode.add(PaymentEnum.RETUR_BY_VENDOR.getCode());
			statusCode.add(PaymentEnum.BAGGING_BY_COUNTER.getCode());
			statusCode.add(PaymentEnum.ACCEPT_WITHOUT_RESI.getCode());
			statusRequest.add(RequestPickupEnum.IN_COURIER.getValue());
			statusRequest.add(RequestPickupEnum.IN_WAREHOUSE.getValue());
			if(req.getCari().isEmpty()) {
				lPayment = getPaymentByUserId(req.getFilterby(), req.getUserId(), req.getIdSearch(), null, pageable, statusCode, req.getStatus());
				lOrder = getPaymentByUserId(req.getFilterby(), req.getUserId(), statusRequest, req.getIdSearch(), null, pageable, req.getStatus());
			}else {
				lPayment = getPaymentByUserId(req.getFilterby(), req.getUserId(), req.getIdSearch(), req.getCari(), pageable, statusCode, req.getStatus());
				lOrder = getPaymentByUserId(req.getFilterby(), req.getUserId(), statusRequest, req.getIdSearch(), req.getCari(), pageable, req.getStatus());
			}
			break;
		default:
			break;
	 }
	 Integer seq =0;
	 if(lPayment.size() > 0) {
		 for(TPaymentEntity pay:lPayment) {
			 result.add(setDeliveryResponse(pay, seq++));
		 } 
	 }
	 if(lOrder.size() > 0) {
		 for(TPickupOrderRequestEntity pd : lOrder) {
			 List<TPickupOrderRequestDetailEntity> ldetail = tOrderRequestDetailRepo.findAllByOrderRequestEntityAndIsPayIn(pd, Arrays.asList(0,1));
			 if(ldetail.size() > 0) {
				 for(TPickupOrderRequestDetailEntity dt:ldetail) {
					 DeliveryResponse respon = setDeliveryResponse(pd, seq, dt);
					 if(!result.contains(respon))
						 result.add(respon);
				 }
			 }else {
				 result.add(setDeliveryResponse(pd, seq, null));
			 }
		 }
	 }	 
	Comparator<DeliveryResponse> dateSorter = (a,b)->a.getTrxDate().compareToIgnoreCase(b.getTrxDate());
	Collections.sort(result, dateSorter.reversed());
	 return result;
 }

}
