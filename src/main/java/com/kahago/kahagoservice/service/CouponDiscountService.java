package com.kahago.kahagoservice.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.kahago.kahagoservice.model.response.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kahago.kahagoservice.component.FirebaseComponent;
import com.kahago.kahagoservice.entity.MCouponCategoryUserEntity;
import com.kahago.kahagoservice.entity.MCouponDiscountEntity;
import com.kahago.kahagoservice.entity.MCouponOptionPaymentEntity;
import com.kahago.kahagoservice.entity.MCouponProductEntity;
import com.kahago.kahagoservice.entity.MCouponVendorEntity;
import com.kahago.kahagoservice.entity.MOptionPaymentEntity;
import com.kahago.kahagoservice.entity.MProductSwitcherEntity;
import com.kahago.kahagoservice.entity.MSwitcherEntity;
import com.kahago.kahagoservice.entity.MTutorialEntity;
import com.kahago.kahagoservice.entity.MUserCategoryEntity;
import com.kahago.kahagoservice.enummodel.BlastType;
import com.kahago.kahagoservice.exception.InternalServerException;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.dto.BlastDTO;
import com.kahago.kahagoservice.model.request.CouponDiscountRequest;
import com.kahago.kahagoservice.model.request.ImageRequest;
import com.kahago.kahagoservice.model.request.OptionPayment;
import com.kahago.kahagoservice.model.request.ProductSw;
import com.kahago.kahagoservice.model.request.UserCategory;
import com.kahago.kahagoservice.model.request.Vendor;
import com.kahago.kahagoservice.repository.MCouponCategoryRepo;
import com.kahago.kahagoservice.repository.MCouponDiscountRepo;
import com.kahago.kahagoservice.repository.MCouponOptionPaymentRepo;
import com.kahago.kahagoservice.repository.MCouponProductRepo;
import com.kahago.kahagoservice.repository.MCouponVendorRepo;
import com.kahago.kahagoservice.repository.MTutorialRepo;
import com.kahago.kahagoservice.util.CommonConstant;
import com.kahago.kahagoservice.util.DateTimeUtil;

import static com.kahago.kahagoservice.util.ImageConstant.*;

/**
 * @author Ibnu Wasis
 */
@Service
public class CouponDiscountService {
	@Autowired
	private MCouponDiscountRepo mDiscountRepo;
	@Autowired
	private MCouponCategoryRepo mCategoryRepo;
	@Autowired
	private MCouponProductRepo mProductRepo;
	@Autowired
	private MCouponVendorRepo mVendorRepo;
	@Autowired
	private MCouponOptionPaymentRepo mOptionPaymentRepo;
	@Autowired
	private MTutorialRepo mTutorialRepo;
	@Autowired
	private FirebaseComponent firebase;
	
	
	@Value("${kahago.image.coupon}")
	private String uploadingDir;
	
	private static final Logger log = LoggerFactory.getLogger(CouponDiscountService.class);
	
	public Page<CouponDiscountResponse> getAllDiscount(String couponCode,Integer reference,Pageable pageable){
		Page<MCouponDiscountEntity> lCoupon = mDiscountRepo.findAllByCouponCodeAndReference(couponCode, reference, pageable);
		System.out.print(lCoupon.getPageable());
		return new PageImpl<>(
				lCoupon.getContent().stream().map(this::toDto).collect(Collectors.toList()),
				lCoupon.getPageable(),
				lCoupon.getTotalElements());
	}
	
