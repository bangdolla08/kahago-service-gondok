package com.kahago.kahagoservice.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.entity.MPickupTimeEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.exception.CustomError;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.dto.PaymentDto;
import com.kahago.kahagoservice.model.request.PriceRequest;
import com.kahago.kahagoservice.model.request.RebookReq;
import com.kahago.kahagoservice.model.response.BookDataResponse;
import com.kahago.kahagoservice.model.response.BookResponse;
import com.kahago.kahagoservice.model.response.PriceDetail;
import com.kahago.kahagoservice.model.response.PriceResponse;
import com.kahago.kahagoservice.model.response.RebookResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.repository.MPickupTimeRepo;
import com.kahago.kahagoservice.repository.TAreaRepo;
import com.kahago.kahagoservice.repository.THistoryBookRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.util.DateTimeUtil;

/**
 * @author Ibnu Wasis
 */
@Service
public class RebookService {
	@Autowired
	private TPaymentRepo tPaymentRepo;
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private PriceService priceService;
	@Autowired
	private MPickupTimeRepo mPickupTimeRepo;
	@Autowired
	private BookService bookService;
	@Autowired
	private HistoryTransactionService historyTransactionService;
	
	private static final String SBY = "SUB";
	private static final String SDA = "SDA";
	
	private static final Logger logger = LoggerFactory.getLogger(RebookService.class);
	public RebookResponse getAllExpired(String userId, String startDate, String endDate) {
		List<TPaymentEntity> lpayment = new ArrayList<TPaymentEntity>();
		List<BookDataResponse> lbooks = new ArrayList<>();
		List<Integer> status = new ArrayList<>();
		status.add(PaymentEnum.EXPIRED_PAYMENT.getCode());
		if((startDate == null || startDate.isEmpty()) && (endDate == null|| endDate.isEmpty())){
			lpayment = tPaymentRepo.findPaylater(status, userId);
		}else {
			try {
			LocalDate start = DateTimeUtil.getDateFrom(startDate, "dd/MM/yyyy");
			LocalDate end = DateTimeUtil.getDateFrom(endDate, "dd/MM/yyyy");
			lpayment = tPaymentRepo.findByUserIdAndStatusAndPickupDate(userId, PaymentEnum.EXPIRED_PAYMENT.getCode(),
					start, end);
			}catch (ParseException e) {
				// TODO: handle exception
				logger.error(e.getMessage());
				throw new NotFoundException("Error Parsing Tanggal");
			}
		}
		BigDecimal total = BigDecimal.ZERO;
		int i = 1;
		for(TPaymentEntity pay : lpayment) {
			BookDataResponse data = paymentService.toBookDataResponse(pay, i);
			total = total.add(pay.getAmount());
			lbooks.add(data);
			i++;
		}
		if(lbooks.size() == 0) {
			throw new NotFoundException("Data Tidak Ditemukan!");
		}
		return RebookResponse.builder()
				.TotalOutStanding(total)
				.lbook(lbooks)
				.build();
	}
	@Transactional(rollbackOn=Exception.class)
	public Response<String> rebookSave(RebookReq request){
		TPaymentEntity payment = tPaymentRepo.findByBookingCodeIgnoreCaseContaining(request.getBookingCode());
		MPickupTimeEntity time = mPickupTimeRepo.findByIdPickupTime(request.getPickupTimeId());
		TPaymentEntity entityHistory = paymentService.createOldPayment(payment);
		if(payment == null)throw new NotFoundException("Data Tidak Ditemukan!");
		PriceRequest pricereq = new PriceRequest();
		PriceDetail pDetail = null;
		if(payment.getOrigin().equals("Surabaya")) {
			pricereq.setOrigin(SBY);
		}else if(payment.getOrigin().equals("Sidoarjo"))pricereq.setOrigin(SDA);
		else pricereq.setOrigin("");
		
		pricereq.setCommodityId(Long.valueOf(payment.getGoodsId()));
		pricereq.setDestination(payment.getIdPostalCode().getKecamatanEntity().getAreaDetailId());
		pricereq.setWeight((payment.getMinWeight()<=0)?1:payment.getMinWeight());
		pricereq.setUserId(request.getUserId());
		PriceResponse priceresp = priceService.findPrice(pricereq);
		for(PriceDetail pr : priceresp.getPrices()) {
			if(pr.getProductCode().equals(payment.getProductSwCode().getProductSwCode().toString())) {
				pDetail = pr;
				break;
			}
		}
		if(pDetail == null) {
			throw new NotFoundException("Tarif Tidak Ditemukan!");
		}
		int divgross = Integer.valueOf(payment.getGrossWeight().toString());
		if(divgross < Integer.valueOf(payment.getVolume().toString())) {
			divgross = Integer.valueOf(payment.getVolume().toString());
		}
		divgross+= payment.getTotalPackKg();
		BigDecimal total = pDetail.getPrice().multiply(BigDecimal.valueOf(divgross));
		BigDecimal amount = total.add(payment.getExtraCharge());
		if(payment.getInsurance() != null || payment.getInsurance().compareTo(BigDecimal.ZERO) > 0) {
			amount = amount.add(payment.getInsurance());
		}
		if(payment.getShippingSurcharge() != null || payment.getShippingSurcharge().compareTo(BigDecimal.ZERO) > 0) {
			amount = amount.add(payment.getShippingSurcharge());
		}
		
		//update booking
		try {
			LocalDate pickupDate = bookService.getPickupDate(DateTimeUtil.getDateFrom(request.getPickupDate(), "yyyy-MM-dd"));
			payment.setAmount(amount);
			payment.setPrice(total);
			payment.setPickupDate(pickupDate);
			payment.setPickupTimeId(time);
			payment.setPickupTime(time.getTimeFrom().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+" - "+ time.getTimeTo().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
			payment.setTrxDate(LocalDate.now());
			payment.setTrxTime(DateTimeUtil.getDateTime("HHmm"));
			payment.setStatus(PaymentEnum.PENDING.getCode());
			payment.setPriceKg(pDetail.getPrice());
			payment.setStatusPay(0);
			tPaymentRepo.save(payment);
			this.historyTransactionService.createHistory(entityHistory, payment, payment.getUserId().getUserId());
		}catch (ParseException e) {
			// TODO: handle exception
			logger.error(e.getMessage());
			return new Response<>(
					ResponseStatus.FAILED.value(),
					ResponseStatus.FAILED.getReasonPhrase()
					);
		}
		
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase()
				);		
	}

}
