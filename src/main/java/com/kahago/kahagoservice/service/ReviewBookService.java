package com.kahago.kahagoservice.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.configuration.CommonConfig;
import com.kahago.kahagoservice.entity.MAreaDetailEntity;
import com.kahago.kahagoservice.entity.MAreaEntity;
import com.kahago.kahagoservice.entity.MBankDepositEntity;
import com.kahago.kahagoservice.entity.MCouponDiscountEntity;
import com.kahago.kahagoservice.entity.MOptionPaymentEntity;
import com.kahago.kahagoservice.entity.MPickupTimeEntity;
import com.kahago.kahagoservice.entity.MUserPriorityEntity;
import com.kahago.kahagoservice.entity.TBookEntity;
import com.kahago.kahagoservice.entity.TOptionPaymentEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.entity.TPaymentHistoryEntity;
import com.kahago.kahagoservice.entity.TPickupAddressEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestDetailEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestEntity;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.RequestPickupEnum;
import com.kahago.kahagoservice.enummodel.StatusPayEnum;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.request.DetailBooking;
import com.kahago.kahagoservice.model.response.DetailTrxResponse;
import com.kahago.kahagoservice.repository.MAreaDetailRepo;
import com.kahago.kahagoservice.repository.MAreaRepo;
import com.kahago.kahagoservice.repository.MBankDepositRepo;
import com.kahago.kahagoservice.repository.MCouponDiscountRepo;
import com.kahago.kahagoservice.repository.MOptionPaymentRepo;
import com.kahago.kahagoservice.repository.MUserPriorityRepo;
import com.kahago.kahagoservice.repository.TBookRepo;
import com.kahago.kahagoservice.repository.TOptionPaymentRepo;
import com.kahago.kahagoservice.repository.TPaymentHistoryRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestDetailRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestRepo;
import com.kahago.kahagoservice.util.Common;
import com.kahago.kahagoservice.util.DateTimeUtil;
import com.kahago.kahagoservice.util.UniqueRandom;

import static com.kahago.kahagoservice.util.ImageConstant.*;

/**
 * @author Ibnu Wasis
 */
@Service
public class ReviewBookService {
	@Autowired
	private TPaymentRepo tPaymentRepo;
	@Autowired
	private TPaymentHistoryRepo tHistoryRepo;
	@Autowired
	private MCouponDiscountRepo mDiscountRepo;
	@Autowired
	private MOptionPaymentRepo mOptionRepo;
	@Autowired
	private MBankDepositRepo mDepositRepo;
	@Autowired
	private TBookRepo tbookRepo;
	@Autowired
	private TPickupOrderRequestRepo tPickupOrderRequestRepo;
	@Autowired
	private TPickupOrderRequestDetailRepo tPickupOrderRequestDetailRepo;
	@Autowired
	private MUserPriorityRepo userPriorityRepo;
	@Autowired
	private MAreaRepo mAreaRepo;
	
