package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.broadcast.MailerComponent;
import com.kahago.kahagoservice.entity.*;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.dto.UserDto;
import com.kahago.kahagoservice.model.request.EditProfileReq;
import com.kahago.kahagoservice.model.request.PasswordReq;
import com.kahago.kahagoservice.model.request.ReferenceRequest;
import com.kahago.kahagoservice.model.request.UserRequest;
import com.kahago.kahagoservice.model.response.*;
import com.kahago.kahagoservice.repository.*;
import com.kahago.kahagoservice.security.ResponseAuth;
import com.kahago.kahagoservice.util.CommonConstant;
import com.kahago.kahagoservice.util.DateTimeUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author bangd
 */

@Service
public class UserService {

    @Autowired
    private MUserRepo userRepo;
    @Autowired
    private MOfficeRepo officeRepo;
    @Autowired
    private TOfficeRepo tOfficeRepo;
    @Autowired
    private OfficeCodeService officeCodeService;
    private static final String FLAG = "0";
    @Autowired
    private TCreditRepo creditRepo;

    @Autowired
    private MUserCategoryRepo userCategoryRepo;

    @Autowired
    private MPostalCodeRepo postalCodeRepo;
    @Autowired
    private TPaymentRepo paymentEntity;
    
    @Autowired
    private TFeeReferenceRepo tFeeReferenceRepo;
    
    @Autowired
    private BCryptPasswordEncoder encoder;

    @Value("${email.activate}")
    private Boolean isEmail;

    @Value("${email.redirect.url.forgot}")
    private String pathUrl;

    @Autowired
    private MailerComponent mailerComponent;
    
    @Autowired
    private MUserPriorityRepo mUserPriorityRepo;

    // TODO: 15/11/2019 Caritau Tentang type User dan
    public UserDto getMUserEntity(String userId){
        return toUserDto(userRepo.getOne(userId));
    }

    public UserDto getUserByIdAndReff(String search){
        return toUserDto(userRepo.getMUserEntitiesBy(search));
    }

    public MUserEntity get(String userid) {
    	return userRepo.getOne(userid);
    }

    public ProfileRes profileUser(String userId) {
        return createUserData(userId,null);
    }

    public void editUser(String userId, UserRequest req) {
        MUserEntity entity = userRepo.findById(userId).orElseThrow(() -> new NotFoundException("User is not exist"));

        entity.setUserCategory(userCategoryRepo.findById(req.getIdUserCategory()).orElse(entity.getUserCategory()));
        entity.setDepositType(!req.getDepositType().isEmpty() ? req.getDepositType() : entity.getDepositType());
        entity.setCourierFlag(req.getCourierFlag() != null ? req.getCourierFlag() : entity.getCourierFlag());
        entity.setCreditDay(req.getCreditDay() != null ? req.getCreditDay() : entity.getCreditDay());
        entity.setRefNum(req.getRefNum() != null ? req.getRefNum() : entity.getRefNum());

        userRepo.save(entity);
    }

    public UserOfCategoryResp findByIds(String userId) {
        MUserEntity entity = userRepo.findById(userId).orElseThrow(() -> new NotFoundException("User is not exist"));
        return toDtoUserOfCategory(entity);
    }

    private UserOfCategoryResp toDtoUserOfCategory(MUserEntity entity) {
        return UserOfCategoryResp.builder()
                .courierFlag(entity.getCourierFlag())
                .userCategoryName(entity.getUserCategory().getNameCategory())
                .userCateroryId(entity.getUserCategory().getSeqid())
                .userCategoryRole(entity.getUserCategory().getRoleName())
                .depositType(entity.getDepositType())
                .creditDay(entity.getCreditDay())
                .referenceNumber(entity.getRefNum())
                .name(entity.getName())
                .userId(entity.getUserId())
                .email(entity.getEmail())
                .sex(entity.getSex())
                .hp(entity.getHp())
                .build();
    }

    public Page<UserOfCategoryResp> findAllUser(Pageable pageable) {
        Page<MUserEntity> entity = userRepo.findAll(pageable);
        return new PageImpl<>(
                entity.stream().map(this::toDtoUserOfCategory).collect(Collectors.toList()),
                entity.getPageable(),
                entity.getTotalElements()
        );
    }

    private ReferenceRequest referenceRequest;
    private UserDto userDto;
    
