 package com.kahago.kahagoservice.service;

import static com.kahago.kahagoservice.util.ImageConstant.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.kahago.kahagoservice.entity.*;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.PickupDetailEnum;
import com.kahago.kahagoservice.enummodel.PickupEnum;
import com.kahago.kahagoservice.enummodel.RequestPickupEnum;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.exception.NotFoundException;

import com.kahago.kahagoservice.model.projection.StatusPickupCourier;
import com.kahago.kahagoservice.model.request.ManifestListRequest;
import com.kahago.kahagoservice.model.response.*;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kahago.kahagoservice.component.FirebaseComponent;
import com.kahago.kahagoservice.model.dto.PaymentDto;
import com.kahago.kahagoservice.model.request.ManifestPickupRequest;
import com.kahago.kahagoservice.model.request.PickupAddressRequest;
import com.kahago.kahagoservice.repository.MPostalCodeRepo;
import com.kahago.kahagoservice.repository.MUserRepo;
import com.kahago.kahagoservice.repository.TBookRepo;
import com.kahago.kahagoservice.repository.THistoryBookRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.repository.TPickupAddressRepo;
import com.kahago.kahagoservice.repository.TPickupDetailRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestDetailRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestRepo;
import com.kahago.kahagoservice.repository.TPickupRepo;
import com.kahago.kahagoservice.util.Common;
import com.kahago.kahagoservice.util.DateTimeUtil;

import lombok.SneakyThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Ibnu Wasis
 */
 @Service
public class ManifestPickupService {
	@Autowired 
	private TPickupDetailRepo tPickupDetailRepo;
	@Autowired
	private TPickupRepo tPickupRepo;
	@Autowired
	private TBookRepo tBookRepo;
	@Autowired
	private TPaymentRepo tPaymentRepo;
	@Autowired
	private TPickupAddressRepo tpickupAddress;
	@Autowired
	private MPostalCodeRepo mpostalCodeRepo;
	@Autowired
	private THistoryBookRepo thistoryBookRepo;
	@Autowired
	private MUserRepo mUserRepo;
	@Autowired
	private FirebaseComponent firebase;
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private RequestPickUpService requestPickUpService;
	@Autowired
	private HistoryTransactionService historyTransactionService;
	@Autowired
	private AssignPickupService assignPickupService;
	@Autowired
	private TPickupOrderRequestDetailRepo tPickupOrderRequestDetailRepo;
	@Autowired
	private TPickupOrderRequestRepo tPickupOrderRequestRepo;
	@Autowired
	private BookCounterService bookCounterService;
	@Autowired
    private DepositBookService depositBookService;
	@Value("${file.upload-dir}")
	private String uploadingDir;
	@Value("${endpoint.report.manifest}")
	private String endpointReport;
//	@Autowired
//	private AssignPickupService assignPickupService;

	
	public static final Logger logger = LoggerFactory.getLogger(ManifestPickupService.class);
	private static final Integer FLAG = 1;
	private ManifestPickupRequest mrequest;
		
	public List<ManifestPickup> findByCourierId(ManifestPickupRequest req){
		logger.info("===>List Manifest Pickup<===="+req.toString());
		mrequest = req;
		List<TPickupDetailEntity> listPickupDetail = new ArrayList<>();
		List<ManifestPickup> lmanifest = new ArrayList<>();
		if(req.getNoManifest() != null && !req.getNoManifest().isEmpty()) {
			listPickupDetail = tPickupDetailRepo.findByCourierIdAndStatusAndCode(req.getUserId(), req.getStatusCode(), req.getNoManifest());
		}else {
			listPickupDetail = tPickupDetailRepo.findByCourierIdAndStatus(req.getUserId(), req.getStatusCode());
			}
		List<String> lcode = new ArrayList<>();
		for(TPickupDetailEntity dt: listPickupDetail) {
			ManifestPickup mnifest = new ManifestPickup();
			if(lcode.isEmpty()) {
				lcode.add(dt.getPickupId().getCode());
				mnifest = toManifestPickup(dt);
			}else if(!lcode.contains(dt.getPickupId().getCode())) {
				lcode.add(dt.getPickupId().getCode());
				mnifest = toManifestPickup(dt);
			}
			lmanifest.add(mnifest);
		}
		if(req.getNoManifest() != null && !req.getNoManifest().isEmpty()) {
			statusPickupCourierByManifest(req.getNoManifest(), lmanifest);
		}
		return lmanifest;
	}


	/**
	 * Improve by Endro
	**/
	private void statusPickupCourierByManifest(String manifest, List<ManifestPickup> lmanifest) {
		if (lmanifest.size() != 0) {
			List<StatusPickupCourier> statusPickupCouriers = tPickupDetailRepo.courierStatusPickupPerPickAddr(manifest);
			for (DetailManifestPickup dmp : lmanifest.get(0).getDetail()) {
				for (StatusPickupCourier spc : statusPickupCouriers) {
					if (dmp.getPickupAddressId().equals(spc.getPickupAddrId())) {
						if (spc.getStatusPickup().equals("Finish")) {
							dmp.setIsFinish(true);
						} else {
							dmp.setIsFinish(false);
						}
					}
				}
			}
		}
	}
	
