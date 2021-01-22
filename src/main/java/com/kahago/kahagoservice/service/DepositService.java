package com.kahago.kahagoservice.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.entity.TDepositEntity;
import com.kahago.kahagoservice.repository.MBankDepositRepo;
import com.kahago.kahagoservice.repository.TDepositRepo;
import com.kahago.kahagoservice.util.Common;

@Service
public class DepositService {
	@Autowired
	private TDepositRepo depRepo;
	private String count = "00000";
	public TDepositEntity getTiketDeposit(TDepositEntity entity) {
		String tgl = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
		 List<TDepositEntity> lastTiket = depRepo.findByTiketNoStartingWithOrderByTiketNoDesc(tgl);
		 TDepositEntity dep = lastTiket.stream().findFirst().orElse(TDepositEntity.builder()
				 .tiketNo(tgl.concat(count)).build());
		 String tiket = Common.getCounter(dep.getTiketNo(), 6, 10);
		 tiket = tgl.concat(tiket);
		 entity.setTiketNo(tiket);
		return entity;
	}
	
	public void save(TDepositEntity entity) {
		depRepo.save(entity);
	}
	
	public TDepositEntity depReset(TDepositEntity entity) {
//		entity.setBankDepCode(bankDepRepo.);
		entity.setIdPayment(null);
		entity.setIdTicket(null);
		entity.setIsConfirmTransfer((byte)1);
		entity.setInsufficientFund(0);
		return entity;
	}
}
