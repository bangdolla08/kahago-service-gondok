package com.kahago.kahagoservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.entity.MUserEntity;
import com.kahago.kahagoservice.model.request.RegistrationUser;
import com.kahago.kahagoservice.repository.MUserCategoryRepo;
import com.kahago.kahagoservice.repository.MUserRepo;

@Service
public class RegistrationService {

	@Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private MUserRepo userRepo;

    @Autowired
    private MUserCategoryRepo userCategoryRepo;
    
    public void createUser(RegistrationUser reg) {
    	MUserEntity user = MUserEntity.builder()
    			.userId(reg.getUsername().replace(" ", ""))
    			.email(reg.getUsername().replace(" ", ""))
    			.name(reg.getName())
    			.password(encoder.encode(reg.getPassword()))
    			.accountType(reg.getTypeUser())
    			.userLevel(Integer.valueOf(reg.getUserLevel()))
    			.hp(reg.getTelp())
    			.areaOriginId(reg.getAreaKonsolidator())
    			.creditDay(reg.getCreditDay())
    			.depositType(reg.getTypeDeposit())
    			.userCategory(userCategoryRepo.findById(Integer.valueOf(reg.getUserCategory())).get())
    			.isBranch(Integer.valueOf(reg.getBranchAccess()))
    			.build();
    	userRepo.save(user);
    }
}
