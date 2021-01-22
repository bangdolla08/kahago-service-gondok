package com.kahago.kahagoservice.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.kahago.kahagoservice.entity.MPickupTimeEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.entity.TPickupDetailEntity;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.BookRequest;
import com.kahago.kahagoservice.model.request.DetailBooking;
import com.kahago.kahagoservice.model.response.BookResponse;
import com.kahago.kahagoservice.repository.MPickupTimeRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.repository.TPickupAddressRepo;
import com.kahago.kahagoservice.repository.TPickupDetailRepo;
import com.kahago.kahagoservice.util.Common;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EditBookService {
	@Autowired
	private TPaymentRepo payRepo;
	@Autowired
	private TPickupAddressRepo pickupAddressRepo;
	@Autowired
	private MPickupTimeRepo pickupTimeRepo;
	@Autowired
	private BookService bookService;
	@Autowired
	private TPickupDetailRepo tPickupDetailRepo;
	@Autowired
	private HistoryTransactionService historyTransactionService;
	@Autowired
	private PaymentService paymentService;
	
	@Transactional
	public BookResponse editBook(BookRequest req) {
		log.info("==> Update Booking <==");
		TPaymentEntity pay = payRepo.findByBookingCodeIgnoreCaseContaining(req.getBookingCode());
		TPickupDetailEntity pickupDtl = tPickupDetailRepo.findByBookIdBookingCode(pay.getBookingCode()).orElse(null);
		DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss");
		checkValidasiUser(pay,req.getUserId());
		checkStatusPayment(pay);
		checkVolumeAndWeight(req);
		MPickupTimeEntity time = pickupTimeRepo.findByIdPickupTime(req.getIdPickupTime());
		// add kondidi jika edit pikcup time @Ibnu
		if(!pay.getPickupTimeId().getIdPickupTime().equals(req.getIdPickupTime()) && pickupDtl != null) {
			TPaymentEntity oldPay = paymentService.createOldPayment(pay);
			if(pay.getStatus().equals(PaymentEnum.ASSIGN_PICKUP.getCode())||pay.getStatus().equals(PaymentEnum.DRAFT_PICKUP.getCode())) {
				pay.setStatus(PaymentEnum.REQUEST.getCode());
				tPickupDetailRepo.delete(pickupDtl);
				historyTransactionService.createHistory(oldPay, pay, req.getUserId());
			}
		}
		//end
		pay.setNote(req.getNote());
		pay.setSenderName(req.getSenderName());
		pay.setSenderAddress(req.getSenderAddress());
		pay.setSenderTelp(req.getSenderTelp());
		pay.setSenderEmail(req.getSenderEmail());
		pay.setReceiverName(req.getReceiverName());
		pay.setReceiverAddress(req.getReceiverAddress());
		pay.setReceiverEmail(req.getReceiverEmail());
		pay.setReceiverTelp(req.getReceiverTelp());
		pay.setPickupAddrId(pickupAddressRepo.getOne(Integer.valueOf(req.getPickupId())));
		pay.setPickupDate(bookService.getPickupDate(LocalDate.parse(req.getPickupDate())));
		pay.setPickupTime(time.getTimeFrom().format(format)+" - "+time.getTimeTo().format(format));
		pay.setPickupTimeId(time);
		payRepo.save(pay);
		return BookResponse.builder()
				.bookingCode(pay.getBookingCode())
				.senderName(pay.getSenderName())
				.receiverName(pay.getReceiverName())
				.officerId(pay.getUserId().getUserId())
				.urlResi(Common.getResi(pay))
				.origin(pay.getOrigin())
				.destination(pay.getDestination())
				.trxDate(req.getTrxDate())
				.amount(pay.getAmount().toString())
				.build();
	}
	private void checkVolumeAndWeight(BookRequest req) {
		// TODO Auto-generated method stub
		String vol = Optional.ofNullable(req.getTotalVolume()).orElse("0");
		String weight = Optional.ofNullable(req.getTotalGrossWeight()).orElse("0");
		if(Integer.valueOf(vol)<=0
				|| Integer.valueOf(weight)<=0) {
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Total Volume dan Gross Weight Tidak Boleh 0");
		}
	}
	private void checkStatusPayment(TPaymentEntity pay) {
		// TODO Auto-generated method stub
		if(pay.getStatus() > PaymentEnum.ASSIGN_PICKUP.getCode() && !pay.getStatus().equals(PaymentEnum.DRAFT_PICKUP.getCode())) {
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,ResponseStatus.NOT_CHANGED.getReasonPhrase());
		}
		
	}
	private void checkValidasiUser(TPaymentEntity pay, String userId) {
		// TODO Auto-generated method stub
		if(!pay.getUserId().getUserId().equalsIgnoreCase(userId))
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseStatus.NEED_APPROVAL.getReasonPhrase());
		
	}
}
