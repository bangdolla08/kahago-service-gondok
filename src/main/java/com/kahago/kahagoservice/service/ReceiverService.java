package com.kahago.kahagoservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.entity.MReceiverEntity;
import com.kahago.kahagoservice.repository.MReceiverRepo;

@Service
public class ReceiverService {
	@Autowired
	private MReceiverRepo receiverRepo;
	
	public void save(MReceiverEntity entity) {
		receiverRepo.save(entity);
	}
}
