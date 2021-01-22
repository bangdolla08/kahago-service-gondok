package com.kahago.kahagoservice.schedulling;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import javax.transaction.Transactional;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kahago.kahagoservice.client.TransferFeignService;
import com.kahago.kahagoservice.client.model.request.ReqTransfer;
import com.kahago.kahagoservice.client.model.response.RespTransfer;
import com.kahago.kahagoservice.client.model.response.TransferVendorResponse;
import com.kahago.kahagoservice.component.PawoonComponent;
import com.kahago.kahagoservice.entity.TCounterTransferEntity;
import com.kahago.kahagoservice.entity.TDepositEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.entity.TRespTransferEntity;
import com.kahago.kahagoservice.enummodel.DepositEnum;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.StatusPayEnum;
import com.kahago.kahagoservice.repository.TCounterTransferRepo;
import com.kahago.kahagoservice.repository.TDepositRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.repository.TRespTransferRepo;
import com.kahago.kahagoservice.service.BookService;
import com.kahago.kahagoservice.service.PaymentService;
import com.kahago.kahagoservice.util.DateTimeUtil;

import lombok.SneakyThrows;
import net.sf.jasperreports.components.headertoolbar.json.HeaderToolbarElementJsonHandler.GroupInfo;

/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 10 Des 2019
 */
@Component
public class TransferSchedulling {
	
	private static final Logger log = LoggerFactory.getLogger(TransferSchedulling.class);
	@Autowired
	private PaymentService payService;
	@Autowired
	private BookService bookService;
	@Autowired
	private PawoonSchedulling pawoonDepo;
	@Autowired
	private PawoonComponent pawoonComp;
	@Autowired
	private TRespTransferRepo transferRepo;
	@Autowired
	private TDepositRepo depRepo;
	@Autowired
	private TPaymentRepo payRepo;
	@Autowired
	private TCounterTransferRepo counterRepo;
//	@Autowired
//	private TransferFeignService transferService;
	
	@Value("${transfer.configid}")
	private String configid;
	@Value("${transfer.username}")
	private String username;
	@Value("${transfer.password}")
	private String password;
	@Value("${url.service.transfer}")
	private String url;
	@Scheduled(cron="*/10 * 5-23 * * ?")
//	@Transactional
	@SneakyThrows
	public void doCheckPayment() {
		log.info("==> Cek Payment Transfer <==");
		List<Integer> lsStatus = Arrays.asList(PaymentEnum.PENDING.getCode(),
				PaymentEnum.UNPAID_RECEIVE.getCode());
		List<TPaymentEntity> lspay = payService.getPayTransferByStatusAndGroupByAmountUniq(lsStatus);
		String ket = "Pembayaran Berhasil Via BCA";
		for(TPaymentEntity pay:lspay) {
			if(isAvailUniq(pay.getAmountUniq())) {
				log.info("Update Book: "+pay.getBookingCode());
				if(payService.updateTransfer(pay)) {
					JSONArray jsarray = new JSONArray();
					jsarray.put(pay.getNoTiket());
					pawoonComp.sendNotif(pay, pay.getAmountUniq().doubleValue(), 
							jsarray, ket, "1", "payment", "3");
				}
			}
		}
	}
	
	@Scheduled(cron="*/10 * 5-21 * * ?")
	@SneakyThrows
	public void doCheckMutasi() {
		log.info("===> Cek Mutasi Bank <===");
		Date dt1=new Date();
		Date dt2=new Date();
		dt1 = DateTimeUtil.getDateTime("yyyy-MM-dd kk:mm:ss", 
				DateTimeUtil.getDateTime("yyyy-MM-dd")
					.concat(" ")
					.concat("05:00:00"));
		dt2 = DateTimeUtil.getDateTime("yyyy-MM-dd kk:mm:ss", 
				DateTimeUtil.getDateTime("yyyy-MM-dd")
				.concat(" ")
				.concat("23:00:00"));
		if(isDeposit() || isPayment()) {
			doCounterTrf();
			ResponseEntity<String> resp = send2Bank(dt1, dt2);
			mapper = new ObjectMapper();
			String response = resp.getBody();
			TransferVendorResponse trfResp = mapper.readValue(response, TransferVendorResponse.class);
			if(trfResp!=null) {
				List<RespTransfer> lstrf = trfResp.getResultData();
				lstrf.forEach(entry2DB());
			}
		}
		
	}