    public ReferenceReport getReference(ReferenceRequest request,String userId) throws ParseException{
    	Page<UserList> details=null;
    	if(userId.isEmpty()) {
    		details = getUserReference(request,"");
    	}else {
    		details = getUserReference(request, userId);
    	}
    	
    	return ReferenceReport.builder()
    			.totalBooking(String.valueOf(details.getContent().stream().mapToInt(x-> toInteger(x.getTotalBooking())).sum()))
    			.totalPenjualan(String.valueOf(details.getContent().stream().mapToInt(x-> toInteger(x.getPenjualan())).sum()))
    			.totalDepositPertama(String.valueOf(details.getContent().stream().mapToInt(x-> toInteger(x.getDepositPertama())).sum()))
    			.totalFeeDepositPertama(String.valueOf(details.getContent().stream().mapToInt(x-> toInteger(x.getFeeDepositPertama())).sum()))
    			.totalDepositLanjutan(String.valueOf(details.getContent().stream().mapToInt(x-> toInteger(x.getDepositLanjutan())).sum()))
    			.totalFeeDepositLanjutan(String.valueOf(details.getContent().stream().mapToInt(x-> toInteger(x.getDepositLanjutan())).sum()))
    			.totalFeePenjualan(String.valueOf(details.getContent().stream().mapToInt(x-> toInteger(x.getFeePenjualan())).sum()))
    			.userName(userDto.getMUserEntity().getName())
    			.accountNo(userDto.getMUserEntity().getAccountNo())
    			.detail(details)
    		    .build();
    }
    public Page<UserList> getUserReference(ReferenceRequest request,String userId) throws ParseException {
        referenceRequest=request;
        if(userId.isEmpty()) {
        	userDto=getMUserEntity(request.getUserid());
        }else {
        	userDto = getMUserEntity(userId);
        }
        
        LocalDate startDate= DateTimeUtil.getDateFrom(request.getStartDate(),"yyyyMMdd");
        LocalDate endDate=DateTimeUtil.getDateFrom(request.getEndDate(),"yyyyMMdd");
        Page<MUserEntity> userEntities;
        if(request.getActive()==null)request.setActive(false);
        if(request.getActive())
            userEntities=userRepo.getPaymentByReferance(userDto.getMUserEntity().getAccountNo(),startDate,endDate,request.getPageable());
        else
            userEntities=userRepo.getUserReferance(userDto.getMUserEntity().getAccountNo(),request.getPageable());
 
        return new PageImpl<>(
                userEntities.getContent().stream().map(this::toDtoUserList).collect(Collectors.toList()),
                userEntities.getPageable(),
                userEntities.getTotalElements());
    }

    public UserList toDtoUserList(MUserEntity userEntity){
    	LocalDate startDate = LocalDate.now();
    	LocalDate endDate = LocalDate.now();
    	Integer nominal_deposit=0;
    	Integer fee_deposit=0;
    	Integer nominal_deposit_lanjutan=0;
    	Integer fee_deposit_lanjutan=0;
    	Integer fee_trx_penjualan=0;
    	 List<TFeeReferenceEntity> tfeeRef = new ArrayList<>();
        Boolean haveDetail=true;
            try{
                startDate= DateTimeUtil.getDateFrom(referenceRequest.getStartDate(),"yyyyMMdd");
                endDate=DateTimeUtil.getDateFrom(referenceRequest.getEndDate(),"yyyyMMdd");
                tfeeRef = tFeeReferenceRepo.findByUserIdAndTglTrx(userEntity.getUserId(),
                		DateTimeUtil.getDatetoString(startDate, "yyyy-MM-dd"),
                		DateTimeUtil.getDatetoString(endDate, "yyyy-MM-dd"));
            }catch (Exception e){

            }        
        for(TFeeReferenceEntity tf : tfeeRef) {
        	if(tf.getUnitFee().getIdMFeeTrx().equals(1)) {
        		nominal_deposit=nominal_deposit + tf.getNominal();
        		fee_deposit = fee_deposit + tf.getFeeNominal();
        	}
        	else if(tf.getUnitFee().getIdMFeeTrx().equals(2)) {
        		nominal_deposit_lanjutan = nominal_deposit_lanjutan + tf.getNominal();
        		fee_deposit_lanjutan = fee_deposit_lanjutan + tf.getFeeNominal();
        	}else if(tf.getUnitFee().getIdMFeeTrx().equals(3)) {
        		fee_trx_penjualan = fee_trx_penjualan + tf.getFeeNominal();
        	}
        }
        BigDecimal TotalAmount = paymentEntity.countTotalAmountByUserIdUserId(userEntity.getUserId(), startDate, endDate);
        Integer TotalTrx = paymentEntity.countByUserIdUserId(userEntity.getUserId(), startDate, endDate);
        if(TotalTrx==null)haveDetail=false;
        return UserList.builder()
                .email(userEntity.getUserId())
                .name(userEntity.getName())
                .phoneNumber(userEntity.getHp())
                .haveDetail(haveDetail)
                .city(userEntity.getAreaOriginId())
                .totalBooking(TotalTrx==null?"0":TotalTrx.toString())
                .penjualan(TotalAmount==null?"0":TotalAmount.toString())
                .depositPertama(nominal_deposit.toString())
                .feeDepositPertama(fee_deposit.toString())
                .depositLanjutan(nominal_deposit_lanjutan.toString())
                .feeDepositLanjutan(fee_deposit_lanjutan.toString())
                .feePenjualan(fee_trx_penjualan.toString())
                .build();
    }