	public CouponDiscountResponse getById(Integer idCoupon) {
		MCouponDiscountEntity coupon = mDiscountRepo.findAllById(idCoupon);
		return toDto(coupon);
	}
	private CouponDiscountResponse toDto(MCouponDiscountEntity entity) {
		List<UserCategoryResponse> luserCat = new ArrayList<UserCategoryResponse>();
		List<VendorResponse> lvendor = new ArrayList<>();
		List<ProductResponse> lproduct= new ArrayList<>();
		List<OptionPaymentResponse> lOption = new ArrayList<>();
		for(MCouponCategoryUserEntity ucat:mCategoryRepo.findAllByIdCoupon(entity.getId())) {
			UserCategoryResponse uc = CategoryUserService.toDtoUserCategory(ucat.getIdCategoryUser());
			luserCat.add(uc);
		}
		for(MCouponVendorEntity vd : mVendorRepo.findAllByIdCoupon(entity.getId())) {
			VendorResponse vendor = VendorResponse.builder()
					.swicherCode(vd.getSwitcherCode().getSwitcherCode())
					.displayName(vd.getSwitcherCode().getName())
					.build();
			lvendor.add(vendor);
		}
		for(MCouponProductEntity pd : mProductRepo.findAllByIdCoupon(entity.getId())) {
			ProductResponse prd = ProductResponse.builder()
					.productCode(pd.getProductSwCode().getProductSwCode())
					.productDisplayName(pd.getProductSwCode().getName())
					.build();
			lproduct.add(prd);
		}
		for(MCouponOptionPaymentEntity op : mOptionPaymentRepo.findAllByIdCoupon(entity.getId())) {
			OptionPaymentResponse opt = OptionPaymentResponse.builder()
											.seqid(op.getOptionPaymentId().getSeqid())
											.codePayment(op.getOptionPaymentId().getCode())
											.codeVendor(op.getOptionPaymentId().getCodeVendor().toString())
											.description(op.getOptionPaymentId().getDescription())
											.images(op.getOptionPaymentId().getPathImage())
											.isActive(op.getOptionPaymentId().getIsActive())
											.isDeposit(op.getOptionPaymentId().getIsDeposit())
											.isPayment(op.getOptionPaymentId().getIsPayment())
											.isPhone(op.getOptionPaymentId().getIsPhone())
											.minNominal(op.getOptionPaymentId().getMinNominal().intValue())
											.offTimeEnd(op.getOptionPaymentId().getOffTimeEnd().toString())
											.offTimeStart(op.getOptionPaymentId().getOffTimeStart().toString())
											.operator(op.getOptionPaymentId().getOperatorSw())
											.build();
			lOption.add(opt);
		}
		return CouponDiscountResponse.builder()
				.couponCode(entity.getCouponCode())
				.couponName(entity.getCouponName())
				.percetageDiscont(entity.getPercentageDiscount())
				.percetageNominal(entity.getNominalDiscount())
				.maxDiscount(entity.getMaxDiscount())
				.minTransaction(entity.getMinTransaction())
				.startDate(entity.getExpiredStartDate())
				.endDate(entity.getExpiredEndDate())
				.inOneUse(entity.getIsOneUse())
				.isDevice(entity.getIsDevice())
				.isUserCategory(CommonConstant.toBoolean(entity.getByCategoryUser()==null?0:entity.getByCategoryUser()))
				.isOptionPayment(CommonConstant.toBoolean(entity.getByOptionPayment()))
				.isFirstUse(entity.getIsFirstUse())
				.isVendor(CommonConstant.toBoolean(entity.getByVendor()))
				.isProductSw(CommonConstant.toBoolean(entity.getByProduct()))
				.urlFrontImage(PREFIX_PATH_IMAGE_COUPON+entity.getUrlFrontImage().substring(entity.getUrlFrontImage().lastIndexOf("/")+1))
				.urlBackImage(PREFIX_PATH_IMAGE_COUPON+entity.getUrlBackgroundImage().substring(entity.getUrlBackgroundImage().lastIndexOf("/")+1))
				.isPublic(entity.getIsPublic())
				.urlBlastImage(PREFIX_PATH_IMAGE_COUPON+entity.getPathBlastImage().substring(entity.getPathBlastImage().lastIndexOf("/")+1))
				.isActive(entity.getIsActive())
				.isDashboard(entity.isShowDashboard())
				.desc(entity.getDescription())
				.isReceiver(entity.getIsReceiver())
				.referenceCode(entity.getReferenceCoupon())
				.listPayment(lOption.size()==0?null:lOption)
				.listProduct(lproduct.size()==0?null:lproduct)
				.listVendor(lvendor.size()==0?null:lvendor)
				.userCategory(luserCat.size()==0?null:luserCat)
				.idCoupon(entity.getId())
				.couponType(CommonConstant.toBoolean(entity.getCouponType()))
				.limitDiscount(entity.getLimitDiscount())
				.build();
	}
	
