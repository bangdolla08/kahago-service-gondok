package com.kahago.kahagoservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.entity.MProductSwitcherEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.model.request.DimensiReq;
import com.kahago.kahagoservice.model.response.DimensiResp;
import com.kahago.kahagoservice.repository.TPaymentRepo;

@Service
public class UtilityService {
	@Autowired
	private TPaymentRepo payRepo;
	public DimensiResp getDimensi(DimensiReq req) {
		TPaymentEntity pay = payRepo.findByBookingCodeIgnoreCaseContaining(req.getBookingCode());
		
		return DimensiResp.builder()
				.bookingCode(req.getBookingCode())
				.height(req.getHeight())
				.length(req.getLength())
				.width(req.getWidth())
				.volumeWeight(getVolume(req,pay.getProductSwCode()).toString())
				.build();
	}
	
	private  Double getVolume(DimensiReq req,MProductSwitcherEntity psw) {
		double weightVolume = calculateVol(req,psw.getPembagiVolume());
		double tailVol = weightVolume % 1;
		if(weightVolume < 1) return (double) 1;
        if(tailVol >= psw.getPembulatanVolume()) {
            weightVolume = Math.ceil(weightVolume);
        }else {
            weightVolume = Math.floor(weightVolume);
        }
        
        return weightVolume;
	}

	private double calculateVol(DimensiReq req, Double pembagiVolume) {
		// TODO Auto-generated method stub
		Double vol = Double.valueOf(req.getHeight()) * Double.valueOf(req.getLength()) * Double.valueOf(req.getWidth());
		vol = vol / pembagiVolume;
		return vol;
	}
	
}