	public DetailTrxResponse getReviewBook(String bookingCode){
		TPaymentEntity payment = tPaymentRepo.findByBookingCodeIgnoreCaseContaining(bookingCode);
		if (payment == null) {
			throw new NotFoundException("Data Tidak Ditemukan");
		}
		return DetailTrxResponse.builder()
				.bookingCode(payment.getBookingCode())
				.comodity(payment.getComodity())
				.origin(payment.getOrigin())
				.destination(payment.getDestination())
				.vendor(payment.getProductSwCode().getSwitcherEntity().getDisplayName())
				.productName(payment.getProductSwCode().getDisplayName())
				.goodsDesc(payment.getGoodsDesc())
				.quantity(payment.getJumlahLembar())
				.totalGrossWeight(payment.getGrossWeight().toString())
				.priceKg(payment.getPriceKg())
				.price(payment.getPrice())
				.isPack(payment.getTotalPackKg()>0?true:false)
				.insuranceValue(payment.getInsurance().toString())
				.totalSurcharge(payment.getShippingSurcharge())
				.totalPrice(payment.getAmount())
				.discountValue(BigDecimal.ZERO)
				.build();
	}
	public DetailTrxResponse getReviewVerifikasi(String bookingCode){
		TPaymentEntity payment = tPaymentRepo.findByBookingCodeIgnoreCaseContaining(bookingCode);
		if(payment == null) {
			throw new NotFoundException("Data tidak Ditemukan");
		}
		return DetailTrxResponse.builder()
			   .bookingCode(payment.getBookingCode())
			   .comodity(payment.getComodity())
			   .origin(payment.getOrigin())
			   .destination(payment.getDestination())
			   .vendor(payment.getProductSwCode().getSwitcherEntity().getDisplayName())
			   .productName(payment.getProductSwCode().getDisplayName())
			   .goodsDesc(payment.getGoodsDesc())
			   .quantity(payment.getJumlahLembar())
			   .totalGrossWeight(payment.getGrossWeight().toString())
			   .priceKg(payment.getPriceKg())
			   .price(payment.getPrice())
			   .isPack(payment.getTotalPackKg()>0?true:false)
			   .insuranceValue(payment.getInsurance().toString())
			   .totalSurcharge(payment.getShippingSurcharge())
			   .totalPrice(payment.getAmount())
			   .discountCode("")
			   .discountValue(BigDecimal.ZERO)
			   .differenceAmount(getDiffAmount(payment.getBookingCode()))
			   .build();
	}
	
	public HashMap<String, BigDecimal> getReviewPaylater(String userId){
		HashMap<String, BigDecimal> maper = new HashMap<>();
		List<TPaymentEntity> lPayment = tPaymentRepo.findPendingPaymentByUserId(userId, PaymentEnum.PENDING.getCode());
		BigDecimal result = BigDecimal.ZERO;
		if(lPayment.size() > 0) {
			for(TPaymentEntity tp : lPayment) {
				result = result.add(tp.getAmount());
			}
			maper.put("total_price", result);
		}else {
			throw new NotFoundException("Data Tidak Ditemukan");
		}
		return maper;
	}
	
