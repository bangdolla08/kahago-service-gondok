package com.kahago.kahagoservice.service;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.kahago.kahagoservice.entity.*;
import com.kahago.kahagoservice.model.request.ListGudangApprovalReq;
import com.kahago.kahagoservice.model.request.RequestBook;
import com.kahago.kahagoservice.model.response.*;
import com.kahago.kahagoservice.repository.*;
import com.kahago.kahagoservice.util.Common;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.aspectj.weaver.patterns.IScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.kahago.kahagoservice.component.vendor.BukaSendComponent;
import com.kahago.kahagoservice.component.vendor.DakotaComponent;
import com.kahago.kahagoservice.component.vendor.IndahCargoComponent;
import com.kahago.kahagoservice.component.vendor.JETComponent;
import com.kahago.kahagoservice.component.vendor.JNEComponent;
import com.kahago.kahagoservice.component.vendor.LionParcelComponent;
import com.kahago.kahagoservice.component.vendor.PCPComponent;
import com.kahago.kahagoservice.component.vendor.POPComponent;
import com.kahago.kahagoservice.component.vendor.POSComponent;
import com.kahago.kahagoservice.component.vendor.SicepatComponent;
import com.kahago.kahagoservice.component.vendor.TikiComponent;
import com.kahago.kahagoservice.component.vendor.WahanaComponent;
import com.kahago.kahagoservice.enummodel.MutasiEnum;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.PickupEnum;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.enummodel.WarehouseEnum;
import com.kahago.kahagoservice.exception.InternalServerException;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.dto.PaymentDto;
import com.kahago.kahagoservice.model.request.BookRequestIndah;
import com.kahago.kahagoservice.model.request.WarehouseVerificationReq;

/**
 * @author Ibnu Wasis
 */
@Service @Slf4j
public class WarehouseVerificationService {
	@Autowired
	private TWarehouseReceiveDetailRepo tDetailRepo;
	@Autowired
	private MUserRepo mUserRepo;
	@Autowired
	private TPaymentRepo tPaymentRepo;
	@Autowired
	private TFeeTrxRepo tFeeTrxRepo;
	@Autowired
	private MOfficeRepo mOfficeRepo;
	@Autowired
	private MVendorPropRepo mVendorPropRepo;
	@Autowired
	private TSttVendorRepo tSttVendorRepo;
	@Autowired
	private TPickupDetailRepo tPickupDetailRepo;
	@Autowired
	private MGoodsRepo mGoodsRepo;
	@Autowired
	private TAreaRepo areaRepo;
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private HistoryTransactionService historyTransactionService;
	@Autowired
	private BookService bookService;
	@Autowired
	private LionParcelComponent lp;
	@Autowired
	private SicepatComponent sicepat;
	@Autowired
	private JETComponent jet;
	@Autowired
	private POPComponent pop;
	@Autowired
	private TikiComponent tiki;
	@Autowired
	private DakotaComponent dakota;
	@Autowired
	private POSComponent pos;
	@Autowired
	private WahanaComponent wahana;
	@Autowired
	private JNEComponent jne;
	@Autowired
	private IndahCargoComponent indah;
	@Autowired
	private PCPComponent pcp;
	@Value("${url.cetak}")
	private String urlcetak;
	@Autowired
	private TFeeReferenceRepo tFeeReferenceRepo;
	@Autowired
	private TMutasiRepo tMutasiRepo;
	@Autowired
	private TCreditRepo tCreditRepo;
	@Autowired
	private ApprovalRejectWarehouseService approvalRejectWarehouseService;
	@Autowired
	private TProductSurchargeRepo tProductSurchargeRepo;
	@Autowired
	private THistoryBookRepo tHistoryBookRepo;
	@Autowired
	private TOutgoingCounterDetailRepo outgoingCounterDetailRepo;
	@Autowired
	private TOutgoingListRepo outgoingListRepo;
	@Autowired
	private BookCounterService bookCounterService;
	@Autowired
	private DepositBookService depositBookService;
	@Autowired
	private BukaSendComponent bukaSend;

	@Getter @Setter
	private Boolean isCount;
	private Integer seq=0;
	private static final Integer FLAG_FEE = 3;
	private static final Integer CODE_LP = 301;
	private static final Integer CODE_SICEPAT = 302;
	private static final Integer CODE_JET = 303;
	private static final Integer CODE_POP = 304;
	private static final Integer CODE_TIKI = 305;
	private static final Integer CODE_PCP = 306;
	private static final Integer CODE_TEBA = 307;
	private static final Integer CODE_DAKOTA = 308;
	private static final Integer CODE_POS = 309;
	private static final Integer CODE_WAHANA = 310;
	private static final Integer CODE_JNE = 311;
	private static final Integer CODE_INDAH = 312;
	private static final Integer CODE_BUKASEND = 314;
	private static final String STATUS_SUCCESS = "200";
	private static final Integer FLAG = 1;
	
	private static final int HARGA_PACKING = 50000;
	private static final Logger logger = LoggerFactory.getLogger(WarehouseVerificationService.class);
	private static final Integer PRODUCT_CODE_REGPACK_LP =1;
	