	@Transactional()
	public SaveResponse saveAdd(CouponDiscountRequest request,String userAdmin) {
		MCouponDiscountEntity discount = new MCouponDiscountEntity();
		isValidate(request);
		if(request.getFrontImage()==null) {
			throw new NotFoundException("Image promo depan tidak boleh kosong");
		}
		if(request.getBackImage()==null) {
			throw new NotFoundException("Image promo belakang tidak boleh kosong");
		}
		if(request.getBlastImage()==null) {
			throw new NotFoundException("Image Blast tidak boleh kosong");
		}
		if(!request.getGenerateKuponAuto()) {
			 String[] splitCoupon =  request.getCouponCode().replace(" ", "").split(",");
			 List<String> grater7Char = Arrays.stream(splitCoupon).filter(v -> v.length() > 7).collect(Collectors.toList());
			
		}
		if(isExist(request.getCouponCode())) {
			throw new NotFoundException("Kode Kupon sudah ada");
		}
		if(request.getGenerateKuponAuto()) {
			Integer reference=null;
			for(String temp:generateCouponCode(request.getTotalCoupon())) {
				discount = toEntity(request, temp,userAdmin);
				if(reference!=null)
					discount.setReferenceCoupon(reference);
				discount = mDiscountRepo.save(discount);
				if(reference==null)
					reference=discount.getId();
				insertTo(discount, request);
			}
		}else {
			String []splitCoupon = request.getCouponCode().replace(" ", "").split(",");
			if(splitCoupon.length != 1) {
				Integer reference = null;
				for(String temp:splitCoupon) {
					discount = toEntity(request, temp,userAdmin);
					if(reference!=null)
						discount.setReferenceCoupon(reference);
					discount = mDiscountRepo.save(discount);
					if(reference==null)
						reference=discount.getId();
					insertTo(discount, request);
				}
			}else {
				discount=toEntity(request, splitCoupon[0],userAdmin);
				discount = mDiscountRepo.save(discount);
				insertTo(discount, request);
			}
		}
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Berhasil Simpan Kupon Diskon Baru")
				.build();
	}
	
	@Transactional()
	public SaveResponse editSave(CouponDiscountRequest request,String userAdmin) {
		MCouponDiscountEntity discont = mDiscountRepo.findAllById(request.getIdCoupon());
		if(discont==null) throw new NotFoundException("Data Tidak Ditemukan !");
		List<MCouponDiscountEntity> lDiscount = mDiscountRepo.findAllByReferenceCoupon(request.getIdCoupon());
		MCouponDiscountEntity disc = toEntity(request, request.getCouponCode(),userAdmin);
		disc.setCouponCode(discont.getCouponCode());
		disc.setCreatedBy(discont.getCreatedBy());
		disc.setCreatedAt(discont.getCreatedAt());
		disc.setIsActive(discont.getIsActive());
		disc.setUpdateAt(LocalDateTime.now());
		disc.setUpdateBy(userAdmin);
		if(request.getFrontImage()==null) {
			disc.setUrlFrontImage(discont.getUrlFrontImage());
		}
		if(request.getBackImage()==null) {
			disc.setUrlBackgroundImage(discont.getUrlBackgroundImage());
		}
		if(request.getBlastImage()==null) {
			disc.setPathBlastImage(discont.getPathBlastImage());
		}		
		disc.setId(request.getIdCoupon());
		disc.setReferenceCoupon(discont.getReferenceCoupon());
		clearData(disc);
		insertTo(disc, request);
		mDiscountRepo.save(disc);
		for(MCouponDiscountEntity coupon : lDiscount) {
			disc.setId(coupon.getId());
			disc.setCouponCode(coupon.getCouponCode());
			disc.setReferenceCoupon(coupon.getReferenceCoupon());
			mDiscountRepo.save(disc);
		}
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Berhasil Update Coupon")
				.build();
	}
	