	@SuppressWarnings("unused")
	public DetailTrxResponse getDetailTrx(String bookingCode,String userId, String qrcode,Boolean isBooking) {
		TPaymentEntity payment = new TPaymentEntity();
		TPickupOrderRequestEntity tpickup = new TPickupOrderRequestEntity();
		TPickupOrderRequestDetailEntity tpickupDtl = new TPickupOrderRequestDetailEntity();
		String confirmResi = "Fitur ini akan menonaktifkan Fitur Ubah Pesanan dan Batalkan Pesanan. Apakah anda yakin ingin melanjutkan ?";
		TPickupAddressEntity pickupAdd = new TPickupAddressEntity();
		if(isBooking) {
			payment = tPaymentRepo.findByBookingCodeIgnoreCaseContaining(bookingCode);
			pickupAdd = payment.getPickupAddrId();
			if(payment == null) {				
				throw new NotFoundException("Data tidak Ditemukan");
			}
		}else {
			tpickup = tPickupOrderRequestRepo.findByPickupOrderId(bookingCode);
			pickupAdd = tpickup.getPickupAddressEntity();
			if(!qrcode.equals("-")) {
				tpickupDtl = tPickupOrderRequestDetailRepo.findByOrderRequestEntityAndQrCodeOrQrcodeExt(tpickup, qrcode, qrcode);
			}
			if(tpickup==null)throw new NotFoundException("Data tidak Ditemukan");
			return getDetailPickup(tpickup, tpickupDtl,pickupAdd);
		}
		
		
		String statusCode="";
		String qrCode="";
		
//		if(payment.getNoTiket() != null) {
//		if((!payment.getNoTiket().equals("0")&&payment.getPaymentOption()!=null) || !payment.getNoTiket().equals("0")) {
//			statusCode="2";
//		}else statusCode="0";
//		}
		statusCode = "2";
		PaymentEnum payEnum = PaymentEnum.getPaymentEnum(payment.getStatus());
		List<PaymentEnum> lsPayEnum = new ArrayList<>();
		lsPayEnum.add(PaymentEnum.PENDING);
		lsPayEnum.add(PaymentEnum.REQUEST);
		lsPayEnum.add(PaymentEnum.ASSIGN_PICKUP);
		lsPayEnum.add(PaymentEnum.DRAFT_PICKUP);
		if(lsPayEnum.contains(payEnum) && StatusPayEnum.VERIFICATION!=StatusPayEnum.getEnum(payment.getStatusPay())) {
			statusCode = "0";
		}
		if(payment.getQrcode().equals("-")&& payment.getStatus() < PaymentEnum.PICKUP_BY_KURIR.getValue()) {
			qrCode = Common.gerQrCode();
			payment.setQrcode(qrCode);
			payment.setQrcodeDate(LocalDate.now());
			tPaymentRepo.save(payment);
		}else {
			if(payment.getQrcode()!=null) {
				qrCode = payment.getQrcode();
			}
		}
		MUserPriorityEntity userPriority = userPriorityRepo.findByUserCategory(payment.getUserId().getUserCategory().getSeqid());
		Boolean isResiAuto = Optional.ofNullable(userPriority.getIsResiAuto()).orElse(false);
		MAreaDetailEntity area= payment.getIdPostalCode().getKecamatanEntity();
		MAreaEntity origin = mAreaRepo.findByKotaEntityAreaKotaId(pickupAdd.getPostalCode().getKecamatanEntity().getKotaEntity().getAreaKotaId());
		String urlResi = "api/resi/kahago?bookingcode="+payment.getBookingCode()+"&userid="+payment.getUserId().getUserId();
		return DetailTrxResponse.builder()
				.origin(payment.getOrigin())
				.destination(payment.getIdPostalCode().getKecamatanEntity().getKecamatan()+","+payment.getIdPostalCode().getKecamatanEntity().getKotaEntity().getName())
				.destinationCode(String.valueOf(area.getAreaId())+"-"+String.valueOf(area.getAreaDetailId()))
				.senderName(payment.getSenderName())
				.senderAddress(payment.getSenderAddress())
				.senderTelp(payment.getSenderTelp())
				.senderEmail(payment.getSenderEmail()==null?"":payment.getSenderEmail())
				.receiverName(payment.getReceiverName())
				.receiverAddress(payment.getReceiverAddress())
				.receiverTelp(payment.getReceiverTelp())
				.receiverPostalCode(payment.getIdPostalCode().getPostalCode())
				.receiverEmail(payment.getReceiverEmail()==null?"":payment.getReceiverEmail())
				.quantity(payment.getJumlahLembar())
				.comodity(payment.getComodity())
				.goodsId(payment.getGoodsId().toString())
				.receiverIdPostalCode(payment.getIdPostalCode().getIdPostalCode().toString())
				.serviceType(payment.getServiceType())
				.goodsDesc(payment.getGoodsDesc())
				.totalGrossWeight(payment.getGrossWeight().toString())
				.priceKg(payment.getPriceKg())
				.priceGoods(payment.getPriceGoods())
				.note(payment.getNote())
				.totalInsurance(payment.getInsurance())
				.officerId(payment.getUserId().getUserId())
				.totalExtraCharge(payment.getExtraCharge())
				.kelurahan(payment.getIdPostalCode().getKelurahan())
				.kecamatan(payment.getIdPostalCode().getKecamatanEntity().getKecamatan())
				.kota(payment.getIdPostalCode().getKecamatanEntity().getKotaEntity().getName())
				.provinsi(payment.getIdPostalCode().getKecamatanEntity().getKotaEntity().getProvinsiEntity().getName())
				.totalPrice(payment.getAmount())
				.productCode(payment.getProductSwCode().getDisplayName())
				.vendor(payment.getProductSwCode().getSwitcherEntity().getDisplayName())
				.bookingCode(payment.getBookingCode())
				.totalPackKg(payment.getTotalPackKg())
				.stt(payment.getStt())
				.status(payment.getStatus())
				.totalVolume(payment.getVolume())
				.totalSurcharge(payment.getShippingSurcharge())
				.minWeight(payment.getProductSwCode().getMinWeight())
				.pembagiVolume(payment.getProductSwCode().getPembagiVolume().toString())
				.kgSurcharge(payment.getProductSwCode().getKgSurcharge().toString())
				.maxJumlahKoli(payment.getProductSwCode().getMaxJumlahKoli().toString())
				.maxKgKoli(payment.getProductSwCode().getMaxKgKoli().toString())
				.qrcode(qrCode)
				.jenisModa(payment.getProductSwCode().getJenisModa().getIdModa().toString())
				.namaModa(payment.getProductSwCode().getJenisModa().getNamaModa())
				.addressPickup(payment.getPickupAddrId().getAddress())
				.pickupAddressId(payment.getPickupAddrId().getPickupAddrId().toString())
				.pickupDate(payment.getPickupDate().toString())
				.pickupTime(payment.getPickupTime())
				.pickupTimeId(payment.getPickupTimeId().getIdPickupTime().toString())
				.discountCode(payment.getDiscountCode()==null?"":payment.getDiscountCode())
				.discountValue(payment.getDiscountValue()==null?BigDecimal.ZERO:payment.getDiscountValue())
				.couponName(getCouponName(payment.getDiscountCode()))
				.paymentOption(payment.getPaymentOption()==null?"":getDescOptionPayment(payment.getPaymentOption()))
				.statusCode(statusCode)
				.productSwCode(payment.getProductSwCode().getProductSwCode().toString())
				.kodeUnik(payment.getInsufficientFund().toString())
				.imageUrl(PREFIX_PATH_IMAGE_VENDOR + payment.getProductSwCode().getSwitcherEntity().getImg().substring(payment.getProductSwCode().getSwitcherEntity().getImg().lastIndexOf("/") + 1))
				.trxDate(DateTimeUtil.getString2Date(payment.getTrxDate().toString(),"yyyy-MM-dd", "dd MMM yyyy")+" "+DateTimeUtil.getString2Date(payment.getTrxTime(),"hhmm", "HH:mm"))
				.urlResi(urlResi)
				.detailBooking(getDetailBook(payment.getBookingCode()))
				.price(payment.getPrice())
				.productName(payment.getProductSwCode().getDisplayName())
				.senderSave("")
				.receiverSave("")
				.productType("")
				.ppn("")
				.isResiAuto(isResiAuto)
				.insuranceValue(payment.getInsurance()==null?"":payment.getInsurance().toString())
				.statusDesc(PaymentEnum.getPaymentEnum(payment.getStatus()).getString())
				.isPack(payment.getTotalPackKg()>0?true:false)
				.differenceAmount(BigDecimal.ZERO)
				.noTiket(payment.getNoTiket()==null?"":payment.getNoTiket())
				.isBooking(isBooking)
				.originId(origin==null?"":origin.getAreaId())
				.confirmResi(confirmResi)
				.build();
	}
	
