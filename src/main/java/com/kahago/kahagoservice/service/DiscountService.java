package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.entity.MCouponCategoryUserEntity;
import com.kahago.kahagoservice.entity.MCouponDiscountEntity;
import com.kahago.kahagoservice.entity.MCouponProductEntity;
import com.kahago.kahagoservice.entity.MCouponVendorEntity;
import com.kahago.kahagoservice.entity.MProductSwitcherEntity;
import com.kahago.kahagoservice.entity.MUserEntity;
import com.kahago.kahagoservice.entity.TDiscountEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.enummodel.DiscountEnum;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.exception.CustomError;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.request.DiscountReq;
import com.kahago.kahagoservice.model.response.CouponRes;
import com.kahago.kahagoservice.model.response.DiscountResp;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.repository.MCouponCategoryRepo;
import com.kahago.kahagoservice.repository.MCouponDiscountRepo;
import com.kahago.kahagoservice.repository.MCouponProductRepo;
import com.kahago.kahagoservice.repository.MCouponVendorRepo;
import com.kahago.kahagoservice.repository.MUserCategoryRepo;
import com.kahago.kahagoservice.repository.MUserRepo;
import com.kahago.kahagoservice.repository.TDiscountRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.util.Common;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import static com.kahago.kahagoservice.util.ImageConstant.*;


/**
 * @author Hendro yuwono
 */
@Slf4j
@Service
public class DiscountService {
    @Autowired
    private MCouponDiscountRepo couponDiscountRepo;

    @Autowired
    private TPaymentRepo paymentRepo;
    @Autowired
    private MUserRepo userRepo;
    @Autowired
    private MCouponCategoryRepo couponCategoryRepo;
    @Autowired
    private MCouponVendorRepo couponVendorRepo;
    @Autowired
    private MCouponProductRepo couponProductRepo;
    @Autowired
    private PaymentService payService;
//    @Autowired
//    private TDiscountRepo discRepo;
    @Setter @Getter
    private Integer totalItem;
    private Double totalAmount;
    public List<CouponRes> checkDiscount(String userId) {
        return couponDiscountRepo.findCouponActive(LocalDate.now(), LocalDate.now()).stream()
                .map(this::toDtoCoupon).collect(Collectors.toList());
        }

