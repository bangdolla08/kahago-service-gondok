package com.kahago.kahagoservice.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import com.kahago.kahagoservice.model.response.ApprovalReport;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;

import com.kahago.kahagoservice.component.FirebaseComponent;
import com.kahago.kahagoservice.entity.MUserEntity;
import com.kahago.kahagoservice.entity.TCreditEntity;
import com.kahago.kahagoservice.entity.TDepositEntity;
import com.kahago.kahagoservice.entity.TFeeReferenceEntity;
import com.kahago.kahagoservice.entity.TFeeTrxEntity;
import com.kahago.kahagoservice.entity.TMutasiEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.enummodel.ApprovalTopUpEnum;
import com.kahago.kahagoservice.enummodel.DepositEnum;
import com.kahago.kahagoservice.enummodel.DepositTypeEnum;
import com.kahago.kahagoservice.enummodel.MutasiEnum;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.enummodel.StatusPayEnum;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.request.ApprovalTopUpReq;
import com.kahago.kahagoservice.model.request.DepositRequest;
import com.kahago.kahagoservice.model.response.DepositResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.repository.MUserRepo;
import com.kahago.kahagoservice.repository.TCreditRepo;
import com.kahago.kahagoservice.repository.TDepositRepo;
import com.kahago.kahagoservice.repository.TFeeReferenceRepo;
import com.kahago.kahagoservice.repository.TFeeTrxRepo;
import com.kahago.kahagoservice.repository.TMutasiRepo;
import com.kahago.kahagoservice.util.DateTimeUtil;
import com.kahago.kahagoservice.util.EmailSender;

import lombok.Getter;
import lombok.SneakyThrows;

/**
 * @author Ibnu Wasis
 */
@Service
public class ApprovalTopupService {
	@Autowired
	private TDepositRepo depositRepo;
	@Autowired
	private TFeeTrxRepo tFeeTrxRepo;
	@Autowired
	private TMutasiRepo tMutasiRepo;
	@Autowired
	private MUserRepo userRepo;
	@Autowired
	private TFeeReferenceRepo feeReferenceRepo;
	@Autowired
	private FirebaseComponent firebase;
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private TCreditRepo creditRepo;
	
	@Getter
	private JSONArray jsonArray;
	
	private static final Logger log = LoggerFactory.getLogger(ApprovalTopupService.class);

	public List<ApprovalReport> reportApprovalTopUp(String typeDeposit, Integer status, LocalDate startDate, LocalDate endDate) {

		return criteria(typeDeposit, status, startDate, endDate)
				.stream()
				.map(this::newReport)
				.collect(Collectors.toList());
	}

