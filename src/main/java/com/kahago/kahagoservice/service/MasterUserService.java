package com.kahago.kahagoservice.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kahago.kahagoservice.entity.MOfficeEntity;
import com.kahago.kahagoservice.entity.MUserEntity;
import com.kahago.kahagoservice.entity.TOfficeEntity;
import com.kahago.kahagoservice.enummodel.DepositEnum;
import com.kahago.kahagoservice.enummodel.DepositTypeEnum;
import com.kahago.kahagoservice.exception.InternalServerException;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.request.MasterUserRequest;
import com.kahago.kahagoservice.model.response.MasterUserResponse;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.repository.MOfficeRepo;
import com.kahago.kahagoservice.repository.MUserCategoryRepo;
import com.kahago.kahagoservice.repository.MUserRepo;
import com.kahago.kahagoservice.repository.TOfficeRepo;
import com.kahago.kahagoservice.util.CommonConstant;

/**
 * @author Ibnu Wasis
 */
@Service
public class MasterUserService {
	@Autowired
	private MUserRepo mUserRepo;
	@Autowired
	private TOfficeRepo tOfficeRepo;
	@Autowired
	private BCryptPasswordEncoder encoder;
	@Autowired
	private SignUpService signUpService;
	@Autowired
	private MUserCategoryRepo userCategoryRepo;
	@Autowired
	private MOfficeRepo mOfficeRepo;
	
	public Page<MasterUserResponse> getAllUser(MasterUserRequest request,Pageable pageable){
		MUserEntity userRef = null;
		if(request.getReference() != null) {
			userRef = mUserRepo.getMUserEntitiesBy(request.getReference());
		}
		Page<MUserEntity> lUser = mUserRepo.findAllBySearchString(request.getUserId(), request.getUserCategoryId(), (userRef==null?null:userRef.getAccountNo()), request.getUserType(), request.getSearch(), request.getOfficeCode(), pageable);
		
		return new PageImpl<>(
				lUser.getContent().stream().map(this::toDto).collect(Collectors.toList()),
				lUser.getPageable(), 
				lUser.getTotalElements());
	}
	
	private MasterUserResponse toDto(MUserEntity entity) {
		List<TOfficeEntity> office = tOfficeRepo.findByUserIdUserId(entity.getUserId());
		MUserEntity userRef = null;
		if(entity.getRefNum() != null && !entity.getRefNum().isEmpty()) {
			userRef = mUserRepo.findByAccountNo(entity.getRefNum());
		}
		return MasterUserResponse.builder()
				.accountNo(entity.getAccountNo())
				.branchName(office.size()==0?"-":office.get(0).getOfficeCode().getName())
				.courierFlag(CommonConstant.toBoolean(entity.getCourierFlag()))
				.idUserCategory(entity.getUserCategory().getSeqid())
				.userCategory(entity.getUserCategory().getNameCategory())
				.userType(entity.getDepositType().equals("0")?"DEPOSIT":"KREDIT")
				.idUserType(Integer.valueOf(entity.getDepositType()))
				.name(entity.getName())
				.officeCode(office.size()==0?"-":office.get(0).getOfficeCode().getOfficeCode())
				.telpNo(entity.getHp())
				.userId(entity.getUserId())
				.userReference(userRef==null?"":userRef.getUserId())
				.creditDay(entity.getCreditDay()==null?0:Integer.valueOf(entity.getCreditDay()))
				.build();
	}
	@Transactional
	public SaveResponse addUser(MasterUserRequest req,String userAdmin) {
		if(req.getUserId()==null || req.getPassword()==null) {
			throw new InternalServerException("User Id dan password tidak boleh kosong !");
		}
		if(mUserRepo.existsById(req.getUserId())) {
			throw new InternalServerException("User Id sudah dipakai!");
		}
		if(req.getUserType().equals(1)) {
			if(req.getCreditDay()==null) {
				throw new InternalServerException("Credit Day tidak boleh kosong !");
			}
		}
		if(req.getPhoneNo().length() < 9 ) {
        	throw new InternalServerException("Telepon Penerima minimal 10 Digit !");
        }else if(!req.getPhoneNo().matches("[0-9]+")) {
        	throw new InternalServerException("Telepon Penerima harus Nomor !");
        }
		if(!req.getUserId().contains("@")) {
			throw new InternalServerException("User Id harus email !");
		}
		String passsword = encoder.encode(req.getPassword());
		MUserEntity entity = new MUserEntity();
		entity.setUserId(req.getUserId().replace(" ", ""));
        entity.setPassword(passsword);
        entity.setName(req.getName());
        entity.setEmail(req.getUserId().replace(" ", ""));
        if(req.getAccountNo() != null) {
        	entity.setAccountNo(req.getAccountNo());
        }else {
            entity.setAccountNo(signUpService.validAccountNumber());
        }
        entity.setUserLevel(1);
        entity.setAccountType("2");
        entity.setStatusLayanan("1");
        entity.setRegisterDate(LocalDateTime.now());
        if(req.getReference() != null) {
    		//MUserEntity userRef = mUserRepo.getMUserEntitiesBy(req.getReference());
    		if(!mUserRepo.existsByAccountNo(req.getReference())) throw new NotFoundException("Data User Reference Tidak Ditemukan !");
        	entity.setRefNum(req.getReference());
        }else {
        	entity.setRefNum("");
        }
        
        entity.setHp(req.getPhoneNo());

        entity.setStatusLogin("1");
        entity.setStatusLayanan("1");
        entity.setExpPassword(LocalDateTime.now());
        entity.setAccountType("1");
        entity.setAreaOriginId("SUB");
        entity.setLastUpdate(LocalDateTime.now());
        entity.setLastTimeSession(LocalDateTime.now());
        entity.setBalance(new BigDecimal(0));
        if(req.getUserType() == null) {
        	 entity.setDepositType("0");
        }else {
        	entity.setDepositType(req.getUserType().toString());
        }
       
        entity.setCourierFlag(0);
        entity.setQtyDeposit(0);
        entity.setMitraFlag(0);
        entity.setIsBranch(0);
        entity.setCourierFlag(req.getIsCourier()?1:0);
        entity.setLastUser(userAdmin);
        if(req.getUserCategory() == null) {
        	 entity.setUserCategory(userCategoryRepo.findById(1).orElseThrow(() -> new NotFoundException("User Category is Not found")));
        }else {
        	 entity.setUserCategory(userCategoryRepo.findById(req.getUserCategory()).orElseThrow(() -> new NotFoundException("User Category is Not found")));
        }
        if(req.getCreditDay() == null) {
        	 entity.setCreditDay("0");
        }else {
        	 entity.setCreditDay(req.getUserType().toString());
        }
        mUserRepo.save(entity);
        if(req.getOfficeCode() != null && !req.getOfficeCode().equals("-")) {
        	MOfficeEntity office = mOfficeRepo.findById(req.getOfficeCode()).orElseThrow(()->new NotFoundException("Office Code tidak ditemukan !"));
        	TOfficeEntity tOffice = new TOfficeEntity();
        	tOffice.setUserId(entity);
        	tOffice.setOfficeCode(office);
        	tOfficeRepo.save(tOffice);
        }
        return SaveResponse.builder()
        		.saveStatus(1)
        		.saveInformation("Berhasil Simpan User Baru")
        		.build();
	}
	
