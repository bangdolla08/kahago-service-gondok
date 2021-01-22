package com.kahago.kahagoservice.service;
/**
 * @author Ibnu Wasis
 */

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.kahago.kahagoservice.model.response.BookDataResponse;
import com.kahago.kahagoservice.util.Common;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.component.FirebaseComponent;
import com.kahago.kahagoservice.entity.MGroupListEntity;
import com.kahago.kahagoservice.entity.MOfficeEntity;
import com.kahago.kahagoservice.entity.MUserEntity;
import com.kahago.kahagoservice.entity.TBookDetailHistoryEntity;
import com.kahago.kahagoservice.entity.TBookEntity;
import com.kahago.kahagoservice.entity.TCreditEntity;
import com.kahago.kahagoservice.entity.TFeeReferenceEntity;
import com.kahago.kahagoservice.entity.TFeeTrxEntity;
import com.kahago.kahagoservice.entity.TMutasiEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.entity.TPaymentHistoryEntity;
import com.kahago.kahagoservice.entity.TWarehouseReceiveDetailEntity;
import com.kahago.kahagoservice.enummodel.DepositTypeEnum;
import com.kahago.kahagoservice.enummodel.MutasiEnum;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.enummodel.WarehouseEnum;
import com.kahago.kahagoservice.exception.InternalServerException;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.dto.PaymentDto;
import com.kahago.kahagoservice.model.request.ApprovalRejectWarehouseReq;
import com.kahago.kahagoservice.model.response.ApprovalRejectWarehouseResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.model.response.UrlResiResponse;
import com.kahago.kahagoservice.repository.MGroupListRepo;
import com.kahago.kahagoservice.repository.MOfficeRepo;
import com.kahago.kahagoservice.repository.MUserRepo;
import com.kahago.kahagoservice.repository.TBookDetailHistoryRepo;
import com.kahago.kahagoservice.repository.TCreditRepo;
import com.kahago.kahagoservice.repository.TFeeReferenceRepo;
import com.kahago.kahagoservice.repository.TFeeTrxRepo;
import com.kahago.kahagoservice.repository.TMutasiRepo;
import com.kahago.kahagoservice.repository.TPaymentHistoryRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.repository.TWarehouseReceiveDetailRepo;
import static com.kahago.kahagoservice.util.ImageConstant.*;


@Service @Slf4j
public class ApprovalRejectWarehouseService {
	@Autowired
	private TWarehouseReceiveDetailRepo tWarehouseReceiveDetailRepo;
	@Autowired
	private TPaymentHistoryRepo tPaymentHistoryRepo;
	@Autowired
	private TPaymentRepo tPaymentRepo;
	@Autowired
	private MUserRepo mUserRepo;
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private TBookDetailHistoryRepo tBookDetailHistoryRepo;
	@Autowired
	private WarehouseVerificationService verificationService;
	@Autowired
	private HistoryTransactionService historyTransactionService;
	@Autowired
	private TMutasiRepo tMutasiRepo;
	@Autowired
	private TCreditRepo tCreditRepo;
	@Autowired
	private MOfficeRepo mOfficeRepo;
	@Autowired
	private TFeeTrxRepo tfeeTrxRepo;
	@Autowired
	private TFeeReferenceRepo tFeeReferenceRepo;
	@Autowired
	private FirebaseComponent firebase;
	@Autowired
	private BookService bookService;
	@Autowired
	private TBookDetailHistoryRepo bookDetailHist;
	@Autowired
	private MGroupListRepo mGroupListRepo;
	
	@Value("${url.cetak}")
	private String urlcetak;
	private String branchName="";
	