    private CouponRes toDtoCoupon(MCouponDiscountEntity entity) {
        String images = PREFIX_PATH_IMAGE_COUPON + entity.getUrlFrontImage().substring(entity.getUrlFrontImage().lastIndexOf("/") + 1);
        return CouponRes.builder()
                .code(entity.getCouponCode())
                .description(entity.getDescription())
                .expiredDate(entity.getExpiredEndDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .title(entity.getCouponName())
                .image(images)
                .build();
    }
    
    public Response<DiscountResp> validasiVoucher(DiscountReq req,String action) {
    	log.info("==> Validasi discount  "+action+"<==");
    	log.info(Common.json2String(req));
    	DiscountResp discResp = DiscountResp.builder()
    			.nominal("0")
    			.subTotal("0")
    			.nominal("0").build();
    	List<String> lbooks = req.getBooks().stream()
    			.filter(b->b.getTypeTrx().equals("1"))
    			.map(l -> l.getBookingCode()).collect(Collectors.toList());
    	List<TPaymentEntity> lspay = paymentRepo.findAllById(lbooks);
    	lspay.forEach(payService.resetPayment());
    	paymentRepo.saveAll(lspay);
    	Double oldAmount = lspay.stream().mapToDouble(p->p.getAmount().doubleValue()).sum();
    	TPaymentEntity pay = lspay.stream().findFirst().get();
    	MCouponDiscountEntity kupon = couponDiscountRepo.findByCode(req.getCode());
    	MUserEntity user = userRepo.getMUserEntitiesBy(req.getUserId());
    	
    	if(kupon==null) {
    		throw new ResponseStatusException(HttpStatus.NOT_FOUND, DiscountEnum.NOT_USED.getKeterangan());
    	}
    	if(kupon.getIsOneUse()) {
    		if(isUsed(req.getUserId(),kupon.getCouponCode())) {
    			throw new ResponseStatusException(HttpStatus.NOT_FOUND, DiscountEnum.NOT_USED.getKeterangan());
    		}
    	}
    	if(kupon.getIsActive()==false) {
    		throw new ResponseStatusException(HttpStatus.NOT_FOUND, DiscountEnum.NOT_USED.getKeterangan());
    	}
    	if(kupon.getIsFirstUse()) {
    		if(isFirst(req.getUserId())==false){
    			throw new ResponseStatusException(HttpStatus.NOT_FOUND, DiscountEnum.NOT_USED.getKeterangan());
    		}
    	}
    	if(kupon.getIsDevice()) {
    		if(isUsed(req.getUserId(), req.getCode())){
    			throw new ResponseStatusException(HttpStatus.NOT_FOUND, DiscountEnum.NOT_USED.getKeterangan());
    		}
    	}
    	
    	if(kupon.getExpiredStartDate().isAfter(LocalDate.now())) {
    		throw new ResponseStatusException(HttpStatus.NOT_FOUND, DiscountEnum.NOT_YET_VOUCHER.getKeterangan());
    	}
    	if(kupon.getExpiredEndDate().isBefore(LocalDate.now())) {
    		throw new ResponseStatusException(HttpStatus.NOT_FOUND, DiscountEnum.EXPIRED_VOUCHER.getKeterangan());
    	}
    	if(1==kupon.getByCategoryUser()) {
    		if(isCategory(user.getUserCategory().getSeqid(),kupon.getId())==false) {
    			throw new ResponseStatusException(HttpStatus.NOT_FOUND, DiscountEnum.NOT_USED.getKeterangan());
    		}
    	}
    	if(1==kupon.getByVendor()) {
    		if(isVendor(pay.getProductSwCode(),kupon.getId())==false) {
    			throw new ResponseStatusException(HttpStatus.NOT_FOUND, DiscountEnum.NOT_USED.getKeterangan());
    		}
    	}
    	if(1==kupon.getByProduct()) {
    		if(isProduct(pay.getProductSwCode(),kupon.getId())==false) {
    			throw new ResponseStatusException(HttpStatus.NOT_FOUND, DiscountEnum.NOT_USED.getKeterangan());
    		}
    	}
    	List<TPaymentEntity> lsppayfilter = new ArrayList<TPaymentEntity>();
    	lspay.forEach(insertFilterListPay(kupon, lsppayfilter));
    	this.totalItem = lsppayfilter.size();
    	this.totalAmount = lsppayfilter.stream().mapToDouble(p->p.getAmount().doubleValue()).sum() - kupon.getMinTransaction();
    	if(totalAmount < 0) {
    		throw new ResponseStatusException(HttpStatus.NOT_FOUND, DiscountEnum.MIN_NOMINAL_TRX.getKeterangan());
    	}
    	if(lsppayfilter.size()<=0) {
    		throw new ResponseStatusException(HttpStatus.NOT_FOUND, DiscountEnum.NOT_USED.getKeterangan());
    	}
    	

//    	TDiscountEntity discount = discountRepo.findByNoTiket(pay.getNoTiket())
//    			.orElse(new TDiscountEntity());
//    	discount.setDiscountCode(kupon.getCouponCode());
//    	discount.setDiscountValue(getValueDiskon(kupon, oldAmount));
//    	discount.setNoTiket(pay.getNoTiket());
//    	discount.setLastUpdate(LocalDateTime.now());
//    	BigDecimal nominal = kupon.getNominalDiscount();
    	BigDecimal discSisa = getValueDiskon(kupon, oldAmount,user.getUserId());
    	lsppayfilter.forEach(setDiscountValue2(getPercentageDisc(oldAmount, kupon,discSisa.doubleValue()),kupon.getCouponCode()));
//    	if("save".equalsIgnoreCase(action)) {
//    		paymentRepo.saveAll(lsppayfilter);
//    	}
//    	discountRepo.save(discount);
    	Double disc = lsppayfilter.stream().mapToDouble(p->p.getDiscountValue().doubleValue()).sum();
    	disc = Math.floor(disc);
    	if(disc < discSisa.doubleValue()) {
    		Double sisaDisc = discSisa.doubleValue() - disc.longValue();
    		TPaymentEntity pays = lsppayfilter.stream().filter(p->p.getAmount().doubleValue()-sisaDisc>0).findAny().orElse(null);
    		if(pays!=null) {
    			lsppayfilter.remove(pays);
        		pays.setDiscountValue(new BigDecimal(pays.getDiscountValue().doubleValue()+sisaDisc));
        		pays.setAmount(new BigDecimal(pays.getAmount().doubleValue()-sisaDisc));
        		lsppayfilter.add(pays);
    		}else {
    			Double persendisc = (sisaDisc / lsppayfilter.size())/100;
    			lsppayfilter.forEach(setDiscountValue2(persendisc, kupon.getCouponCode()));
    		}
    	}
    	disc = lsppayfilter.stream().mapToDouble(p->p.getDiscountValue().doubleValue()).sum();
    	discResp.setNominal(String.valueOf(oldAmount.longValue()));
    	discResp.setNominalDiscount(String.valueOf(disc));
    	discResp.setSubTotal(String.valueOf(oldAmount - disc));

    	return new Response<DiscountResp>(HttpStatus.OK.name(), HttpStatus.OK.getReasonPhrase(), discResp);
    }

	public Consumer<? super TPaymentEntity> setDiscountValue2(Double percentage,String discountCode) {
		// TODO Auto-generated method stub
		return p->{
			Double discValue = p.getAmount().doubleValue() * percentage;
			p.setDiscountCode(discountCode);
			p.setDiscountValue(new BigDecimal(p.getDiscountValue().doubleValue()+ discValue.longValue()));
			p.setAmount(p.getAmount().subtract(p.getDiscountValue()));
		};
	}

	private  Consumer<? super TPaymentEntity> setDiscountValue(MCouponDiscountEntity kupon) {
		return p ->{
			BigDecimal amt = kupon.getNominalDiscount();
			Double totalUsed = paymentRepo.totalDiscountValueByDiscounCodeAndUserId(kupon.getCouponCode());
    		double sisaDisc = 0;
			if(0==kupon.getCouponType()) {
	    		Double persen = kupon.getPercentageDiscount();
	    		Double nilai = persen / 100;
	    		amt = new BigDecimal(p.getAmount().doubleValue() * nilai);
	    		//add Ibnu limit discount
	    		//end 
	    		if (amt.doubleValue() > Double.valueOf(kupon.getMaxDiscount()) 
	    				&& Double.valueOf(kupon.getMaxDiscount()) > 0)
					amt = new BigDecimal(kupon.getMaxDiscount());
	    		//add Ibnu limit discount
	    		if(kupon.getLimitDiscount().intValue() > 0) {
	    			sisaDisc = kupon.getLimitDiscount().doubleValue() - totalUsed;
	    			if(sisaDisc < 0)sisaDisc = 0;
	    			if(amt.intValue() > sisaDisc)
	    				amt = new BigDecimal(sisaDisc);
	    		}
	    		//end
	    	}
	    	else {
	    		if(totalItem==0) totalItem = 1;
				Double nilai = kupon.getNominalDiscount().doubleValue()/totalItem ;/// totalItem;
				if(p.getAmount().doubleValue() < nilai) nilai = p.getAmount().doubleValue();
				amt = new BigDecimal(nilai);
				//add Ibnu limit discount
	    		if(kupon.getLimitDiscount().intValue() > 0) {
	    			sisaDisc = kupon.getLimitDiscount().intValue() - totalUsed;
	    			if(sisaDisc < 0)sisaDisc = 0;
	    			if(amt.intValue() > sisaDisc )
	    				amt = new BigDecimal(sisaDisc);
	    		}
	    		//end
	    	}
			amt = new BigDecimal(Math.floor(amt.doubleValue()));
    		p.setDiscountCode(kupon.getCouponCode());
    		p.setDiscountValue(amt);
    		p.setAmount(p.getAmount().subtract(amt));
    	};
	}

	private Double getLimitDiskon(MCouponDiscountEntity kupon,String userId) {
    	BigDecimal amtSisa = new BigDecimal("0");
		if(kupon.getLimitDiscount().doubleValue() > 0) {
			List<TPaymentEntity> lspaydisc = paymentRepo.findByDiscountCodeAndUserId(kupon.getCouponCode(),userId);
			Double totalUsed = lspaydisc.stream().mapToDouble(p->p.getDiscountValue().doubleValue()).sum();
			amtSisa = kupon.getLimitDiscount().subtract(new BigDecimal(totalUsed));
			if(amtSisa.doubleValue()<=0) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, DiscountEnum.NOT_USED.getKeterangan());
			}
		}
		return amtSisa.doubleValue();
	}
	public BigDecimal getValueDiskon(MCouponDiscountEntity kupon, Double amount,String userid) {
		BigDecimal amt = kupon.getNominalDiscount();
		Double sisaAmt = getLimitDiskon(kupon,userid);
		if(0==kupon.getCouponType()) {
    		Double persen = kupon.getPercentageDiscount();
    		Double nilai = persen / 100;
    		amt = new BigDecimal(amount * nilai);
    		if (amt.doubleValue() > Double.valueOf(kupon.getMaxDiscount()) 
    				&& Double.valueOf(kupon.getMaxDiscount()) > 0)
				amt = new BigDecimal(kupon.getMaxDiscount());
    	}
		if(sisaAmt>0) amt=(amt.doubleValue()>sisaAmt)?new BigDecimal(sisaAmt):amt;
		return amt;
	}
	private Consumer<? super TPaymentEntity> insertFilterListPay(MCouponDiscountEntity kupon,
			List<TPaymentEntity> lsppayfilter) {
		return p -> {
    		if(1==kupon.getByVendor()) {
    			if(isVendor(p.getProductSwCode(), kupon.getId())==false) {
    				return;
    			}
    		}
    		if(1==kupon.getByProduct()) {
    			if(isProduct(p.getProductSwCode(), kupon.getId())) {
    				return;
    			}
    		}
    		lsppayfilter.add(p);
    	};
	}

	public Double getPercentageDisc(Double amount, MCouponDiscountEntity kupon, Double limitDisc) {
		BigDecimal amt = new BigDecimal(amount);
		BigDecimal disc = new BigDecimal("0");
		Double maxdisc = Double.valueOf(kupon.getMaxDiscount());
		if(limitDisc>0) 
			maxdisc = limitDisc;
		if(0==kupon.getCouponType()) {
			Double persen = kupon.getPercentageDiscount();
			disc = amt.multiply(new BigDecimal((persen/100)));
			if (amt.doubleValue() > maxdisc ) {
				disc = new BigDecimal(maxdisc).multiply(new BigDecimal((persen/100)));
			}
		}
		else {
			disc = kupon.getNominalDiscount(); 
			if(amt.doubleValue()<=kupon.getNominalDiscount().doubleValue()) {
				disc = amt;
			}
		}

		Double diskon = disc.doubleValue()/amt.doubleValue();
		BigDecimal dk = new BigDecimal(diskon);
		dk = dk.setScale(2, RoundingMode.HALF_UP);
		return dk.doubleValue();
	}
	private boolean isProduct(MProductSwitcherEntity productSwCode, Integer id) {
		// TODO Auto-generated method stub
		MCouponProductEntity couponProduct = couponProductRepo
				.findByIdCouponAndProduct(id, productSwCode).stream().findFirst().orElse(null);
		if(couponProduct!=null)return true;
		return false;
	}

	private boolean isVendor(MProductSwitcherEntity productSwCode, Integer id) {
		// TODO Auto-generated method stub
		MCouponVendorEntity couponVendor = couponVendorRepo
				.findByIdCouponAndVendorEntities(id, productSwCode.getSwitcherEntity()).stream().findFirst().orElse(null);
		if(couponVendor!=null) return true;
		return false;
	}

	private boolean isCategory(Integer userCategory, Integer id) {
		// TODO Auto-generated method stub
		MCouponCategoryUserEntity couponCategory = couponCategoryRepo
				.findByIdCategoryUserAndUserEntity(id, userCategory).stream().findFirst().orElse(null);
		if(couponCategory!=null) return true;
		return false;
	}

	private boolean isUsed(String userId, String code) {
		// TODO Auto-generated method stub
		List<TPaymentEntity> pay = paymentRepo.findByDiscountCodeAndUserId(code,userId);
		pay = pay.stream().filter(p-> p.getStatusPay()==0).collect(Collectors.toList());
		if(pay.size()==0) return false;
		return true;
	}
	
	private boolean isFirst(String userId) {
		// TODO Auto-generated method stub
		List<TPaymentEntity> pay = paymentRepo.findByUserIdGroupbyNoTiket(userId);
		if(pay.size() >=1) return false;
		return true;
	}
    
	public void doResetDisc() {
		List<TPaymentEntity> lspay = paymentRepo.findByPaymentOptionAndStatus(null,PaymentEnum.PENDING.getCode());
		lspay.stream().forEach(diskon());
		paymentRepo.saveAll(lspay);
	}

	private Consumer<? super TPaymentEntity> diskon() {
		// TODO Auto-generated method stub
		return p ->{
			p.setAmount(p.getAmount().add(p.getDiscountValue()));
			p.setDiscountCode(null);
			p.setDiscountValue(new BigDecimal(0));
		};
	}
	
}