	public MasterUserResponse getUserByUserId(String userId) {
		MUserEntity user = mUserRepo.getMUserEntitiesBy(userId);
		
		return toDto(user);
	}
	
	@Transactional
	public SaveResponse editUser(MasterUserRequest request,String userAdmin) {
		if(request.getUserId()== null) {
			throw new InternalServerException("User Id dan password tidak boleh kosong !");
		}
		MUserEntity user = mUserRepo.getMUserEntitiesBy(request.getUserId());
		if(user == null) {
			throw new NotFoundException("Data User Tidak Ditemukan !");
		}
		if(request.getUserType().equals(1)) {
			if(request.getCreditDay()==null) {
				throw new InternalServerException("Credit Day tidak boleh kosong !");
			}
		}
		if(request.getPhoneNo().length() < 9 ) {
        	throw new InternalServerException("Telepon Penerima minimal 10 Digit !");
        }else if(!request.getPhoneNo().matches("[0-9]+")) {
        	throw new InternalServerException("Telepon Penerima harus Nomor !");
        }	
		user.setName(request.getName());
		user.setHp(request.getPhoneNo());
		if(request.getReference() != null && !request.getReference().isEmpty()) {
    		MUserEntity userRef = mUserRepo.getMUserEntitiesBy(request.getReference());
    		if(userRef == null) throw new NotFoundException("Data User Reference Tidak Ditemukan !");
        	user.setRefNum(request.getReference());
        }else {
        	user.setRefNum("");
        }
		if(request.getUserCategory() == null) {
       	 user.setUserCategory(userCategoryRepo.findById(1).orElseThrow(() -> new NotFoundException("User Category is Not found")));
       }else {
       	 user.setUserCategory(userCategoryRepo.findById(request.getUserCategory()).orElseThrow(() -> new NotFoundException("User Category is Not found")));
       }
	  
	   user.setDepositType(request.getUserType()==null?"0":request.getUserType().toString());
       user.setCreditDay(request.getCreditDay()==null?"0":request.getCreditDay().toString());
       if(request.getAccountNo() != null) {
    	   user.setAccountNo(request.getAccountNo());
       }
       user.setCourierFlag(request.getIsCourier()?1:0);
       user.setLastUser(userAdmin);
       mUserRepo.save(user);
       if(request.getOfficeCode() != null && !request.getOfficeCode().equals("-")) {
       	MOfficeEntity office = mOfficeRepo.findById(request.getOfficeCode()).orElseThrow(()->new NotFoundException("Office Code tidak ditemukan !"));
       	TOfficeEntity tOffice = new TOfficeEntity();
       	tOffice.setUserId(user);
       	tOffice.setOfficeCode(office);
       	tOfficeRepo.save(tOffice);
       }else {
    	   List<TOfficeEntity> lOffice = tOfficeRepo.findByUserIdUserId(user.getUserId());
    	   if(lOffice.size() > 0) {
    		   tOfficeRepo.delete(lOffice.get(0));
    	   }
       }
       return SaveResponse.builder()
       		.saveStatus(1)
       		.saveInformation("Berhasil Edit User "+request.getUserId())
       		.build();
	}
	
	@Transactional
	public SaveResponse changePassword(String password,String userId) {
		String passswordEnc = encoder.encode(password);
		MUserEntity user = mUserRepo.getMUserEntitiesBy(userId);
		if(user == null) throw new NotFoundException("Data User Tidak Ditemukan !");
		user.setPassword(passswordEnc);
		mUserRepo.save(user);
		return SaveResponse.builder()
	       		.saveStatus(1)
	       		.saveInformation("Berhasil Change password User "+userId)
	       		.build();
	}
}