	@Transactional()
	public SaveResponse deleteCoupon(int id) {
		MCouponDiscountEntity discount = mDiscountRepo.getOne(id);
		if(discount != null) {
			if(discount.getIsActive()) {
				discount.setIsActive(false);
			}else {
				discount.setIsActive(true);
			}
			mDiscountRepo.save(discount);
		}
		List<MCouponDiscountEntity>couponList = mDiscountRepo.findAllByReferenceCoupon(id);
		for(MCouponDiscountEntity coupon : couponList) {
			if(coupon.getIsActive()) {
				coupon.setIsActive(false);
			}else {
				coupon.setIsActive(true);
			}
			mDiscountRepo.save(discount);
		}
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Berhasil update Coupon")
				.build();
	}
	
	
	private Boolean isValidate(CouponDiscountRequest request) {
		Boolean flag = true;
		if(!request.getGenerateKuponAuto()&&request.getCouponCode().isEmpty()) {
			flag=false;
			throw new NotFoundException("Kode Kupon tidak boleh kosong");
		}
		if(!request.getCouponType()&& request.getPercetage()==null) {
			flag=false;
			throw new NotFoundException("Nilai persentase tidak boleh kosong");
		}
		if(request.getCouponType()&&request.getNominal()==null) {
			flag=false;
			throw new NotFoundException("Nominal tidak boleh kosong");
		}
		if(request.getIsMaxDiscount()&&request.getMaxDiscount()==null) {
			flag=false;
			throw new NotFoundException("Maksimal Diskon tidak boleh kosong");
		}
		if(request.getIsMinTrx()&&request.getMinimumTrx()==null) {
			flag=false;
			throw new NotFoundException("Minimum transaksi tidak boleh kosong");
		}
		if(request.getIsUserCategory()&&request.getLUserCategory().size()==0) {
			flag=false;
			throw new NotFoundException("Daftar User Kategori tidak boleh kosong");
		}
		if(request.getIsVendor()&&request.getLVendor().size()==0) {
			flag=false;
			throw new NotFoundException("Daftar Vendor tidak boleh kosong");
		}
		if(request.getIsProduct()&&request.getLProduct().size()==0) {
			flag=false;
			throw new NotFoundException("Daftar Product tidak boleh kosong");
		}
		if(request.getIsOptionPayment()&&request.getOptionPayment().size()==0) {
			flag=false;
			throw new NotFoundException("Daftar Metode Pembayaran tidak boleh kosong");
		}
		return flag;
	}
	
	private Boolean isExist(String couponCode) {
		return mDiscountRepo.findByCode(couponCode)!=null;
	}
	