	private static final String FLAG = "0";
	private static final Integer PRODUCT_CODE_REGPACK_LP =1;
	public Page<ApprovalRejectWarehouseResponse> getListWarehouse(ApprovalRejectWarehouseReq request,Pageable pageable){
		List<Integer> listStatus = new ArrayList<>(Arrays.asList(PaymentEnum.ACCEPT_IN_WAREHOUSE.getCode(),
																 PaymentEnum.HOLD_BY_WAREHOUSE.getCode(),
																 PaymentEnum.APPROVE_BY_COUNTER.getCode(),
																 PaymentEnum.OUTGOING_BY_COUNTER.getCode(),
																 PaymentEnum.BAGGING_BY_COUNTER.getCode())); 
		//Page<TWarehouseReceiveDetailEntity> lwarehouse = tWarehouseReceiveDetailRepo.findAllByFilter( request.getBookId(), request.getUserId(), request.getVendorId(), listStatus, request.getOfficeCode(),pageable);
		Page<TPaymentEntity> lPayment = tPaymentRepo.getPaymentByListStatusAndVendorAndUserId(request.getBookId(), request.getUserId(), listStatus, request.getOfficeCode(), request.getVendorId(), pageable);
		MOfficeEntity office = mOfficeRepo.findAllByOfficeCode(request.getOfficeCode());
		if(office!=null) {
			branchName = office.getName();
		}
		return new PageImpl<>(lPayment.getContent().stream().map(this::toDto).collect(Collectors.toList()),
					lPayment.getPageable(), 
					lPayment.getTotalElements());
	}

	public BookDataResponse getBookData(String qrCode,String officeCode,String userAdmin){
		MUserEntity user = mUserRepo.getMUserEntitiesBy(userAdmin);
		Integer menuIdReject = 100;
		List<Integer> listStatus = new ArrayList<>(Arrays.asList(PaymentEnum.ACCEPT_IN_WAREHOUSE.getCode(),
																 PaymentEnum.HOLD_BY_WAREHOUSE.getCode(),
																 PaymentEnum.APPROVE_BY_COUNTER.getCode(),
																 PaymentEnum.OUTGOING_BY_COUNTER.getCode(),
																 PaymentEnum.BAGGING_BY_COUNTER.getCode())); 
		List<Integer> listStatusWarehouse = new ArrayList<>(Arrays.asList(WarehouseEnum.RECEIVE_IN_WAREHOUSE.getCode(),
																		 WarehouseEnum.APPROVE.getCode(),
																		 WarehouseEnum.HOLD_WAREHOUSE.getCode()));
		/*TWarehouseReceiveDetailEntity warehouseReceiveDetailEntity=tWarehouseReceiveDetailRepo
				.findByStatusAndBookIdOrQrcodeOrQrcodeext(listStatusWarehouse,qrCode,officeCode,listStatus);*/
		TPaymentEntity payment = tPaymentRepo.getPaymentByListStatusAndVendorAndUserId(qrCode, null, listStatus, officeCode, null);
		
		if(!payment.getStatus().equals(PaymentEnum.HOLD_BY_WAREHOUSE.getCode())) {
			MGroupListEntity group = mGroupListRepo.findByMenuIdMenuIdAndUserCategory(menuIdReject, user.getUserCategory().getSeqid());
			if(group == null) {
				throw new NotFoundException("Gagal, Pesanan Tidak ditemukan !");
			}
		}
		BookDataResponse bookDataResponse=paymentService.toBookDataResponse(payment);
		bookDataResponse.setApprovalRejectWarehouseResponse(toDto(payment));
		return bookDataResponse;
	}
	
