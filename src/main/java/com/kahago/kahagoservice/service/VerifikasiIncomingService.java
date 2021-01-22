package com.kahago.kahagoservice.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.kahago.kahagoservice.entity.TBookDetailHistoryEntity;
import com.kahago.kahagoservice.entity.TCreditEntity;
import com.kahago.kahagoservice.entity.TMutasiEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.entity.TPaymentHistoryEntity;
import com.kahago.kahagoservice.enummodel.MutasiEnum;
import com.kahago.kahagoservice.enummodel.PayTypeEnum;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.enummodel.TypeTrxEnum;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.TbookDetailHistory;
import com.kahago.kahagoservice.model.response.VerifikasiIncoming;
import com.kahago.kahagoservice.model.response.VerifikasiRespon;
import com.kahago.kahagoservice.repository.TBookDetailHistoryRepo;
import com.kahago.kahagoservice.repository.TCreditRepo;
import com.kahago.kahagoservice.repository.TMutasiRepo;
import com.kahago.kahagoservice.repository.TPaymentHistoryRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.util.Common;
import com.kahago.kahagoservice.util.DateTimeUtil;

/**
 * @author Ibnu Wasis
 */
@Service
public class VerifikasiIncomingService {
	@Autowired
	private TBookDetailHistoryRepo tDetailHistoryRepo;
	@Autowired
	private TPaymentRepo tPaymentRepo;
	@Autowired
	private TPaymentHistoryRepo tHistoryRepo;
	@Autowired
	private TMutasiRepo tMutasiRepo;
	@Autowired
	private TCreditRepo tCreditRepo;
	
	public static final String FLAG = "0";
	
	public VerifikasiRespon getVerifikasi(String bookingCode) {
		List<TBookDetailHistoryEntity> lsbookHistory = tDetailHistoryRepo.
				findByBookingCode(bookingCode);//findFirstByBookingCode(bookingCode);
		TPaymentEntity payment = tPaymentRepo.findByBookingCodeIgnoreCaseContaining(bookingCode);
		List<TPaymentHistoryEntity> pHistory = tHistoryRepo.findHistoryByBookingCodeLimit(bookingCode);
		if(pHistory.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseStatus.NOT_FOUND.getReasonPhrase());
		TPaymentHistoryEntity lastPayHist = pHistory.stream().findFirst().get();
		return VerifikasiRespon.builder()
				.bookingCode(payment.getBookingCode())
				.origin(payment.getOrigin())
				.destination(payment.getDestination())
				.vendorName(payment.getProductSwCode().getSwitcherEntity().getDisplayName())
				.productName(payment.getProductSwCode().getDisplayName())
				.senderName(payment.getSenderName())
				.receiverName(payment.getReceiverName())
				.extraCharge(payment.getExtraCharge().toString())
				.insurance(payment.getInsurance().toString())
				.amount(lastPayHist.getAmount().toString())
				.lastTotalAmount(lastPayHist.getLastAmount().toString())
				.books(lsbookHistory.stream().map(this::todoDetail).collect(Collectors.toList()))
				.build();
	}
	
	public List<VerifikasiIncoming> getlistVerifikasi(String userid){
		List<TPaymentEntity> lspay = tPaymentRepo
				.findByStatusAndUserId(PaymentEnum.HOLD_BY_WAREHOUSE.getCode(), userid);
		if(lspay.isEmpty()) throw new NotFoundException(ResponseStatus.NOT_FOUND.getReasonPhrase());
		List<VerifikasiIncoming> lsver = new ArrayList<VerifikasiIncoming>();
		//lspay.forEach(getListResponVerifikasi(lsver));
		return lsver;
	}

	@Transactional(rollbackOn = Exception.class)
	public VerifikasiRespon getPay(VerifikasiRespon req) {
		TPaymentEntity pay = tPaymentRepo.findByBookingCodeIgnoreCaseContaining(req.getBookingCode());
		TPaymentHistoryEntity payHist = tHistoryRepo.findHistoryByBookingCodeLimit(req.getBookingCode()).stream().findFirst().get();
		if(pay==null || payHist==null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseStatus.NOT_FOUND.getReasonPhrase());
		BigDecimal amt = payHist.getLastAmount().subtract(payHist.getAmount());
		pay.setAmount(amt);
		
		String resi = "";
		if(PayTypeEnum.getEnum(Integer.valueOf(req.getPayType()))==PayTypeEnum.PAY_NOW) {
			pay.setTrxServer(new Timestamp(Instant.now().toEpochMilli()));
			resi = Common.getResi(pay);
		}
		return VerifikasiRespon.builder()
				.bookingCode(pay.getBookingCode())
				.amount(pay.getAmount().toString())
				.senderName(pay.getSenderName())
				.receiverName(pay.getReceiverName())
				.userId(pay.getUserId().getUserId())
				.urlResi(resi)
				.build();
	}
	private Consumer<? super TPaymentEntity> getListResponVerifikasi(List<VerifikasiIncoming> lsver) {
		return p->{
			String tgl = p.getTrxDate().toString();
			VerifikasiIncoming ver = VerifikasiIncoming.builder()
					.tag("verifikasi")
					.idTrx(p.getBookingCode())
					.nominal(p.getAmount().toString())
					.statusTrx("1")
					.tgl(DateTimeUtil
							.getString2Date(tgl.concat(" ")
									.concat(p.getTrxTime()), "yyyy-MM-dd kkmm", "dd MMM yyyy kk:mm"))
					.tipeTrx(TypeTrxEnum.PAYMENT.getCodeString())
					.title("Barang Bermasalah : "+p.getBookingCode())
					.body("Informasi Paket Tidak Sesuai")
					.userid(p.getUserId().getUserId())
					.build();
			lsver.add(ver);

		};
	}
	

