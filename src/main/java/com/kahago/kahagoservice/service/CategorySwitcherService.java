package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.entity.MOptionPaymentEntity;
import com.kahago.kahagoservice.entity.MSwitcherEntity;
import com.kahago.kahagoservice.entity.MUserCategoryEntity;
import com.kahago.kahagoservice.entity.MUserPriorityEntity;
import com.kahago.kahagoservice.entity.TCategorySwitcherEntity;
import com.kahago.kahagoservice.entity.TOptionPaymentEntity;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.request.CategoryOptionPaymentReq;
import com.kahago.kahagoservice.model.request.CategorySwitcherSaveReq;
import com.kahago.kahagoservice.model.request.UserPriorityRequest;
import com.kahago.kahagoservice.model.response.OptionPaymentResponse;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.model.response.UserCategoryResponse;
import com.kahago.kahagoservice.model.response.UserPriorityResponse;
import com.kahago.kahagoservice.repository.MOptionPaymentRepo;
import com.kahago.kahagoservice.repository.MSwitcherRepo;
import com.kahago.kahagoservice.repository.MUserCategoryRepo;
import com.kahago.kahagoservice.repository.MUserPriorityRepo;
import com.kahago.kahagoservice.repository.TCategorySwitcherRepo;
import com.kahago.kahagoservice.repository.TOptionPaymentRepo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hendro yuwono
 */
@Service
public class CategorySwitcherService {

    @Autowired
    private TCategorySwitcherRepo categorySwitcherRepo;

    @Autowired
    private MSwitcherRepo switcherRepo;

    @Autowired
    private MUserCategoryRepo userCategoryRepo;
    
    @Autowired
    private MOptionPaymentRepo optionPaymentRepo;
    
    @Autowired
    private TOptionPaymentRepo tOptionPaymentRepo;
    
    @Autowired
    private MUserPriorityRepo mUserPriorityRepo;
    
    @Autowired
    private MasterOptPaymentService optionPaymentService;

    @Transactional
    public void save(CategorySwitcherSaveReq response) {
        MUserCategoryEntity user = userCategoryRepo.findById(response.getIdUserCategory()).orElseThrow(() -> new NotFoundException("Id user category is not found"));
        MSwitcherEntity switcher = switcherRepo.findById(response.getSwitcherCode()).orElseThrow(() -> new NotFoundException("Switcher is not found"));

        TCategorySwitcherEntity entity = new TCategorySwitcherEntity();
        entity.setIdUserCategory(user.getSeqid());
        entity.setSwitcherCode(switcher);
        categorySwitcherRepo.save(entity);
    }
    
    @Transactional
    public SaveResponse saveCategotyOptionPayment(CategoryOptionPaymentReq request) {
    	MUserCategoryEntity userCat = userCategoryRepo.findById(request.getIdUserCategory()).orElseThrow(()-> new NotFoundException("Id user Category is not found"));
    	MOptionPaymentEntity optPayment = optionPaymentRepo.findById(request.getOptionPaymentId()).orElseThrow(()->new NotFoundException("Option Payment is Not Found"));
    	
    	TOptionPaymentEntity entity = new TOptionPaymentEntity();
    	entity.setUserCategory(userCat);
    	entity.setOptionPayment(optPayment);
    	entity.setIsDeposit(optPayment.getIsDeposit());
    	entity.setIsPayment(optPayment.getIsPayment());
    	tOptionPaymentRepo.save(entity);
    	
    	return SaveResponse.builder()
    			.saveStatus(1)
    			.saveInformation("Berhasil Simpan Category Option Payment")
    			.build();
    }
    
    @Transactional
    public SaveResponse saveUserPriority(UserPriorityRequest request) {
    	MUserCategoryEntity userCat = userCategoryRepo.findById(request.getIdUserCategory()).orElseThrow(()-> new NotFoundException("Id user Category is not found"));
    	
    	MUserPriorityEntity entity = new MUserPriorityEntity();
    	entity.setUserCategory(userCat.getSeqid());
    	entity.setRequest1(request.getRequestOne());
    	entity.setRequest2(request.getRequestTwo());
    	entity.setRequest3(request.getRequestThree());
    	entity.setPaylater(request.getPayLater());
    	entity.setMinKiriman(request.getMinKiriman());
    	entity.setIsResiAuto(request.getIsResiAuto());
    	mUserPriorityRepo.save(entity);
    	
    	return SaveResponse.builder()
    			.saveStatus(1)
    			.saveInformation("Berhasil Simpan User Priority")
    			.build();
    }
    
    public List<UserCategoryResponse> getAllOptionPaymentByCategoryUser(){
    	List<MUserCategoryEntity> allUserCat = userCategoryRepo.findAll();
    	List<UserCategoryResponse> result = new ArrayList<>();
    	for(MUserCategoryEntity ucat:allUserCat) {
    		List<TOptionPaymentEntity> loptPayment = tOptionPaymentRepo.findByUserCategory(ucat);
    		List<OptionPaymentResponse> detail = new ArrayList<>();
    		for(TOptionPaymentEntity opt:loptPayment) {
    			OptionPaymentResponse optres = toDetail(opt.getOptionPayment());
    			detail.add(optres);
    		}
    		UserCategoryResponse response = UserCategoryResponse.builder()
    										.id(ucat.getSeqid())
    										.nameCategory(ucat.getNameCategory())
    										.roleName(ucat.getRoleName())
    										.optionPayment(detail.size()>0?detail:null)
    										.build();
    		result.add(response);
    	}
    	return result;
    }
    
