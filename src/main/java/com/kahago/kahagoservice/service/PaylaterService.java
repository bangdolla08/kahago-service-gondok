package com.kahago.kahagoservice.service;

import static com.kahago.kahagoservice.util.ImageConstant.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.transaction.Transactional;

import com.kahago.kahagoservice.model.response.BookDataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.entity.TPaymentHistoryEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestDetailEntity;
import com.kahago.kahagoservice.enummodel.PayLaterEnum;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.RequestPickupEnum;
import com.kahago.kahagoservice.enummodel.StatusPayEnum;
import com.kahago.kahagoservice.enummodel.UserCategoryEnum;
import com.kahago.kahagoservice.model.request.PaylaterIssuedRequest;
import com.kahago.kahagoservice.model.response.PaylaterList;
import com.kahago.kahagoservice.repository.TPaymentHistoryRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestDetailRepo;
import com.kahago.kahagoservice.util.DateTimeUtil;


/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 24 Des 2019
 */
@Service
public class PaylaterService {
	
	@Autowired
	private TPaymentRepo paymentRepo;

	@Autowired
	private PaymentService paymentService;
	@Autowired
	private BookService bookService;
	@Autowired
	private DiscountService discok;
	@Autowired
	private HistoryTransactionService hisPayment;
	@Autowired
	private TPaymentHistoryRepo tPaymentHistoryRepo;
	@Autowired
	private TPickupOrderRequestDetailRepo tPickupOrderRequestDetailRepo;
	
	public List<BookDataResponse> getListPaylater(PaylaterIssuedRequest request){
		List<Integer> status = new ArrayList<>();
		List<Integer> statusPay = new ArrayList<>() ;
		status.add(PaymentEnum.PENDING.getCode());
		status.add(PaymentEnum.UNPAID_RECEIVE.getCode());
		status.add(PaymentEnum.HOLD_BY_ADMIN.getCode());
		//status.add(PaymentEnum.HOLD_BY_WAREHOUSE.getCode());
		statusPay.add(StatusPayEnum.VERIFICATION.getCode());
		statusPay.add(StatusPayEnum.PICKUP_NOT_PAID.getCode());
		List<TPaymentEntity> lspayment = paymentRepo
				.findPaylater(status,request.getUserId());
		List<TPickupOrderRequestDetailEntity> ltpickup = tPickupOrderRequestDetailRepo.findByUserIdAndStatusPay(request.getUserId(), statusPay,RequestPickupEnum.REQUEST.getValue());
		List<BookDataResponse> lspay = new ArrayList<>();
		List<BookDataResponse> lspaylater = new ArrayList<>();
		DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("HH:mm");
		Integer paystatus[] = {0,3,2};
		discok.doResetDisc();
		int i = 1;
		for(TPaymentEntity pay:lspayment) {
			BookDataResponse bookDataResponse = toBookData(null, i, pay);
			bookDataResponse.setAmount(getAmount(pay));
			bookDataResponse.setRemainingTime(DateTimeUtil.getTimeDiff(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
					pay.getPickupDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).concat(" ").concat(pay.getPickupTimeId().getTimeFrom().toString().concat(":00"))));
			bookDataResponse.setTrxDate(pay.getTrxDate().format(formatDate)+" "+DateTimeUtil.getString2Date(pay.getTrxTime(), "HHmm", "HH:mm"));
			bookDataResponse.setIsBooking(true);
			lspay.add(bookDataResponse);
			i++;
		}
		for(TPickupOrderRequestDetailEntity reqDtl : ltpickup) {
			BookDataResponse bookDataResponse = toBookData(reqDtl, i,null);
			bookDataResponse.setAmount(reqDtl.getAmount());
			bookDataResponse.setRemainingTime(DateTimeUtil.getTimeDiff(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
					reqDtl.getOrderRequestEntity().getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).concat(" ").concat(reqDtl.getOrderRequestEntity().getPickupTimeEntity().getTimeFrom().toString().concat(":00"))));
			bookDataResponse.setTrxDate(reqDtl.getOrderRequestEntity().getCreateDate().format(formatDate)+" "+reqDtl.getOrderRequestEntity().getCreateDate().format(formatTime));
			bookDataResponse.setIsBooking(false);
			lspay.add(bookDataResponse);
			i++;
		}
		Comparator<BookDataResponse> sortDate = (a,b)->a.getTrxDate().compareToIgnoreCase(b.getTrxDate());
	    Collections.sort(lspay,sortDate.reversed());
	    for(int x = 0;x<paystatus.length;x++) {
	    	for(BookDataResponse dr : lspay) {
		    	if(dr.getStatusCode().equals(paystatus[x].toString())) {
		    		lspaylater.add(dr);
		    	}
		    }
	    }
		return lspaylater;
	}
	
	@Transactional
	public BookDataResponse getIssuedPaylater(PaylaterIssuedRequest request) {
		TPaymentEntity pay = paymentRepo.getOne(request.getBookingCode());
		TPaymentEntity oldPay = pay;
		pay = bookService.getPaymentWEB(pay);
		hisPayment.createHistory(oldPay, pay, pay.getUserId().getUserId());
		paymentRepo.save(pay);
		return BookDataResponse.builder()
				.bookingCode(pay.getBookingCode())
				.build();
	}
	
	private BigDecimal getAmount(TPaymentEntity payment) {
		TPaymentHistoryEntity tPayHistory =  tPaymentHistoryRepo.findFirstByBookingCodeAndLastStatusOrderByTrxServerDesc(payment, payment.getStatus());
		BigDecimal amount = BigDecimal.ZERO;
		if(payment.getStatus().equals(PaymentEnum.HOLD_BY_ADMIN.getCode())) {
			amount = payment.getAmount().subtract(tPayHistory.getAmount());
		}else if(payment.getStatus().equals(PaymentEnum.HOLD_BY_WAREHOUSE.getCode())) {
			amount = payment.getAmount().subtract(tPayHistory.getAmount());
		}else {
			amount = payment.getAmount();
		}
		return amount;
	}
	
	private BookDataResponse toBookData(TPickupOrderRequestDetailEntity entity,int seq,TPaymentEntity pay) {
		DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("HH:mm");
		//String images = PREFIX_PATH_IMAGE_VENDOR + entity.getProductSwitcherEntity().getSwitcherEntity().getImg().substring(entity.getProductSwitcherEntity().getSwitcherEntity().getImg().lastIndexOf("/") + 1);
		if(pay!=null) {
			return BookDataResponse.builder()
					.seq(seq)
					.amount(pay.getAmount())
					.bookingCode(pay.getBookingCode())
					.receiverName(pay.getReceiverName())
					.shipperName(pay.getSenderName())
					.statusCode(pay.getStatusPay().toString())
					.statusDesc(PaymentEnum.getPaymentEnum(pay.getStatus()).getString())
					.isBooking(true)
					.qrcode(pay.getQrcode()==null?"-":pay.getQrcode())
					.build();
		}
		return BookDataResponse.builder()
				.seq(seq)
				.amount(entity.getAmount())
				.bookingCode(entity.getOrderRequestEntity().getPickupOrderId())
				.receiverName(entity.getNamaPenerima())
				.shipperName(entity.getOrderRequestEntity().getUserEntity().getName())
				.statusCode(entity.getIsPay().toString())
				.statusDesc(RequestPickupEnum.getPaymentEnum(entity.getStatus()).toString())
				.isBooking(false)
				.qrcode(entity.getQrCode()==null?"-":entity.getQrCode())
				.build();
	}
}






