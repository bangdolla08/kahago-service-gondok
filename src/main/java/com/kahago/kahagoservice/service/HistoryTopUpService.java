package com.kahago.kahagoservice.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.entity.TDepositEntity;
import com.kahago.kahagoservice.enummodel.DepositEnum;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.request.HistoryTopUpReq;
import com.kahago.kahagoservice.model.response.HistoryTopUpResponse;
import com.kahago.kahagoservice.repository.TDepositRepo;
import com.kahago.kahagoservice.util.DateTimeUtil;

/**
 * @author Ibnu Wasis
 */
@Service
public class HistoryTopUpService {
	@Autowired
	private TDepositRepo tDepositRepo;
	
	public static final Logger logger = LoggerFactory.getLogger(HistoryTopUpService.class);
	
	public List<HistoryTopUpResponse> getHistory(HistoryTopUpReq request){
		List<HistoryTopUpResponse> lhistory = new ArrayList<HistoryTopUpResponse>();
		try {
			
			List<TDepositEntity> ldeposit = tDepositRepo.findAllByUserIdAndTrxRequestAndStatus(request.getUserId(),DateTimeUtil.getDateTime("dd/MM/yyyy", request.getStartDate()),
					DateTimeUtil.getDateTime("dd/MM/yyyy", request.getEndDate()));
			int seq =0;
			for (TDepositEntity de:ldeposit) {
				seq++;
				lhistory.add(toDto(de, seq));
			}
		}catch (ParseException e) {
			// TODO: handle exception
			logger.error(e.getMessage());
			throw new NotFoundException("Error Parsing Tanggal!");
		}
		
		
		return lhistory;
	}
	
	private HistoryTopUpResponse toDto(TDepositEntity entity,Integer seq) {
		String tglTrx = DateTimeUtil.getTimetoString(entity.getTrxRequest(), "dd MMM yyyy hh:mm");
		return HistoryTopUpResponse.builder()
				.seq(seq)
				.bankTransfer(entity.getBankDepCode().getBankId().getName()==null?entity.getBankDepCode().getAccNo():entity.getBankDepCode().getBankId().getName())
				.noRekTransfer(entity.getBankDepCode().getAccNo())
				.nominal(entity.getNominal())
				.noTiket(entity.getTiketNo())
				.desc(entity.getDescription())
				.status(DepositEnum.getKeterangan(entity.getStatus()))
				.tglTrx(tglTrx)
				.build();
	}
}
