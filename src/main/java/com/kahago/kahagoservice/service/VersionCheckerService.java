package com.kahago.kahagoservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.entity.MPropEntity;
import com.kahago.kahagoservice.model.response.VersionResponse;
import com.kahago.kahagoservice.repository.MPropRepo;

/**
 * @author Ibnu Wasis
 */
@Service
public class VersionCheckerService {
	@Autowired
	private MPropRepo mPropRepo;
	
	public VersionResponse getVersion(String platform) {
		Integer plat = (platform==null)?0:1;
		VersionResponse resp = new VersionResponse();
		MPropEntity prop = mPropRepo.findFirstByStatusAndPlatformOrderByReleaseDateDesc(Byte.valueOf("1"),plat);
		resp.setFlag(String.valueOf(prop.getFlag()));
		resp.setVersion(String.valueOf(prop.getVersion()));
		
		return resp;
	}

}