	private List<TDepositEntity> criteria(String typeDeposit, Integer status, LocalDate startDate, LocalDate endDate) {
		return depositRepo.findAll((Specification<TDepositEntity>) (root, criteriaQuery, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("userId").get("depositType"), typeDeposit)));
			predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("bankDepCode").get("isBank"), 1)));
			if (status != null)
				predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("status"), status)));

			if (startDate != null && endDate != null)
				predicates.add(criteriaBuilder.and(criteriaBuilder.between(root.get("trxRequest"), startDate.atStartOfDay(), endDate.plusDays(1L).atStartOfDay())));


			return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
		});
	}

	private ApprovalReport newReport(TDepositEntity entity) {
		return ApprovalReport.builder()
				.noTicket(entity.getTiketNo())
				.bankCode(entity.getBankDepCode().getBankId().getBankCode())
				.bankName(entity.getBankDepCode().getBankId().getName())
				.accountNo(entity.getBankDepCode().getAccNo())
				.accountName(entity.getBankDepCode().getAccName())
				.dateOfTransaction(entity.getTrxRequest().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")))
				.uniqueNumber(entity.getInsufficientFund() == null ? "" : entity.getInsufficientFund().toString())
				.nominal(entity.getNominal().toString())
				.userId(entity.getUserId().getUserId())
				.status(DepositEnum.getKeterangan(entity.getStatus()))
				.userPhoneNumber(entity.getUserId().getHp())
				.processBy(entity.getLastUser())
				.dateOfApproval(entity.getLastUpdate() == null ? "" : entity.getLastUpdate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")))
				.description(entity.getDescription())
				.build();
	}

	@SneakyThrows
	public Page<DepositResponse> getApprovalTopUp(String cari,String bankId,String depositeType,Integer status,String startDate,String endDate,Pageable pageable){
		Page<TDepositEntity> ldeposite = depositRepo.findAllByUserIdDepositeTypeAndTgl(depositeType, bankId, status,cari ,
				DateTimeUtil.getDateFrom(startDate, "dd/MM/yyyy"), DateTimeUtil.getDateFrom(endDate, "dd/MM/yyyy"),pageable);
		return new PageImpl<>(
				ldeposite.getContent().stream().map(this::toDto).collect(Collectors.toList()),
				ldeposite.getPageable(),
				ldeposite.getTotalElements());
				
	}
	
	private DepositResponse toDto(TDepositEntity entity) {
		return DepositResponse.builder()
				.accountName(entity.getBankDepCode().getAccName())
				.accountNo(entity.getBankDepCode().getAccNo())
				.bankCode(entity.getBankDepCode().getBankId().getBankCode())
				.bankName(entity.getBankDepCode().getBankId().getName())
				.nominal(entity.getNominal().toString())
				.noTiket(entity.getTiketNo())
				.userId(entity.getUserId().getUserId())
				.trxDate(entity.getTrxRequest().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")))
				.status(entity.getStatus())
				.statusDesc(DepositEnum.getKeterangan(entity.getStatus()))
				.userPhone(entity.getUserId().getHp())
				.description(entity.getDescription()==null?"-":entity.getDescription())
				.build();
	}
	@Transactional(rollbackOn=Exception.class)
	public SaveResponse saveTopUp(ApprovalTopUpReq request){
		jsonArray = new JSONArray();
		TDepositEntity tDepositEntity = depositRepo.findFirstByTiketNoLike(request.getTiketNo());
		if(tDepositEntity == null) {
			throw new NotFoundException("Data Tidak Ditemukan !");
		}
		if(!tDepositEntity.getStatus().equals(ApprovalTopUpEnum.REQUEST.getStatusCode())) {
			throw new NotFoundException("Status deposit sudah Approve atau Reject !");
		}
		MUserEntity user = tDepositEntity.getUserId();
		int fee = 1;
		if(user.getQtyDeposit() > 0)fee=2;
		TFeeTrxEntity tfee = tFeeTrxRepo.findByIdMUserCategoryAndFee(user.getUserCategory().getSeqid(), fee);
		if(tfee != null) {
			TFeeReferenceEntity feeRef = new TFeeReferenceEntity();
			feeRef.setTrxNo(tDepositEntity.getTiketNo());
			feeRef.setTglTrx(Timestamp.valueOf(tDepositEntity.getTrxRequest()));
			feeRef.setUserId(user.getUserId());
			feeRef.setNominal(tDepositEntity.getNominal().intValue());
			feeRef.setFeeNominal(tDepositEntity.getNominalApproval()*(tfee.getFee()/100));
			feeRef.setFeePersen(tfee.getFee());
			feeRef.setLastUpdate(Timestamp.valueOf(LocalDateTime.now()));
			feeRef.setLastUser(request.getUserAdmin());
			feeRef.setUnitFee(tfee);
			feeReferenceRepo.save(feeRef);
		}
		tDepositEntity.setIsConfirmTransfer((byte) 1);
		if(request.getStatus().equals(ApprovalTopUpEnum.APPROVE.getStatusCode())) {
			tDepositEntity.setStatus(ApprovalTopUpEnum.APPROVE.getStatusCode());
			tDepositEntity.setLastUpdate(LocalDateTime.now());
			tDepositEntity.setLastUser(request.getUserAdmin());
			tDepositEntity.setTrxKonfirmasi(LocalDateTime.now());
			tDepositEntity.setUserApprove(request.getUserAdmin());
			tDepositEntity.setTrxApprove(LocalDateTime.now());
			
			BigDecimal nominal = tDepositEntity.getNominal().multiply(new BigDecimal("-1"));
			BigDecimal saldo = user.getBalance();
			TMutasiEntity mutasi = tMutasiRepo.findFirstByUserIdOrderByCounterMutasiDesc(user);
			if(mutasi != null) {
				saldo = mutasi.getSaldo();
			}else {
				mutasi = new TMutasiEntity();
			}
			saldo = saldo.add(nominal);
			user.setBalance(saldo);
			
			mutasi.setAmount(nominal);
			mutasi.setDescr("Approval Deposit via "+tDepositEntity.getBankDepCode().getBankId().getBankCode().toUpperCase());
			mutasi.setTrxNo(tDepositEntity.getTiketNo());
			mutasi.setUserId(user);
			mutasi.setProductSwCode("-");
			mutasi.setSaldo(saldo);
			mutasi.setTrxDate(LocalDate.now());
			mutasi.setTrxServer(LocalDateTime.now());
			mutasi.setTrxTime(LocalTime.now());
			mutasi.setUpdateBy(request.getUserAdmin());
			mutasi.setTrxType(MutasiEnum.DEPOSIT.getCode());
			tMutasiRepo.save(mutasi);
			userRepo.save(user);
			depositRepo.save(tDepositEntity);
			jsonArray.put(tDepositEntity.getTiketNo());
//			getNotifDeposit(tDepositEntity, "TopUp Berhasil via ", MutasiEnum.DEPOSIT.getKeterangan(), "1", "2", "TopUp");
		}else {
			tDepositEntity.setStatus(ApprovalTopUpEnum.REJECT.getStatusCode());
			tDepositEntity.setLastUpdate(LocalDateTime.now());
			tDepositEntity.setLastUser(request.getUserAdmin());
			depositRepo.save(tDepositEntity);
			jsonArray.put(tDepositEntity.getTiketNo());
//			getNotifDeposit(tDepositEntity, "TopUp Gagal via ", MutasiEnum.DEPOSIT.getKeterangan(), "0", "2", "TopUp");
		}
		Boolean isPay = false;
		List<TPaymentEntity> lPayment = paymentService.findByNoTiket(tDepositEntity.getTiketNo());
		for(TPaymentEntity pay :lPayment) {
			isPay = true;
			if(tDepositEntity.getStatus().equals(ApprovalTopUpEnum.APPROVE.getStatusCode())) {
				pay.setStatus(PaymentEnum.REQUEST.getCode());
				pay.setStatusPay(StatusPayEnum.PAID.getCode());
//				getNotifDeposit(tDepositEntity, "Pembayaran Berhasil Via ", MutasiEnum.BOOKING.getKeterangan(), "1", "1","payment");
			}else {
				pay.setNoTiket("0");
				pay.setStatusPay(StatusPayEnum.NOT_PAID.getCode());
				pay.setIsConfirmTransfer((byte) 0);
				pay.setAmountUniq(BigDecimal.ZERO);
				pay.setAmount(pay.getAmount().add(pay.getDiscountValue()));
//				getNotifDeposit(tDepositEntity, "Pembayaran Gagal Via ", MutasiEnum.BOOKING.getKeterangan(), "0", "1","payment");
			}
			paymentService.save(pay);
		}
		
		if(tDepositEntity.getStatus().equals(ApprovalTopUpEnum.APPROVE.getStatusCode())&&isPay) {
			BigDecimal saldo = user.getBalance();
			BigDecimal nominal = tDepositEntity.getNominal();
			TMutasiEntity mutasi = tMutasiRepo.findFirstByUserIdOrderByCounterMutasiDesc(user);
			if(mutasi != null) {
				saldo = mutasi.getSaldo();
			}
			saldo = saldo.add(nominal);
			user.setBalance(saldo);
			mutasi = new TMutasiEntity();
			mutasi.setAmount(nominal);
			mutasi.setDescr("Booking");
			mutasi.setTrxNo(tDepositEntity.getTiketNo());
			mutasi.setUserId(user);
			mutasi.setProductSwCode("-");
			mutasi.setSaldo(saldo);
			mutasi.setTrxDate(LocalDate.now());
			mutasi.setTrxServer(LocalDateTime.now());
			mutasi.setTrxTime(LocalTime.now());
			mutasi.setUpdateBy(request.getUserAdmin());
			mutasi.setTrxType(MutasiEnum.BOOKING.getCode());
			tMutasiRepo.save(mutasi);
		}
		if(isPay=false) {
			jsonArray.put(tDepositEntity.getTiketNo());
			if(request.getStatus().equals(ApprovalTopUpEnum.APPROVE.getStatusCode())) {
				getNotifDeposit(tDepositEntity, "TopUp Berhasil via ", MutasiEnum.DEPOSIT.getKeterangan(), "1", "2", "TopUp",jsonArray);
			}else {
				getNotifDeposit(tDepositEntity, "TopUp Gagal via ", MutasiEnum.DEPOSIT.getKeterangan(), "0", "2", "TopUp",jsonArray);
			}
			this.sendEmail(true, user, tDepositEntity);
		}else {
			jsonArray = new JSONArray();
			lPayment.forEach(p->jsonArray.put(p.getBookingCode()));
			if(tDepositEntity.getStatus().equals(ApprovalTopUpEnum.APPROVE.getStatusCode())) {
				getNotifDeposit(tDepositEntity, "Pembayaran Berhasil Via ", MutasiEnum.BOOKING.getKeterangan(), "1", "1","payment",jsonArray);
			}else {
				getNotifDeposit(tDepositEntity, "Pembayaran Gagal Via ", MutasiEnum.BOOKING.getKeterangan(), "0", "1","payment",jsonArray);
			}
		}
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Proses Approval Berhasil")
				.build();
	}
	@SneakyThrows
	private void getNotifDeposit(TDepositEntity entity,String desc,String ket,String statusTrx,String typeTrx,String title,JSONArray jsonArray) {
		JSONObject data = new JSONObject();
		log.info("===>Notif Deposit<===");
		//notif
//		try {
			data.put("idTrx", entity.getTiketNo());
			data.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")));
			data.put("userid", entity.getUserId().getUserId());
			data.put("nominal", entity.getNominal());
			data.put("type_trx", typeTrx); //1. Book, 2. Deposit
			data.put("tag", title);
			data.put("status_trx", statusTrx);
			data.put("body", jsonArray);
			data.put("tittle", desc+entity.getBankDepCode().getBankId().getBankCode());
			firebase.notif(title.concat(" ").concat(ket),entity.getTiketNo(), data, title,entity.getUserId().getTokenNotif());
//		}catch (JSONException e) {
//			// TODO: hangdle exception
//			log.error(e.getMessage());
//			e.printStackTrace();
//		}
		
	}
	@org.springframework.transaction.annotation.Transactional(isolation = Isolation.DEFAULT)
	public SaveResponse saveCredit(ApprovalTopUpReq request){
		jsonArray = new JSONArray();
		TDepositEntity tdeposit = depositRepo.findFirstByTiketNoLike(request.getTiketNo());
		if(tdeposit == null) {
			throw new NotFoundException("Data Tidak Ditemukan !");
		}
		if(!tdeposit.getStatus().equals(ApprovalTopUpEnum.REQUEST.getStatusCode())) {
			throw new NotFoundException("Status deposit sudah Approve atau Reject !");
		}
		MUserEntity user = tdeposit.getUserId();
		TMutasiEntity mutasi = tMutasiRepo.findFirstByUserIdOrderByCounterMutasiDesc(user);
		if(request.getStatus().equals(ApprovalTopUpEnum.APPROVE.getStatusCode())) {
			tdeposit.setStatus(ApprovalTopUpEnum.APPROVE.getStatusCode());
			tdeposit.setLastUpdate(LocalDateTime.now());
			tdeposit.setLastUser(request.getUserAdmin());
			tdeposit.setTrxKonfirmasi(LocalDateTime.now());
			tdeposit.setUserApprove(request.getUserAdmin());
			tdeposit.setTrxApprove(LocalDateTime.now());
			BigDecimal saldo = user.getBalance();
			BigDecimal nominal = tdeposit.getNominal().multiply(new BigDecimal("-1"));
			if(mutasi!=null) {
				saldo = mutasi.getSaldo();
			}else {
				mutasi = new TMutasiEntity();
			}
			saldo = saldo.add(nominal);
			user.setBalance(saldo);
			if(user.getDepositType().equals(DepositTypeEnum.CREDIT.getValue())) {
				List<TCreditEntity> lCredit = creditRepo.findAllByUserIdAndFlagAndTiketNo(user.getUserId(), "0", tdeposit.getTiketNo());
				for(TCreditEntity cr:lCredit) {
					cr.setFlag("1");
					creditRepo.save(cr);
				}
				List<TCreditEntity> lcredit = creditRepo.findAllByUserIdAndFlag(user.getUserId(), "0");
//				if(!lcredit.isEmpty()) {
//					TCreditEntity credit = lcredit.get(0);
//					credit.setTglMulai(credit.getTgl().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
//					credit.setCreditDay(user.getCreditDay());
//					credit.setTglSelesai(LocalDate.now().plusDays(Long.valueOf(user.getCreditDay())).format(DateTimeFormatter.ofPattern("yyyyMMdd")));
//					credit.setUserId(user.getUserId());
//					credit.setFlag("1");
//					creditRepo.save(credit);
//				}else {
				if(lcredit.isEmpty()) {
					TCreditEntity credit = new TCreditEntity();
					credit.setTgl(LocalDate.now());
					credit.setTglMulai(credit.getTgl().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
					credit.setCreditDay(user.getCreditDay());
					credit.setTglSelesai(LocalDate.now().plusDays(Long.valueOf(user.getCreditDay())).format(DateTimeFormatter.ofPattern("yyyyMMdd")));
					credit.setFlag("0");
					credit.setNominal(BigDecimal.ZERO);
					credit.setUserId(user.getUserId());
					creditRepo.save(credit);
				}
			}
			mutasi.setAmount(nominal);
			mutasi.setDescr("Approval Deposit via "+tdeposit.getBankDepCode().getBankId().getBankCode());
			mutasi.setTrxNo(tdeposit.getTiketNo());
			mutasi.setUserId(user);
			mutasi.setProductSwCode("-");
			mutasi.setSaldo(saldo);
			mutasi.setTrxDate(LocalDate.now());
			mutasi.setTrxServer(LocalDateTime.now());
			mutasi.setTrxTime(LocalTime.now());
			mutasi.setUpdateBy(request.getUserAdmin());
			mutasi.setTrxType(MutasiEnum.DEPOSIT.getCode());
			user.setQtyDeposit(user.getQtyDeposit()+1);
			tMutasiRepo.save(mutasi);
			userRepo.save(user);
			depositRepo.save(tdeposit);
			jsonArray.put(tdeposit.getTiketNo());
			getNotifDeposit(tdeposit, "TopUp Berhasil via ", MutasiEnum.DEPOSIT.getKeterangan(), "1", "2", "TopUp",jsonArray);
		}else {
			tdeposit.setStatus(ApprovalTopUpEnum.REJECT.getStatusCode());
			tdeposit.setLastUpdate(LocalDateTime.now());
			tdeposit.setLastUser(request.getUserAdmin());
			List<TCreditEntity> lCredit = creditRepo.findAllByUserIdAndFlagAndTiketNo(user.getUserId(), "0", tdeposit.getTiketNo());
			for(TCreditEntity cr:lCredit) {
				cr.setFlag("0");
				cr.setTiketNo("");
				creditRepo.save(cr);
			}
			jsonArray.put(tdeposit.getTiketNo());
			depositRepo.save(tdeposit);
			getNotifDeposit(tdeposit, "TopUp Gagal via ", MutasiEnum.REFUND.getKeterangan(), "0", "2", "TopUp",jsonArray);
		}
		this.sendEmail(true, user, tdeposit);
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Proses Approval Berhasil")
				.build();
	}
	@Async("asyncExecutor")
	public void sendEmail (boolean isTopupDeposit,MUserEntity user,TDepositEntity deposit) {
		try {
			MUserEntity userRef = userRepo.getMUserEntitiesBy(user.getRefNum());
			String ccEmail ="";
			if(userRef != null)ccEmail = userRef.getEmail();
			EmailSender.sendNotifDeposit(
					user.getEmail(), 
					ccEmail, 
					EmailSender.getContentEmailApproval(isTopupDeposit, deposit.getTiketNo(), user.getUserId(), deposit.getNominal().toString(), deposit.getBankDepCode().getBankId().getName(), deposit.getBankDepCode().getAccNo(), deposit.getBankDepCode().getAccName(), deposit.getDescription(), ApprovalTopUpEnum.findApprovalId(deposit.getStatus()))
					);
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
}
