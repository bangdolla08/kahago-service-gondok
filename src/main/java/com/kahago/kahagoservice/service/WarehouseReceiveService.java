package com.kahago.kahagoservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.entity.TWarehouseReceiveEntity;
import com.kahago.kahagoservice.repository.TWarehouseReceiveRepo;

@Service
public class WarehouseReceiveService {
	@Autowired
	private TWarehouseReceiveRepo warehouseReceiveRepo;

	public void save(TWarehouseReceiveEntity entity) {
		warehouseReceiveRepo.save(entity);
	}
	
	public TWarehouseReceiveEntity get(Integer id) {
		return warehouseReceiveRepo.getOne(id);
	}
}
