package com.kahago.kahagoservice.component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.kahago.kahagoservice.entity.MBankDepositEntity;
import com.kahago.kahagoservice.entity.MUniqTransferEntity;
import com.kahago.kahagoservice.entity.TDepositEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestEntity;
import com.kahago.kahagoservice.entity.TRespTransferEntity;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.response.OptionPaymentListResponse;
import com.kahago.kahagoservice.repository.MUniqTransferRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.repository.TRespTransferRepo;
import com.kahago.kahagoservice.util.DateTimeUtil;

@Component
public class TransferComponent {
	
	private static final Logger log = LoggerFactory.getLogger(TransferComponent.class);
	@Autowired
	private MUniqTransferRepo uniqTrfRepo;
	@Autowired
	private TPaymentRepo payRepo;
	@Autowired
	private TRespTransferRepo respTrfRepo;
	public String getUniqTransfer(BigDecimal amount) {
		log.info("==> get unik transfer <==");
		long base = 1000;
//		String sql = "Select nominal from m_uniq_transfer "
//				+ "where status = 0 and nominal <= ? order by seqid desc limit 1";
		double amt = amount.doubleValue() % base;
		if(amt<=0) {
			amt = base - 1;
		}
		amt--;
		List<MUniqTransferEntity> lstrf = uniqTrfRepo.findAllByStatusNominal(0, new BigDecimal(amt));
		MUniqTransferEntity trf = lstrf.stream().findFirst().get();
		long nominal = trf.getNominal().longValue();

		trf.setStatus(1);
		uniqTrfRepo.save(trf);
		String hasil = String.valueOf(amt - nominal);
		log.info("==> Uniq transfer => "+hasil);
		return hasil;
	}
	
	public OptionPaymentListResponse doTransfer(TPaymentEntity pays,MBankDepositEntity bank,String tiket, Double limitransfer) {
		BigDecimal amount = pays.getAmount();
		String uniq = (pays.getAmountUniq().doubleValue()<=0)?getUniqTransfer(amount):String.valueOf(pays.getInsufficientFund().toString());
		BigDecimal pretotal = amount;
		BigDecimal total = pretotal.subtract(new BigDecimal(uniq));
		if(total.doubleValue() < limitransfer) throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,ResponseStatus.LIMIT_TRANSFER.getReasonPhrase());
		TPaymentEntity pay = pays;
		String imagePath = "/api/bank/logo/"+bank.getBankId().getBankCode()+".png";
		return OptionPaymentListResponse.builder()
				.accountName(bank.getAccName())
				.accountNo(bank.getAccNo())
				.imageBank(imagePath)
				.nominal(pretotal.toString())
				.noTiket(tiket)
				.uniqNumber(uniq)
				.statusUniq("-")
				.totalNominal(total.toString())
				.endTime(DateTimeUtil.getDateTime("EEEE, dd MMMMM yyyy")
						.concat(" ")
						.concat(pay.getPickupTimeId().getTimeFrom().format(DateTimeFormatter.ofPattern("kk:mm:ss"))))
				.build();
	}
	
	public OptionPaymentListResponse doTransferReqPickup(TPickupOrderRequestEntity pick,BigDecimal totalAmt,MBankDepositEntity bank,String tiket) {
		BigDecimal amount = totalAmt;
		String uniq = getUniqTransfer(amount);
		BigDecimal total = amount.subtract(new BigDecimal(uniq));
		String imagePath = "api/bank/logo/"+bank.getBankId().getBankCode()+".png";
		return OptionPaymentListResponse.builder()
				.accountName(bank.getAccName())
				.accountNo(bank.getAccNo())
				.imageBank(imagePath)
				.nominal(amount.toString())
				.noTiket(tiket)
				.uniqNumber(uniq)
				.statusUniq("-")
				.totalNominal(total.toString())
				.endTime(DateTimeUtil.getDateTime("EEEE, dd MMMMM yyyy")
						.concat(" - ")
						.concat(pick.getPickupTimeEntity().getTimeFrom().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
						)
				.build();
	}
	
	public OptionPaymentListResponse doTransferReqDeposit(BigDecimal totalAmt,MBankDepositEntity bank,String tiket) {
		BigDecimal amount = totalAmt;
		String uniq = "0";
		if(bank.getIsRobot()==true) {
			uniq = getUniqTransfer(amount);
		}
		BigDecimal total = amount.subtract(new BigDecimal(uniq));
		String imagePath = "api/bank/logo/"+bank.getBankId().getBankCode()+".png";
		return OptionPaymentListResponse.builder()
				.accountName(bank.getAccName())
				.accountNo(bank.getAccNo())
				.imageBank(imagePath)
				.nominal(amount.toString())
				.noTiket(tiket)
				.uniqNumber(uniq)
				.statusUniq("-")
				.totalNominal(total.toString())
				.endTime(DateTimeUtil.getDateTime("EEEE, dd MMMMM yyyy")
						.concat(" - ")
						.concat("21:00:00"))
				.build();
	}
	
}
