package com.kahago.kahagoservice.schedulling;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kahago.kahagoservice.broadcast.MailerComponent;
import com.kahago.kahagoservice.entity.MBankDepositEntity;
import com.kahago.kahagoservice.entity.TDepositEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.enummodel.DepositEnum;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.StatusPayEnum;
import com.kahago.kahagoservice.repository.MBankDepositRepo;
import com.kahago.kahagoservice.repository.TDepositRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.service.DepositService;
import com.kahago.kahagoservice.util.Common;

import lombok.SneakyThrows;

@Component
public class TransferEmailSchedulling {
	
	private static final Logger log = LoggerFactory.getLogger(TransferEmailSchedulling.class);
	
	@Autowired
	private TPaymentRepo payRepo;
	@Autowired
	private MBankDepositRepo bankDepRepo;
	@Autowired
	private MailerComponent mailService;
	@Autowired
	private DepositService depService;
	@Autowired
	private TDepositRepo depRepo;
	
	@Value("${email.to}")
	private String emailto;
	@Value("${email.cc}")
	private String emailcc;
	@Value("${url.boc}")
	private String urlboc;
	
	@Scheduled(cron="0/10 * 5-23 * * ?")
	@SneakyThrows
	public void doEmailPay() {
		log.info("===> Cek Payment Transfer Email <===");
		List<TPaymentEntity> lspay = payRepo.findByStatusAndIsConfirmTransferAndTimeDiff(PaymentEnum.PENDING.getCode(), (byte) 2, LocalDate.now());
		lspay.forEach(send2Email());
	}

	@Scheduled(cron="0/10 * 5-23 * * ?")
	@SneakyThrows
	public void doEmailDeposit() {
		log.info("====> Cek Deposit Transfer Email <===");
		List<TDepositEntity> lsdep = depRepo.findAllByInsufficientAndIsConfirm();
		lsdep.forEach(send2EmailDeposit());
	}
	
	
	private Consumer<? super TDepositEntity> send2EmailDeposit() {
		// TODO Auto-generated method stub
		return p -> {
			
		};
	}

	@SneakyThrows
	private Consumer<? super TPaymentEntity> send2Email() {
		return p -> {
			MBankDepositEntity bank = bankDepRepo.findById(Integer.valueOf(p.getPaymentOption())).orElse(null);
			if(bank!=null) {
				String  content = mailService.getContent();
				content = content.replace("#judul", "PEMBAYARAN VIA TRANSFER")
							.replace("#notiket", p.getNoTiket())
							.replace("#userid", p.getUserId().getUserId())
							.replace("#nominal", Common.getCurrIDR(p.getAmountUniq().doubleValue()))
							.replace("#namabank", bank.getBankId().getName())
							.replace("#noakun", bank.getAccNo())
							.replace("#namaakun", bank.getAccName())
							.replace("#ket", "Nominal Unik "+p.getAmountUniq().toString())
							.replace("#urlboc", urlboc);
				try {
					mailService.sendNotifDeposit(emailto, emailcc, content, MailerComponent.SUBJECT_PAY);
				} catch (AddressException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				List<TPaymentEntity> lspay = payRepo.findByNoTiket(p.getNoTiket());
				List<String> lsBook = lspay.stream().map(b -> b.getBookingCode()).collect(Collectors.toList());
				//insert to deposit
				TDepositEntity deposit = TDepositEntity.builder()
						.bankDepCode(bank)
						.description(StringUtils.join(lsBook, ","))
						.trxServer(LocalDateTime.now())
						.trxRequest(LocalDateTime.now())
						.nominal(p.getAmountUniq())
						.isConfirmTransfer((byte)1)
						.insufficientFund(0)
						.status(DepositEnum.REQUEST.getValue())
						.lastUpdate(LocalDateTime.now())
						.lastUser(p.getUserId().getUserId())
						.userId(p.getUserId())
						.build();
				deposit = depService.getTiketDeposit(deposit);
				depService.save(deposit);
				
				lspay.forEach(updateTiket(deposit));
				payRepo.saveAll(lspay);
				
			}
		};
	}

	private Consumer<? super TPaymentEntity> updateTiket(TDepositEntity deposit) {
		return o -> {
			o.setIsConfirmTransfer((byte)1);
			o.setNoTiket(deposit.getTiketNo());
		};
	}

}