	private ApprovalRejectWarehouseResponse toDto(TPaymentEntity entity) {
		TPaymentHistoryEntity paymentHistory = tPaymentHistoryRepo.findFirstByBookingCodeAndLastStatusOrderByTrxServerDesc(entity, entity.getStatus());
		List<String> dimensi = new ArrayList<String>();
		List<String> dimensiHis = new ArrayList<String>();
		TWarehouseReceiveDetailEntity warehouseDtl = tWarehouseReceiveDetailRepo.findByBookId(entity);
		for(TBookEntity tbook : entity.getTbooks()) {
			String dm = tbook.getLength() + "x" + tbook.getWidth() + "x" + tbook.getHeight();
			dimensi.add(dm);
		}
		List<TBookDetailHistoryEntity> tbookHis = new ArrayList<>();
		if(paymentHistory != null) {
			tbookHis = bookDetailHist.findByPaymentHistory(paymentHistory);
		}
		dimensiHis = tbookHis.stream().map(this::toDimensiHis).collect(Collectors.toList());
		String image = entity.getProductSwCode().getSwitcherEntity().getImg().substring(entity.getProductSwCode().getSwitcherEntity().getImg().lastIndexOf("/")+1);
		return ApprovalRejectWarehouseResponse.builder()
				.idWarehouseDetail(warehouseDtl == null?0:warehouseDtl.getIdWarehouseReceiveDetail())
				.bookId(entity.getBookingCode())
				.userId(entity.getUserId().getUserId())
				.destination(entity.getIdPostalCode().getKecamatanEntity().getKecamatan()+","+entity.getIdPostalCode().getKecamatanEntity().getKotaEntity().getName())
				.dimensi(dimensi)
				.dimensiHistory(dimensiHis)
				.volumeWeight(paymentHistory == null?"-":paymentHistory.getVolume().toString()+" Kg")
				.weight(paymentHistory ==null?"-":paymentHistory.getGrossWeight().toString()+" Kg")
				.newVolumeWeight(paymentHistory ==null?"-":paymentHistory.getLastVolume().toString()+" Kg")
				.newWeight(paymentHistory==null?"-":paymentHistory.getLastGrossWeight().toString()+" Kg")
				.vendorName(entity.getProductSwCode().getSwitcherEntity().getName())
				.productName(entity.getProductSwCode().getName())
				.status(entity.getStatus())
				.statusDesc(warehouseDtl==null?"-":WarehouseEnum.getWarehouseEnum(warehouseDtl.getStatus()).getString())
				.urlImage(PREFIX_PATH_IMAGE_VENDOR + image)
				.volume(paymentHistory==null?"-":paymentHistory.getVolume().toString()+" Kg")
				.newVolume(paymentHistory==null?"-":paymentHistory.getLastVolume().toString()+" Kg")
				.branch(branchName)
				.priceDifference(paymentHistory==null?0:paymentHistory.getLastAmount().subtract(paymentHistory.getAmount()).longValue())
				.build();
	}
//	@Transactional(rollbackOn=Exception.class)
	public Response<SaveResponse> doApproveReject(Integer idWarehouse,Boolean status,String userAdmin,String bookId){		
		TWarehouseReceiveDetailEntity entity = tWarehouseReceiveDetailRepo.findByIdWarehouseReceiveDetail(idWarehouse);
		TPaymentEntity payment = new TPaymentEntity();
		if(entity == null) {
			payment = tPaymentRepo.findByBookingCodeIgnoreCaseContaining(bookId);
			if(payment == null) {
				throw new NotFoundException("Data Tidak Ditemukan");
			}
		}else {
			payment = tPaymentRepo.findByBookingCodeIgnoreCaseContaining(entity.getBookId().getBookingCode());
		}
		MUserEntity user = payment.getUserId();
		TPaymentHistoryEntity paymentHistory = tPaymentHistoryRepo.findFirstByBookingCodeAndLastStatusOrderByLastUpdateDesc(payment, payment.getStatus());
		if(paymentHistory == null) {
			throw new NotFoundException("History Pesanan Tidak Ditemukan");
		}
		BigDecimal insufficient = paymentHistory.getAmount().subtract(paymentHistory.getLastAmount());
		TMutasiEntity mutasi = new TMutasiEntity();
		List<TCreditEntity> lcredit = new ArrayList<>();
		TCreditEntity credit = new TCreditEntity();
		mutasi.setUserId(user);
		mutasi.setProductSwCode(payment.getProductSwCode().getProductSwCode().toString());
		List<TPaymentHistoryEntity> lPayHistory = tPaymentHistoryRepo.findHistoryByBookingCodeLimit(payment.getBookingCode());
		if(insufficient.compareTo(BigDecimal.ZERO) < 0 && status && user.getDepositType().equals("0")) {
			if(insufficient.multiply(new BigDecimal("-1")).compareTo(user.getBalance().multiply(new BigDecimal("-1"))) > 0) {
				throw new NotFoundException("Saldo User Kurang, User hanya memiliki saldo : "+user.getBalance().multiply(new BigDecimal("-1")).toString());
			}
		}else if(insufficient.compareTo(BigDecimal.ZERO) < 0 && status && user.getDepositType().equals("1")) {
			lcredit = tCreditRepo.findByUserAndNominalGraterZero(user.getUserId(), "0");
			if(lcredit.size() > 0) {
				if(insufficient.multiply(new BigDecimal("-1")).compareTo(user.getBalance()) > 0 
						&& lcredit.get(0).getCreditDay().compareTo(user.getCreditDay()) >= 0) {
					throw new NotFoundException("Saldo User Kurang, User hanya memiliki saldo : "+user.getBalance().toString());
				}
			}
			
		}
		TPaymentEntity entityPaymentHistory=paymentService.createOldPayment(payment);
		BigDecimal saldo = BigDecimal.ZERO;
		String uri ="";
		Boolean isRefund = true;
		for(TPaymentHistoryEntity payHistory : lPayHistory) {
			if(payHistory.getIsRefund().equals(0)) {
				isRefund = false;
			}
		}
		try {
			if(status) {
				if(!payment.getStatus().equals(PaymentEnum.HOLD_BY_WAREHOUSE.getCode())) {
					throw new NotFoundException("Pesanan Tidak Dapat Diapprove Karena Status Pesanan tidak Hold By Warehouse !");
				}
				payment = getNewPayment(payment, paymentHistory);
				insufficient = insufficient.multiply(new BigDecimal(-1));
				if(insufficient.compareTo(BigDecimal.ZERO) <= 0) {
					mutasi.setDescr(MutasiEnum.EDIT_BOOK_REFUND.getKeterangan());
					mutasi.setTrxType(MutasiEnum.EDIT_BOOK_REFUND.getCode());
				}else {
					mutasi.setDescr(MutasiEnum.EDIT_BOOK_ADD.getKeterangan());
					mutasi.setTrxType(MutasiEnum.EDIT_BOOK_ADD.getCode());
				}
				verificationService.acceptInWarehouse(payment, user);
				saldo = user.getBalance().add(insufficient);
				entity.setStatus(WarehouseEnum.APPROVE.getCode());
				payment.setStatus(PaymentEnum.ACCEPT_IN_WAREHOUSE.getCode());
				if(verificationService.getIsCount()) {
					payment.setStatus(PaymentEnum.ACCEPT_WITHOUT_RESI.getCode());
				}
//				if(payment.getProductSwCode().getProductSwCode().equals(PRODUCT_CODE_REGPACK_LP.longValue())) {
//					payment.setStatus(PaymentEnum.ACCEPT_WITHOUT_RESI.getCode());
//				}
				mutasi.setAmount(insufficient);
				mutasi.setSaldo(saldo);
				mutasi.setTrxNo("ACCEPT "+payment.getBookingCode());
				mutasi.setTrxDate(LocalDate.now());
				mutasi.setTrxTime(LocalTime.now());
				user.setBalance(saldo);
				this.historyTransactionService.createHistory(entityPaymentHistory, payment, userAdmin);
				payment.setGrossWeight(paymentHistory.getLastGrossWeight());
				mUserRepo.save(user);
				tMutasiRepo.save(mutasi);
				tWarehouseReceiveDetailRepo.save(entity);
				payment=this.paymentService.get(payment.getBookingCode());
				List<TCreditEntity> lsitCre=tCreditRepo.findFirstByUserIdAndTglOrderBySeqDesc(user.getUserId(), payment.getTrxDate());
				if(user.getDepositType().equals("1")) {
					credit = tCreditRepo.findFirstByUserIdAndTglOrderBySeqDesc(user.getUserId(), payment.getTrxDate()).get(0);
					if (credit != null) {
						credit.setNominal(credit.getNominal().add(insufficient));
						if (credit.getNominal().equals(BigDecimal.ZERO)) credit.setFlag("1");
						tCreditRepo.save(credit);
					} else {
						insertTCredit(payment, user, new BigDecimal("-1"), insufficient, true);
					}
				}
				 if(payment.getResi().equals("-"))
                     uri = "api/resi/kahago?bookingcode="
                             + payment.getBookingCode()+
                             "&userid="+ payment.getUserId().getUserId();
                 else
                     uri=payment.getResi();
				
			}else {
				if(entity != null) {
					entity.setStatus(WarehouseEnum.CANCEL_WAREHOUSE.getCode());
				}
				payment = getOldPayment(payment, paymentHistory);
				payment.setStatus(PaymentEnum.CANCEL_BY_WAREHOUSE.getCode());
				//reject method
				if(isRefund) {
					rejectPayment(user, payment, entity);
				}else {
					rejectPaymentNotRefund(user, payment, entity);
				}
				
				//List<TCreditEntity> lsitCre=tCreditRepo.findFirstByUserIdAndTglOrderBySeqDesc(user.getUserId(), payment.getTrxDate());
//				credit = tCreditRepo.findFirstByUserIdAndTglOrderBySeqDesc(user.getUserId(), payment.getTrxDate()).get(0);
				/*if(credit != null) {
					credit.setNominal(credit.getNominal().subtract(payment.getAmount()));
					if(credit.getNominal().equals(BigDecimal.ZERO))credit.setFlag("0");
					tCreditRepo.save(credit);
				}*/
				this.historyTransactionService.createHistory(entityPaymentHistory, payment, userAdmin);
				tPaymentRepo.save(payment);

			}
		}catch (NotFoundException ex) {
			// TODO: handle exception
			throw ex;
		}catch (Exception e) {
			// TODO: handle exception
			payment.setStatus(PaymentEnum.HOLD_BY_WAREHOUSE.getCode());
			entity.setStatus(WarehouseEnum.HOLD_WAREHOUSE.getCode());
			tPaymentRepo.save(payment);
			tWarehouseReceiveDetailRepo.save(entity);
			log.error(e.toString());
			e.printStackTrace();
			throw new InternalServerException(e.getMessage());
		}
		SaveResponse saveResponse = SaveResponse.builder()
				.saveInformation("Approve By WareHouse")
				.saveStatus(1)
				.linkResi(uri)
				.build();
		if(!status) {
			 saveResponse = SaveResponse.builder()
						.saveInformation("Reject By WareHouse")
						.saveStatus(2)
						.build();
		}
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				saveResponse
				);
	}
	
	private TPaymentEntity getNewPayment(TPaymentEntity pay,TPaymentHistoryEntity payHistory) throws Exception{
		pay.setAmount(payHistory.getLastAmount());
		pay.setStatus(PaymentEnum.RECEIVE_IN_WAREHOUSE.getCode());
        pay.setExtraCharge(BigDecimal.valueOf(payHistory.getLastExtraCharge().doubleValue()));
        pay.setInsurance(BigDecimal.valueOf(payHistory.getLastInsurance().doubleValue()));
        pay.setPriceKg(payHistory.getLastPriceKg());
        pay.setGrossWeight(payHistory.getLastGrossWeight());
        pay.setVolume(payHistory.getLastVolume());
        pay.setPrice(payHistory.getLastPrice());
        List<TBookDetailHistoryEntity> lBook = tBookDetailHistoryRepo.findByPaymentHistory(payHistory);
        for(int index=0;index < pay.getTbooks().size();index++ ) {
        	TBookEntity tbook = pay.getTbooks().get(index);
        	tbook.setWidth(lBook.get(index).getLastWidth());
        	tbook.setVolWeight(lBook.get(index).getLastVolWeight());
        	tbook.setGrossWeight(lBook.get(index).getLastGrossWeight());
        	tbook.setHeight(lBook.get(index).getLastLength());
        	tbook.setHeight(lBook.get(index).getLastHeight());
        	pay.getTbooks().set(index, tbook);
        }
        return pay;
        
	}
	private TPaymentEntity getOldPayment(TPaymentEntity pay,TPaymentHistoryEntity payHistory) throws Exception{
		pay.setAmount(payHistory.getAmount());
		pay.setStatus(PaymentEnum.RECEIVE_IN_WAREHOUSE.getCode());
        pay.setExtraCharge(payHistory.getExtraCharge());
        pay.setInsurance(payHistory.getInsurance());
        pay.setPriceKg(payHistory.getPriceKg());
        pay.setGrossWeight(payHistory.getGrossWeight());
        pay.setVolume(payHistory.getVolume());
        pay.setPrice(payHistory.getPrice());
        List<TBookDetailHistoryEntity> lBook = tBookDetailHistoryRepo.findByPaymentHistory(payHistory);
        for(int index=0;index < pay.getTbooks().size();index++ ) {
        	TBookEntity tbook = pay.getTbooks().get(index);
        	tbook.setWidth(lBook.get(index).getWidth());
        	tbook.setVolWeight(lBook.get(index).getVolWeight());
        	tbook.setGrossWeight(lBook.get(index).getGrossWeight());
        	tbook.setHeight(lBook.get(index).getLength());
        	tbook.setHeight(lBook.get(index).getHeight());
        	pay.getTbooks().set(index, tbook);
        }
        return pay;
        
	}
	
	private void rejectPayment(MUserEntity user,TPaymentEntity pay,TWarehouseReceiveDetailEntity entity)throws JSONException {
		TPaymentEntity paymente = paymentService.get(pay.getBookingCode());
		if(!pay.getStatus().equals(PaymentEnum.ACCEPT_IN_WAREHOUSE.getCode())) {
			if (paymente.getStatus().equals(PaymentEnum.REQUEST.getCode())) {
				throw new NotFoundException("Status pesanan masih Request");
			}
			MOfficeEntity office = mOfficeRepo.findAllByOfficeCode(pay.getOfficeCode());
			paymente.setOfficeCode(office.getOfficeCode());
			//TFeeTrxEntity feeTrx = tfeeTrxRepo.findByIdMUserCategoryAndFee(user.getUserCategory().getSeqid(), 3);
			String ket = "";
			String title = "book_reject";
			//notif
			JSONObject data=makeJsonTrxNotif(paymente,user);
			/*if(feeTrx != null) {
				TFeeReferenceEntity tFeeRef = new TFeeReferenceEntity();
				tFeeRef.setTrxNo(paymente.getBookingCode());
				tFeeRef.setTglTrx(paymente.getTrxServer());
				tFeeRef.setUserId(user.getUserId());
				tFeeRef.setNominal(paymente.getAmount().intValue());
				tFeeRef.setFeeNominal(new BigDecimal(paymente.getAmount().doubleValue() * (Double.valueOf(feeTrx.getFee()) / 100)).intValue());
				tFeeRef.setFeePersen(feeTrx.getFee());
				tFeeRef.setLastUpdate(new Timestamp(Instant.now().toEpochMilli()));
				tFeeRef.setLastUser(user.getUserId());
				tFeeRef.setUnitFee(feeTrx);
				tFeeReferenceRepo.save(tFeeRef);
			}*/
			paymente.setStt("-");
			paymente.setTrxTime(paymente.getTrxTime());
			double amt = paymente.getAmount().doubleValue() - paymente.getInsufficientFund().doubleValue();
			TMutasiEntity mutasiSaldo = tMutasiRepo.findFirstByUserIdOrderByCounterMutasiDesc(user);
			Double saldo = user.getBalance().doubleValue() + (amt * -1);
			if (mutasiSaldo != null) {
				saldo = tMutasiRepo.findFirstByUserIdOrderByCounterMutasiDesc(user).getSaldo().doubleValue()
						+ (amt * -1);
			}
			TMutasiEntity mutasinew = new TMutasiEntity();
			mutasinew.setTrxNo("RFN" + paymente.getBookingCode());
			mutasinew.setAmount(new BigDecimal(amt * -1));
			mutasinew.setDescr("Refund Transaksi " + paymente.getBookingCode());
			mutasinew.setTrxDate(LocalDate.now());
			mutasinew.setTrxTime(LocalTime.now());
			mutasinew.setSaldo(new BigDecimal(saldo));
			mutasinew.setUpdateBy(user.getUserId());
			mutasinew.setUserId(paymente.getUserId());
			mutasinew.setTrxType(MutasiEnum.REFUND.getCode());
			user.setBalance(new BigDecimal(saldo));
			if (user.getDepositType().equals("1")) {
				TCreditEntity cr = tCreditRepo.findByUserIdAndFlagAndTglOrderBySeqDesc(user.getUserId(),"0",pay.getTrxDate()).get();
				if(cr==null) {
					insertTCredit(paymente, user, new BigDecimal("-1"), pay.getAmount(),false);
				}
				double hutang = cr.getNominal().doubleValue() - amt;
				cr.setNominal(new BigDecimal(hutang));
				if(cr.getNominal().equals(BigDecimal.ZERO)) {
					cr.setFlag("1");
				}
				tCreditRepo.save(cr);
			}
			data.put("status_trx", "0"); //0.failed, 1.Success
			data.put("tag", title);
			paymente.setStatus(PaymentEnum.CANCEL_BY_WAREHOUSE.getCode());
			mUserRepo.save(user);
			tMutasiRepo.save(mutasinew);
			tPaymentRepo.save(paymente);
			firebase.notif(data.getString("tittle"),data.getString("body"), data, title,user.getTokenNotif());
		}
	}
	private JSONObject makeJsonTrxNotif(TPaymentEntity payment, MUserEntity user) throws JSONException {
		JSONObject data = new JSONObject();
		data.put("idTrx", payment.getBookingCode());
		data.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy MMM dd HH:mm")));
		data.put("userid", user.getUserId());
		data.put("nominal", payment.getAmount());
		data.put("type_trx", "1"); //1. Book, 2. Deposit
		data.put("body", "Barang dibatalkan oleh Gudang");
		data.put("tittle", "Cancel By Warehouse"+" "+payment.getBookingCode());
		return data;
	}
	private String toDimensiHis(TBookDetailHistoryEntity detailBook) {
		return detailBook.getLength() + "x" + detailBook.getWidth() + "x" + detailBook.getHeight();
	}
	public void insertTCredit(TPaymentEntity pay, MUserEntity user, BigDecimal mat,BigDecimal amount,Boolean status) {
		if(user.getDepositType().equals(DepositTypeEnum.CREDIT.getValue())) {
			TCreditEntity credit = tCreditRepo.findByTglAndUserIdAndFlag(pay.getTrxDate(), user.getUserId(), FLAG);
			if(credit==null) {
				credit = tCreditRepo.findByUserIdAndFlag(user.getUserId(), FLAG).orElse(new  TCreditEntity().builder()
						.nominal(new BigDecimal("0")).build());
				if(credit.getNominal().doubleValue()>0) {
					credit = TCreditEntity.builder()
							.flag("0")
							.userId(user.getUserId())
							.nominal(new BigDecimal("0"))
							.tgl(LocalDate.now())
							.tglMulai(credit.getTglMulai())
							.tglSelesai(credit.getTglSelesai())
							.creditDay(String.valueOf(Integer.valueOf(bookService.checkValidateCredit(user))))
							.build();
				}else {
					credit = TCreditEntity.builder()
							.flag("0")
							.userId(user.getUserId())
							.nominal(new BigDecimal("0"))
							.tgl(LocalDate.now())
							.tglMulai(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
							.tglSelesai(LocalDate.now().plusDays(Integer.valueOf(user.getCreditDay())).format(DateTimeFormatter.ofPattern("yyyyMMdd")))
							.creditDay(user.getCreditDay())
							.build();
				}
			}
			if(!status) {
				amount = amount.multiply(new BigDecimal("-1"));
			}
			BigDecimal nominal = credit.getNominal().add(amount);
			credit.setNominal(nominal);
			tCreditRepo.save(credit);
		}
		
	}
	private void rejectPaymentNotRefund(MUserEntity user,TPaymentEntity paymente,TWarehouseReceiveDetailEntity entity) throws JSONException{
		if(!paymente.getStatus().equals(PaymentEnum.ACCEPT_IN_WAREHOUSE.getCode())) {
			if (paymente.getStatus().equals(PaymentEnum.REQUEST.getCode())) {
				throw new NotFoundException("Status pesanan masih Request");
			}
			MOfficeEntity office = mOfficeRepo.findAllByOfficeCode(paymente.getOfficeCode());
			paymente.setOfficeCode(office.getOfficeCode());
			String title = "book_reject";
			//notif
			JSONObject data=makeJsonTrxNotif(paymente,user);
			paymente.setStt("-");	
			data.put("status_trx", "0"); //0.failed, 1.Success
			data.put("tag", title);
			paymente.setStatus(PaymentEnum.CANCEL_BY_WAREHOUSE.getCode());
			tPaymentRepo.save(paymente);
			firebase.notif(title,PaymentEnum.CANCEL_BY_WAREHOUSE.getKeterangan(), data, title,user.getTokenNotif());
		}
	}
}
