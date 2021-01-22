package com.kahago.kahagoservice.component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.transaction.Transactional;

import org.apache.commons.lang.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kahago.kahagoservice.client.model.request.NotificationFaspay;
import com.kahago.kahagoservice.client.model.response.ResponseNotifFaspay;
import com.kahago.kahagoservice.entity.TDepositEntity;
import com.kahago.kahagoservice.entity.TMutasiEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestDetailEntity;
import com.kahago.kahagoservice.enummodel.DepositEnum;
import com.kahago.kahagoservice.enummodel.MutasiEnum;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.enummodel.StatusPayEnum;
import com.kahago.kahagoservice.model.response.ResponseGlobal;
import com.kahago.kahagoservice.repository.MProductSwitcherRepo;
import com.kahago.kahagoservice.repository.MUserRepo;
import com.kahago.kahagoservice.repository.TDepositRepo;
import com.kahago.kahagoservice.repository.TMutasiRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestDetailRepo;
import com.kahago.kahagoservice.service.BookService;
import com.kahago.kahagoservice.service.FirebaseService;
import com.kahago.kahagoservice.service.OptionPaymentService;
import com.kahago.kahagoservice.service.PaymentService;
import com.kahago.kahagoservice.util.DateTimeUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

@Component
public class FaspayComponent {
	@Autowired
	private FirebaseComponent firebase;
	@Autowired
	private TPaymentRepo payRepo;
	@Autowired
	private BookService bookService;
	@Autowired
	private TDepositRepo depRepo;
	@Autowired
	private TPickupOrderRequestDetailRepo podRepo;
	@Autowired
	private MProductSwitcherRepo pswRepo;
	@Autowired
	private TMutasiRepo mutasiRepo;
	@Autowired
	private PaymentService payService;
	@Getter
	private TDepositEntity depEntity;
	@Autowired
	private OptionPaymentService opService;
	@Autowired
	private MUserRepo userRepo;
	@Setter @Getter
	private List<TPaymentEntity> lsPay;
	@Setter @Getter
	private List<TPickupOrderRequestDetailEntity> lsPickupOrder;
	@SneakyThrows
	public ResponseGlobal getValidasi(String billNo,String status) {
		JSONObject data = new JSONObject();
		String tag = "payment";
		String ket="Pembayaran Berhasil";
		JSONArray jsarray = new JSONArray();
		if("2".equals(status)) {
			if(isDeposit(billNo)) {
				return ResponseGlobal.builder()
						.rc(ResponseStatus.OK.value())
						.description(ResponseStatus.OK.getReasonPhrase())
						.build();
			}
			else if(this.isPayment(billNo)) {
				return ResponseGlobal.builder()
						.rc(ResponseStatus.OK.value())
						.description(ResponseStatus.OK.getReasonPhrase())
						.build();
			}
		}else if(Integer.valueOf(status)>2) {
			if(isDeposit(billNo)) {
				TDepositEntity dep = getDepEntity();
				dep.setStatus(DepositEnum.CANCEL.getValue());
				dep.setTrxServer(LocalDateTime.now());
				depRepo.save(dep);
			}else if(isPayment(billNo)) {
				ket = "Pembayaran Gagal";
				List<TPaymentEntity> lspay = getLsPay();
				TPaymentEntity pay = lspay.stream().findFirst().get();
				data.put("idTrx", pay.getNoTiket());
				data.put("date", LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
				data.put("userid", pay.getUserId().getUserId());
				data.put("nominal", "0"); //nominal
				data.put("type_trx", "3"); //1. Book, 2. Deposit, 3. Payment
				data.put("tag", "payment");
				data.put("tittle", "Pembayaran Gagal via "+pay.getPaymentOption().toUpperCase());
				data.put("status_trx", "0");//0.failed, 1.Success
				jsarray.put(pay.getNoTiket());
				firebase.notif(tag, ket, data, tag, pay.getUserId().getTokenNotif());
				lspay.forEach(payService.resetPayment());
				payRepo.saveAll(lspay);
			}
			return ResponseGlobal.builder()
					.rc(ResponseStatus.FAILED.value())
					.description(ResponseStatus.FAILED.getReasonPhrase())
					.build();
		}
		return ResponseGlobal.builder()
				.rc(ResponseStatus.IN_PROCCESS.value())
				.description(ResponseStatus.IN_PROCCESS.getReasonPhrase())
				.build();
	}
	
	@SneakyThrows
	@Transactional
	public ResponseNotifFaspay doUpdate(NotificationFaspay notif) {
		String tag = "payment";
		String ket="Pembayaran Berhasil";
		Integer status = Integer.valueOf(notif.getPaymentStatusCode());
		if(2==status) {
			if(isDeposit(notif.getBillNo())) {
				ket = "Deposit Berhasil";
				TDepositEntity dep = getDepEntity();
				depositFirebase(tag, ket, dep,"1");
				TPaymentEntity pay = TPaymentEntity.builder()
						.amount(dep.getNominal().multiply(new BigDecimal("-1")))
						.userId(dep.getUserId())
						.productSwCode(pswRepo.findBySwictherCode(300).stream().findFirst().get())
						.build();
				String desc = "Approval Deposit via "+dep.getBankDepCode().getAccName().toUpperCase();
				TMutasiEntity mutasi = bookService.insertMutasi(pay, dep.getTiketNo(), desc, MutasiEnum.DEPOSIT);
				mutasiRepo.save(mutasi);
				userRepo.save(bookService.updateBalanceUser(mutasi.getUserId().getUserId(), mutasi.getSaldo().doubleValue()));
				dep.setStatus(DepositEnum.PROSES_TOPUP.getValue());
				dep.setUserApprove(dep.getUserId().getUserId());
				dep.setTrxApprove(LocalDateTime.now());
				depRepo.save(dep);
			}else if(isPayment(notif.getBillNo())) {
				List<TPaymentEntity> lspay = getLsPay();
				TPaymentEntity pay = lspay.stream().findFirst().get();
				pay.setAmount(new BigDecimal(lspay.stream().mapToDouble(p->p.getAmount().doubleValue()).sum()));
				paymentNotif(tag, ket, pay,"1");
				lspay.forEach(opService.updatePaymentWH(pay.getNoTiket(), PaymentEnum.REQUEST));
				lspay.forEach(p->p.setStatusPay(StatusPayEnum.PAID.getCode()));
				
				payRepo.saveAll(lspay);
				
			}
		}
		else if(status>2) {
			if(isDeposit(notif.getBillNo())) {
				ket = "Deposit Gagal";
				depositFirebase(tag, ket, getDepEntity(),"0");
			}else if(isPayment(notif.getBillNo())) {
				ket = "Pembayaran Gagal";
				List<TPaymentEntity> lspay = getLsPay();
				TPaymentEntity pay = lspay.stream().findFirst().get();
				pay.setAmount(new BigDecimal(lspay.stream().mapToDouble(p->p.getAmount().doubleValue()).sum()));
				paymentNotif(tag, ket, pay,"0");
				lspay.forEach(payService.resetPayment());
				payRepo.saveAll(lspay);
			}
		}
		return ResponseNotifFaspay.builder()
				.billNo(notif.getBillNo())
				.response(notif.getRequest())
				.trxId(notif.getTrxId())
				.merchant(notif.getMerchant())
				.merchantId(notif.getMerchantId())
				.billNo(notif.getBillNo())
				.responseCode(ResponseStatus.OK.value())
				.responseDesc(ResponseStatus.OK.getReasonPhrase())
				.responseDate(DateTimeUtil.getDateTime("yyyy-MM-dd kk:mm:ss"))
				.build();
	}

	private void paymentNotif(String tag, String ket, TPaymentEntity pay,String status) throws JSONException {
		JSONObject data = new JSONObject();
		JSONArray jsarray = new JSONArray();
		data.put("idTrx", pay.getNoTiket());
		data.put("date", LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
		data.put("userid", pay.getUserId().getUserId());
		data.put("nominal", pay.getAmount().doubleValue()); //nominal
		data.put("type_trx", "3"); //1. Book, 2. Deposit, 3. Payment
		data.put("tag", "payment");
		data.put("tittle", ket+" via "+pay.getPaymentOption().toUpperCase());
		data.put("status_trx", status);//0.failed, 1.Success
		jsarray.put(pay.getNoTiket());
		data.put("body", jsarray); //description
		firebase.notif(tag, ket, data, tag, pay.getUserId().getTokenNotif());
	}

	private void depositFirebase(String tag, String ket, TDepositEntity dep,String status)
			throws JSONException {
		JSONObject data = new JSONObject();
		JSONArray jsarray = new JSONArray();
		data.put("idTrx", dep.getTiketNo());
		data.put("date", LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
		data.put("userid", dep.getUserId().getUserId());
		data.put("nominal", dep.getNominal().toString()); //nominal
		data.put("type_trx", "2"); //1. Book, 2. Deposit, 3. Payment
		data.put("tag", "TopUp");
		data.put("tittle", ket+" via "+dep.getBankDepCode().getAccName().toUpperCase());
		data.put("status_trx", status);//0.failed, 1.Success
		jsarray.put(dep.getTiketNo());
		data.put("body", jsarray); //description
		firebase.notif(tag, ket, data, tag, dep.getUserId().getTokenNotif());
	}
	private boolean isDeposit(String billNo) {
		// TODO Auto-generated method stub
		this.depEntity = depRepo.findById(billNo).orElse(null);
		if(this.depEntity==null) return false;
		return true;
	}
	
	private boolean isPayment(String billNo) {
		setLsPay(payRepo.findAllByNoTiket(billNo));
		if(getLsPay().isEmpty()) return false;
		return true;
	}
	
	private boolean isPickupOrder(String billNo) {
		this.setLsPickupOrder(podRepo.findAllByIdTicket(billNo));
		return true;
	}
	
}