	private MCouponDiscountEntity toEntity(CouponDiscountRequest request,String couponCode,String userAdmin) {
		MCouponDiscountEntity entity = new MCouponDiscountEntity();
		entity.setCouponCode(couponCode);
		entity.setCouponName(request.getCouponName());
		if(request.getCouponType()) {
			entity.setCouponType(1);
			entity.setNominalDiscount(request.getNominal());
			entity.setPercentageDiscount(0.0);
		}else {
			entity.setCouponType(0);
			entity.setPercentageDiscount(request.getPercetage());
			entity.setNominalDiscount(BigDecimal.ZERO);
		}
		if(request.getIsMaxDiscount()) {
			entity.setMaxDiscount(request.getMaxDiscount());
		}else {
			entity.setMaxDiscount(0);
		}
		if(request.getIsMinTrx()) {
			entity.setMinTransaction(request.getMinimumTrx());
		}else {
			entity.setMinTransaction(0);
		}
		try {
			entity.setExpiredStartDate(DateTimeUtil.getDateFrom(request.getStartDate(), "ddMMyyyy"));
			entity.setExpiredEndDate(DateTimeUtil.getDateFrom(request.getEndDate(), "ddMMyyyy"));
		}catch (ParseException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		entity.setIsOneUse(request.getIsOneUse());
		entity.setIsDevice(request.getIsDevice());
		if(request.getIsUserCategory()) {
			entity.setByCategoryUser(1);
		}else {
			entity.setByCategoryUser(0);
		}
		if(request.getIsProduct()) {
			entity.setByProduct(1);
		}else {
			entity.setByProduct(0);
		}
		if(request.getIsVendor()) {
			entity.setByVendor(1);
		}else {
			entity.setByVendor(0);
		}
		if(request.getIsOptionPayment()) {
			entity.setByOptionPayment(1);
		}else {
			entity.setByOptionPayment(0);
		}
		entity.setDescription(request.getDesc());
		entity.setIsActive(request.getIsActive());
		entity.setCreatedBy(userAdmin);
		entity.setCreatedAt(LocalDateTime.now());
		entity.setUpdateAt(LocalDateTime.now());
		entity.setUpdateBy(userAdmin);
		entity.setIsPublic(request.getIsPublic());
		entity.setIsReceiver(request.getIsReceiver());
		entity.setShowDashboard(request.getIsDashboard());
		entity.setIsFirstUse(request.getIsFirstUse());
		if(request.getLimitDiscount()==null) {
			entity.setLimitDiscount(BigDecimal.ZERO);
		}else {
			entity.setLimitDiscount(request.getLimitDiscount());
		}
		if(request.getFrontImage()!=null) {
			entity.setUrlFrontImage(uploadFile(request.getFrontImage(), "DIS-", couponCode, "-01"));
		}
		if(request.getBackImage()!=null) {
			entity.setUrlBackgroundImage(uploadFile(request.getBackImage(), "DIS-", couponCode, "-02"));
		}
		if(request.getBlastImage()!=null) {
			entity.setPathBlastImage(uploadFile(request.getBlastImage(), "BLS-", couponCode, "-01"));
		}
		return entity;
	}
	
	private void insertTo(MCouponDiscountEntity entity,CouponDiscountRequest request) {
		if(entity.getByCategoryUser().equals(1)) {
			for(UserCategory et:request.getLUserCategory()) {
				mCategoryRepo.insertToMuserCategory(entity.getId(), et.getUserCategoryId());
			}
		}
		if(entity.getByVendor().equals(1)) {
			for(Vendor v :request.getLVendor()) {
				mVendorRepo.insertToCouponVendor(entity.getId(), v.getSwitcherCode());
			}
			
		}
		if(entity.getByProduct().equals(1)) {
			for(ProductSw p:request.getLProduct()) {
				mProductRepo.insertToCouponProduct(entity.getId(), p.getProductSwCode());
			}
		}
		if(entity.getByOptionPayment().equals(1)) {
			for(OptionPayment op:request.getOptionPayment()) {
				mOptionPaymentRepo.insertToCouponOption(entity.getId(), op.getOptionPaymentId());
			}
		}
	}
	@Transactional
	void clearData(MCouponDiscountEntity entity) {
		if(mCategoryRepo.findAllByIdCoupon(entity.getId()).size() > 0) {
			mCategoryRepo.deleteByIdCoupon(entity.getId());
		}
		if(mProductRepo.findAllByIdCoupon(entity.getId()).size() > 0) {
			mProductRepo.deleteByIdCoupon(entity.getId());
		}
		if(mOptionPaymentRepo.findAllByIdCoupon(entity.getId()).size() > 0) {
			mOptionPaymentRepo.deleteById(entity.getId());
		}
		if(mVendorRepo.findAllByIdCoupon(entity.getId()).size() > 0) {
			mVendorRepo.deleteByIdCoupon(entity.getId());
		}
	}
	private List<String> generateCouponCode(int value) {
        List<String> idGenerated = new ArrayList<>();

        while (idGenerated.size() != value) {
            String coupon = RandomStringUtils.randomAlphanumeric(7).toUpperCase();

            if (!isExist(coupon)) {
                idGenerated.add(coupon);
            }
        }
        return idGenerated;
    }
	
	public void blastPromo(Integer idBlast,BlastType blastType) {
		MTutorialEntity mTutorialEntity = new MTutorialEntity();
		MCouponDiscountEntity couponEntity = new MCouponDiscountEntity();
		BlastDTO blastDTO = new BlastDTO();
		if(blastType.equals(BlastType.COUPON)) {
			couponEntity = mDiscountRepo.getOne(idBlast);
			blastDTO.setTitle(couponEntity.getCouponName());
			blastDTO.setDescription(couponEntity.getDescription());
			blastDTO.setIdBlast(couponEntity.getCouponCode());
			blastDTO.setImageLocation("/blast/"+blastType.name()+"/"+couponEntity.getId().toString()+couponEntity.getPathBlastImage().substring(couponEntity.getPathBlastImage().lastIndexOf(".")));
			if(couponEntity.getByCategoryUser().equals(1)) {
				List<Integer> lusercat = new ArrayList<>();
				List<MCouponCategoryUserEntity> lucat = mCategoryRepo.findAllByIdCoupon(couponEntity.getId());
				for(MCouponCategoryUserEntity ucat : lucat) {
					lusercat.add(ucat.getIdCategoryUser().getSeqid());
				}
				blastDTO.setUserCategory(lusercat);
			}
		}else if(blastType.equals(BlastType.PROMO)) {
			mTutorialEntity = mTutorialRepo.findBySeqid(idBlast);
			blastDTO.setTitle(mTutorialEntity.getPromoName());
			blastDTO.setDescription(mTutorialEntity.getDescription());
			blastDTO.setIdBlast(mTutorialEntity.getSeqid()+"");
			blastDTO.setImageLocation("/blast/"+blastType.name()+"/"+mTutorialEntity.getSeqid().toString()+mTutorialEntity.getPathBlastImage().substring(mTutorialEntity.getPathBlastImage().lastIndexOf(".")));
		}
		blastDTO.setTypeBlast(blastType.getInteger());
		Gson gson = new Gson();
		JSONObject data = new JSONObject();
		try {
			String topik = "Blast";
			topik = "infodev";
			data.put("idTrx", "blast");
	        data.put("date", LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
	        data.put("userid", "-");
	        data.put("nominal", "0");
	        data.put("type_trx", "blast"); //1. Book, 2. Deposit, 3.news
	        data.put("tittle", "blast");
	        data.put("tag", "blast");
	        data.put("body", gson.toJson(blastDTO));
	        data.put("status_trx", "1"); //0.failed, 1.Success
	        firebase.notifAll("Blast", "Blast Promo Terbaru", data, "Blast",topik);
		}catch (Exception e) {
			// TODO: handle exception
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	public String uploadFile(ImageRequest req,String kode,String couponCode,String kodebl) {
		String path="";
		try {
			byte[] bytes = Base64.decodeBase64(req.getContent());
			path=uploadingDir+kode+couponCode.toUpperCase()+kodebl+req.getFileName().substring(req.getFileName().lastIndexOf("."));
			Path fileLoc  = Paths.get(path);
			Files.write(fileLoc, bytes);
		}catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new InternalServerException(e.getMessage());
		}
		return path;
	}
}