    private MPostalCodeEntity findPostalById(String id) {
        Integer idPostalCode = Optional.ofNullable(id).filter(v -> !v.equals("-")).map(Integer::parseInt).orElse(0);
        return postalCodeRepo.findById(idPostalCode).orElse(new MPostalCodeEntity());
    }

    private UserDto toUserDto(MUserEntity mUserEntity){
        UserDto userDto=UserDto.builder()
                .mUserEntity(mUserEntity)
                .profileRes(createUserData(mUserEntity.getUserId(),null))
                .build();
        userDto.setBalance(mUserEntity.getBalance());
        return userDto;
    }

    public void saveEdit(EditProfileReq request, String id) {
        MUserEntity entity = findUser(id);
        Integer postalCode = postalCodeRepo.findById(request.getIdPostalCode()).map(MPostalCodeEntity::getIdPostalCode).orElse(0);
        entity.setName(request.getName() == null ? entity.getName() : request.getName());
        entity.setEmail(request.getEmail() == null ? entity.getEmail() : request.getEmail());
        entity.setAddr(request.getAddress() == null ? entity.getAddr() : request.getAddress());
        entity.setIdPostalCode(postalCode == 0 ? entity.getIdPostalCode() : String.valueOf(postalCode));
        entity.setHp(request.getHp() == null ? entity.getHp() : request.getHp());
        entity.setPob(request.getPlaceOfBirth() == null ? entity.getPob() : request.getPlaceOfBirth());
        entity.setDob(request.getDateOfBirth() == null ? entity.getPob() : request.getDateOfBirth());
        entity.setIdType(request.getIdType() == null ? entity.getIdType() : request.getIdType());
        entity.setBankCode(request.getBankCode() == null ? entity.getBankCode() : request.getIdType());
        entity.setNamaRekening(request.getNamaRekening() == null ? entity.getNamaRekening() : request.getNamaRekening());
        entity.setNoRekening(request.getNoRekening() == null ? entity.getNoRekening() : request.getNoRekening());

        userRepo.save(entity);
    }

    public ProfileRes.Profile getEditProfile(EditProfileReq request, String id){
    	this.saveEdit(request, id);
    	MUserEntity user = findUser(id);
    	return getProfile(user);
    }
    
    
    public void changePassword(PasswordReq request, String id) {
        MUserEntity user = findUser(id);
        if (isPasswordNotMatch(request.getOldPassword(), user.getPassword())) {
            throw new NotFoundException("Kata Sandi Anda Salah");
        }
        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepo.save(user);
    }

    private boolean isPasswordNotMatch(String password, String passwordEncode) {
        return !encoder.matches(password, passwordEncode);
    }

    public void forgotPassword(String userId) throws MessagingException {
        MUserEntity user = findUser(userId);
        String token = CommonConstant.randomString(15);

        user.setPassSession(token);
        user.setLastTimeSession(LocalDateTime.now());

        userRepo.save(user);

        if (isEmail) {
            mailerComponent.sendResetPassword(user.getEmail(), token, pathUrl);
        }
    }

    private MUserEntity findUser(String id) {
        return userRepo.findById(id).orElseThrow(() -> new NotFoundException("User is not exist"));
    }

