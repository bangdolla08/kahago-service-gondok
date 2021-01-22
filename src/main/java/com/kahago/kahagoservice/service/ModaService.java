package com.kahago.kahagoservice.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.entity.MModaEntity;
import com.kahago.kahagoservice.model.response.ModaResponse;
import com.kahago.kahagoservice.repository.MModaRepo;

/**
 * @author Ibnu Wasis
 */
@Service
public class ModaService {
	@Autowired
	private MModaRepo mModaRepo;
	
	public List<ModaResponse> getListModa(){
		List<ModaResponse> result = new ArrayList<ModaResponse>();
		List<MModaEntity> lModa = mModaRepo.findAllByFlag(Byte.valueOf("1"));
		for(MModaEntity moda :lModa) {
			ModaResponse resp = new ModaResponse();
			resp.setModaId(moda.getIdModa());
			resp.setModaName(moda.getNamaModa());
			resp.setDescription(moda.getDeskripsi());
			result.add(resp);
		}
		
		return result;
	}
}