	private ManifestPickup toManifestPickup(TPickupDetailEntity entity) {
		
		//List<TPickupDetailEntity> detail = tPickupDetailRepo.findAllByPickupId(entity.getPickupId());//tPickupDetailRepo.findByCode(entity.getPickupId().getCode());
		List<DetailManifestPickup> ldetail = new ArrayList<>();
		MUserEntity courier = entity.getPickupId().getCourierId();
		String images = "";
		String productVendor="";
		if(entity.getBookId()!=null) {
			images = PREFIX_PATH_IMAGE_VENDOR + entity.getBookId().getProductSwCode().getSwitcherEntity().getImg().substring(entity.getBookId().getProductSwCode().getSwitcherEntity().getImg().lastIndexOf("/") + 1);
			productVendor=entity.getBookId().getProductSwCode().getDisplayName();
		}
		int seq=0;
		Integer totalBooking = tPickupDetailRepo.countByPickupId(entity.getPickupId());
		Integer totalBarang = (tPickupDetailRepo.countQuantityBookId(null, entity.getPickupId().getIdPickup())==null?0:
								tPickupDetailRepo.countQuantityBookId(null, entity.getPickupId().getIdPickup()));
		
				totalBarang +=(tPickupDetailRepo.countQuantityPickupReq(null, entity.getPickupId().getIdPickup())==null?0:
								tPickupDetailRepo.countQuantityPickupReq(null, entity.getPickupId().getIdPickup()));
				
		Integer tBarangBelum = (tPickupDetailRepo.countQuantityBookId(PickupDetailEnum.ASSIGN_PICKUP.getValue(), entity.getPickupId().getIdPickup())==null?0:
								tPickupDetailRepo.countQuantityBookId(PickupDetailEnum.ASSIGN_PICKUP.getValue(), entity.getPickupId().getIdPickup())); 
		
				tBarangBelum +=	(tPickupDetailRepo.countQuantityPickupReq(PickupDetailEnum.ASSIGN_PICKUP.getValue(), entity.getPickupId().getIdPickup())==null?0:
								tPickupDetailRepo.countQuantityPickupReq(PickupDetailEnum.ASSIGN_PICKUP.getValue(), entity.getPickupId().getIdPickup()));
		if(mrequest.getNoManifest() != null && !mrequest.getNoManifest().isEmpty()) {
			List<TPickupDetailEntity> detail = tPickupDetailRepo.findAllByPickupCode(entity.getPickupId().getCode());
			for(TPickupDetailEntity td:detail) {
					seq=seq+1;
					/*if(td.getBookId()!=null) {
						totalBarang = totalBarang + td.getBookId().getJumlahLembar();
						if(td.getStatus().equals(PickupDetailEnum.ASSIGN_PICKUP.getValue())) {
							tBarangBelum = tBarangBelum + td.getBookId().getJumlahLembar();
						}
					}else {
						totalBarang = totalBarang + (td.getPickupOrderRequestEntity().getQty()!=null?td.getPickupOrderRequestEntity().getQty():0);
						if(td.getStatus().equals(PickupDetailEnum.ASSIGN_PICKUP.getValue())) {
							tBarangBelum = tBarangBelum + (td.getPickupOrderRequestEntity().getQty()!=null?td.getPickupOrderRequestEntity().getQty():0);
						}
					}*/					
					Boolean same = false;
					DetailManifestPickup dmp = detailManifest(td, seq);
					for(DetailManifestPickup dm:ldetail) {
						if(dm.getPickupAddressId().equals(dmp.getPickupAddressId())
								&& dm.getCustomerId().equals(dmp.getCustomerId())) {
							same = true;
						}
					}
//					if(!same && !dmp.getStatus().equals(PaymentEnum.CANCEL_BY_USER.getCode()))ldetail.add(detailManifest(td,seq));
					if(!same && !dmp.getStatus().equals(PaymentEnum.CANCEL_BY_USER.getCode()))ldetail.add(dmp);
				}
					
			}
				
		
		
		return ManifestPickup.builder()				
				.noManifest(entity.getPickupId().getCode())
				.productVendor(productVendor)
				.statusCode(entity.getPickupId().getStatus())
				.statusDesc(PickupEnum.getEnumByNumber(entity.getPickupId().getStatus()).toString())
				.imageVendor(images)
				.timePickup(DateTimeUtil.TimePickup(entity.getPickupId().getPickupDate().toString(), entity.getPickupId().getTimePickupFrom().toString(),
						entity.getPickupId().getTimePickupTo().toString()))
				.detail(ldetail.size()>0?ldetail:null)
				.courierName(courier.getName())
				.totalBooking(totalBooking==null?"0":totalBooking.toString())
				.jumlahBarang(totalBarang==null?"0":totalBarang.toString())
				.belumTerima(tBarangBelum==null?"0":tBarangBelum.toString())
				.build();
	}
	private DetailManifestPickup detailManifest(TPickupDetailEntity entity,Integer seq) {
		String description ="";
		String lastUpdateManifest="";
		String lastUpdatePickup="";
		Integer status = 0;
		TPaymentEntity pay = entity.getBookId();
		if(entity.getPickupId().getModifyAt()!=null) {
			lastUpdateManifest= DateTimeUtil.getTimetoString(entity.getPickupId().getModifyAt(), "dd MMM yyyy hh:mm");
		}
		if(!entity.getStatus().equals(0)) {
			status = 1;
		}
		if(entity.getModifyDate()!=null) {
			lastUpdatePickup= DateTimeUtil.getTimetoString(entity.getModifyDate(), "dd MMM yyyy hh:mm");
		}
		String images="";
		if (pay == null) {
			TPickupOrderRequestEntity pickupRequest = entity.getPickupOrderRequestEntity();
			if(pickupRequest.getPickupAddressEntity().getDescription()!=null) {
				description=entity.getPickupOrderRequestEntity().getPickupAddressEntity().getDescription();
			}
			
			List<TBookEntity> lsbook = new ArrayList<>();
			return DetailManifestPickup.builder()
					.seq(seq)
					.address(pickupRequest.getPickupAddressEntity().getAddress())
					.addressNote(description)
					.customerId(pickupRequest.getUserEntity().getUserId())
					.customerName("-")
					.kecamatan(pickupRequest.getPickupAddressEntity().getPostalCode().getKecamatanEntity().getKecamatan())
					.kelurahan(pickupRequest.getPickupAddressEntity().getPostalCode().getKelurahan())
					.customerTelp(pickupRequest.getUserEntity().getHp())
					.pickupAddressId(pickupRequest.getPickupAddressEntity().getPickupAddrId())
					.qty(pickupRequest.getQty())
					.volume(Long.valueOf("0"))
					.weight(Long.valueOf("0"))
					.vendor("")
					.bookingCode(pickupRequest.getPickupOrderId())
					.pickupAddressId(pickupRequest.getPickupAddressEntity().getPickupAddrId())
					.dimensi(getDimensi(lsbook))
					.imageVendor(images)
					.productVendor("")
					.status(pickupRequest.getStatus())
					.statusDesc(RequestPickupEnum.getPaymentEnum(pickupRequest.getStatus()).toString())
					.lastUpdateManifestAt(lastUpdateManifest)
					.lastUpdatePickupAt(lastUpdatePickup)
					.kota(pickupRequest.getPickupAddressEntity().getPostalCode().getKecamatanEntity().getKotaEntity().getName())
					.postalCode(pickupRequest.getPickupAddressEntity().getPostalCode().getPostalCode())
					.isInsurance("0")
					.pickupStatus(status)
					.flag(pickupRequest.getPickupAddressEntity().getFlag().toString())
					.isBooking(false)
					.receiverName("-")
					.receiverAddress("-")
					.receiverTelp("-")
					.build();
					
		}else {
			if(pay.getPickupAddrId().getDescription() != null) {
				description = pay.getPickupAddrId().getDescription();
			}
			 images = PREFIX_PATH_IMAGE_VENDOR + pay.getProductSwCode().getSwitcherEntity().getImg().substring(pay.getProductSwCode().getSwitcherEntity().getImg().lastIndexOf("/") + 1);
			 
		}
		return DetailManifestPickup.builder()
				.seq(seq)
				.address(pay.getPickupAddrId().getAddress())
				.addressNote(description)
				.customerId(pay.getUserId().getUserId())
				.customerName(pay.getSenderName())
				.kecamatan(pay.getPickupAddrId().getPostalCode().getKecamatanEntity().getKecamatan())
				.kelurahan(pay.getPickupAddrId().getPostalCode().getKelurahan())
				.customerTelp(pay.getUserId().getHp())
				.pickupAddressId(pay.getPickupAddrId().getPickupAddrId())
				.qty(pay.getJumlahLembar())
				.volume(pay.getVolume())
				.weight(pay.getGrossWeight())
				.vendor(pay.getProductSwCode().getSwitcherEntity().getDisplayName())
				.bookingCode(pay.getBookingCode())
				.pickupAddressId(entity.getPickupAddrId().getPickupAddrId())
				.dimensi(getDimensi(pay.getTbooks()))
				.imageVendor(images)
				.productVendor(pay.getProductSwCode().getDisplayName())
				.status(pay.getStatus())
				.statusDesc(PaymentEnum.getPaymentEnum(pay.getStatus()).toString())
				.lastUpdateManifestAt(lastUpdateManifest)
				.lastUpdatePickupAt(lastUpdatePickup)
				.kota(pay.getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getName())
				.postalCode(pay.getPickupAddrId().getPostalCode().getPostalCode())
				.isInsurance(pay.getInsurance()== BigDecimal.ZERO?"0":"1")
				.pickupStatus(status)
				.flag(pay.getPickupAddrId().getFlag().toString())
				.isBooking(true)
				.receiverName(entity.getBookId().getReceiverName())
				.receiverAddress(entity.getBookId().getReceiverAddress())
				.receiverTelp(entity.getBookId().getReceiverTelp())
				.build(); 
		
	}
	public List<ManifestPickupResponse> getDetailManifest(ManifestPickupRequest req) {
		logger.info("===>Detail Manifest<===="+req.toString());
		List<TPickupDetailEntity> DetailPickup= new ArrayList<TPickupDetailEntity>();
		List<ManifestPickupResponse> mrp = new ArrayList<>();
		if(req.getPickupAddressId() != null ) {
			DetailPickup = tPickupDetailRepo.findByNoManifestAndCustomerIdAndIdPickupAddres(req.getNoManifest(), req.getCustomerId(), req.getPickupAddressId());
//			DetailPickup = tPickupDetailRepo.findByNoManifestAndCustomerIdAndIdPickupAddresAndPickupDate(req.getNoManifest(), 
//					req.getCustomerId(), req.getPickupAddressId(),LocalDate.now().minusDays(1));
			DetailPickup.addAll(tPickupDetailRepo.findByNoManifestAndCustomerIdAndIdPickupAddresPickupRequest(req.getNoManifest(), req.getCustomerId(), req.getPickupAddressId()));
		}else {
			DetailPickup = tPickupDetailRepo.findByNoManifestAndCustomerId(req.getNoManifest(), req.getCustomerId());
			DetailPickup.addAll(tPickupDetailRepo.findByNoManifestAndCustomerIdPickupRequest(req.getNoManifest(), req.getCustomerId()));
		}
		List<TPickupDetailEntity> lbelum = DetailPickup.stream().filter(detail -> PickupDetailEnum.ASSIGN_PICKUP.getValue().equals(detail.getPickupId().getStatus())).collect(Collectors.toList());
		
		for(TPickupDetailEntity td : DetailPickup) {
			String desc = "";
			String lastUpdateManifest="";
			String lastUpdatePickup="";
			String timefrom = td.getPickupId().getTimePickupFrom().toString();
			String timeto = td.getPickupId().getTimePickupTo().toString();
			TPaymentEntity pay = td.getBookId();
			if(pay!=null)
			if (pay.getPickupAddrId().getDescription()!= null ) {
				desc = pay.getPickupAddrId().getDescription();
			}
			if(td.getPickupId().getModifyAt()!=null) {
				lastUpdateManifest= DateTimeUtil.getTimetoString(td.getPickupId().getModifyAt(), "dd MMM yyyy hh:mm");
			}
			if(td.getModifyDate()!=null) {
				lastUpdatePickup= DateTimeUtil.getTimetoString(td.getModifyDate(), "dd MMM yyyy hh:mm");
			}
			String uri = endpointReport;
			uri = uri.replaceAll("#code", td.getPickupId().getCode());
			List<DetailManifestPickup> ldetail = new ArrayList<>();
			int seq = 0;
			for(TPickupDetailEntity dt : DetailPickup) {
				seq = seq+1;
				DetailManifestPickup dtl = detailManifest(dt, seq);
				if(!dtl.getStatus().equals(PaymentEnum.CANCEL_BY_USER.getCode())) {
					ldetail.add(dtl);
				}				
			}
				
			ManifestPickupResponse mr = ManifestPickupResponse.builder()
					.longitude(td.getPickupAddrId().getLongitude())
					.latitude(td.getPickupAddrId().getLatitude())
					.customerName(pay!=null?pay.getUserId().getName():td.getPickupOrderRequestEntity().getUserEntity().getName())
					.customerTelp(pay!=null?pay.getUserId().getHp():td.getPickupOrderRequestEntity().getUserEntity().getHp())
					.statusCode(td.getPickupId().getStatus())
					.noManifest(td.getPickupId().getCode())
					.statusDesc(PickupEnum.getEnumByNumber(td.getPickupId().getStatus()).toString())
					.address(td.getPickupAddrId().getAddress())
					.kelurahan(td.getPickupAddrId().getPostalCode().getKelurahan())
					.kecamatan(td.getPickupAddrId().getPostalCode().getKecamatanEntity().getKecamatan())
					.kota(td.getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getName())
					.provinsi(td.getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getProvinsiEntity().getName())
					.postalCode(td.getPickupAddrId().getPostalCode().getPostalCode())
					.addressNote(desc)
					.pickupDate(DateTimeUtil.getString2Date(td.getPickupId().getPickupDate().toString(), "yyyy-MM-dd", "dd-MM-yyyy"))
					.pickupTime(timefrom+" - "+timeto)
					.detail(ldetail)
					.idPostalCode(td.getPickupAddrId().getPostalCode().getIdPostalCode())
					.pickupAddressId(td.getPickupAddrId().getPickupAddrId())
					.countAssign(String.valueOf(lbelum.size()))
					.linkManifest(uri)
					.lastUpdateManifestAt(lastUpdateManifest)
					.lastUpdatePickupAt(lastUpdatePickup)
					.flag(td.getPickupAddrId().getFlag().toString())
					.build();
				
			Boolean same = false;
			for(ManifestPickupResponse mpr :mrp) {
				if(mpr.getCustomerName().equals(mr.getCustomerName()) &&
						mpr.getPickupAddressId().equals(mr.getPickupAddressId())) {
					same=true;
				}
			}
			if(!same) {
				mrp.add(mr);
			}
		}
		return mrp;
	}
	