	public Page<BookDataResponse> getAll(ListGudangApprovalReq listGudangApprovalReq){
		Page<TWarehouseReceiveDetailEntity> lwareHouse
				=tDetailRepo.findAllByStatusAndBookIdOrQrcodeOrQrcodeext(WarehouseEnum.RECEIVE_IN_WAREHOUSE.getValue(),
				listGudangApprovalReq.getBookingCodeOrQrCode(),
				listGudangApprovalReq.getOfficeCode(),
				listGudangApprovalReq.getUserIdSearch(),
				listGudangApprovalReq.getVendorCode(),
				PaymentEnum.RECEIVE_IN_WAREHOUSE.getCode(),
				listGudangApprovalReq.getPageRequest());
		seqId=0;
		return new PageImpl<>(
				lwareHouse.getContent().stream().map(this::toResponse).collect(Collectors.toList()),
				lwareHouse.getPageable(),
				lwareHouse.getTotalElements()
		);
	}

	public Page<BookDataResponse> getAllIN(RequestBook req){
		Page<TWarehouseReceiveDetailEntity> lwareHouse
				=tDetailRepo.findAllByStatusAndBookIdOrQrcodeOrQrcodeextAndOfficeCodeIN(WarehouseEnum.RECEIVE_IN_WAREHOUSE.getValue(),
				req.getQrCode(),
				req.getOfficeCode(),
				req.getUserId(),
				req.getVendorCode(),
				PaymentEnum.RECEIVE_IN_WAREHOUSE.getCode(),
				req.getFilter(),
				req.getPageRequest());
		seqId=0;
		return new PageImpl<>(
				lwareHouse.getContent().stream().map(this::toResponse).collect(Collectors.toList()),
				lwareHouse.getPageable(),
				lwareHouse.getTotalElements()
		);
	}
	public BookDataResponse getDetail(String bookingCode,String officeCode){
		seqId=0;
		TWarehouseReceiveDetailEntity warehouseReceiveDetailEntity=tDetailRepo
				.findByStatusAndBookIdOrQrcodeOrQrcodeext(Arrays.asList(WarehouseEnum.RECEIVE_IN_WAREHOUSE.getCode()),bookingCode,officeCode,Arrays.asList(PaymentEnum.RECEIVE_IN_WAREHOUSE.getCode()));
		return toResponse(warehouseReceiveDetailEntity,true);
	}

	Integer seqId;
	private BookDataResponse toResponse(TWarehouseReceiveDetailEntity entity){
		return this.toResponse(entity,true);
	}
	private BookDataResponse toResponse(TWarehouseReceiveDetailEntity entity,Boolean useDetail){
		if(seqId==null){
			seqId=0;
		}
		seqId++;
		TPaymentEntity paymentEntity=entity.getBookId();
		BookDataResponse response=paymentService.toBookDataResponse(paymentEntity,seqId);
		response.setPriceKg(paymentEntity.getPriceKg());
		response.setPembagiVolume(paymentEntity.getProductSwCode().getPembagiVolume().longValue());
		response.setPembulatanVolume(paymentEntity.getProductSwCode().getPembulatanVolume());
		response.setIsInsurance(paymentEntity.getInsurance().doubleValue()>0);
		response.setIsPack(paymentEntity.getExtraCharge().doubleValue()>0&&paymentEntity.getTotalPackKg()>0);
		response.setInsurance(paymentEntity.getInsurance().doubleValue());
		response.setExtraCharge(paymentEntity.getExtraCharge().doubleValue());
		response.setPriceGoods(paymentEntity.getPriceGoods().doubleValue());
		response.setTotalPackKg(paymentEntity.getTotalPackKg());
		response.setPresentaseAsuransi(0.3);
		if(useDetail){
			List<BookDetailResponse> bookDetailResponses=new ArrayList<>();
			for (TBookEntity bookEntity:entity.getBookId().getTbooks()) {
				bookDetailResponses.add(
						BookDetailResponse.builder()
						.grossWeight(Double.valueOf(bookEntity.getGrossWeight()))
						.height(Double.valueOf(bookEntity.getHeight()))
						.length(Double.valueOf(bookEntity.getLength()))
						.width(Double.valueOf(bookEntity.getWidth()))
						.volWeight(Double.valueOf(bookEntity.getVolWeight()))
						.build()
				);
			}
			response.setDetailBook(bookDetailResponses);			
			response.setSurcharge(getListSurcharge(paymentEntity.getProductSwCode().getProductSwCode().intValue()));
			
		}
		return response;
	}
	
	private WarehouseVerificationResponse toDto(TWarehouseReceiveDetailEntity entity) {
		seq =seq + 1;
		return WarehouseVerificationResponse.builder()
				.seq(seq)
				.bookId(entity.getBookId().getBookingCode())
				.userId(entity.getBookId().getUserId().getUserId())
				.destination(entity.getBookId().getDestination())
				.jumlahLembar(entity.getBookId().getJumlahLembar())
				.volume(entity.getBookId().getVolume())
				.Weight(entity.getBookId().getGrossWeight())
				.goodDesc(entity.getBookId().getGoodsDesc())
				.vendorName(entity.getBookId().getProductSwCode().getSwitcherEntity().getDisplayName())
				.productName(entity.getBookId().getProductSwCode().getDisplayName())
				.statusDesc(WarehouseEnum.getWarehouseEnum(entity.getStatus()).getString())
				.officeName(entity.getBookId().getOfficeCode())
				.nominal(entity.getBookId().getAmount())
				.books(entity.getBookId().getTbooks())
				.build();
	}
	
