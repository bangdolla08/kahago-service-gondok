package com.kahago.kahagoservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.component.PawoonComponent;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.model.request.PawoonRequest;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.repository.TPaymentRepo;


/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 4 Des 2019
 */
@Service
public class PawoonService {
	@Autowired
	private PawoonComponent pawoonService;
	@Autowired
	private TPaymentRepo payRepo;
	public Response<String> getStatus(PawoonRequest req){
		List<TPaymentEntity> lspay = payRepo.findAllById(req.getBooks());
//		pawoonService.getStatusPayment(lspay, req.getHit());
		return new Response<>(HttpStatus.OK.name(), HttpStatus.OK.getReasonPhrase());
	}
}
