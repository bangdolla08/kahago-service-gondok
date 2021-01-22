package com.kahago.kahagoservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.ls.LSProgressEvent;

import com.kahago.kahagoservice.entity.MPermohonanEntity;
import com.kahago.kahagoservice.entity.TPermohonanEntity;
import com.kahago.kahagoservice.enummodel.PermohonanDetailEnum;
import com.kahago.kahagoservice.enummodel.PermohonanEnum;
import com.kahago.kahagoservice.exception.InternalServerException;
import com.kahago.kahagoservice.model.request.PermohonanListReq;
import com.kahago.kahagoservice.model.request.PermohonanReq;
import com.kahago.kahagoservice.model.request.PermohonanSaveReq;
import com.kahago.kahagoservice.model.response.PermohonanDetailResp;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.repository.MPermohonanRepo;
import com.kahago.kahagoservice.repository.TPermohonanRepo;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 11 Jun 2020
 */

@Slf4j
@Service
public class KebijakanService {
	@Autowired
	private TPermohonanRepo tPermohonanRepo;
	@Autowired
	private MPermohonanRepo mPermohonanRepo;
	@Autowired
	private PermohonanService mohonService;
	
	public Page<PermohonanDetailResp> getDataKebijakan(PermohonanListReq req){
		Page<TPermohonanEntity> lsPermohonan = tPermohonanRepo
				.findAllByStatusPermohonan(req.getStatus(), req.getVendorCode(), req.getNoPermohonan(), req.getBookId(), PermohonanDetailEnum.WAITING_APPROVE.getValue(),req.getPageRequest());
		
		return new PageImpl<>(
				lsPermohonan.getContent().stream().map(mohonService::getDetailDataBookKebijakan).collect(Collectors.toList()), 
				lsPermohonan.getPageable(), 
				lsPermohonan.getTotalElements());
		
	}
	
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	public SaveResponse doOpenPermohonan(PermohonanSaveReq req,String userid) {
		log.info("==> Open Permohonan ==> "+req.getNoPermohonan());
		MPermohonanEntity permohonan = mPermohonanRepo.findById(req.getNoPermohonan()).orElseThrow(()-> new InternalServerException("Nomor Permohonan Tidak Ditemukan"));
		permohonan.setLastUpdate(LocalDateTime.now());
		permohonan.setLastUser(userid);
		permohonan.setStatus(PermohonanEnum.DRAFT.getValue());
		mPermohonanRepo.saveAndFlush(permohonan);
//		List<TPermohonanEntity> lsPermohonanEntities = tPermohonanRepo.findByNomorPermohonan(permohonan);
//		lsPermohonanEntities.forEach(p-> p.setStatus(PermohonanDetailEnum.DRAFT.getValue()));
//		tPermohonanRepo.saveAll(lsPermohonanEntities);
		
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Success")
				.build();
		
	}
	
	public SaveResponse doSaveApprovalReject(PermohonanSaveReq req,String userid) {
		List<TPermohonanEntity> lsPermohonan = req.getDatas().stream().map(d-> mohonService.getDataApprovalReject(d, userid,req.getNoPermohonan())).collect(Collectors.toList());
		if(lsPermohonan.isEmpty()==false) {
			MPermohonanEntity permohonanEntity = lsPermohonan.stream().findAny().get().getNomorPermohonan();
			permohonanEntity.setStatus(PermohonanEnum.DRAFT.getValue());
			permohonanEntity.setLastUpdate(LocalDateTime.now());
			permohonanEntity.setLastUser(userid);
			mPermohonanRepo.saveAndFlush(permohonanEntity);
		}
		tPermohonanRepo.saveAll(lsPermohonan);
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Success")
				.build();
	}
	
}