	private ResponseEntity<String> send2Bank(Date dt1, Date dt2) {
		RestTemplate rest = new RestTemplate();
		HttpHeaders head = new HttpHeaders();
		head.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		map.add("username", username);
		map.add("password", password);
		map.add("startdate", String.valueOf(dt1.getTime()/1000));
		map.add("enddate", String.valueOf(dt2.getTime()/1000));
		map.add("configid", configid);
		
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map,head);
		ResponseEntity<String> resp = rest.
				postForEntity(url, request, String.class);
		log.info("resp trf: "+resp.getBody());
		return resp;
	}

	private Consumer<? super RespTransfer> entry2DB() {
		return p -> {
			transferRepo.save(insertTrespTransfer(p));
		};
	}

	private TRespTransferEntity insertTrespTransfer(RespTransfer p) {
		TRespTransferEntity entity = TRespTransferEntity.builder()
				.id(p.getId())
				.moduleId(p.getModuleid())
				.date(Long.valueOf(p.getDate()))
				.sender(p.getSender())
				.note(p.getNote())
				.debit(p.getDebit())
				.kredit(p.getKredit())
				.md5(p.getMd5())
				.lastUpdate(LocalDate.now())
				.lastTime(new Timestamp(Instant.now().toEpochMilli()))
				.build();
		return entity;
	}
	private ObjectMapper mapper;
	@Scheduled(cron="*/10 * 5-21 * * ?")
	@Transactional
	public void doCheckDeposit() {
		log.info("===> Cek Deposit Transfer <===");
		List<TDepositEntity> lsdepo = depRepo.findAllByInsufficientAndIsConfirm();
		lsdepo.stream().forEach(depoConsumer());
		depRepo.saveAll(lsdepo);
	}

	private Consumer<? super TDepositEntity> depoConsumer() {
		return d -> {
			BigDecimal uniqamount = d.getNominal().subtract(new BigDecimal(d.getInsufficientFund()));
			if(isAvailUniq(uniqamount)) {
				d.setStatus(DepositEnum.PROSES_TOPUP.getValue());
				d.setIsConfirmTransfer((byte) 1);
				pawoonDepo.saveTrxDeposit(d,d.getBankDepCode().getBankId().getBankCode().toUpperCase());
			}
		};
	}
	private boolean isAvailUniq(BigDecimal amountUniq) {
		// TODO Auto-generated method stub
		TRespTransferEntity resp = transferRepo.findByKreditAndLastUpdateOrderByIdDesc(amountUniq,LocalDate.now()).orElse(null);
		if(resp==null) return false;
		return true;
	}
	
	private boolean isDeposit() {
		List<TDepositEntity> ls = depRepo.findAllByInsufficientAndIsConfirm();
		if(ls.size() > 0) {
			return true;
		}
		return false;
	}

	private boolean isPayment() {
		// TODO Auto-generated method stub
		List<Integer> lsStatus = Arrays.asList(PaymentEnum.PENDING.getCode(),
				PaymentEnum.UNPAID_RECEIVE.getCode());
		List<TPaymentEntity> lspay = payRepo.findByStatusAndPickupDate(lsStatus, LocalDate.now());
		if(lspay.size() > 0) {
			return true;
		}
		return false;
	}
	
	@Async("asyncExecutor")
	private void doCounterTrf() {
		TCounterTransferEntity entity = counterRepo.findByTrxDate(LocalDate.now())
				.orElse(TCounterTransferEntity.builder()
						.count(0).trxDate(LocalDate.now()).build());
		entity.setCount(entity.getCount()+1);
		counterRepo.save(entity);
	}
}