	private BigDecimal getDiffAmount(String bookingCode) {
		List<TPaymentHistoryEntity> lpHistory = tHistoryRepo.findHistoryByBookingCodeLimit(bookingCode);
		TPaymentHistoryEntity pHistory = null;
		if(lpHistory.size() > 0) {
			pHistory = lpHistory.get(0);
		}
		return pHistory.getLastAmount().subtract(pHistory.getAmount());
	}
	
	private String getCouponName(String discountCode) {
		MCouponDiscountEntity coupon = mDiscountRepo.findByCode(discountCode);
		return coupon!=null?coupon.getCouponName():"";
	}
	
	private String getDescOptionPayment(String optionPayment) {
		String result="";
		MOptionPaymentEntity moption = mOptionRepo.findByCode(optionPayment);
		MBankDepositEntity bankdepo = new MBankDepositEntity();
		if(moption!=null) {
			result = moption.getDescription();
		}else {
			bankdepo = mDepositRepo.getOne(Integer.valueOf(optionPayment));
			result = bankdepo.getBankId().getBankCode();
		}
		return result;
	}
	
	private List<DetailBooking> getDetailBook(String bookingCode){
		List<DetailBooking> ldtl = new ArrayList<DetailBooking>();
		List<TBookEntity> lbook = tbookRepo.findByBookingCode(bookingCode);
		int seq=0;
		for(TBookEntity bk : lbook) {
			seq=seq+1;
			DetailBooking dtl = new DetailBooking();
			dtl.setSeq(String.valueOf(seq));
			dtl.setGrossWeight((double)Math.round(Double.valueOf(bk.getGrossWeight())));
			dtl.setHeight((double)Math.round((Double.valueOf(bk.getHeight()))));
			dtl.setLength((double)Math.round(Double.valueOf(bk.getLength())));
			dtl.setVolume((double)Math.round(Double.valueOf(bk.getVolWeight())));
			dtl.setWidth((double)Math.round(Double.valueOf(bk.getWidth())));
			ldtl.add(dtl);
		}
		return ldtl;
	}
	
