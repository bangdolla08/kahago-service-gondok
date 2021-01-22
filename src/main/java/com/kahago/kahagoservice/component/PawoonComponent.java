package com.kahago.kahagoservice.component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.aspectj.weaver.bcel.Utility;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kahago.kahagoservice.client.PawoonFeignService;
import com.kahago.kahagoservice.client.model.response.ResponseModel;
import com.kahago.kahagoservice.entity.TDepositEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestDetailEntity;
import com.kahago.kahagoservice.enummodel.PawoonStatusEnum;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.enummodel.StatusPayEnum;
import com.kahago.kahagoservice.exception.InProgressException;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.service.BookService;
import com.kahago.kahagoservice.service.DepositService;
import com.kahago.kahagoservice.service.HistoryTransactionService;
import com.kahago.kahagoservice.service.OptionPaymentService;
import com.kahago.kahagoservice.service.PaymentService;
import com.kahago.kahagoservice.util.Common;
import com.kahago.kahagoservice.util.DateTimeUtil;

import lombok.SneakyThrows;


/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 4 Des 2019
 */
@Component
public class PawoonComponent {
	
	private static final Logger log = LoggerFactory.getLogger(PawoonComponent.class);

	@Autowired
	private FirebaseComponent firebase;
	@Autowired
	private PawoonFeignService pawonService;
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private HistoryTransactionService hisTrans;
	@Autowired
	private DepositService depService;
//	@Autowired
//	private OptionPaymentService opService;
	public Boolean getStatusPayment(List<TPaymentEntity> lspay,String hit, List<TPickupOrderRequestDetailEntity> lspick) {
		log.info("===> Pawoon Check Status <===");
		TPaymentEntity pay = null;
		if(lspay.isEmpty()) {
			TPickupOrderRequestDetailEntity pick = lspick.stream().findAny().get();
			pay = TPaymentEntity.builder()
					.userId(pick.getOrderRequestEntity().getUserEntity())
					.idPayment(pick.getIdPayment())
					.idTicket(pick.getIdTicket())
					.noTiket(pick.getNoTiket())
					.build();
		}else {
			pay = lspay.stream().findFirst().get();
		}
		
		Double tag = lspay.stream().mapToDouble(p->p.getAmount().doubleValue()).sum();
		tag += lspick.stream().mapToDouble(p->p.getAmount().doubleValue()).sum();
		JSONArray jsarray = new JSONArray();
		lspay.stream().forEach(o-> jsarray.put(o.getBookingCode()));
		lspick.stream().forEach(p->jsarray.put(p.getOrderRequestEntity().getPickupOrderId()));
		ResponseModel resp = pawonService.requestStatusPawon(String.valueOf(Math.round(tag)), pay.getIdTicket(), pay.getIdPayment());
		if(resp!=null) {
			if(Integer.valueOf(hit)>=8 || PawoonStatusEnum.UNKNOWN==PawoonStatusEnum.getEnum(resp.getPaymentStatusCode())) {
				log.info("==> Pembayaran Go Pay Gagal "+pay.getNoTiket()+" <==");
				lspay.stream().forEach(paymentService.resetPayment());
				lspick.stream().forEach(p->p.setIsPay(StatusPayEnum.PICKUP_NOT_PAID.getCode()));
				sendNotif(pay, tag, jsarray,"Pembayaran Gagal Via GoPay ","0","payment","3");
				return false;
			}else if(PawoonStatusEnum.SUCCESS
					==PawoonStatusEnum.getEnum(resp.getPaymentStatusCode())) {
				//notif
	    		sendNotif(pay, tag, jsarray,"Pembayaran Berhasil Via GoPay ","1","payment","3");
			}else {
				log.info("Response:>"+ ResponseStatus.IN_PROCCESS.getReasonPhrase());
//				throw new InProgressException(ResponseStatus.IN_PROCCESS.getReasonPhrase());
				return false;
			}
		}
		
		return true;
		
	}
	
	public Response getStatusDep(TDepositEntity dep,Integer hit) {
		log.info("==> Deposit Checking Pawoon");
		JSONArray jsarray = new JSONArray();
		Double tag = dep.getNominal().doubleValue();
		ResponseModel resp = pawonService.requestStatusPawon(String.valueOf(Math.round(tag)), dep.getIdTicket(), dep.getIdPayment());
		log.info("Response Pawoon "+Common.json2String(resp));
		TPaymentEntity pay = TPaymentEntity.builder()
				.noTiket(dep.getTiketNo())
				.userId(dep.getUserId())
				.build();
		if(resp!=null) {
			if(Integer.valueOf(hit)>=8 
					|| PawoonStatusEnum.UNKNOWN==PawoonStatusEnum.getEnum(resp.getPaymentStatusCode())) {
				log.info("==> Top Up Go Pay Gagal "+dep.getTiketNo()+" <==");
				sendNotif(pay, tag, jsarray,"Top Up Gagal Via GoPay ","0","TopUp","2");
				depService.save(depService.depReset(dep));
//				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ResponseStatus.FAILED.getReasonPhrase());
				return new Response<>(ResponseStatus.FAILED.value(), ResponseStatus.FAILED.getReasonPhrase());
			}else if(PawoonStatusEnum.SUCCESS
					==PawoonStatusEnum.getEnum(resp.getPaymentStatusCode())) {
				//notif
				sendNotif(pay, tag, jsarray,"Top Up Berhasil Via GoPay ","1","TopUp","2");
				return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase());
			}else {
				return new Response<>(ResponseStatus.FAILED.value(), ResponseStatus.FAILED.getReasonPhrase());
			}
		}
		return new Response<>(ResponseStatus.FAILED.value(), ResponseStatus.FAILED.getReasonPhrase());
	}
	@SneakyThrows
	public void sendNotif(TPaymentEntity pay, Double tag, JSONArray jsarray,String ket, 
			String status,String sTag,String tipeTrx) {
		ObjectMapper obj = new ObjectMapper();
		JSONObject data = new JSONObject();
		data.put("idTrx", pay.getNoTiket());
		data.put("date", DateTimeUtil.getDateTime("dd MMM yyyy"));
		data.put("userid", pay.getUserId().getUserId());
		data.put("nominal", tag.toString()); //nominal
		data.put("type_trx", tipeTrx); //1. Book, 2. Deposit, 3. Payment
		data.put("tag", sTag);
		data.put("tittle", ket);
		data.put("status_trx", status); //0. failed, 1. success
	
		String token = pay.getUserId().getTokenNotif();
		
		data.put("body", jsarray); //description
		firebase.notif(data.getString("tittle"), data.getString("body").replace("[", "").replaceAll("]", ""), data, "", token);
	}
	
}
