package com.kahago.kahagoservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.icu.math.BigDecimal;
import com.kahago.kahagoservice.entity.MBankDepositEntity;
import com.kahago.kahagoservice.entity.MUserEntity;
import com.kahago.kahagoservice.entity.TDepositEntity;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.request.TopUpRequest;
import com.kahago.kahagoservice.model.request.UserDetail;
import com.kahago.kahagoservice.model.response.BankDeposit;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.repository.MBankDepositRepo;
import com.kahago.kahagoservice.repository.MUserRepo;
import com.kahago.kahagoservice.repository.TDepositRepo;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ibnu Wasis
 */
@Service
@Slf4j
public class TopUpSaldoService {
	@Autowired
	private MBankDepositRepo mBankDepositRepo;
	@Autowired
	private TDepositRepo tDepositRepo;
	@Autowired
	private DepositService depositService;
	@Autowired
	private MUserRepo mUserRepo;
	@Autowired
	private BankService bankService;
	
	private static final Integer STATUS = 0;
	
	public SaveResponse saveTopUp(TopUpRequest request, String userCreate) {
		log.info("==> Top Up Saldo Service <== "+userCreate);
		MBankDepositEntity bankdepCode = mBankDepositRepo.findById(request.getBankDepCode()).orElseThrow(()->new NotFoundException("Bank Tidak Ditemukan !"));
		if(!bankdepCode.getIsBank()) throw new NotFoundException("Tidak bisa top up dengan Bank ini !");
		for(UserDetail user:request.getUserId()) {
			MUserEntity userId = mUserRepo.findById(user.getUserId()).orElseThrow(()->new NotFoundException("User Tidak Ditemukan !"));
			TDepositEntity entity = TDepositEntity.builder()
									.userId(userId)
									.bankDepCode(bankdepCode)
									.nominal(request.getNominal())
									.nominalApproval(0)
									.lastUser(userCreate)
									.lastUpdate(LocalDateTime.now())
									.description(request.getDecription())
									.insufficientFund(0)
									.status(STATUS)
									.trxRequest(LocalDateTime.now())
									.trxServer(LocalDateTime.now())
									.build();
			entity = depositService.getTiketDeposit(entity);
			tDepositRepo.save(entity);
		}
		
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Berhasil Top Up")
				.build();
	}
	
	public List<BankDeposit> findBankTopUp(){
		return bankService.getBankByStatusAndIsBank();
	}
	
}