	private DetailTrxResponse getDetailPickup(TPickupOrderRequestEntity pickup,TPickupOrderRequestDetailEntity pickupDtl,TPickupAddressEntity pickupAdd) {
		MUserPriorityEntity userPriority = userPriorityRepo.findByUserCategory(pickup.getUserEntity().getUserCategory().getSeqid());
		Boolean isResiAuto = Optional.ofNullable(userPriority.getIsResiAuto()).orElse(false);
		DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss");
		DateTimeFormatter formaDate = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
		String confirmResi = "Fitur ini akan menonaktifkan Fitur Ubah Pesanan dan Batalkan Pesanan. Apakah anda yakin ingin melanjutkan ?";
		String images = "-";
		if(pickupDtl.getProductSwitcherEntity()!=null) {
			images = PREFIX_PATH_IMAGE_VENDOR + pickupDtl.getProductSwitcherEntity().getSwitcherEntity().getImg().substring(pickupDtl.getProductSwitcherEntity().getSwitcherEntity().getImg().lastIndexOf("/") + 1);
		}
		MAreaEntity origin = mAreaRepo.findByKotaEntityAreaKotaId(pickupAdd.getPostalCode().getKecamatanEntity().getKotaEntity().getAreaKotaId());
		return DetailTrxResponse.builder()
				.origin(pickup.getPickupAddressEntity().getPostalCode().getKecamatanEntity().getKotaEntity().getName())
				.destination(pickupDtl.getAreaId()==null?"-":pickupDtl.getAreaId().getKecamatan()+","+pickupDtl.getAreaId().getKotaEntity().getName())
				.destinationCode(pickupDtl.getAreaId()==null?"-":String.valueOf(pickupDtl.getAreaId().getAreaId())+"-"+String.valueOf(pickupDtl.getAreaId().getAreaDetailId()))
				.senderName(pickup.getUserEntity().getName())
				.senderAddress(pickup.getPickupAddressEntity().getAddress())
				.senderTelp(pickup.getUserEntity().getHp())
				.senderEmail(pickup.getUserEntity().getEmail())
				.receiverName(pickupDtl.getNamaPenerima()==null?"-":pickupDtl.getNamaPenerima())
				.receiverAddress("-")
				.receiverTelp("-")
				.receiverPostalCode("-")
				.receiverEmail("-")
				.quantity(pickupDtl==null?pickup.getQty():pickupDtl.getQty())
				.comodity("-")
				.goodsId("-")
				.receiverIdPostalCode("-")
				.serviceType("-")
				.goodsDesc("-")
				.totalGrossWeight(pickupDtl.getWeight()==null?"-":pickupDtl.getWeight().toString())
				.priceKg(BigDecimal.ZERO)
				.priceGoods(BigDecimal.ZERO)
				.note("-")
				.totalInsurance(BigDecimal.ZERO)
				.officerId("-")
				.totalExtraCharge(BigDecimal.ZERO)
				.kelurahan("-")
				.kecamatan(pickupDtl.getAreaId()==null?"-":pickupDtl.getAreaId().getKecamatan())
				.kota(pickupDtl.getAreaId()==null?"-":pickupDtl.getAreaId().getKotaEntity().getName())
				.provinsi(pickupDtl.getAreaId()==null?"-":pickupDtl.getAreaId().getKotaEntity().getProvinsiEntity().getName())
				.totalPrice(pickupDtl.getAmount()==null?BigDecimal.ZERO:pickupDtl.getAmount())
				.productCode(pickupDtl.getProductSwitcherEntity()==null?"-":pickupDtl.getProductSwitcherEntity().getDisplayName())
				.vendor(pickupDtl.getProductSwitcherEntity()==null?"-":pickupDtl.getProductSwitcherEntity().getSwitcherEntity().getDisplayName())
				.bookingCode(pickup.getPickupOrderId())
				.totalPackKg(Double.valueOf("0"))
				.stt("-")
				.status(pickupDtl.getStatus()==null?pickup.getStatus():pickupDtl.getStatus())
				.isResiAuto(isResiAuto)
				.totalVolume(Long.valueOf("0"))
				.totalSurcharge(BigDecimal.ZERO)
				.minWeight(pickupDtl.getProductSwitcherEntity()==null?0:pickupDtl.getProductSwitcherEntity().getMinWeight())
				.pembagiVolume(pickupDtl.getProductSwitcherEntity()==null?"-":pickupDtl.getProductSwitcherEntity().getPembagiVolume().toString())
				.kgSurcharge(pickupDtl.getProductSwitcherEntity()==null?"-":pickupDtl.getProductSwitcherEntity().getKgSurcharge().toString())
				.maxJumlahKoli(pickupDtl.getProductSwitcherEntity()==null?"-":pickupDtl.getProductSwitcherEntity().getMaxJumlahKoli().toString())
				.maxKgKoli(pickupDtl.getProductSwitcherEntity()==null?"-":pickupDtl.getProductSwitcherEntity().getMaxKgKoli().toString())
				.qrcode(pickupDtl.getQrCode()==null?"-":pickupDtl.getQrCode())
				.jenisModa("-")
				.namaModa("-")
				.addressPickup(pickup.getPickupAddressEntity().getAddress())
				.pickupAddressId(pickup.getPickupAddressEntity().getPickupAddrId().toString())
				.pickupDate(pickup.getOrderDate().toString())
				.pickupTime(pickup.getPickupTimeEntity().getTimeFrom().format(format)+" - "+pickup.getPickupTimeEntity().getTimeTo().format(format))
				.pickupTimeId(pickup.getPickupTimeEntity().getIdPickupTime().toString())
				.discountCode("-")
				.discountValue(BigDecimal.ZERO)
				.couponName("-")
				.paymentOption("-")
				.statusCode(pickupDtl.getIsPay().toString())
				.productSwCode(pickupDtl.getProductSwitcherEntity()==null?"-":pickupDtl.getProductSwitcherEntity().getProductSwCode().toString())
				.kodeUnik("-")
				.imageUrl(images)
				.trxDate(pickup.getCreateDate().format(formaDate))
				.urlResi("-")
				.detailBooking(getDetailBook("-"))
				.price(BigDecimal.ZERO)
				.productName(pickupDtl.getProductSwitcherEntity()==null?"-":pickupDtl.getProductSwitcherEntity().getDisplayName())
				.senderSave("")
				.receiverSave("")
				.productType("")
				.ppn("")
				.insuranceValue("-")
				.statusDesc(RequestPickupEnum.getPaymentEnum(pickup.getStatus()).toString())
				.isPack(false)
				.differenceAmount(BigDecimal.ZERO)
				.noTiket("-")
				.isBooking(false)
				.originId(origin==null?"":origin.getAreaId())
				.confirmResi(confirmResi)
				.build();
	}
}