	public void acceptInWarehouse(TPaymentEntity pay,MUserEntity user) throws Exception{
//		log.info("Save Payment");
//		log.info(Common.json2String(payment));
		String resi = "";
//		TPaymentEntity pay = tPaymentRepo.findByBookingCodeIgnoreCaseContaining(payment.getBookingCode());
		if(pay.getGrossWeight().compareTo(pay.getMinWeight().longValue()) < 0) {
			pay.setGrossWeight(pay.getMinWeight().longValue());
		}
//		log.info("Save Payment");
//		log.info(Common.json2String(pay));
		TFeeTrxEntity tfee = tFeeTrxRepo.findByIdMUserCategoryAndFee(pay.getUserId().getUserCategory().getSeqid(), FLAG_FEE);
		if(tfee != null) {
			TFeeReferenceEntity tFeeRef = new TFeeReferenceEntity();
			Double nominal = pay.getAmount().doubleValue() * (Double.valueOf(tfee.getFee())/100);
			tFeeRef.setTrxNo(pay.getBookingCode());
			tFeeRef.setTglTrx(pay.getTrxServer());
			tFeeRef.setUserId(pay.getUserId().getUserId());
			tFeeRef.setNominal(Integer.valueOf(pay.getAmount().toString()));
			tFeeRef.setFeeNominal(nominal.intValue());
			tFeeRef.setFeePersen(tfee.getFee());
			tFeeRef.setLastUpdate(new Timestamp( Instant.now().toEpochMilli()));
			tFeeRef.setLastUser(user.getUserId());
			tFeeRef.setUnitFee(tfee);
		}
		pay.setTrxTime(pay.getTrxTime());
		String stt= pay.getStt();
		MOfficeEntity office = mOfficeRepo.findAllByOfficeCode(pay.getOfficeCode());
		MVendorPropEntity vendorProp = mVendorPropRepo.findAllBySwitcherCodeAndActionAndOrigin(pay.getProductSwCode().getSwitcherEntity(), "book", pay.getOrigin());
		String origin = (pay.getProductSwCode().getSwitcherEntity().getSwitcherCode()==301)?null:pay.getOrigin();
		TSttVendorEntity sttE =null;
		if(stt.equals("-")) {
			sttE = tSttVendorRepo
					.findFirstBySwitcherCodeAndFlagAndOrigin(pay.getProductSwCode().getSwitcherEntity().getSwitcherCode(), 0,origin);
		}
		if(sttE!=null 
//				&& !pay.getProductSwCode().getProductSwCode().equals(PRODUCT_CODE_REGPACK_LP.longValue())
				) {
			stt = sttE.getStt();
		}
		this.setIsCount(false);	
		if(vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_LP)) {
			BookResponseLP response= new BookResponseLP();
//			if(pay.getProductSwCode().getProductSwCode().equals((PRODUCT_CODE_REGPACK_LP.longValue()))) {
//				response.setRc(ResponseStatus.OK.value());
//				payment.setStatus(PaymentEnum.ACCEPT_WITHOUT_RESI.getValue());
//			}else {
			this.setIsCount(isPickupVendor(office, vendorProp,CODE_LP));
			if(getIsCount()) {
				response.setRc(ResponseStatus.OK.value());
				pay.setStatus(PaymentEnum.ACCEPT_WITHOUT_RESI.getValue());
			}else {
				response= lp.getPayment(vendorProp.getClientCode(), stt, pay.getProductSwCode().getOperatorSw(),
						vendorProp.getUrl(), pay);
			}
			
//			}
			if(!response.getRc().equals(ResponseStatus.OK.value())) {
				logger.info("Failed from vendor LP RC:" + response.getRc());
//				pay.setStatus(PaymentEnum.REQUEST.getCode());
				throw new Exception("Failed By vendor: " + response.getRd());
			}
		}else if(vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_SICEPAT)) {
			BookResponseSicepat respon = sicepat.getPayment(vendorProp.getClientCode(), stt, pay.getProductSwCode().getOperatorSw(), vendorProp.getUrl(), 
					pay, office);
			if(!respon.getStatus().equals(STATUS_SUCCESS)) {
				logger.info("Failed from vendor Sicepat RC:" + respon.getStatus());
				throw new Exception("Failed By vendor: " + respon.getErrorMessage());
			}
		}else if(vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_JET) && stt.equals("-")) {
			BookResponseJet respon = jet.getPayment(vendorProp.getClientCode(), stt, pay.getProductSwCode().getOperatorSw(), 
					vendorProp.getUrl(), pay, office);
			if(!respon.getRc().equals(ResponseStatus.OK.value())) {
				logger.info("Failed from vendor JET RC:" + respon.getRc());
//				pay.setStatus(PaymentEnum.REQUEST.getCode());
				throw new Exception("Failed By vendor: " + respon.getDescription());
			}
			stt = respon.getNoResi();
		}else if(vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_POP)) {
			BookResponseJet respon = pop.getPayment(vendorProp.getClientCode(), stt, pay.getProductSwCode().getOperatorSw(), 
					vendorProp.getUrl(), pay);
			if(!respon.getRc().equals(ResponseStatus.OK.value())) {
				logger.info("Failed from vendor POP RC:" + respon.getRc());
//				pay.setStatus(PaymentEnum.REQUEST.getCode());
				throw new Exception("Failed By vendor: " + respon.getDescription());
			}
			stt = respon.getNoResi();
		}else if(vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_TIKI)) {
			BookResponseJet respon = tiki.getPayment(vendorProp.getClientCode(), stt, pay.getProductSwCode().getOperatorSw(), 
					vendorProp.getUrl(), pay);
			if(!respon.getRc().equals(ResponseStatus.OK.value())) {
				logger.info("Failed from vendor TIKI RC:" + respon.getRc());
//				pay.setStatus(PaymentEnum.REQUEST.getCode());
				throw new Exception("Failed By vendor: " + respon.getDescription());
			}
			resi = "api/resi/tiki?bookingcode="+pay.getBookingCode()
			+ "&userid=" + pay.getUserId().getUserId();
			stt = respon.getNoResi();
			pay.setResi(resi);
		}else if (vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_PCP)) {
			logger.info("--PCP Book--");
//			BookResponsePCP respon = pcp.getPayment(vendorProp.getUrl(), pay,vendorProp.getClientCode());
//			if(!respon.getStatus()) {
//				logger.info("Failed from vendor PCP :"+respon.getMsg());
//				throw new Exception("Failed By vendor: " + respon.getMsg());
//			}
//			stt = respon.getAwbNo().substring(12);
			pay.setStatus(PaymentEnum.ACCEPT_IN_WAREHOUSE.getCode());
		}else if (vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_TEBA)) {
			logger.info("--Teba Book--");
			pay.setStatus(PaymentEnum.ACCEPT_IN_WAREHOUSE.getCode());
			stt = pay.getBookingCode();
		}else if(vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_DAKOTA) && stt.equals("-")) {
			BookResponseJet respon = dakota.getPayment(vendorProp.getClientCode(), stt, pay.getProductSwCode().getOperatorSw(),
					vendorProp.getUrl(), pay);
			if(!respon.getRc().equals(ResponseStatus.OK.value())) {
				logger.info("Failed from vendor Dakota RC:" + respon.getRc());
//				pay.setStatus(PaymentEnum.REQUEST.getCode());
				throw new Exception("Failed By vendor: " + respon.getDescription());
			}

			stt = respon.getNoResi();
			pay.setResi(respon.getUrlResi());
		}else if (vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_POS)) {
			BookResponseJet respon = new BookResponseJet();
			//dev
//			respon.setRc(ResponseStatus.OK.value());
//			respon.setNoResi("123123213");
			respon = pos.getPayment(vendorProp.getClientCode(), stt, pay.getProductSwCode().getOperatorSw(),vendorProp.getUrl(), pay, office, user);
			if(!respon.getRc().equals(ResponseStatus.OK.value())) {
				logger.info("Failed from vendor POS RC:" + respon.getRc());
//				pay.setStatus(PaymentEnum.REQUEST.getCode());
				throw new Exception("Failed By vendor: " + respon.getDescription());
			}
			stt = respon.getNoResi();
			resi = "api/resi/pos?bookingcode="+pay.getBookingCode()
			+ "&userid=" + pay.getUserId().getUserId()+"&officerid="+user.getUserId();
			pay.setResi(resi);
			/*pay.setResi(respon.getUrlResi()
					.concat("&userid="+pay.getUserId().getUserId())
					.concat("&officerid="+user.getUserId())); */
			pay.setDatarekon(respon.getDescription());
			pay.setPrice(new BigDecimal(respon.getFee()));
			pay.setHtnbPos(new BigDecimal(respon.getInsurance()));
		}else if(vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_WAHANA) && stt.equals("-")) {
			BookResponseJet respon = wahana.getPayment(vendorProp.getClientCode(), stt, pay.getProductSwCode().getOperatorSw(), vendorProp.getUrl(), pay, office);
			if(!respon.getRc().equals(ResponseStatus.OK.value())) {
				logger.info("Failed from vendor Wahana RC:" + respon.getRc());
				throw new Exception("Book Failed : " + "Failed By vendor: " + respon.getDescription());
			}

			stt = respon.getNoResi();
			pay.setDatarekon(respon.getRoutingCode());
			resi = "api/resi/wahana?bookingcode="+pay.getBookingCode()
					+ "&userid=" + pay.getUserId().getUserId();
			pay.setResi(resi);
		}else if(vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_JNE) && stt.equals("-")) {
			BookResponseJNE respon = jne.getPayment(vendorProp.getClientCode(), stt, pay.getProductSwCode().getOperatorSw(), vendorProp.getUrl(), pay, office);
			if(!respon.getRc().equals(STATUS_SUCCESS)) {
				logger.info("Failed from vendor JNE RC:" + respon.getRc());
				throw new Exception("Book Failed : " + "Failed By vendor: " + respon.getDescription());
			}
			stt=respon.getNoResi();
		}else if(vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_INDAH)) {
			pay.setStt(sttE.getStt());
			BookResponseIndah respon = indah.getPayment(vendorProp.getUrl(), pay,vendorProp);
			if(!respon.getCode().equals(STATUS_SUCCESS)) {
				logger.info("Failed from vendor Indah Cargo Code :"+respon.getCode());
				throw new Exception("Book Failed : " + "Failed By vendor: " + respon.getMessage());
			}
			if(respon.getResult().size() > 0) stt=respon.getResult().get(0).getNoResi();
			resi = "api/resi/indah?bookingcode="+pay.getBookingCode() 
					+ "&userid=" + pay.getUserId().getUserId();
			pay.setResi(resi);
		}else if(vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_BUKASEND)) {
			BookResponseBukaSend respon = bukaSend.getPayment(pay, vendorProp.getUrl(), vendorProp.getClientCode());
			if(!respon.getRc().equals(STATUS_SUCCESS)) {
				logger.info("Failed from vendor Buka Send :"+respon.getRc());
				throw new Exception("Book Failed : " + "Failed By vendor: " + respon.getDescription());
			}
			stt = respon.getIdBukaSend()+"|"+respon.getResiNo();
			
			
		}
		pay.setStt(stt);
		if(vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_LP)
				|| vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_SICEPAT)
				|| vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_TIKI)
				|| vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_PCP)
				|| vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_INDAH)) {
			if(
//					!pay.getProductSwCode().getProductSwCode().equals((PRODUCT_CODE_REGPACK_LP.longValue())) && 
					sttE != null) {
				sttE.setFlag(FLAG);
				tSttVendorRepo.save(sttE);
			}
			
		}
		pay.setStatus(pay.getStatus());
		if(pay.getStatus().equals(PaymentEnum.CANCEL_BY_WAREHOUSE.getCode())) {
			Double bal = pay.getUserId().getBalance().doubleValue() -
					pay.getAmount().doubleValue();
			pay.getUserId().setBalance(new BigDecimal(bal));
			mUserRepo.save(pay.getUserId());
		}
		log.info("Save Payment");
