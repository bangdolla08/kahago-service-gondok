package com.kahago.kahagoservice.schedulling;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.transaction.Transactional;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kahago.kahagoservice.component.FirebaseComponent;
import com.kahago.kahagoservice.entity.THistoryBookEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.entity.TPaymentHistoryEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestDetailEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestEntity;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.RequestPickupEnum;
import com.kahago.kahagoservice.repository.THistoryBookRepo;
import com.kahago.kahagoservice.repository.TPaymentHistoryRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestDetailRepo;
import com.kahago.kahagoservice.service.BookService;
import com.kahago.kahagoservice.service.HistoryTransactionService;
import com.kahago.kahagoservice.service.PaymentService;
import com.kahago.kahagoservice.service.RequestPickUpService;

/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 9 Des 2019
 */
@Component
public class PaymentSchedulling {
	
	private static final Logger log = LoggerFactory.getLogger(PaymentSchedulling.class);

	@Autowired
	private PaymentService payService;
	@Autowired
	private BookService bookService;
	@Autowired
	private THistoryBookRepo histBookRepo;
	@Autowired
	private HistoryTransactionService histService;
	@Autowired
	private FirebaseComponent firebase;
	@Autowired
	private RequestPickUpService requestPickupService;
	@Autowired
	private TPickupOrderRequestDetailRepo tPickupOrderReqDtlRepo;
	
	@Scheduled(fixedDelayString = "${fixedDelay.in.milliseconds}")
//	@Transactional
	public void ExpiredBook() {
		log.info("===> Expired Payment <===");
		List<TPaymentEntity> lsPay = payService.getByStatusAndTime(PaymentEnum.PENDING.getCode(), LocalTime.now(), LocalDate.now());
		List<TPickupOrderRequestDetailEntity> lpickupReq = requestPickupService.getPickupOrderReqDetail(RequestPickupEnum.REQUEST, LocalDate.now(), LocalTime.now());
		List<THistoryBookEntity> lsHisBook = new ArrayList<THistoryBookEntity>();
		/*for(TPaymentEntity pay:lsPay) {
			THistoryBookEntity hisBook = bookService.initializeHistoryBook(pay.getBookingCode(), pay.getJumlahLembar(), pay.getUserId().getUserId(), PaymentEnum.EXPIRED_PAYMENT.getKeterangan());
			lsHisBook.add(hisBook);
		}
		histBookRepo.saveAll(lsHisBook);*/ //comment by Ibnu issue Booking Ulang (Rebooking) tidak perlu menyimpan di t_history_book
		lsPay.stream().forEach(p -> p.setStatus(PaymentEnum.EXPIRED_PAYMENT.getCode()));
		lpickupReq.stream().forEach(p->{
			p.setStatus(RequestPickupEnum.EXPIRED_PAYMENT.getValue());
		});
		payService.saveAll(lsPay);
		lsPay.stream().forEach(createHistory());
		for(TPickupOrderRequestDetailEntity td:lpickupReq) {
			if(chekReqPickup(td.getOrderRequestEntity())) {
				td.getOrderRequestEntity().setStatus(RequestPickupEnum.EXPIRED_PAYMENT.getValue());
			}
		}
		requestPickupService.saveAll(lpickupReq);
	}


	public Consumer<? super TPaymentEntity> createHistory() {
		return p->{
			TPaymentEntity oldpay = p;
			oldpay.setStatus(PaymentEnum.PENDING.getCode());
			p.setStatus(PaymentEnum.EXPIRED_PAYMENT.getCode());
			histService.createHistory(oldpay, p, p.getUserId().getUserId());
		};
	}
	
	
	@Scheduled(fixedDelayString = "${delayNotif.in.milliseconds}")
	public void NotifExpiredBook() {
		log.info("====> Notif Expired Payment <===");
		List<TPaymentEntity> lsPay = payService.getByExpiredPayment(PaymentEnum.PENDING.getCode(), LocalTime.now(), LocalDate.now());
		List<TPickupOrderRequestDetailEntity> lPickupReq = requestPickupService.getExpiredPickupReq(RequestPickupEnum.REQUEST, LocalDate.now(), LocalTime.now());
		for(TPaymentEntity pay : lsPay) {
			String title= "exp_pay";
	    	JSONObject data = new JSONObject();
	    	try {
	    		data.put("idTrx", pay.getBookingCode());
	    		data.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")));
	    		data.put("userid", pay.getUserId().getUserId());
	    		data.put("nominal", pay.getAmount());
	    		data.put("type_trx", "1"); //1. Book, 2. Deposit, 3. Pickup
	    		data.put("tag", title);
	    		data.put("status_trx", "1"); //0. failed, 1.Success
	    		data.put("tittle", "Expired Payment Id Pesanan "+pay.getBookingCode());
	    		data.put("body", "Pesanan "+pay.getBookingCode()+" Akan Kadaluarsa");
	    		firebase.notif(title, "Permintaan Pembayaran Pesanan", data, title, pay.getUserId().getTokenNotif());
	    	}catch (JSONException e) {
				// TODO: handle exception
	    		log.error("===> Error notif expired <===");
	    		log.error(e.getMessage());
	    		e.printStackTrace();
			}
		}
		for(TPickupOrderRequestDetailEntity dtl: lPickupReq) {
			String title= "exp_pay";
	    	JSONObject data = new JSONObject();
	    	try {
	    		data.put("idTrx", dtl.getOrderRequestEntity().getPickupOrderId());
	    		data.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")));
	    		data.put("userid", dtl.getOrderRequestEntity().getUserEntity().getUserId());
	    		data.put("nominal", dtl.getAmount());
	    		data.put("type_trx", "1"); //1. Book, 2. Deposit, 3. Pickup
	    		data.put("tag", title);
	    		data.put("status_trx", "1"); //0. failed, 1.Success
	    		data.put("tittle", "Expired Payment Id Pesanan "+dtl.getOrderRequestEntity().getPickupOrderId());
	    		data.put("body", "Pesanan "+dtl.getOrderRequestEntity().getPickupOrderId()+" Akan Kadaluarsa");
	    		firebase.notif(title, "Permintaan Pembayaran Pesanan", data, title, dtl.getOrderRequestEntity().getUserEntity().getTokenNotif());
	    	}catch (JSONException e) {
				// TODO: handle exception
	    		log.error("===> Error notif expired <===");
	    		log.error(e.getMessage());
	    		e.printStackTrace();
			}
		}
	}
	
	private Boolean chekReqPickup(TPickupOrderRequestEntity entity) {
		Boolean result = true;
		List<TPickupOrderRequestDetailEntity> lPickupDtl = tPickupOrderReqDtlRepo.findAllByOrderRequestEntity(entity);
		for(TPickupOrderRequestDetailEntity td:lPickupDtl) {
			if(!td.getStatus().equals(RequestPickupEnum.EXPIRED_PAYMENT.getValue())) {
				result = false;
				break;
			}
		}
		return result;
	}
}
