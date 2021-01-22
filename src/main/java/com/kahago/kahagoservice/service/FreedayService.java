package com.kahago.kahagoservice.service;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.entity.MFreedateEntity;
import com.kahago.kahagoservice.entity.MFreedayEntity;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.response.FreedayResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.repository.MFreedateRepo;
import com.kahago.kahagoservice.repository.MFreedayRepo;
import com.kahago.kahagoservice.util.DateTimeUtil;

/**
 * @author Ibnu Wasis
 */
@Service
public class FreedayService {
	@Autowired
	private MFreedateRepo mFreedateRepo;
	@Autowired
	private MFreedayRepo mFreedayRepo;
	
	private static final Logger logger = LoggerFactory.getLogger(FreedayService.class);
	
	public List<FreedayResponse> getAllFreeday() {
		List<FreedayResponse> lFreeday = new ArrayList<FreedayResponse>();
		List<MFreedateEntity> lfreedate = mFreedateRepo.findAll();
		List<MFreedayEntity> lfreeday = mFreedayRepo.findAll();
		for(MFreedateEntity fdt : lfreedate) {
			FreedayResponse fr = new FreedayResponse();
			fr.setDayName("");
			fr.setBulan(String.valueOf(fdt.getBulan()));
			fr.setTanggal(String.valueOf(fdt.getTgl()));
			fr.setTahun(String.valueOf(fdt.getTahun()));
			fr.setDescription(fdt.getDescription());
			lFreeday.add(fr);
		}
		for(MFreedayEntity fd:lfreeday) {
			FreedayResponse fr = new FreedayResponse();
			if(fd.getIsActive() == 0)continue;
			fr.setDayName(fd.getDayName());
			fr.setBulan("");
			fr.setTahun("");
			fr.setTanggal("");
			fr.setDescription(fd.getDescription());
			lFreeday.add(fr);
		}
		return lFreeday;
	}
	
	@Transactional(rollbackOn=Exception.class)
	public Response<String> FreedaySave(String dayName,String tanggal,String description){
		int isActive = 1;
		MFreedayEntity freeday = new MFreedayEntity();
		MFreedateEntity freedate = new MFreedateEntity();
		try {
			if(dayName != null ) {
				if(!dayName.isEmpty()) {
					freeday = mFreedayRepo.findByDayNameIgnoreCaseContaining(dayName);
					if(freeday != null) {
						freeday.setIsActive(isActive);
						freeday.setDescription(description);
					}else {
						freeday = new MFreedayEntity();
						freeday.setDayName(dayName);
						freeday.setIsActive(isActive);
						freeday.setDescription(description);
					}
					mFreedayRepo.save(freeday);
				}
			}else if(tanggal != null) {
				LocalDate date = DateTimeUtil.getDateFrom(tanggal, "yyyy-MM-dd"); 
				freedate.setTgl(date.getDayOfMonth());
				freedate.setBulan(date.getMonthValue());
				freedate.setTahun(date.getYear());
				freedate.setDescription(description);
				mFreedateRepo.save(freedate);
			}else {
				throw new NotFoundException("Hari atau tanggal tidak boleh kosong !");
			}
		}catch (ParseException e) {
			// TODO: handle exception
			logger.error(e.getMessage());
			return new Response<>(
					ResponseStatus.FAILED.value(),
					ResponseStatus.FAILED.getReasonPhrase()
					);
			
		}
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase()
				);
	}
	
	@Transactional(rollbackOn=Exception.class)
	public Response<String> deleteHoliday(String dayName,String tanggal){
		try {
			MFreedayEntity freeday = null;
			MFreedateEntity freedate = null;			
			if(dayName != null) {
				freeday = mFreedayRepo.findByDayNameIgnoreCaseContaining(dayName);
			}if(tanggal != null) {
				LocalDate date = DateTimeUtil.getDateFrom(tanggal, "yyyy-MM-dd");
				freedate = mFreedateRepo.findByTahunAndBulanAndTgl(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
			}
			if(freeday !=null) {
				freeday.setIsActive(0);
				mFreedayRepo.save(freeday);
			}else if(freedate != null) {
				mFreedateRepo.delete(freedate);
			}else {
				throw new NotFoundException("Data Tidak Ditemukan !");
			}				
		}catch (ParseException e) {
			// TODO: handle exception
			logger.error(e.getMessage());
			return new Response<>(
					ResponseStatus.FAILED.value(),
					ResponseStatus.FAILED.getReasonPhrase()
					);
		}
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase()
				);
		
	}
	
	public Boolean checkHoliday(LocalDate date) {
		Boolean result = false;
		String day = "";
    	int isActive = 1;
    	day = DateTimeUtil.getNameDay(date);
		MFreedateEntity freeDate = mFreedateRepo.findByTahunAndBulanAndTgl(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
		MFreedayEntity freeDay = mFreedayRepo.findByDayNameIgnoreCaseContainingAndIsActive(day, isActive);
		if(freeDate != null || freeDay != null) {
			result = true;
		}
		return result;
	}
}
