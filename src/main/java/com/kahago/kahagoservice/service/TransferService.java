package com.kahago.kahagoservice.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.entity.TDepositEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.model.request.TransferRequest;
import com.kahago.kahagoservice.repository.TDepositRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;

@Service
public class TransferService {
	
	private static final Logger log = LoggerFactory.getLogger(TransferService.class);

	@Autowired
	private TPaymentRepo payRepo;
	@Autowired
	private TDepositRepo depRepo;
	public void doUpdatePay(TransferRequest req) {
		log.info("==> SERVICE Transfer Update Payment <==");
		List<TPaymentEntity> lspay = payRepo.findByUserIdAndInsuficientFundAndIsConfirmTransferAndStatusAndAmountUniq(req.getUserId(), 
				new BigDecimal(req.getUniqNumber()), (byte) 0, 0, new BigDecimal(req.getTotalNominal()));
		lspay.forEach(p -> {
			p.setIsConfirmTransfer((byte) 2);
			p.setTrxServer(new Timestamp(Instant.now().toEpochMilli()));
		});
		payRepo.saveAll(lspay);
	}
	
	public void doUpdateTopup(TransferRequest req) {
		log.info("===> Service Transfer Update Topup <===");
		BigDecimal nominal = new BigDecimal(req.getTotalNominal()).add(new BigDecimal(req.getUniqNumber()));
		List<TDepositEntity> lsdep = depRepo.findAllByListDepo(nominal, Integer.valueOf(req.getUniqNumber()), (byte) 0, req.getUserId());
		lsdep.forEach(d -> {
			d.setIsConfirmTransfer((byte) 2);
			d.setTrxServer(LocalDateTime.now());
		});
		depRepo.saveAll(lsdep);
	}
	
}