//		log.info(Common.json2String(pay));
		tPaymentRepo.saveAndFlush(pay);
	}

	private Boolean isPickupVendor(MOfficeEntity office, MVendorPropEntity vendorProp,Integer switcherCode) {
		Boolean isCount;
		String officeCode = office.getOfficeCode();
		if(office.getUnitType().equals("2")) {
			officeCode = office.getParentOffice();
		}
		isCount = outgoingListRepo.checkCount(officeCode, vendorProp.getPickupCount().longValue(), LocalDate.now(),switcherCode);		
		return isCount;
	}
	
	@Transactional
	public SaveResponse saveWarehouseVerification (WarehouseVerificationReq request,String userNameApprove) {
		String uri = "";
		MUserEntity user = mUserRepo.getOne(userNameApprove);
		TPickupDetailEntity pickupDetail = tPickupDetailRepo.findFirstByBookIdBookingCode(request.getBookingCode());
		TPaymentEntity payment = tPaymentRepo.findByBookingCodeIgnoreCaseContaining(request.getBookingCode());
		TPaymentEntity historyPayment = paymentService.createOldPayment(payment);
		TWarehouseReceiveDetailEntity warehouse = tDetailRepo.findFirstByBookIdAndStatus(request.getBookingCode(), WarehouseEnum.RECEIVE_IN_WAREHOUSE.getValue());
		try {
			boolean isCounter = false;
			boolean isQrcodeExt = false;
			if(request.getExtraCharge().compareTo(BigDecimal.ZERO) >= 0) {
				payment.setExtraCharge(request.getExtraCharge());
			}
			warehouse.setApprovalRejectAt(LocalDateTime.now());
			warehouse.setApprovalRejectBy(userNameApprove);
			isCounter = outgoingCounterDetailRepo.existByBookingCode(payment);
			if(isCounter) {
				if(request.getQrcodeExt() == null) {
					throw new NotFoundException("Qrcode External Tidak terisi");
				}
				isQrcodeExt = tPaymentRepo.existByQRCodeExt(bookCounterService.replaceRegexQrcodeExt(request.getQrcodeExt()));
				if(isQrcodeExt) {
					throw new InternalServerException("Qrcode External Duplikat");
				}
				if(depositBookService.checkQrCodeExt(bookCounterService.replaceRegexQrcodeExt(request.getQrcodeExt()))) {
					throw new InternalServerException("QrCode : "+request.getQrcodeExt() +" sudah digunakan !");
				}
				payment.setQrcodeExt(bookCounterService.replaceRegexQrcodeExt(request.getQrcodeExt()));
			}
			if(request.getStatus() == PickupEnum.ACCEPT_IN_WAREHOUSE.getValue()) {
				payment.setStatus(PaymentEnum.ACCEPT_IN_WAREHOUSE.getCode());
				warehouse.setStatus(WarehouseEnum.APPROVE.getCode());
				if(!isCounter)	{
					acceptInWarehouse(payment, user);
				}else {
					setIsCount(false);
				}
				
				payment.setGrossWeight(historyPayment.getGrossWeight());
				if(getIsCount()) {
					payment.setStatus(PaymentEnum.ACCEPT_WITHOUT_RESI.getCode());
				}
				if(payment.getResi().equals("-"))
					uri = "api/resi/kahago?bookingcode="
							+ payment.getBookingCode()+
							"&userid="+ payment.getUserId().getUserId();
				else
					uri=payment.getResi();
				List<THistoryBookEntity> lHistoryBook = tHistoryBookRepo.findByBookingCode(payment.getBookingCode());
				lHistoryBook.forEach(x->x.setStt(payment.getStt()));
				tHistoryBookRepo.saveAll(lHistoryBook);
				this.historyTransactionService.createHistory(historyPayment, payment, userNameApprove);
				tPaymentRepo.save(payment);
				warehouse.setBookId(payment);
			}else if(request.getStatus()==PickupEnum.HOLD_BY_WAREHOUSE.getValue()) {

//				if(request.getTotalPackKg())
				int countGros=0,countVolume=0;
				double extraCharge=0;
				double priceInsurance=0;
				double weightPack=0;
				payment.setStatus(PaymentEnum.HOLD_BY_WAREHOUSE.getCode());
				Map<String, Double> weights = bookService.mapAllWeights(request.getIsPack(),request.getDetail() , payment.getProductSwCode().getPembulatanVolume());
				int seq = 0;
				List<TBookEntity> tbookOld = historyTransactionService.createoldTbook(historyPayment.getTbooks());
				
				for(TBookEntity tbook : payment.getTbooks()) {
					tbook.setBookingCode(payment.getBookingCode());
					tbook.setLength(request.getDetail().get(seq).getLength().toString());
					tbook.setGrossWeight(request.getDetail().get(seq).getGrossWeight().toString());
					tbook.setHeight(request.getDetail().get(seq).getHeight().toString());
					tbook.setVolWeight(request.getDetail().get(seq).getVolume().toString());
					tbook.setWidth(request.getDetail().get(seq).getWidth().toString());
					countGros+=request.getDetail().get(seq).getGrossWeight().intValue();
					countVolume+=request.getDetail().get(seq).getVolume().intValue();
					seq+=1;
				}
				TAreaEntity area=areaRepo.findTopByAreaIdKecamatanEntityAreaDetailIdAndProductSwCodeProductSwCode(payment.getIdPostalCode().getKecamatanEntity().getAreaDetailId(),payment.getProductSwCode().getProductSwCode());
				Integer moreWeght=0;
				if(countGros>=countVolume){
					moreWeght=countGros;

				}else if(countGros<=countVolume){
					moreWeght=countVolume;
				}
				if(moreWeght<area.getMinimumKg()){
					throw new InternalServerException("Lah Kok Iki Wis benner langsung approve ae cik ngapain di hold barang vendor "+area.getProductSwCode().getDisplayName()+" "+area.getVendor().getDisplayName()+" Minimum Kilone iku "+area.getMinimumKg()+" Gak Oleh Hold!!!");
				}
				if(request.getIsPack()) {
					extraCharge = bookService.calculatesExtraCharge(request.getIsPack(),HARGA_PACKING,request.getDetail().size());
					weightPack = weights.get("packing");
					logger.info("Hasil Extra Charge : "+extraCharge);
					logger.info("Hasil weight Pack :"+weightPack);
					
					if(request.getExtraCharge().compareTo(BigDecimal.valueOf(extraCharge))==0 && request.getTotalPackKg().equals(weightPack)) {
						payment.setExtraCharge(request.getExtraCharge());
						payment.setTotalPackKg(weightPack);
					}else throw new NotFoundException("Nilai extra Charge tidak sesuai !: "+extraCharge+"|| Total packing Kg tidak sesuai !:"+weightPack);
				}else {
					payment.setExtraCharge(BigDecimal.ZERO);
					payment.setTotalPackKg(Double.valueOf("0"));
				}
				if(request.getIsInsurance()) {
					priceInsurance = bookService.calculatesPriceOfInsurance(mGoodsRepo.findById(payment.getGoodsId().longValue()).get().getInsuranceValue().doubleValue(),request.getPriceGoods().doubleValue());
					priceInsurance = Math.ceil(priceInsurance);
					logger.info("Hasil Insurance : "+priceInsurance);
					payment.setInsurance(new BigDecimal(priceInsurance));
//					if(request.getInsurance().compareTo(BigDecimal.valueOf(priceInsurance))==0) {
//						payment.setInsurance(request.getInsurance());
//					}else throw new NotFoundException("Nilai Insurance tidak sesuai !: "+priceInsurance);	
				}else {
					 payment.setInsurance(BigDecimal.ZERO);
				}
				double price = bookService.calculatesPrice((Double) weights.get("betterWeight") + payment.getTotalPackKg(), payment.getPriceKg().doubleValue());
				BigDecimal amountTot = BigDecimal.valueOf(price).add(payment.getExtraCharge().add(payment.getInsurance().add(payment.getShippingSurcharge())));
				if(payment.getDiscountValue()==null) {
					payment.setDiscountValue(BigDecimal.ZERO);
				}
				if(request.getAmount().equals(amountTot)) {
					payment.setAmount(request.getAmount().subtract(payment.getDiscountValue()));
				}else {
					payment.setAmount(amountTot.subtract(payment.getDiscountValue()));
				}
				payment.setPriceGoods(request.getPriceGoods());
				payment.setGrossWeight(Long.valueOf(countGros));
				payment.setVolume(Long.valueOf(countVolume));
				warehouse.setStatus(WarehouseEnum.HOLD_WAREHOUSE.getCode());
				historyTransactionService.createHistory(historyPayment, payment, userNameApprove);
				historyTransactionService.createHistoryBook(tbookOld, payment.getTbooks(), payment);
				roolBackPayment(payment, historyPayment);
			}else {
				throw new NotFoundException("Status tidak Sesuai!");
			}
			if(pickupDetail != null) {
				pickupDetail.setStatus(request.getStatus());
				tPickupDetailRepo.save(pickupDetail);
			}
			warehouse.setBookId(payment);
			tDetailRepo.save(warehouse);
		}catch (NotFoundException ex) {
			// TODO: handle exception
			throw new NotFoundException(ex.getMessage());
		}catch (Exception e) {
			// TODO: handle exception
			payment.setStatus(PaymentEnum.RECEIVE_IN_WAREHOUSE.getCode());
			warehouse.setStatus(WarehouseEnum.RECEIVE_IN_WAREHOUSE.getCode());
			paymentService.save(payment);
			tDetailRepo.save(warehouse);
			logger.error("Error Verifikasi Booking :"+e.getMessage());
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			throw new InternalServerException(e.getMessage());
		}
		SaveResponse saveResponse = SaveResponse.builder()
				.saveInformation("Approve By WareHouse")
				.saveStatus(1)
				.linkResi(uri)
				.build();
		if(request.getStatus().equals(PickupEnum.HOLD_BY_WAREHOUSE.getValue())) {
			saveResponse=SaveResponse.builder()
					.saveInformation("Hold By WareHouse")
					.saveStatus(2)
					.build();
		}
		return saveResponse;
	}
	public List<SurchargeDetailResponse> getListSurcharge(Integer productSwCode){
		List<SurchargeDetailResponse> result = new ArrayList<>();
		List<TProductSurchargeEntity> lSurcharge = tProductSurchargeRepo.findAllByProductSwCodeAndStatus(productSwCode,true);
		/*SurchargeDetailResponse sc = new SurchargeDetailResponse();
		sc.setSeq(1);
		sc.setStart("50");
		sc.setTo("75");
		sc.setPersen(0.5);
		result.add(sc);
		SurchargeDetailResponse sc1 = new SurchargeDetailResponse();
		sc1.setSeq(2);
		sc1.setStart("76");
		sc1.setTo("100");
		sc1.setPersen(1.0);
		result.add(sc1);
		SurchargeDetailResponse sc2 = new SurchargeDetailResponse();
		sc2.setSeq(3);
		sc2.setStart("101");
		sc2.setTo("200");
		sc2.setPersen(1.5);
		result.add(sc2);
		SurchargeDetailResponse sc3 = new SurchargeDetailResponse();
		sc3.setSeq(4);
		sc3.setStart("200");
		sc3.setTo("-");
		sc3.setPersen(2.0);
		result.add(sc3);*/
		if(lSurcharge != null) {
			for(TProductSurchargeEntity se:lSurcharge) {
				SurchargeDetailResponse surRes = new SurchargeDetailResponse();
				surRes.setId(se.getId());
				surRes.setPersen(se.getPercent());
				surRes.setStart(se.getStartKg().toString());
				surRes.setTo(se.getToKg()==null?"":se.getToKg().toString());
				result.add(surRes);
			}
		}
		
		return result;
		
	}
	
	private void roolBackPayment(TPaymentEntity newPayment,TPaymentEntity oldPayment) {
		newPayment.setPrice(oldPayment.getPrice());
		newPayment.setPriceKg(oldPayment.getPriceKg());
		newPayment.setTbooks(oldPayment.getTbooks());
		newPayment.setAmount(oldPayment.getAmount());
		newPayment.setGrossWeight(oldPayment.getGrossWeight());
		newPayment.setVolume(oldPayment.getVolume());
		newPayment.setBookingCode(oldPayment.getBookingCode());
		newPayment.setInsurance(oldPayment.getInsurance());
		newPayment.setExtraCharge(oldPayment.getExtraCharge());
		newPayment.setPrice(oldPayment.getPrice());
		newPayment.setAmount(oldPayment.getAmount());
		newPayment.setPriceKg(oldPayment.getPriceKg());
		newPayment.setGrossWeight(oldPayment.getGrossWeight());
		newPayment.setVolume(oldPayment.getVolume());
		newPayment.setTbooks(oldPayment.getTbooks());
	}
	
	@Transactional
	public SaveResponse approveCounter(String bookingCode, Boolean status, String userAdmin) {
		TPaymentEntity payment = paymentService.getBookAndStatusAndUserId(bookingCode, PaymentEnum.RECEIVE_IN_COUNTER.getCode(), userAdmin);
		MUserEntity user = mUserRepo.getMUserEntitiesBy(userAdmin);
		TPaymentEntity oldPayment = paymentService.createOldPayment(payment);
		String uri="";
		try {
			if(payment.getStatus().equals(PaymentEnum.RECEIVE_IN_COUNTER.getValue())) {
				if(status) {
					acceptInWarehouse(payment, payment.getUserId());
					payment.setStatus(PaymentEnum.APPROVE_BY_COUNTER.getValue());
					paymentService.save(payment);
					if(payment.getResi().equals("-"))
						uri = "api/resi/kahago?bookingcode="
								+ payment.getBookingCode()+
								"&userid="+ payment.getUserId().getUserId();
					else
						uri=payment.getResi();
					historyTransactionService.createHistory(oldPayment, payment, user.getUserId());
				}else {
					rejectPaymentByCounter(payment, user);
					payment.setStatus(PaymentEnum.CANCEL_BY_USER.getValue());
					paymentService.save(payment);
					historyTransactionService.createHistory(oldPayment, payment, user.getUserId());
				}
			}else {
				throw new NotFoundException("Pesanan tidak Ditemukan !");
			}
			
		}catch (NotFoundException ex) {
			// TODO: handle exception
			logger.error(ex.getMessage());
			ex.printStackTrace();
			throw new NotFoundException(ex.getMessage());
		}
		catch (Exception e) {
			// TODO: handle exception
			payment.setStatus(PaymentEnum.RECEIVE_IN_COUNTER.getValue());
			paymentService.save(payment);
			logger.error("Error Verifikasi Booking :"+e.getMessage());
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			throw new InternalServerException(e.getMessage());
		}
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Berhasil Approve/Reject By Counter")
				.linkResi(uri)
				.build();
	}
	@Transactional
	void rejectPaymentByCounter(TPaymentEntity pay, MUserEntity user) {
		TPaymentEntity payment = paymentService.get(pay.getBookingCode());
		TFeeTrxEntity feeTrx = tFeeTrxRepo.findByIdMUserCategoryAndFee(user.getUserCategory().getSeqid(), 3);
		if(feeTrx != null) {
			TFeeReferenceEntity tFeeRef = new TFeeReferenceEntity();
			tFeeRef.setTrxNo(payment.getBookingCode());
			tFeeRef.setTglTrx(payment.getTrxServer());
			tFeeRef.setUserId(user.getUserId());
			tFeeRef.setNominal(payment.getAmount().intValue());
			tFeeRef.setFeeNominal(new BigDecimal(payment.getAmount().doubleValue() * (Double.valueOf(feeTrx.getFee()) / 100)).intValue());
			tFeeRef.setFeePersen(feeTrx.getFee());
			tFeeRef.setLastUpdate(new Timestamp(Instant.now().toEpochMilli()));
			tFeeRef.setLastUser(user.getUserId());
			tFeeRef.setUnitFee(feeTrx);
			tFeeReferenceRepo.save(tFeeRef);
		}
		payment.setStt("-");
		double amt = payment.getAmount().doubleValue() - payment.getInsufficientFund().doubleValue();
		TMutasiEntity mutasiSaldo = tMutasiRepo.findFirstByUserIdOrderByCounterMutasiDesc(user);
		Double saldo = user.getBalance().doubleValue() + (amt * -1);
		if (mutasiSaldo != null) {
			saldo = tMutasiRepo.findFirstByUserIdOrderByCounterMutasiDesc(user).getSaldo().doubleValue()
					+ (amt * -1);
		}
		TMutasiEntity mutasinew = new TMutasiEntity();
		mutasinew.setTrxNo("RFN" + payment.getBookingCode());
		mutasinew.setAmount(new BigDecimal(amt * -1));
		mutasinew.setDescr("Refund Transaksi " + payment.getBookingCode());
		mutasinew.setTrxDate(LocalDate.now());
		mutasinew.setTrxTime(LocalTime.now());
		mutasinew.setSaldo(new BigDecimal(saldo));
		mutasinew.setUpdateBy(user.getUserId());
		mutasinew.setUserId(payment.getUserId());
		mutasinew.setTrxType(MutasiEnum.REFUND.getCode());
		user.setBalance(new BigDecimal(saldo));
		if (user.getDepositType().equals("1")) {
			TCreditEntity cr = tCreditRepo.findByUserIdAndFlagAndTglOrderBySeqDesc(user.getUserId(),"0",pay.getTrxDate()).get();
			if(cr==null) {
				approvalRejectWarehouseService.insertTCredit(payment, user, new BigDecimal("-1"), pay.getAmount(),false);
			}
			double hutang = cr.getNominal().doubleValue() - amt;
			cr.setNominal(new BigDecimal(hutang));
			if(cr.getNominal().equals(BigDecimal.ZERO)) {
				cr.setFlag("1");
			}
			tCreditRepo.save(cr);
		}
		mUserRepo.save(user);
		tMutasiRepo.save(mutasinew);
		tPaymentRepo.save(payment);
	}
	
}
