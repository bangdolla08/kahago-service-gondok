package com.kahago.kahagoservice.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.entity.TRespTransferEntity;
import com.kahago.kahagoservice.model.response.RespTransferRespone;
import com.kahago.kahagoservice.repository.TRespTransferRepo;

/**
 * @author Ibnu Wasis
 */
@Service
public class ResponseTransferService {
	@Autowired
	private TRespTransferRepo transferRepo;
	
	public Page<RespTransferRespone> getRespTransfer(String startDate,String endDate,Pageable pageable){
		SimpleDateFormat formater = new SimpleDateFormat("ddMMyyyy");
        Date first = new Date();
        Date endD = new Date();
        try {
        	if(startDate!=null && endDate!=null) {
        		first = formater.parse(startDate);
                endD = formater.parse(endDate);
        	}        	
        }catch (ParseException e) {
			// TODO: handle exception
        	e.printStackTrace();
		}
        Page<TRespTransferEntity> ltransfer ;
        if(startDate==null && endDate==null) {
        	ltransfer = transferRepo.findAll(pageable);
        }else {
        	ltransfer = transferRepo.findAllByLastTime(first, endD, pageable);
        }
        return new PageImpl<>(
        		ltransfer.getContent().stream().map(this::toDto).collect(Collectors.toList()), 
        		ltransfer.getPageable(),
        		ltransfer.getTotalElements());
	}
	
	private RespTransferRespone toDto(TRespTransferEntity entity) {
		return RespTransferRespone.builder()
				.sender(entity.getSender())
				.note(entity.getNote())
				.debit(entity.getDebit())
				.kredit(entity.getKredit())
				.tglTransaksi(entity.getLastTime().toString())
				.build();
	}
}
