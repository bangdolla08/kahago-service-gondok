package com.kahago.kahagoservice.schedulling;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kahago.kahagoservice.component.PawoonComponent;
import com.kahago.kahagoservice.entity.MBankDepositEntity;
import com.kahago.kahagoservice.entity.MUserEntity;
import com.kahago.kahagoservice.entity.TDepositEntity;
import com.kahago.kahagoservice.entity.TMutasiEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestDetailEntity;
import com.kahago.kahagoservice.enummodel.DepositEnum;
import com.kahago.kahagoservice.enummodel.MutasiEnum;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.enummodel.StatusPayEnum;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.repository.MBankDepositRepo;
import com.kahago.kahagoservice.repository.MProductSwitcherRepo;
import com.kahago.kahagoservice.repository.MUserRepo;
import com.kahago.kahagoservice.repository.TDepositRepo;
import com.kahago.kahagoservice.repository.TMutasiRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestDetailRepo;
import com.kahago.kahagoservice.service.BookService;
import com.kahago.kahagoservice.service.HistoryTransactionService;
import com.kahago.kahagoservice.service.OptionPaymentService;
import com.kahago.kahagoservice.service.PaymentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PawoonSchedulling {
	@Autowired
	private TPaymentRepo payRepo;
	@Autowired
	private PaymentService payService;
	@Autowired
	private TDepositRepo depRepo;
	@Autowired
	private MBankDepositRepo bankDepRepo;
	@Autowired
	private PawoonComponent pawoonComp;
	@Autowired
	private MProductSwitcherRepo pswRepo;
	@Autowired
    private TMutasiRepo transMutasiRepo;
	@Autowired
	private MUserRepo userRepo;
	@Autowired
	private BookService bookService;
	@Autowired
	private TPickupOrderRequestDetailRepo podReqRepo;
	@Autowired
	private HistoryTransactionService histService;
	@Autowired
	private OptionPaymentService opService;
	@Autowired
	private PaymentSchedulling paymentComp;
	@Scheduled(cron="*/15 * * * * ?")
	public void doCheckStatus() {
		log.info("===> Cek status Pawoon <===");
		List<Integer> lsStatus = new ArrayList<>();
		lsStatus.add(PaymentEnum.PENDING.getCode());
		lsStatus.add(PaymentEnum.UNPAID_RECEIVE.getCode());
		lsStatus.add(PaymentEnum.HOLD_BY_ADMIN.getCode());
		List<TPaymentEntity> lspay = payRepo.
				findAllByStatusAndTimeAndPaymentOption(lsStatus, 
						"gopay",7);
		
		lspay.stream().forEach(doCheck(lsStatus));
		for(TPaymentEntity pay:lspay) {
			pay = payRepo.findById(pay.getBookingCode()).get();
			PaymentEnum stat = PaymentEnum.getPaymentEnum(pay.getStatus());
			if(stat==PaymentEnum.REQUEST) {
				payService.createHistory(pay, PaymentEnum.PENDING, stat);
			}else if(stat==PaymentEnum.FINISH_INPUT_AND_PAID) {
				payService.createHistory(pay, PaymentEnum.UNPAID_RECEIVE, stat);
			}
		}
		
	}
	
	public void doCheckStatusRequestPickup() {
		log.info("===> Cek status Pawoon <===");
		List<Integer> lsStatus = new ArrayList<>();
		lsStatus.add(PaymentEnum.PENDING.getCode());
		List<TPaymentEntity> lspay = payRepo.
				findAllByStatusAndTimeAndPaymentOptionGopay(PaymentEnum.PENDING.getCode(), 
						"gopay",7);
		
		lspay.stream().forEach(doCheck(lsStatus));
	}

	@Scheduled(cron="*/20 * * * * ?")
	public void doResetStatus() {
		log.info("===> Reset Transaksi <===");
		List<TPaymentEntity> lspay = payRepo.
				findByPaymentOptionAndStatusAndIsConfirmTransfer("gopay",PaymentEnum.PENDING.getCode(),(byte) 2);
		lspay.stream().forEach(payService.resetPayment());
		payRepo.saveAll(lspay);
	}
	@Scheduled(cron="*/10 * * * * ?")
	public void doCheckStatusDeposit() {
		log.info("===> Cek status Deposit Pawoon <===");
		List<TDepositEntity> lsdep = depRepo
				.findByBankDepCode(bankDepRepo.findByBankId("gopay").stream().findAny().get());
		lsdep = lsdep.stream().filter(d->d.getIdTicket()!=null && d.getIdPayment()!=null).collect(Collectors.toList());
		lsdep.stream().forEach(doCheckDep());
	}
	@Scheduled(cron="*/11 * * * * ?")
	public void doCheckStatusPickupOrder() {
		log.info("===> Cek status Pickup Order Pawoon <===");
		List<TPickupOrderRequestDetailEntity> lsPick = podReqRepo.findAllByStatusPayAndPaymentOption(StatusPayEnum.VERIFICATION.getCode(), "gopay");
		lsPick = lsPick.stream().filter(d->d.getIdTicket()!=null && d.getIdPayment()!=null).collect(Collectors.toList());
		lsPick.stream().forEach(doCheckPickup());
	}
	private Consumer<? super TPickupOrderRequestDetailEntity> doCheckPickup() {
		// TODO Auto-generated method stub
		return p->{
			Integer count = Optional.ofNullable(p.getCountPawoon()).orElse(0) + 1;
			p.setCountPawoon(count);
			p.setUpdateDate(LocalDateTime.now());
			p.setUpdateBy("SYS");
//			p.setIsPay(StatusPayEnum.PAID.getCode());
			List<TPickupOrderRequestDetailEntity> lspickTicket = podReqRepo.findAllByIdTicket(p.getIdTicket());
			lspickTicket.stream().forEach(t-> t.setCountPawoon(count));
			List<TPaymentEntity> lspayTicket = new ArrayList<>();
			boolean status = pawoonComp.getStatusPayment(lspayTicket, count.toString(),lspickTicket);
			if(status) {
				lspickTicket.stream().forEach(t->t.setIsPay(StatusPayEnum.PAID.getCode()));
			}
			podReqRepo.saveAll(lspickTicket);
			
		};
	}

	private Consumer<? super TDepositEntity> doCheckDep(){
		return d->{
			Integer count = Optional.ofNullable(d.getCountPawoon()).orElse(0) + 1;
			d.setCountPawoon(count);
			Response resp = pawoonComp.getStatusDep(d, count);
			if(resp.getRc()==ResponseStatus.OK.value()) {
				d.setTrxServer(LocalDateTime.now());
				d.setTrxApprove(LocalDateTime.now());
				d.setUserApprove("SYS");
				d.setStatus(DepositEnum.PROSES_TOPUP.getValue());
				d.setIsConfirmTransfer((byte) 1);
				saveTrxDeposit(d,"Gopay");
			}
			
			depRepo.save(d);
		};
	}

	public void saveTrxDeposit(TDepositEntity d,String bankcode) {
		TPaymentEntity payment = TPaymentEntity.builder()
				.amount(d.getNominal().multiply(new BigDecimal("-1")))
				.userId(d.getUserId())
				.productSwCode(pswRepo.findBySwictherCode(300).stream().findAny().get())
				.build();
		TMutasiEntity mutasi = bookService.insertMutasi(payment, d.getTiketNo(), MutasiEnum.DEPOSIT.getKeterangan().concat(" ")
				.concat(bankcode), MutasiEnum.DEPOSIT);
		MUserEntity user = bookService.updateBalanceUser(mutasi.getUserId().getUserId(), mutasi.getSaldo().doubleValue());
		transMutasiRepo.save(mutasi);
		userRepo.save(user);
	}
	private Consumer<? super TPaymentEntity> doCheck(List<Integer> lsstatus) {
		// TODO Auto-generated method stub
		return p->{
			log.info("Update status: "+p.getBookingCode());
			Integer count = p.getCountPawoon() + 1;
			p.setCountPawoon(count);
			List<TPaymentEntity> lspayTicket = payRepo.findByIdTicketAndStatusIn(p.getIdTicket(), lsstatus);
			List<TPickupOrderRequestDetailEntity> lspickTicket = podReqRepo.findAllByIdTicket(p.getIdTicket());
			lspayTicket.stream().forEach(pt -> pt.setCountPawoon(count));
			boolean status = pawoonComp.getStatusPayment(lspayTicket, count.toString(),lspickTicket);
			if(status) {
				lspayTicket.stream().forEach(opService.updatePaymentWH(p.getNoTiket(),PaymentEnum.REQUEST));
				lspickTicket.stream().forEach(t->t.setIsPay(StatusPayEnum.PAID.getCode()));
			}
			payRepo.saveAll(lspayTicket);
			podReqRepo.saveAll(lspickTicket);
			
		};
	}
}
