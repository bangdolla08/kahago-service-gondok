package com.kahago.kahagoservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.entity.MUserEntity;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.request.TokenRequest;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.repository.MUserRepo;


/**
 * @author Ibnu Wasis
 */
@Service
public class FirebaseService {
	@Autowired
	private MUserRepo mUserRepo;
	
	public Response<String> updateToken(TokenRequest request){
		MUserEntity user = mUserRepo.getOne(request.getUserId());
		if(user == null) {
			throw new NotFoundException("User Tidak Ditemukan");
		}
		user.setTokenNotif(request.getToken());
		mUserRepo.save(user);
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase()
				);
	}

}