    public List<UserCategoryResponse> getAllUserPriority(){
    	List<MUserCategoryEntity> allUserCat = userCategoryRepo.findAll();
    	List<UserCategoryResponse> result = new ArrayList<>();
    	for(MUserCategoryEntity ucat : allUserCat) {
    		MUserPriorityEntity userprior = mUserPriorityRepo.findByUserCategory(ucat.getSeqid());
    		UserPriorityResponse response = new UserPriorityResponse();
    		if(userprior != null) {
    			response.setRequest1(userprior.getRequest1());
    			response.setRequest2(userprior.getRequest2());
    			response.setRequest3(userprior.getRequest3());
    			response.setPaylater(userprior.getPaylater());
    			response.setMinKiriman(userprior.getMinKiriman());
    			response.setIsResiAuto(userprior.getIsResiAuto());
    		}else {
    			response.setRequest1(false);
    			response.setRequest2(false);
    			response.setRequest3(false);
    			response.setPaylater(false);
    			response.setIsResiAuto(false);
    			response.setMinKiriman(0);
    		}
    		UserCategoryResponse resp = UserCategoryResponse.builder()
					.id(ucat.getSeqid())
					.nameCategory(ucat.getNameCategory())
					.roleName(ucat.getRoleName())
					.userPriority(response)
					.build();
    		result.add(resp);
    	}
    	return result;
    }
    
    private OptionPaymentResponse toDetail(MOptionPaymentEntity entity) {
    	return optionPaymentService.toDtoOptPayment(entity);
    }
    
    @Transactional
    public SaveResponse deleteOptionPayment(CategoryOptionPaymentReq request) {
    	MUserCategoryEntity userCat = userCategoryRepo.findById(request.getIdUserCategory()).orElseThrow(()-> new NotFoundException("Id user Category is not found"));
    	MOptionPaymentEntity optPayment = optionPaymentRepo.findById(request.getOptionPaymentId()).orElseThrow(()->new NotFoundException("Option Payment is Not Found"));
    	TOptionPaymentEntity entity = tOptionPaymentRepo.findByUserCategoryAndOptionPayment(userCat, optPayment);
    	if(entity == null) {
    		throw new NotFoundException("Data Tidak Ditemukan");
    	}
    	tOptionPaymentRepo.delete(entity);
    	
    	return SaveResponse.builder()
    			.saveStatus(1)
    			.saveInformation("Berhasil Hapus Option Payment")
    			.build();
    }
    
    @Transactional
    public SaveResponse deleteCategorySwitcher(CategorySwitcherSaveReq request) {
    	 MUserCategoryEntity user = userCategoryRepo.findById(request.getIdUserCategory()).orElseThrow(() -> new NotFoundException("Id user category is not found"));
         MSwitcherEntity switcher = switcherRepo.findById(request.getSwitcherCode()).orElseThrow(() -> new NotFoundException("Switcher is not found"));
         TCategorySwitcherEntity entity = categorySwitcherRepo.findByIdUserCategoryAndSwitcherCode(user.getSeqid(), switcher);
         
         if(entity == null)throw new NotFoundException("Data Tidak Ditemukan");
         
         categorySwitcherRepo.delete(entity);
         
         return SaveResponse.builder()
     			.saveStatus(1)
     			.saveInformation("Berhasil Hapus Switcher Category")
     			.build();
    }
    
    @Transactional
    public SaveResponse editUserPriority(UserPriorityRequest request) {
    	MUserPriorityEntity userPrior = mUserPriorityRepo.findByUserCategory(request.getIdUserCategory());
    	
    	if(userPrior == null)throw new NotFoundException("Data Tidak Ditemukan");
    	userPrior.setRequest1(request.getRequestOne());
    	userPrior.setRequest2(request.getRequestTwo());
    	userPrior.setRequest3(request.getRequestThree());
    	userPrior.setPaylater(request.getPayLater());
    	userPrior.setMinKiriman(request.getMinKiriman());
    	userPrior.setIsResiAuto(request.getIsResiAuto());
    	mUserPriorityRepo.save(userPrior);
    	return SaveResponse.builder()
     			.saveStatus(1)
     			.saveInformation("Berhasil Edit User Priority")
     			.build();
    }
    
    @Transactional
    public SaveResponse deleteUserPriority(Integer idUserCategory) {
    	MUserPriorityEntity entity = mUserPriorityRepo.findByUserCategory(idUserCategory);
    	if(entity == null)throw new NotFoundException("Data Tidak Ditemukan");
    	
    	mUserPriorityRepo.delete(entity);
    	return SaveResponse.builder()
     			.saveStatus(1)
     			.saveInformation("Berhasil Delete User Priority")
     			.build();
    }
}