	public Response<String> doSave(String action,String bookingCode){
		TPaymentEntity payment = tPaymentRepo.findByBookingCodeIgnoreCaseContaining(bookingCode);
		List<TPaymentHistoryEntity> pHistory = tHistoryRepo.findHistoryByBookingCodeLimit(bookingCode);
		if(payment == null && pHistory.size() == 0) {
			throw new NotFoundException("Data Tidak Ditemukan !");
		}
		switch (action) {
		case "save":
			BigDecimal amount = pHistory.get(0).getAmount();
			BigDecimal curamount = pHistory.get(0).getLastAmount();
			if(curamount.compareTo(amount) < 0) {
				BigDecimal total = amount.subtract(curamount);
				doMutasi(payment, total,action);
				if(payment.getUserId().getDepositType().equals("1"))doCredit(payment, total);
				payment.setAmount(curamount);
				payment.setNoTiket(payment.getBookingCode());
				payment.setStatus(PaymentEnum.RECEIVE_IN_WAREHOUSE.getCode());
				tPaymentRepo.save(payment);
			}else if(curamount.equals(amount)) {
				payment.setStatus(PaymentEnum.RECEIVE_IN_WAREHOUSE.getCode());
				tPaymentRepo.save(payment);
			}
			break;
		case "cancel":
				BigDecimal ccuramount = pHistory.get(0).getAmount().multiply(new BigDecimal("-1"));
				doMutasi(payment, ccuramount, action);
				if(payment.getUserId().getDepositType().equals("1"))doCredit(payment, ccuramount);
				payment.setStatus(PaymentEnum.CANCEL_BY_USER.getCode());
				tPaymentRepo.save(payment);
			break;
		default:
			break;
		}
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase()
				);
	}
	
	private TbookDetailHistory todoDetail(TBookDetailHistoryEntity entity) {
		Double diffLength = Math.abs(Double.valueOf(entity.getLength()) - Double.valueOf(entity.getLastLength()));
		Double diffWidth = Math.abs(Double.valueOf(entity.getWidth()) - Double.valueOf(entity.getLastWidth()));
		Double diffHeight = Math.abs(Double.valueOf(entity.getHeight()) - Double.valueOf(entity.getLastHeight()));
		Double diffWeight = Math.abs(Double.valueOf(entity.getGrossWeight()) - Double.valueOf(entity.getLastGrossWeight()));
		Double diffVol = Math.abs(Double.valueOf(entity.getVolWeight()) - Double.valueOf(entity.getLastVolWeight()));
		return TbookDetailHistory.builder()
				.bookingCode(entity.getBookingCode())
				.length(entity.getLength())
				.width(entity.getWidth())
				.height(entity.getHeight())
				.grossWeight(entity.getGrossWeight())
				.volumeWeight(entity.getVolWeight())
				.lastLength(entity.getLastLength())
				.lastWidth(entity.getLastWidth())
				.lastHeight(entity.getLastHeight())
				.lastGrossWeight(entity.getLastGrossWeight())
				.lastVolumeWeight(entity.getLastVolWeight())
				.differenceLength(diffLength.toString())
				.differenceWidth(diffWidth.toString())
				.differenceHeight(diffHeight.toString())
				.differenceGrossWeight(diffWeight.toString())
				.differenceVolumeWeight(diffVol.toString())
				.build();
	}
	private void doMutasi(TPaymentEntity payment, BigDecimal total,String action) {
		TMutasiEntity mutasi = new TMutasiEntity();
		TMutasiEntity mutasiprev = tMutasiRepo.findFirstByUserIdOrderByCounterMutasiDesc(payment.getUserId());
		if(action.equals("save")) {
			mutasi.setTrxNo("EBRFN"+payment.getBookingCode());
			mutasi.setTrxType(MutasiEnum.EDIT_BOOK_REFUND.getCode());
		}else {
			mutasi.setTrxNo("RFN"+payment.getBookingCode());
			mutasi.setTrxType(MutasiEnum.REFUND.getCode());
		}
		mutasi.setAmount(total);
		mutasi.setDescr("Refund By User"+payment.getUserId().getUserId());
		mutasi.setUserId(payment.getUserId());
		mutasi.setTrxDate(LocalDate.now());
		mutasi.setTrxTime(LocalTime.now());
		mutasi.setSaldo(mutasiprev.getSaldo().add(mutasi.getAmount()));
		mutasi.setUpdateBy("SYS");
		mutasi.setTrxServer(LocalDateTime.now());
		tMutasiRepo.save(mutasi);
	}
	
	private void doCredit(TPaymentEntity payment, BigDecimal total) {
		TCreditEntity credit = tCreditRepo.findFirstByUserIdAndTglAndFlagOrderBySeqDesc(payment.getUserId().getUserId(), payment.getTrxDate(),FLAG);
		if(credit!=null) {
			credit.setNominal(credit.getNominal().add(total));
			tCreditRepo.save(credit);
		}else {
			TCreditEntity tc= tCreditRepo.findFirstByUserIdAndTglOrderBySeqDesc(payment.getUserId().getUserId(), payment.getTrxDate().minusDays(1)).get(0);
			TCreditEntity cr = new TCreditEntity();
			cr.setUserId(payment.getUserId().getUserId());
			cr.setTgl(payment.getTrxDate());
			cr.setNominal(total);
			cr.setFlag(FLAG);
			cr.setTglMulai(tc.getTglMulai());
			cr.setTglSelesai(tc.getTglSelesai());
			if(tc.getFlag().equals(FLAG)) {
				cr.setCreditDay("-1");
			}else cr.setCreditDay(FLAG);
			tCreditRepo.save(cr);
			
		}
	}
}