    public Response<ProfileRes> login(String userId, ResponseAuth tokenInfo) {
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(), createUserData(userId,tokenInfo));
    }

    public List<UserListRes> getUserDriver(){
        return this.userRepo.findDriver().stream().map(this::toUserListRes).collect(Collectors.toList());
    }

    public List<UserListRes> getListUserPickupRequest(){
    	List<Integer> lsAccountType = Arrays.asList(new Integer[] {1,2});
    	return userRepo.findByAccountTypeAndStatusLayanan(lsAccountType,"1").stream().map(this::toUserListRes).collect(Collectors.toList());
    }
    public List<UserListRes> getListUserAll(){
    	List<Integer> lsAccountType = Arrays.asList(new Integer[] {1,2});
    	return userRepo.findByAccountTypeAndStatusLayanan(lsAccountType,"1").stream().map(this::toUserListRes).collect(Collectors.toList());
    }
    private ProfileRes createUserData(String userId, ResponseAuth tokenInfo){
        MUserEntity user = userRepo.findById(userId).orElseThrow(() -> new NotFoundException("User is not exist"));
        MUserPriorityEntity userPriority = mUserPriorityRepo.findByUserCategory(user.getUserCategory().getSeqid());
        ProfileRes.Profile profile = getProfile(user);
        List<ProfileRes.Branch> branches = findByUserId(user.getUserId());
        Integer seqIds = user.getUserCategory().getSeqid();
        List<MUserCategoryEntity> userCategory = userCategoryRepo.findByseqid(seqIds);
//        MUserCategoryEntity userCategory = user.getUserCategory();

        ProfileRes res = ProfileRes.builder()
                .branch(branches)
                .creditDay(calculateCreditDay(user))
                .depositType(user.getDepositType()) // 0 deposit, 1 credit
                .flagUser(CommonConstant.toBoolean(user.getCourierFlag()))
                .flagMitra(CommonConstant.toBoolean(user.getMitraFlag()))
                .balance(user.getBalance().multiply(new BigDecimal(-1)))
                .profile(profile)
                .feeDepositPertama(0)
                .feeDepositLanjutan(0)
                .feeTrx(0)
                .accountType(user.getUserCategory().getAccountType())
                .referenceUser(userRepo.countByRefNum(user.getAccountNo()))
                .userCategoryName(userCategory.get(0).getNameCategory())
                .userCategoryId(userCategory.get(0).getSeqid())
                .authentication(tokenInfo)
                .request1(userPriority==null?false:userPriority.getRequest1())
                .request2(userPriority==null?false:userPriority.getRequest2())
                .request3(userPriority==null?false:userPriority.getRequest3())
                .minKiriman(userPriority==null?0:userPriority.getMinKiriman())
                .paylater(userPriority==null?false:userPriority.getPaylater())
                .userLevel(user.getUserLevel())
                .build();
        return res;
    }


    private int calculateCreditDay(MUserEntity user) {
        long credit = 0;
        TCreditEntity transCredit = findCreditByUser(user.getUserId());
        if (transCredit != null) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate tglMulai = LocalDate.parse(transCredit.getTglMulai(), dateFormatter);
            long inv = Instant.now().toEpochMilli() - tglMulai.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;
            long sel = TimeUnit.DAYS.convert(inv, TimeUnit.MILLISECONDS);
            credit = Long.valueOf(user.getCreditDay()) - sel;
        }

        return (int) credit;
    }

    private TCreditEntity findCreditByUser(String userId) {
        List<TCreditEntity> credits = creditRepo.findByUserAndNominalGraterZero(userId, FLAG);
        if(credits.isEmpty()) {
            return null;
        }

        return credits.get(0);
    }

    private MPostalCodeEntity findById(String id) {
        Integer idPostalCode = Optional.ofNullable(id).filter(v -> !v.equals("-")).map(Integer::parseInt).orElse(0);
        return postalCodeRepo.findById(idPostalCode).orElse(new MPostalCodeEntity());
    }

    private List<ProfileRes.Branch> findByUserId(String userId) {
        List<MOfficeEntity> entityList= tOfficeRepo.findByUserIdUserId(userId).stream().map(this::toMOfficeEntity).collect(Collectors.toList());
        List<MOfficeEntity> entityWithChild=new ArrayList<>();
        entityList.forEach(mOfficeEntity -> {
//            entityWithChild.addAll(officeRepo.findAllByParentOffice(mOfficeEntity.getOfficeCode()));
        	entityWithChild.addAll(officeCodeService.getBranchList(mOfficeEntity.getOfficeCode()));
        });
        return entityWithChild.stream().map(this::toBranch).collect(Collectors.toList());
    }

    private UserListRes toUserListRes(MUserEntity userEntity){
        return UserListRes.builder()
                .userId(userEntity.getUserId())
                .userName(userEntity.getName())
                .userCategory(userEntity.getUserCategory().getNameCategory())
                .userCategoryId(userEntity.getUserCategory().getSeqid())
                .userPhoneNumber(userEntity.getHp())
                .build();
    }

    private ProfileRes.Branch toBranch(TOfficeEntity entity) {
        String seqId = entity.getOfficeCode().getOfficeCode();
        MOfficeEntity mOffice = officeRepo.findById(seqId).orElseThrow(() -> new NotFoundException("Office Not found"));
        return toBranch(mOffice);
    }

    private MOfficeEntity toMOfficeEntity(TOfficeEntity entity){
        String seqId = entity.getOfficeCode().getOfficeCode();
        return officeRepo.findById(seqId).orElseThrow(() -> new NotFoundException("Office Not found"));
    }

    private ProfileRes.Branch toBranch(MOfficeEntity entity) {
        return ProfileRes.Branch.builder()
                .officeCode(entity.getOfficeCode())
                .officeName(entity.getName())
                .unitType(entity.getUnitType())
                .build();
    }

    public ReportTransactionRes getReportTransaction(){
        return null;
    }
    
    private ProfileRes.Profile getProfile(MUserEntity user) {
    	MPostalCodeEntity postalCode = findById(user.getIdPostalCode());
        return ProfileRes.Profile.builder()
                .name(user.getName())
                .userId(user.getUserId())
                .email(user.getEmail())
                .sex(user.getSex()==null?"":user.getSex())
                .hp(user.getHp())
                .address(user.getAddr())
                .referenceNumber(user.getAccountNo())
                .postalCode(String.valueOf(postalCode.getPostalCode()))
                .kelurahan((postalCode.getKelurahan()==null)?"":postalCode.getKelurahan())
                .kecamatan((postalCode.getKecamatanEntity()==null)?"-":postalCode.getKecamatanEntity().getKecamatan())
                .kota((postalCode.getKecamatanEntity()==null)?"-":postalCode.getKecamatanEntity().getKotaEntity().getName())
                .provinsi((postalCode.getKecamatanEntity()==null)?"-":postalCode.getKecamatanEntity().getKotaEntity().getProvinsiEntity().getName())
                .placeOfBirth(user.getPob()==null?"":user.getPob())
                .dateOfBirth(user.getDob()==null?"":user.getDob())
                .idType(Optional.ofNullable(user.getIdType()).orElse("1"))
                .idNo(Optional.ofNullable(user.getIdNo()).orElse(""))
                .noRekening(Optional.ofNullable(user.getNoRekening()).orElse(""))
                .bankCode(Optional.ofNullable(user.getBankCode()).orElse(""))
                .namaRekening(Optional.ofNullable(user.getNamaRekening()).orElse(""))
                .build();
    }
 
    public void save(MUserEntity entity) {
    	userRepo.save(entity);
    }
    
    private Integer toInteger(String value) {
		return Integer.valueOf(value);
	}
    
    public Response<String> newPass(PasswordReq request){
    	MUserEntity user = userRepo.findByPassSession(request.getToken());
    	if(user == null) {
    		throw new NotFoundException("User Tidak Ditemukan !");
    	}
    	user.setPassword(encoder.encode(request.getNewPassword()));
    	user.setLastUpdate(LocalDateTime.now());
    	userRepo.save(user);
    	
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase()
    			);
    }
    
    public StatusResponse cekUser(String officeCode,String userid) {
    	MOfficeEntity office = officeRepo.findByOfficeCode(officeCode);
    	if(office==null) throw new NotFoundException("Data Tidak Ditemukan");
    	boolean isCounter = false;
    	if(office.getUnitType().equals("2")) {
    		isCounter = true;
    	}
    	return StatusResponse.builder().isCounter(isCounter).build();
    }
}
