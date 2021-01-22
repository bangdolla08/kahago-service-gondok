package com.kahago.kahagoservice.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.kahago.kahagoservice.entity.TCreditEntity;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.response.Credit;
import com.kahago.kahagoservice.repository.TCreditRepo;


@Service
public class BillService {
	@Autowired
	private TCreditRepo creditRepo;
	public List<Credit> getBillCredit(String userid) {
		List<TCreditEntity> lsCredit = creditRepo.findByUserAndNominalGraterZeroAndTiketNo(userid,"0");
		if(lsCredit.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseStatus.NOT_FOUND.getReasonPhrase());
		return lsCredit.stream().map(this::getCredit).collect(Collectors.toList());
	}
	
	private Credit getCredit(TCreditEntity credit) {
		return Credit.builder()
				.date(credit.getTgl().toString())
				.nominal(credit.getNominal().toString())
				.build();
	}
}