	public ManifestPickupResponse getDetailManifestByQrCode(String param,String userId) {
		logger.info("===>get Detail Pickup By Param<===="+param);
		TPickupDetailEntity td=tPickupDetailRepo.findByBookByParam(param,userId);
		if(td==null){
			td = tPickupDetailRepo.findByBookByParamRequest(param,userId);
		}
		TPaymentEntity payment = new TPaymentEntity();
		List <TPickupDetailEntity> pickup = new ArrayList<>();
		if(td == null) {
			td = tPickupDetailRepo.findByQrCode(param, param,param);
			if(td==null) {
				payment = tPaymentRepo.findByQrcodeOrBookingCode(param, PaymentEnum.REQUEST.getValue());
				if(payment == null) {
					throw new NotFoundException("Pesanan Tidak Ditemukan");
				}
				pickup = tPickupDetailRepo.findByCourierIdAndUserId(userId, payment.getUserId().getUserId(), LocalDate.now());
			}else {
				throw new NotFoundException("Pesanan tidak Di-Assign ke Anda");
			}
		}
		
		if(pickup.size() > 0 && td == null) {
			TPickupDetailEntity pd =new TPickupDetailEntity();
			pd.setBookId(payment);
			pd.setPickupAddrId(payment.getPickupAddrId());
			pd.setPickupId(pickup.get(0).getPickupId());
			pd.setStatus(PickupDetailEnum.IN_COURIER.getValue());
			pd.setCreateBy(userId);
			pd.setModifyBy(userId);
			pd.setModifyDate(LocalDateTime.now());
			pd.setCreateDate(LocalDateTime.now());
			tPickupDetailRepo.save(pd);
			payment.setPickupDate(pickup.get(0).getBookId().getPickupDate());
			payment.setPickupTimeId(pickup.get(0).getBookId().getPickupTimeId());
			payment.setPickupTime(pickup.get(0).getBookId().getPickupTime());
			tPaymentRepo.save(payment);
			td = pd;
		}else if(td == null) throw new NotFoundException("Pesanan tidak Bisa Anda Pickup!");
		
		String desc = "";
		String lastUpdateManifest="";
		String lastUpdatePickup="";
		String timefrom = td.getPickupId().getTimePickupFrom().toString();
		String timeto = td.getPickupId().getTimePickupTo().toString();
		if (td.getBookId()!= null) {
			desc = (td.getBookId().getPickupAddrId().getDescription()==null?"":td.getBookId().getPickupAddrId().getDescription());
		}else {
			desc = (td.getPickupOrderRequestEntity().getPickupAddressEntity().getDescription()==null?"":
						td.getPickupOrderRequestEntity().getPickupAddressEntity().getDescription());
		}
		if(td.getPickupId().getModifyAt()!=null) {
			lastUpdateManifest= DateTimeUtil.getTimetoString(td.getPickupId().getModifyAt(), "dd MMM yyyy hh:mm");
		}
		if(td.getModifyDate()!=null) {
			lastUpdatePickup= DateTimeUtil.getTimetoString(td.getModifyDate(), "dd MMM yyyy hh:mm");
		}
		List<DetailManifestPickup> detail = new ArrayList<>();
		detail.add(detailManifest(td,1));
		String uri = endpointReport;
		uri = uri.replaceAll("#code", td.getPickupId().getCode());
		if(td.getBookId()==null) {
			return getDetailPickupRequest(td, timefrom, timeto, detail, 0);
		}
		return ManifestPickupResponse.builder()
				.longitude(td.getBookId().getPickupAddrId().getLongitude())
				.latitude(td.getBookId().getPickupAddrId().getLatitude())
				.customerName(td.getBookId().getUserId().getName())
				.customerTelp(td.getBookId().getUserId().getHp())
				.statusCode(td.getPickupId().getStatus())
				.noManifest(td.getPickupId().getCode())
				.statusDesc(PickupEnum.getEnumByNumber(td.getPickupId().getStatus()).toString())
				.address(td.getBookId().getPickupAddrId().getAddress())
				.kelurahan(td.getBookId().getPickupAddrId().getPostalCode().getKelurahan())
				.kecamatan(td.getBookId().getPickupAddrId().getPostalCode().getKecamatanEntity().getKecamatan())
				.kota(td.getBookId().getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getName())
				.provinsi(td.getBookId().getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getProvinsiEntity().getName())
				.postalCode(td.getBookId().getPickupAddrId().getPostalCode().getPostalCode())
				.addressNote(desc)
				.pickupDate(DateTimeUtil.getString2Date(td.getBookId().getPickupDate().toString(), "yyyy-MM-dd", "dd-MM-yyyy"))
				.pickupTime(td.getBookId().getPickupTime())
				.detail(detail)
				.idPostalCode(td.getBookId().getPickupAddrId().getPostalCode().getIdPostalCode())
				.pickupAddressId(td.getBookId().getPickupAddrId().getPickupAddrId())
				.lastUpdateManifestAt(lastUpdateManifest)
				.lastUpdatePickupAt(lastUpdatePickup)
				.flag(td.getBookId().getPickupAddrId().getFlag().toString())
				.linkManifest(uri)
				.build();
	}
	@Transactional
	public Response<String> updateQrCodeExt(ManifestPickupRequest req){
		logger.info("===>Update Qrcode ext<===="+req.toString());
		TPaymentEntity payment = tPaymentRepo.findByBookingCodeIgnoreCaseContaining(req.getBookingCode());
		TPaymentEntity checkQrCode = tPaymentRepo.findByQrCodeExtOrQrcodeOrBookingCode(req.getQrCodeExt());
		if(payment != null && payment.getStatus().equals(PaymentEnum.PICKUP_BY_KURIR.getValue()) && checkQrCode == null) {
			payment.setQrcodeExt(req.getQrCodeExt());
			tPaymentRepo.save(payment);
		}else {
			return new Response<>(
					ResponseStatus.FAILED.value(),
					"QRCode Sudah pernah digunakan atau Data tidak ditemukan !"
					);
		}
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase()
				);
	}
	
	@SuppressWarnings("unused")
	@Transactional(rollbackOn = Exception.class)
	public Response<String> getSaveManifestFile(String file, String bookingCode, String noManifest, String qrcode,String userId){
		logger.info("===>Save Manifest Pickup <===="+bookingCode+"no manifest:"+noManifest);
		byte[] img = Base64.decodeBase64(file);
		logger.info("file size : "+img.length);
		String filename = noManifest.concat("_").concat(bookingCode).concat(".jpg");
		LocalDateTime today = LocalDateTime.now();
		TPaymentEntity payment = tPaymentRepo.findByBookingCodeIgnoreCaseContaining(bookingCode);
		TPickupEntity pickup= tPickupRepo.findByCode(noManifest);
//		TPickupDetailEntity tPickup = tPickupDetailRepo.findFirstByBookIdBookingCode(bookingCode);
		TPickupDetailEntity tPickup = tPickupDetailRepo.findFirstByPickupIdAndBookIdBookingCode(pickup, bookingCode);
		Date date = new Date();
		List<TPickupDetailEntity> lpickup = new ArrayList<>();
		List<TPaymentEntity> lpayment = new ArrayList<>();
		if(payment != null && tPickup != null) {
			if(!payment.getStatus().equals(PaymentEnum.ASSIGN_PICKUP.getValue())) {
				throw new NotFoundException("Pesanan statusnya sudah ter-pickup atau belum terbayar!");
			}if(!tPickup.getPickupId().getCourierId().getUserId().equals(userId)) {
				throw new NotFoundException("Pesanan Tidak Ditemukan!");
			}
			//if(!isQrcodeExt(qrcode))throw new NotFoundException("QRCode Sudah pernah digunakan !");
			if(depositBookService.checkQrCodeExt(bookCounterService.replaceRegexQrcodeExt(qrcode))) {
				throw new NotFoundException("QrCode : "+qrcode +" sudah digunakan !");
			}
			TPaymentEntity entityPaymentHistory=paymentService.createOldPayment(payment);
			payment.setStatus(PaymentEnum.PICKUP_BY_KURIR.getCode());
			payment.setTrxServer(new Timestamp(date.getTime()));
			payment.setQrcodeExt(bookCounterService.replaceRegexQrcodeExt(qrcode));			
			tPaymentRepo.save(payment);
			this.historyTransactionService.createHistory(entityPaymentHistory, payment, userId);
			try {
				if(file != null) {
					logger.info("Generate file");
					FileOutputStream out = new FileOutputStream(uploadingDir + filename);
					out.write(img);
					out.close();
					tPickup.setPathPic(uploadingDir + filename);
					tPickup.setStatus(PickupDetailEnum.IN_COURIER.getValue());
					tPickup.setModifyDate(LocalDateTime.now());
					tPickup.setModifyBy(userId);
					tPickupDetailRepo.save(tPickup);
				}else {
					tPickup.setStatus(PickupDetailEnum.IN_COURIER.getValue());
					tPickup.setModifyDate(LocalDateTime.now());
					tPickup.setModifyBy(userId);
					tPickupDetailRepo.save(tPickup);
				}
				lpickup = tPickupDetailRepo.findByCorierAndStatusDetailAndCode(userId, PickupEnum.ASSIGN_PICKUP.getValue(), noManifest);
				if(lpickup.size() == 0) {
					pickup.setStatus(PickupEnum.IN_COURIER.getValue());
					pickup.setModifyAt(LocalDateTime.now());
					pickup.setModifyBy(userId);
					tPickupRepo.save(pickup);
				}
				lpayment = tPaymentRepo.findPendingPaymentByUserId(payment.getUserId().getUserId(), PaymentEnum.ASSIGN_PICKUP.getValue());
				if(lpayment.size() == 0) {
					notifPaketPickup(bookingCode, userId, payment.getUserId());
				}
				THistoryBookEntity bookEntity = new THistoryBookEntity();
				bookEntity.setBookingCode(payment.getBookingCode());
				bookEntity.setIssuedBy(tPickup.getPickupId().getCourierId().getUserId());
				bookEntity.setStt("-");
				bookEntity.setPiece(String.valueOf(payment.getJumlahLembar()));
				bookEntity.setRemarks("Pickups by "+tPickup.getPickupId().getCourierId().getName());
				bookEntity.setUpdatedBy(tPickup.getPickupId().getCourierId().getUserId());
				bookEntity.setTrxDate(today);
				thistoryBookRepo.save(bookEntity);
				
			}catch (IOException e) {
				// TODO: handle exception
				logger.error(e.getMessage());
				return new Response<>(
						ResponseStatus.FAILED.value(),
						ResponseStatus.FAILED.getReasonPhrase());
			}
			
		}else {
			throw new NotFoundException("Data Tidak Ditemukan!");
		}
	return new Response<>(
			ResponseStatus.OK.value(),
			ResponseStatus.OK.getReasonPhrase()
			);
	}
	@SuppressWarnings("unused")
	@Transactional(rollbackOn = Exception.class)
	public Response<String> getSaveManifest(MultipartFile file, String bookingCode, String noManifest, String qrcode,String userId){
		logger.info("===>Save Manifest Pickup <===="+bookingCode+"no manifest:"+noManifest);
		logger.info("Nama file gambar : "+file.getName()+" size : "+file.getSize());
		LocalDateTime today = LocalDateTime.now();
		TPaymentEntity payment = tPaymentRepo.findByBookingCodeIgnoreCaseContaining(bookingCode);
		TPickupEntity pickup= tPickupRepo.findByCode(noManifest);
//		TPickupDetailEntity tPickup = tPickupDetailRepo.findFirstByBookIdBookingCode(bookingCode);
		TPickupDetailEntity tPickup = tPickupDetailRepo.findFirstByPickupIdAndBookIdBookingCode(pickup, bookingCode);
		Date date = new Date();
		List<TPickupDetailEntity> lpickup = new ArrayList<>();
		List<TPaymentEntity> lpayment = new ArrayList<>();
		if(payment != null && tPickup != null) {
			if(!payment.getStatus().equals(PaymentEnum.ASSIGN_PICKUP.getValue())) {
				throw new NotFoundException("Pesanan statusnya sudah ter-pickup atau belum terbayar!");
			}if(!tPickup.getPickupId().getCourierId().getUserId().equals(userId)) {
				throw new NotFoundException("Pesanan Tidak Ditemukan!");
			}
			//if(!isQrcodeExt(qrcode))throw new NotFoundException("QRCode Sudah pernah digunakan !");
			TPaymentEntity entityPaymentHistory=paymentService.createOldPayment(payment);
			payment.setStatus(PaymentEnum.PICKUP_BY_KURIR.getCode());
			payment.setTrxServer(new Timestamp(date.getTime()));
			payment.setQrcodeExt(payment.getBookingCode());			
			tPaymentRepo.save(payment);
			this.historyTransactionService.createHistory(entityPaymentHistory, payment, userId);
			try {
				if(file != null) {
					logger.info("Generate file"+file.getOriginalFilename());
					File files = new File(uploadingDir + file.getOriginalFilename());
					file.transferTo(files);
					tPickup.setPathPic(uploadingDir + file.getOriginalFilename());
					tPickup.setStatus(PickupDetailEnum.IN_COURIER.getValue());
					tPickup.setModifyDate(LocalDateTime.now());
					tPickup.setModifyBy(userId);
					tPickupDetailRepo.save(tPickup);
				}else {
					tPickup.setStatus(PickupDetailEnum.IN_COURIER.getValue());
					tPickup.setModifyDate(LocalDateTime.now());
					tPickup.setModifyBy(userId);
					tPickupDetailRepo.save(tPickup);
				}
				lpickup = tPickupDetailRepo.findByCorierAndStatusDetailAndCode(userId, PickupEnum.ASSIGN_PICKUP.getValue(), noManifest);
				if(lpickup.size() == 0) {
					pickup.setStatus(PickupEnum.IN_COURIER.getValue());
					pickup.setModifyAt(LocalDateTime.now());
					pickup.setModifyBy(userId);
					tPickupRepo.save(pickup);
				}
				lpayment = tPaymentRepo.findPendingPaymentByUserId(payment.getUserId().getUserId(), PaymentEnum.ASSIGN_PICKUP.getValue());
				if(lpayment.size() == 0) {
					notifPaketPickup(bookingCode, userId, payment.getUserId());
				}
				THistoryBookEntity bookEntity = new THistoryBookEntity();
				bookEntity.setBookingCode(payment.getBookingCode());
				bookEntity.setIssuedBy(tPickup.getPickupId().getCourierId().getUserId());
				bookEntity.setStt("-");
				bookEntity.setPiece(String.valueOf(payment.getJumlahLembar()));
				bookEntity.setRemarks("Pickups by "+tPickup.getPickupId().getCourierId().getName());
				bookEntity.setUpdatedBy(tPickup.getPickupId().getCourierId().getUserId());
				bookEntity.setTrxDate(today);
				thistoryBookRepo.save(bookEntity);
				
			}catch (IOException e) {
				// TODO: handle exception
				logger.error(e.getMessage());
				return new Response<>(
						ResponseStatus.FAILED.value(),
						ResponseStatus.FAILED.getReasonPhrase());
			}
			
		}else {
			throw new NotFoundException("Data Tidak Ditemukan!");
		}
	return new Response<>(
			ResponseStatus.OK.value(),
			ResponseStatus.OK.getReasonPhrase()
			);
	}
	public Response<String> editPickupAddress(PickupAddressRequest req){
		logger.info("===>Edit Pickup Address<===="+req.toString());
		TPickupAddressEntity pickuAddress = tpickupAddress.getOne(req.getPickupAddressId());
		if(pickuAddress != null) {
			pickuAddress.setAddress(req.getAddress());
			pickuAddress.setDescription(req.getDescription());
			pickuAddress.setPostalCode(mpostalCodeRepo.getOne(req.getIdPostalCode()));
			pickuAddress.setLatitude(req.getLatitude());
			pickuAddress.setLongitude(req.getLongitude());
			pickuAddress.setFlag(FLAG);
			tpickupAddress.save(pickuAddress);
		}else {
			return new Response<>(
					ResponseStatus.NOT_FOUND.value(),
					ResponseStatus.NOT_FOUND.getReasonPhrase()
					);
		}
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase()
				);
	}

	public Integer countBookInCourier(String courierId){
		return tPickupDetailRepo.countByCourierAndStatus(courierId,PaymentEnum.PICKUP_BY_KURIR.getValue());
	}

	public Integer sumJmlLembarInCourier(String courierId){
		return tPickupDetailRepo.sumItemByCourierAndStatus(courierId,PaymentEnum.PICKUP_BY_KURIR.getValue());
	}

	
	public Response<String> getNotifOtw(String courierId,String bookingCode){
		TPaymentEntity payment = tPaymentRepo.findByBookingCodeIgnoreCaseContaining(bookingCode);
		String ket="";
		String title="dari kurir";
		JSONObject data = new JSONObject();
		MUserEntity user = mUserRepo.getOne(courierId);
		try {
			ket = user.getName()+" sedang dalam perjalanan untuk pengambilan barang Anda";
			data.put("idTrx", user.getName());
    		data.put("userid", payment.getUserId().getUserId());
    		data.put("nominal", "");
    		data.put("type_trx", "3"); //1. Book, 2. Deposit, 3. Pickup
    		data.put("tag", title);
    		data.put("status_trx", "1"); //0. failed, 1.Success
    		data.put("tittle", "Pengambilan Barang");
    		data.put("body", ket);
    		firebase.notif(title, ket, data, title, payment.getUserId().getTokenNotif());
		}catch (JSONException e) {
			// TODO: handle exception
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase()
				);
	}
	@Async("asyncExecutor")
	@SneakyThrows
	public void notifPaketPickup(String booking_code,String courier_id,MUserEntity user) {
		String title = "paket_terpickup";
		String ket = "" ;
		JSONObject data = new JSONObject();
		logger.info("==> Send Notif Pickup "+booking_code+" <==");
		MUserEntity courier = mUserRepo.getOne(courier_id);
		//notif
		try {
			ket = "Paket Telah Di Ambil Oleh "+ courier.getName();
			data.put("idTrx", booking_code);
    		data.put("userid", user.getUserId());
    		data.put("nominal", "0");
    		data.put("type_trx", "3"); //1. Book, 2. Deposit, 3. Pickup
    		data.put("tag", title);
    		data.put("status_trx", "1"); //0. failed, 1.Success
    		data.put("tittle", "Paket Terambil");
    		data.put("body", ket);
    		firebase.notif(title, ket, data, title, user.getTokenNotif());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private List<DimensiGoods> getDimensi(String bookingCode){
		List<DimensiGoods> ldimensi = new ArrayList<>();
		List<TBookEntity> ltbook = tBookRepo.findByBookingCode(bookingCode);
		if(ltbook.size()==0) {
			DimensiGoods dg = new DimensiGoods();
			dg.setHeight("-");
			dg.setLength("-");
			dg.setWidth("-");
			ldimensi.add(dg);
			return ldimensi;
		}
		for(TBookEntity te : ltbook) {
			DimensiGoods dg = new DimensiGoods();
			dg.setHeight(te.getHeight());
			dg.setLength(te.getLength());
			dg.setWidth(te.getWidth());
			ldimensi.add(dg);
			}
		return ldimensi;
	}
	
	private List<DimensiGoods> getDimensi(List<TBookEntity> tbooks){
		List<DimensiGoods> ldimensi = new ArrayList<>();
		List<TBookEntity> ltbook = tbooks;
		if(ltbook.size()==0) {
			DimensiGoods dg = new DimensiGoods();
			dg.setHeight("-");
			dg.setLength("-");
			dg.setWidth("-");
			ldimensi.add(dg);
			return ldimensi;
		}
		for(TBookEntity te : ltbook) {
			DimensiGoods dg = new DimensiGoods();
			dg.setHeight(te.getHeight());
			dg.setLength(te.getLength());
			dg.setWidth(te.getWidth());
			ldimensi.add(dg);
			}
		return ldimensi;
	}
	
	private boolean isQrcodeExt(String qrcode) {
		TPaymentEntity payment = tPaymentRepo.findFirstByQrCodeExt(qrcode);
		if(payment != null) {
			return false;
		}
		return true;
	}

	public Page<ManifestPickup> getAllManifest(Pageable pageable, ManifestListRequest manifestListRequest){
		TPaymentEntity payment = null;
		TPickupOrderRequestEntity orderReq = null;
		if(manifestListRequest.getManifestId() != null) {
			payment = tPaymentRepo.findByBookingCodeIgnoreCaseContaining(manifestListRequest.getManifestId());
			orderReq = tPickupOrderRequestRepo.findByPickupOrderId(manifestListRequest.getManifestId());
		}
		LocalDate pickupDate  = null;
		if(manifestListRequest.getPickupDate() != null && !manifestListRequest.getPickupDate().isEmpty()) {
			pickupDate = LocalDate.parse(manifestListRequest.getPickupDate(), DateTimeFormatter.ofPattern("yyyyMMdd"));
		}
		Page<TPickupEntity> manifestPickups=this.tPickupRepo.findByCourierIdStatusManifestId(manifestListRequest.getDriverId(),manifestListRequest.getStatus(),manifestListRequest.getManifestId(),
				pickupDate,manifestListRequest.getIdPickupTime(),pageable);
		if(payment != null) {
			manifestPickups = tPickupRepo.getManifestByBookingCodeAndPickupOrderId(manifestListRequest.getManifestId(), null, manifestListRequest.getDriverId(), 
					manifestListRequest.getStatus(),pickupDate,manifestListRequest.getIdPickupTime(), pageable);
		}else if(orderReq != null) {
			manifestPickups = tPickupRepo.getManifestByBookingCodeAndPickupOrderId(null, manifestListRequest.getManifestId(), manifestListRequest.getDriverId(), 
					manifestListRequest.getStatus(),pickupDate,manifestListRequest.getIdPickupTime(), pageable);
		}
		if(manifestPickups.getContent().size() == 0) {
			manifestPickups = tPickupRepo.getManifestByUserId(manifestListRequest.getManifestId(),manifestListRequest.getDriverId(),manifestListRequest.getStatus(),pickupDate,manifestListRequest.getIdPickupTime(), pageable);
		}
		return new PageImpl<>(
				manifestPickups.getContent().stream().map(this::toManifestPickup).collect(Collectors.toList()),
				manifestPickups.getPageable(),
				manifestPickups.getTotalElements()
		);
	}

	public ManifestDetailRes getBookDataResponsesByManifest(String manifestId){
		TPickupEntity pickupEntity=this.tPickupRepo.findByCode(manifestId);
		List<TPickupDetailEntity> pickupDtl = this.tPickupDetailRepo.findAllByPickupId(pickupEntity);//this.tPickupDetailRepo.findByCode(pickupEntity.getCode());
		ManifestDetailRes manifestDetailRes=new ManifestDetailRes();
		manifestDetailRes.setIdCourier(pickupEntity.getCourierId().getUserId());
		manifestDetailRes.setManifestCode(pickupEntity.getCode());
		manifestDetailRes.setStatus(pickupEntity.getStatus());
		manifestDetailRes.setIdTimePickup(pickupEntity.getTimePickupId().getIdPickupTime());
		manifestDetailRes.setTglPickup(pickupEntity.getPickupDate());
		manifestDetailRes.setIsEditable(false);
//		manifestDetailRes.setDetail(tPickupDetailRepo.findByPickupIdIdPickup(pickupEntity.getIdPickup()).stream().map(this::toBookDataResponse).collect(Collectors.toList()));
		manifestDetailRes.setPickupResponses(getAssignPickupResponses(pickupEntity));
		manifestDetailRes.setValidateTimeToAsiggn(assignPickupService.getTimeStatus(pickupEntity.getPickupDate(),pickupEntity.getTimePickupId().getIdPickupTime(),pickupEntity.getCode()));
		manifestDetailRes.setAreaKotaId(pickupDtl.size()==0?0:pickupDtl.get(0).getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getAreaKotaId());
		manifestDetailRes.setKotaName(pickupDtl.size()==0?"":pickupDtl.get(0).getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getName());
		return manifestDetailRes;
	}

	public AssignPickupResponse getDetailStatus(String manifestId,Integer pickupAddrId){
		TPickupEntity pickupEntity=this.tPickupRepo.findByCode(manifestId);
		AssignPickupResponse status=null;
		List<TPaymentEntity> lpayment=tPickupDetailRepo.findPaymentEntityByPickupId(pickupEntity.getIdPickup(),pickupAddrId);
		List<TPickupOrderRequestEntity> lPickupOrder=tPickupDetailRepo.findRequestPickpuPickupByPickupId(pickupEntity.getIdPickup(),pickupAddrId);
		status=this.assignPickupService.getAssignPickup(lpayment,pickupAddrId,lPickupOrder,true);
		return status;
	}

	private List<AssignPickupResponse> getAssignPickupResponses(TPickupEntity pickupEntity){
		List<AssignPickupResponse> lAssign=new ArrayList<>();
//		List<TPaymentEntity> lpayment=paymentService.getTPaymentByPickupAddressRequest(pickupEntity.);
//		List<TPickupOrderRequestEntity> lPickupOrder=requestPickUpService.getLisPickupOrderByAddress(x);
		List<TPickupAddressEntity> pickupAddressEntity =tPickupDetailRepo.findPickupAddressByPickupId(pickupEntity.getIdPickup());
		if(pickupAddressEntity==null){
			pickupAddressEntity=new ArrayList<>();
		}
		List<TPickupAddressEntity> pickupAddressEntityListPickupReq =tPickupDetailRepo.findRequestPickpuAddressPickupByPickupId(pickupEntity.getIdPickup());
		if(pickupAddressEntityListPickupReq==null){
			pickupAddressEntityListPickupReq=new ArrayList<>();
		}

//		pickupAddressEntity.addAll(pickupAddressEntityListPickupReq);
		for (TPickupAddressEntity entity:pickupAddressEntityListPickupReq) {
			Boolean isAvailable=false;
			for (TPickupAddressEntity entity1:pickupAddressEntity) {
				if(entity.getPickupAddrId().equals(entity1.getPickupAddrId())){
					isAvailable=true;
					break;
				}
			}
			if(!isAvailable){
				pickupAddressEntity.add(entity);
			}
		}
//		List<TPaymentEntity> lpayment=paymentService.getTPaymentByPickupAddressRequest(x.getPickupAddrId());
//		List<TPickupOrderRequestEntity> lPickupOrder=requestPickUpService.getLisPickupOrderByAddress(x.getPickupAddrId());
		List<Integer> statusList=new ArrayList<>();
		statusList.add(RequestPickupEnum.REQUEST.getValue());
		statusList.add(RequestPickupEnum.FINISH_BOOK.getValue());
		statusList.add(RequestPickupEnum.IN_WAREHOUSE.getValue());
		statusList.add(RequestPickupEnum.ASSIGN_PICKUP.getValue());
		statusList.add(RequestPickupEnum.IN_COURIER.getValue());
		statusList.add(RequestPickupEnum.CANCEL_DETAIL.getValue());
		statusList.add(RequestPickupEnum.DRAFT_PICKUP.getValue());
		statusList.add(RequestPickupEnum.EXPIRED_PAYMENT.getValue());
		pickupAddressEntity.forEach(x->{
			List<TPaymentEntity> lpayment= tPickupDetailRepo.findByPickupAddridAndPickupCode(pickupEntity, x.getPickupAddrId());
			List<TPickupOrderRequestEntity> lPickupOrder = requestPickUpService.getLisPickupOrderByAddress(x.getPickupAddrId(),pickupEntity,statusList);
			AssignPickupResponse assRes = this.assignPickupService.getAssignPickup(lpayment, x.getPickupAddrId(), lPickupOrder,true);
			lAssign.add(assRes);
		});
//		List<TPaymentEntity> lpayment=tPickupDetailRepo.findPaymentEntityByPickupId(pickupEntity.getIdPickup());
//		List<TPickupOrderRequestEntity> lPickupOrder=tPickupDetailRepo.findRequestPickpuPickupByPickupId(pickupEntity.getIdPickup());
//		for(TPaymentEntity pay : lpayment) {
//			AssignPickupResponse assRes = this.assignPickupService.getAssignPickup(lpayment, pay.getPickupAddrId().getPickupAddrId(),lPickupOrder);
//			if(!lAssign.contains(assRes)) {
//				lAssign.add(assRes);
//			}
//		}
//		Boolean flag = false;
//		for(TPickupOrderRequestEntity p :lPickupOrder) {
//			for(AssignPickupResponse ap:lAssign) {
//				if(ap.getPickupAddresId().equals(p.getPickupAddressEntity().getPickupAddrId())) {
//					flag=true;
//				}
//			}
//			if(!flag && this.assignPickupService.checkStatusPay(p)) {
//				lpayment = new ArrayList<>();
//				AssignPickupResponse assRes = this.assignPickupService.getAssignPickup(lpayment, p.getPickupAddressEntity().getPickupAddrId(), lPickupOrder);
//				lAssign.add(assRes);
//			}
//		}
		Comparator<AssignPickupResponse> sortPostolCode = (a, b)->a.getPostalCode().compareToIgnoreCase(b.getPostalCode());
		Collections.sort(lAssign, sortPostolCode);
		return lAssign;
	}


	private BookDataResponse toBookDataResponse(TPickupDetailEntity tPickupDetailEntity){
		if(tPickupDetailEntity.getBookId()==null) {
			return paymentService.getDetailRequestPickup(tPickupDetailEntity.getPickupOrderRequestEntity());
		}
		return paymentService.toBookDataResponse(tPickupDetailEntity.getBookId());
	}


	private ManifestPickup toManifestPickup(TPickupEntity manifestEntity){
		Integer totalItem = getTotalItem(manifestEntity);
		Map<String, BigInteger> mpCourier = tPickupDetailRepo.countByPickupPickupOrderStatusIN(manifestEntity.getIdPickup(), Arrays.asList(PickupDetailEnum.IN_COURIER.getValue()));
		Map<String, BigInteger> mpBookInWH = tPickupDetailRepo.countByPickupPickupOrderStatusIN(manifestEntity.getIdPickup(), Arrays.asList(PickupDetailEnum.IN_WAREHOUSE.getValue(),
				PickupDetailEnum.HOLD_WAREHOUSE.getValue()));
//		Map<String, BigInteger> mpItemBook = tPickupDetailRepo.countByPickupPickupOrderStatusIN(manifestEntity.getIdPickup(), null);
		return ManifestPickup.builder()
				.belumTerima(""+tPickupDetailRepo.sumItemByManifestIdAndStatus(manifestEntity.getIdPickup(),PaymentEnum.ASSIGN_PICKUP.getValue()))
				.jumlahBarang(""+totalItem)
				.totalBooking(""+tPickupDetailRepo.countByPickupIdIdPickup(manifestEntity.getIdPickup()))
//				.bookingInCourier(tPickupDetailRepo.countByPickupPickupStatusIN(manifestEntity.getIdPickup(),Arrays.asList(PickupDetailEnum.IN_COURIER.getValue())))
				.bookingInCourier(mpCourier.get("jmlBook").add(mpCourier.get("jmlPickup")).intValue())
//				.bookingInWarehouse(tPickupDetailRepo.countByPickupPickupStatusIN(manifestEntity.getIdPickup(),Arrays.asList(PickupDetailEnum.IN_WAREHOUSE.getValue(),
//						PickupDetailEnum.HOLD_WAREHOUSE.getValue())))
				.bookingInWarehouse(mpBookInWH.get("jmlBook").add(mpBookInWH.get("jmlPickup")).intValue())
				.courierId(manifestEntity.getCourierId().getUserId())
				.totalItemBooking(totalItem)
				.jumlahTitik(getTotalPickupAddress(manifestEntity))
				.noManifest(manifestEntity.getCode())
				.sumAmount(getTotalAmount(manifestEntity))
				.courierName(manifestEntity.getCourierId().getName())
				.timePickup(manifestEntity.getTimePickupId().getTimeFrom().toString()+"-"+manifestEntity.getTimePickupTo().toString())
				.statusCode(manifestEntity.getStatus())
				.statusDesc(PickupEnum.getEnumByNumber(manifestEntity.getStatus()).toString())
				.pickupDate(manifestEntity.getPickupDate().toString())
				.build();
	}
	
	private ManifestPickupResponse getDetailPickupRequest(TPickupDetailEntity td,String timefrom,String timeto,List<DetailManifestPickup>ldetail,int lbelum) {
		String desc = (td.getPickupOrderRequestEntity().getPickupAddressEntity().getDescription()==null?"":td.getPickupOrderRequestEntity().getPickupAddressEntity().getDescription());
		String uri = endpointReport;
		uri = uri.replaceAll("#code", td.getPickupId().getCode());
		String lastUpdateManifest="";
		String lastUpdatePickup="";
		TPickupOrderRequestEntity pickupRequest = td.getPickupOrderRequestEntity();
		return ManifestPickupResponse.builder()
				.longitude(pickupRequest.getPickupAddressEntity().getLongitude())
				.latitude(pickupRequest.getPickupAddressEntity().getLatitude())
				.customerName(pickupRequest.getUserEntity().getName())
				.customerTelp(pickupRequest.getUserEntity().getHp())
				.statusCode(td.getPickupId().getStatus())
				.noManifest(td.getPickupId().getCode())
				.statusDesc(PickupEnum.getEnumByNumber(td.getPickupId().getStatus()).toString())
				.address(pickupRequest.getPickupAddressEntity().getAddress())
				.kelurahan(pickupRequest.getPickupAddressEntity().getPostalCode().getKelurahan())
				.kecamatan(pickupRequest.getPickupAddressEntity().getPostalCode().getKecamatanEntity().getKecamatan())
				.kota(pickupRequest.getPickupAddressEntity().getPostalCode().getKecamatanEntity().getKotaEntity().getName())
				.provinsi(pickupRequest.getPickupAddressEntity().getPostalCode().getKecamatanEntity().getKotaEntity().getProvinsiEntity().getName())
				.postalCode(pickupRequest.getPickupAddressEntity().getPostalCode().getPostalCode())
				.addressNote(desc)
				.pickupDate(DateTimeUtil.getString2Date(td.getPickupId().getPickupDate().toString(), "yyyy-MM-dd", "dd-MM-yyyy"))
				.pickupTime(pickupRequest.getPickupTimeEntity().getTimeFrom().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+" - "+pickupRequest.getPickupTimeEntity().getTimeTo().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
				.detail(ldetail)
				.idPostalCode(0)
				.pickupAddressId(pickupRequest.getPickupAddressEntity().getPickupAddrId())
				.countAssign(String.valueOf(lbelum))
				.linkManifest(uri)
				.lastUpdateManifestAt(lastUpdateManifest)
				.lastUpdatePickupAt(lastUpdatePickup)
				.flag(td.getPickupOrderRequestEntity().getPickupAddressEntity().getFlag().toString())
				.isBooking(false)
				.build();
		}
	@Transactional(rollbackOn=Exception.class) //d
	public Response<String> updatePickupRequest(String userId,String qrcode,String pickupOrderId,String qrcodeExt,String images){
		logger.info("Add/Update Detail Barang Request Pickup :"+pickupOrderId);
		try {
			TPickupDetailEntity tpickupDetail = tPickupDetailRepo.findByPickupOrdeIdAndCourierId(pickupOrderId, userId);
			if(tpickupDetail == null)throw new NotFoundException("Data Tidak Ditemukan!");
			if(depositBookService.checkQrCodeExt(qrcodeExt)) {
				throw new NotFoundException("QrCode : "+qrcodeExt +" sudah digunakan !");
			}
			byte[] img = Base64.decodeBase64(images);
			logger.info("file size : "+img.length);
			String filename = tpickupDetail.getPickupId().getCode().concat("_").concat(qrcodeExt).concat(".png");
			TPickupOrderRequestEntity pickupRequest = tpickupDetail.getPickupOrderRequestEntity();
			TPickupOrderRequestDetailEntity pickupRequestDtl = new TPickupOrderRequestDetailEntity(); // optional
			logger.info("Generate file");
			FileOutputStream out = new FileOutputStream(uploadingDir + filename);
			out.write(img);
			out.close();
			if(qrcode == null) {
				TPickupOrderRequestDetailEntity isQrCode = tPickupOrderRequestDetailRepo.findByQrcodeExt(qrcodeExt);
				if(isQrCode != null) {
					throw new NotFoundException("QrCode sudah pernah dipakai !!");
				}
				pickupRequestDtl.setOrderRequestEntity(pickupRequest);
				pickupRequestDtl.setQty(1);
				pickupRequestDtl.setQrcodeExt(bookCounterService.replaceRegexQrcodeExt(qrcodeExt));
				pickupRequestDtl.setStatus(RequestPickupEnum.IN_COURIER.getValue());
				pickupRequestDtl.setUpdateDate(LocalDateTime.now());
				pickupRequestDtl.setUpdateBy(userId);
				pickupRequestDtl.setCreateDate(LocalDateTime.now());
				pickupRequestDtl.setCreateBy(userId);
				pickupRequestDtl.setQrCode(Common.gerQrCode());
				pickupRequestDtl.setAmount(BigDecimal.ZERO);
				pickupRequestDtl.setIsPay(0);
				pickupRequestDtl.setPathPic(uploadingDir + filename);
				pickupRequest.setQty(pickupRequest.getQty()+FLAG);
			}else {
				pickupRequestDtl = tPickupOrderRequestDetailRepo.findByOrderRequestEntityAndQrCode(pickupRequest, qrcode);
				TPickupOrderRequestDetailEntity pickReq = tPickupOrderRequestDetailRepo.findByQrcodeExt(qrcodeExt);
				if(pickupRequestDtl == null) {
					throw new NotFoundException("Data Tidak Ditemukan!");
				}
				if(qrcode == null || pickReq != null) {
					throw new NotFoundException("New QrCode tidak boleh kosong atau qrcode sudah pernah dipakai !!");
				}
				pickupRequestDtl.setQrcodeExt(bookCounterService.replaceRegexQrcodeExt(qrcodeExt));
				pickupRequestDtl.setStatus(RequestPickupEnum.IN_COURIER.getValue());
				pickupRequestDtl.setUpdateDate(LocalDateTime.now());
				pickupRequestDtl.setUpdateBy(userId);
				pickupRequestDtl.setPathPic(uploadingDir + filename);
			}
			tPickupOrderRequestDetailRepo.save(pickupRequestDtl);
			tPickupOrderRequestRepo.save(pickupRequest);
			historyTransactionService.historyRequestPickup(pickupRequest, pickupRequestDtl, RequestPickupEnum.ASSIGN_PICKUP.getValue(), userId, "");
		}catch (DataAccessException e) {
			// TODO: handle exception
			logger.error(e.getMessage());
			e.printStackTrace();
			return new Response<>(
					ResponseStatus.FAILED.value(),
					ResponseStatus.FAILED.getReasonPhrase()
					);
		}catch (NotFoundException en) {
			// TODO: handle exception
			throw en;
		}
		catch (Exception ex) {
			// TODO: handle exception
			logger.error(ex.getMessage());
			ex.printStackTrace();
			return new Response<>(
					ResponseStatus.FAILED.value(),
					ResponseStatus.FAILED.getReasonPhrase()
					);
		}
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase()
				);
	}
	
	public ManifestPickupResponse getDetailRequestPickup(String courierId,String orderId) {
		TPickupDetailEntity pickupDetail = tPickupDetailRepo.findByPickupOrdeIdAndCourierId(orderId, courierId);
		if(pickupDetail ==null)throw new NotFoundException("Data Tidak Ditemukan !");
		TPickupOrderRequestEntity pickupRequest = pickupDetail.getPickupOrderRequestEntity();
		List<TPickupOrderRequestDetailEntity> pickupRequestDtl = tPickupOrderRequestDetailRepo.findAllByOrderRequestEntity(pickupDetail.getPickupOrderRequestEntity());
		List<DetailManifestRequestPickup> dtailPickup = new ArrayList<>();
		int seq =0;
		for(TPickupOrderRequestDetailEntity pr :pickupRequestDtl) {
			seq=seq+1;
			DetailManifestRequestPickup dtl = new DetailManifestRequestPickup();
			String destination ="-";
			String images = "-";
			if(pr.getAreaId()!=null) {
				destination = pr.getAreaId().getKecamatan()+","+pr.getAreaId().getKotaEntity().getName();
			}
			if(pr.getProductSwitcherEntity()!=null) {
				images = PREFIX_PATH_IMAGE_VENDOR + pr.getProductSwitcherEntity().getSwitcherEntity().getImg().substring(pr.getProductSwitcherEntity().getSwitcherEntity().getImg().lastIndexOf("/")+1);
			}
			dtl.setJumlahLembar(String.valueOf(pr.getQty())==null?"-":String.valueOf(pr.getQty()));
			dtl.setWeight(pr.getWeight()==null?"-":pr.getWeight().toString());
			dtl.setVendorName(pr.getProductSwitcherEntity()==null?"-":pr.getProductSwitcherEntity().getSwitcherEntity().getDisplayName());
			dtl.setReceiverName(pr.getNamaPenerima()==null?"-":pr.getNamaPenerima());
			dtl.setQrcode(pr.getQrCode()==null?"-":pr.getQrCode());
			dtl.setDestination(destination);
			dtl.setProductName(pr.getProductSwitcherEntity()==null?"-":pr.getProductSwitcherEntity().getDisplayName());
			dtl.setRequestStatus(pr.getStatus()==null?0:pr.getStatus());
			dtl.setImageVendor(images);
			dtl.setQrcodeExt(pr.getQrcodeExt()==null?"-":pr.getQrcodeExt());
			dtailPickup.add(dtl);
		}
		return ManifestPickupResponse.builder()
				.longitude(pickupRequest.getPickupAddressEntity().getLongitude())
				.latitude(pickupRequest.getPickupAddressEntity().getLatitude())
				.customerName(pickupRequest.getUserEntity().getName())
				.customerTelp(pickupRequest.getUserEntity().getHp())
				.statusCode(pickupDetail.getPickupId().getStatus())
				.noManifest(pickupDetail.getPickupId().getCode())
				.statusDesc(PickupEnum.getEnumByNumber(pickupDetail.getPickupId().getStatus()).toString())
				.address(pickupRequest.getPickupAddressEntity().getAddress())
				.kelurahan(pickupRequest.getPickupAddressEntity().getPostalCode().getKelurahan())
				.kecamatan(pickupRequest.getPickupAddressEntity().getPostalCode().getKecamatanEntity().getKecamatan())
				.kota(pickupRequest.getPickupAddressEntity().getPostalCode().getKecamatanEntity().getKotaEntity().getName())
				.provinsi(pickupRequest.getPickupAddressEntity().getPostalCode().getKecamatanEntity().getKotaEntity().getProvinsiEntity().getName())
				.postalCode(pickupRequest.getPickupAddressEntity().getPostalCode().getPostalCode())
				.addressNote(pickupRequest.getPickupAddressEntity().getDescription()==null?"":pickupRequest.getPickupAddressEntity().getDescription())
				.pickupDate(DateTimeUtil.getString2Date(pickupDetail.getPickupId().getPickupDate().toString(), "yyyy-MM-dd", "dd-MM-yyyy"))
				.pickupTime(pickupDetail.getPickupId().getTimePickupFrom().toString()+" - "+pickupDetail.getPickupId().getTimePickupTo().toString())
				.idPostalCode(0)
				.pickupAddressId(pickupRequest.getPickupAddressEntity().getPickupAddrId())
				.flag(pickupDetail.getPickupOrderRequestEntity().getPickupAddressEntity().getFlag().toString())
				.detailReq(dtailPickup)
				.isDelete(false)
				.isAdd(true)
				.build();		
	}
	@SuppressWarnings("unused")
	@Transactional(rollbackOn=Exception.class)
	public Response<String> getSaveManifestRequestPickup(MultipartFile file,String courierId,String noManifest,String pickupOrderId){
		TPickupDetailEntity tpickupDtl = tPickupDetailRepo.findByPickupOrdeIdAndCourierId(pickupOrderId, courierId);
		TPickupOrderRequestEntity pickupRequest = tpickupDtl.getPickupOrderRequestEntity();
		List<TPickupOrderRequestDetailEntity> pickupReqDtl = tPickupOrderRequestDetailRepo.findAllByOrderRequestEntity(pickupRequest);
		List<TPickupDetailEntity> lpickup = new ArrayList<>();
		TPickupEntity pickup = tPickupRepo.findByCode(noManifest);
		if(tpickupDtl == null) throw new NotFoundException("Data Tidak Ditemukan !");
		if(pickupReqDtl.size()==0)throw new NotFoundException("Lengkapi Data Pickup (Minimal 1 Barang)");
			try {
				if(file != null) {
					logger.info("Generate file"+file.getOriginalFilename());
					File files = new File(uploadingDir + file.getOriginalFilename());
					file.transferTo(files);
					for(TPickupOrderRequestDetailEntity trd : pickupReqDtl) {
						if(trd.getStatus()==null || trd.getStatus() == RequestPickupEnum.ASSIGN_PICKUP.getValue() || trd.getQrcodeExt()==null) {
							throw new NotFoundException("Ada Barang yang belum discan !");
						}
					}
					//update pickup request
					pickupRequest.setStatus(RequestPickupEnum.IN_COURIER.getValue());
					pickupRequest.setUpdateBy(courierId);
					pickupRequest.setUpdateDate(LocalDateTime.now());
					tPickupOrderRequestRepo.save(pickupRequest);
					//update t pickup detail
					tpickupDtl.setPathPic(uploadingDir + file.getOriginalFilename());
					tpickupDtl.setStatus(PickupDetailEnum.IN_COURIER.getValue());
					tpickupDtl.setModifyDate(LocalDateTime.now());
					tpickupDtl.setModifyBy(courierId);
					tPickupDetailRepo.save(tpickupDtl);
				}else {
					for(TPickupOrderRequestDetailEntity trd : pickupReqDtl) {
						if(trd.getStatus()==null || trd.getStatus() == RequestPickupEnum.ASSIGN_PICKUP.getValue()) {
							throw new NotFoundException("Ada Barang yang belum discan !");
						}
					}
					//update pickup request
					pickupRequest.setStatus(RequestPickupEnum.IN_COURIER.getValue());
					pickupRequest.setUpdateBy(courierId);
					pickupRequest.setUpdateDate(LocalDateTime.now());
					tPickupOrderRequestRepo.save(pickupRequest);
					//update t pickup detail
					tpickupDtl.setStatus(PickupDetailEnum.IN_COURIER.getValue());
					tpickupDtl.setModifyDate(LocalDateTime.now());
					tpickupDtl.setModifyBy(courierId);
					tPickupDetailRepo.save(tpickupDtl);
				}
				lpickup = tPickupDetailRepo.findByCorierAndStatusDetailAndCode(courierId, PickupEnum.ASSIGN_PICKUP.getValue(), noManifest);
				if(lpickup.size() == 0) {
					pickup.setStatus(PickupEnum.IN_COURIER.getValue());
					pickup.setModifyAt(LocalDateTime.now());
					pickup.setModifyBy(courierId);
					tPickupRepo.save(pickup);
				}
				
				
			}catch (IOException e) {
				// TODO: handle exception
				logger.error(e.getMessage());
				return new Response<>(
						ResponseStatus.FAILED.value(),
						ResponseStatus.FAILED.getReasonPhrase());
			}
		
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase()
				);
	}
	
	@Transactional(rollbackOn=Exception.class)
	public Response<String> deleteRequestDetail(String qrcode){
		TPickupOrderRequestDetailEntity reqEntity = tPickupOrderRequestDetailRepo.findByQrCodeOrQrcodeExt(qrcode, qrcode);
		if(reqEntity == null)throw new NotFoundException("Data Tidak Ditemukan !");
		TPickupOrderRequestEntity pickupReq = reqEntity.getOrderRequestEntity();
		pickupReq.setQty(pickupReq.getQty()-FLAG);
		tPickupOrderRequestRepo.save(pickupReq);
		tPickupOrderRequestDetailRepo.delete(reqEntity);
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase()
				);
	}
	
	private Integer getTotalItem(TPickupEntity entity) {
		Integer result = 0;
		Integer itemBook = tPickupDetailRepo.sumItemByManifestIdAndStatus(entity.getIdPickup());
		Integer itemRequest = tPickupDetailRepo.sumQtyPickupRequestByManifestId(entity.getIdPickup());
		if(itemBook != null) {
			result = result + itemBook;
		}
		if(itemRequest != null) {
			result = result + itemRequest;
		}
		
		return result;
	}
	private Integer getTotalPickupAddress(TPickupEntity entity) {
		List<Integer> itemBook = tPickupDetailRepo.countQtyPointPickup(entity.getIdPickup());
		//List<Integer> itemRequest = tPickupDetailRepo.countQtyPointPickupOrder(entity.getIdPickup());
		return itemBook.size();
	}
	private Integer getTotalAmount(TPickupEntity entity){
		Integer result = 0;
		Integer requestPickup=0;
		Integer sumBook=tPickupDetailRepo.sumAmountBookByManifestId(entity.getIdPickup());
		if(sumBook==null)
			sumBook=0;
		List<TPickupOrderRequestEntity> requestEntityList=tPickupDetailRepo.getTPickupDetailEntityBy(entity.getIdPickup());
//		List<String> paymentBookId = this.tPickupOrderRequestDetailRepo.findByOrderRequest(p);
		if(requestEntityList!=null){
			if(requestEntityList.size()>0){
				List<String> bookList=tPickupOrderRequestDetailRepo.countBook(requestEntityList,RequestPickupEnum.FINISH_BOOK.getValue());
				if(bookList.size()>0){
					List<TPaymentEntity> paymentEntityList = this.paymentService.get(bookList);
					if(paymentEntityList.size()>0){
						requestPickup=this.tPaymentRepo.sumByBookList(paymentEntityList);
						if(requestPickup==null)
							requestPickup=0;
					}
				}
			}
		}

		result = sumBook+requestPickup;

		return result;
	}
	public TPickupEntity findByCode(String code) {
		return tPickupRepo.findByCode(code);
	}
}
