package com.kahago.kahagoservice.service;


import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.response.BookDataResponse;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.repository.TPaymentRepo;



/**
 * @author Ibnu Wasis
 */
@Service
public class UpdateResiManualService {
	@Autowired
	private TPaymentRepo paymentRepo;
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private HistoryTransactionService historyTransactionService;
	
	public Page<BookDataResponse> getListPayment(Pageable pageable){
		Page<TPaymentEntity> lPayment = paymentRepo.findAllByStatus(PaymentEnum.ACCEPT_WITHOUT_RESI.getCode(), pageable);
		return new PageImpl<>(lPayment.getContent().stream().map(this::toDto).collect(Collectors.toList()),
				lPayment.getPageable(),
				lPayment.getTotalElements());
	}
	
	private BookDataResponse toDto(TPaymentEntity payment) {
		return paymentService.toBookDataResponse(payment);
	}
	
	@Transactional
	public SaveResponse updateResi(String bookingCode, String stt) {
		TPaymentEntity payment = paymentService.get(bookingCode);
		String uri = "";
		if(payment == null) {
			throw new NotFoundException("Data Tidak Ditemukan");
		}
		TPaymentEntity paymentOld = paymentService.createOldPayment(payment);
		payment.setStatus(PaymentEnum.ACCEPT_IN_WAREHOUSE.getCode());
		payment.setStt(stt);
		paymentRepo.save(payment);
		historyTransactionService.createHistory(paymentOld, payment, payment.getUserId().getUserId());
		uri = "api/resi/kahago?bookingcode="
				+ payment.getBookingCode()+
				"&userid="+ payment.getUserId().getUserId();
		return SaveResponse.builder()
				.saveInformation("Berhasil Update Resi")
				.saveStatus(1)
				.linkResi(uri)
				.build();
	}
	
}
