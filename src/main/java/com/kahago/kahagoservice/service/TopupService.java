package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.client.FaspayFeignService;
import com.kahago.kahagoservice.client.PawoonFeignService;
import com.kahago.kahagoservice.client.model.response.ResponseModel;
import com.kahago.kahagoservice.component.TransferComponent;
import com.kahago.kahagoservice.entity.MBankDepositEntity;
import com.kahago.kahagoservice.entity.MUserEntity;
import com.kahago.kahagoservice.entity.TCreditEntity;
import com.kahago.kahagoservice.entity.TDepositEntity;
import com.kahago.kahagoservice.entity.TMutasiEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.enummodel.DepositEnum;
import com.kahago.kahagoservice.enummodel.DepositTypeEnum;
import com.kahago.kahagoservice.enummodel.DeviceEnum;
import com.kahago.kahagoservice.enummodel.MutasiEnum;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.enummodel.TypeTrxEnum;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.dto.MutasiDompetDto;
import com.kahago.kahagoservice.model.request.DepositRequest;
import com.kahago.kahagoservice.model.request.MutasiReq;
import com.kahago.kahagoservice.model.request.OptionPaymentReq;
import com.kahago.kahagoservice.model.response.OptionPaymentListResponse;
import com.kahago.kahagoservice.repository.MBankDepositRepo;
import com.kahago.kahagoservice.repository.MUserRepo;
import com.kahago.kahagoservice.repository.TCreditRepo;
import com.kahago.kahagoservice.repository.TDepositRepo;
import com.kahago.kahagoservice.repository.TMutasiRepo;
import com.kahago.kahagoservice.repository.TOptionPaymentRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.util.Common;
import com.kahago.kahagoservice.util.CommonConstant;
import com.kahago.kahagoservice.util.DateTimeUtil;

import lombok.SneakyThrows;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

/**
 * @author bangd ON 21/11/2019
 * @project com.kahago.kahagoservice.service
 */
@Service
public class TopupService {
	
	private static final Logger log = LoggerFactory.getLogger(TopupService.class);

    @Autowired
    private TMutasiRepo tMutasiRepo;

    @Autowired
    private TDepositRepo depositRepo;

    @Autowired
    private TPaymentRepo paymentRepo;
    
    @Autowired
    private MBankDepositRepo mDepositRepo;
    @Autowired
    private DepositService depService;
    @Autowired
    private PawoonFeignService pawonService;
    @Autowired
    private TransferComponent transfer;
    @Autowired
    private FaspayFeignService faspayService;
    
    @Autowired
    private OptionPaymentService opService;
    @Value("${transfer.nominal.limit}")
	private BigDecimal limitTransfer;
	@Value("${url.service.pawoon}")
	private String urlPawoon;
    @Autowired
    private TOptionPaymentRepo tOPRepo;
    @Autowired
    private MUserRepo userRepo;
    @Autowired
    private TCreditRepo creditRepo;
    
    private Boolean isMobile = true;

    public Page<MutasiDompetDto> getMutasiDto(Pageable pageable, MutasiReq mutasiReq){
        Page<MutasiDompetDto> mutasiDtos=null;
        List<Integer> ltrxtype = new ArrayList<Integer>();
        if(mutasiReq.getTrxType()==null)mutasiReq.setTrxType(0);
        try{
            if(mutasiReq.getTrxType().equals(0)) {
            	ltrxtype.add(0);
            	ltrxtype.add(1);
            	ltrxtype.add(2);
            	ltrxtype.add(3);
            	ltrxtype.add(4);
            	ltrxtype.add(5);
            }else if(mutasiReq.getTrxType().equals(1)) {
            	ltrxtype.add(1);
            }else if(mutasiReq.getTrxType().equals(2)) {
            	ltrxtype.add(2);
            	ltrxtype.add(3);
            	ltrxtype.add(4);
            	ltrxtype.add(5);
            }
            Page<TMutasiEntity> entities=tMutasiRepo.findAllByParameter(
                    mutasiReq.getUserId(),
                    DateTimeUtil.getDateFrom(mutasiReq.getStartDate(),"yyyyMMdd"),
                    DateTimeUtil.getDateFrom(mutasiReq.getEndDate(),"yyyyMMdd"),
                    ltrxtype,
                    pageable);
            
            mutasiDtos=new PageImpl<>(
                    entities.getContent().stream().map(this::toMutasiDto).collect(Collectors.toList()),
                    entities.getPageable(),
                    entities.getTotalElements()
            );
            
        }catch (Exception e){

        }
        if(mutasiDtos.getTotalElements()==0)throw new NotFoundException("Data Tidak Ditemukan!");
        return mutasiDtos;
    }

    @SneakyThrows
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.DEFAULT)
    public OptionPaymentListResponse topUpSaldo(DepositRequest depositRequest,DeviceEnum devEnum){
    
    	log.info("==> Request Deposit <==");
    	log.info(Common.json2String(depositRequest));
    	String typeTrx = Optional.ofNullable(depositRequest.getTypeTrx()).orElse("0");
        if(depositRequest.getBankDepCode()==null) depositRequest.setBankDepCode(depositRequest.getPaymentOption());
        this.isMobile = true;
        TDepositEntity depositEntity = null;
        MBankDepositEntity bankDep = null;
        if(DeviceEnum.WEB==devEnum) isMobile = false;
        String tiketNo = null;
        if(depositRequest.getNominal().doubleValue()<=0)
        	throw new NotFoundException("Nominal Tidak Boleh Kurang Dari 1 ");
        if(!depositRequest.getBankDepCode().equalsIgnoreCase("dompet")) {
        	bankDep = mDepositRepo.findByBankId(depositRequest.getBankDepCode())
            		.stream().findAny().orElse(null);
            if(bankDep==null) bankDep = mDepositRepo.findById(Integer.valueOf(depositRequest.getBankDepCode())).get();
            depositRequest.setBankDepCode(bankDep.getBankId().getBankCode().toLowerCase());
            if(bankDep.getIsBank()) {
            	depositEntity=checkPandingTopUp(depositRequest.getUserId(), depositRequest.getNominal());
                if(depositEntity!=null && TypeTrxEnum.DEPOSIT==TypeTrxEnum.getEnum(Integer.valueOf(typeTrx))){
                    throw new NotFoundException("Masih Terdapat Deposit Sebanyak "+depositEntity.getNominal()+" Yang Pending");
                }
        		if(depositRequest.getNominal().doubleValue() 
            			< bankDep.getMinNominal()) {
            		throw new NotFoundException("Minimum Transfer "+bankDep.getMinNominal());
            	}
        		depositRequest.setBankDepCode("transfer");
        	}
            double nominal =  depositRequest.getNominal().doubleValue();
            String description = depositRequest.getDescription();
            List<TPaymentEntity> lspay = null;
            if(TypeTrxEnum.PAYMENT==TypeTrxEnum.getEnum(Integer.valueOf(typeTrx))) {
            	typeTrx="-1";
            	List<String> books = depositRequest.getBooks().stream().map(p-> p.getBookingCode()).collect(Collectors.toList());
            	lspay = paymentRepo.findAllById(books);
            	nominal = lspay.stream().mapToDouble(p-> p.getAmount().doubleValue()).sum();
            	description = StringUtils.join(books, ",");
            	depositRepo.updateByDescription(description, depositRequest.getUserId());
            }
            MUserEntity user = userRepo.findById(depositRequest.getUserId()).get();
            depositEntity=TDepositEntity.builder()
                    .userId(user)
                    .nominal(new BigDecimal(nominal))
                    .nominalApproval(0)
                    .bankDepCode(bankDep)
                    .lastUser(depositRequest.getUserId())
                    .description(description)
                    .insufficientFund(0)
                    .status(Integer.valueOf(typeTrx))
                    .trxRequest(LocalDateTime.now())
                    .trxServer(LocalDateTime.now())
                    .build();
            depositEntity =depService.getTiketDeposit(depositEntity);
            tiketNo = depositEntity.getTiketNo();
            if("1".equalsIgnoreCase(user.getDepositType())) {
            	TCreditEntity credit = creditRepo
            			.findByUserIdAndFlagAndTglOrderBySeqDesc(user.getUserId(), "0", DateTimeUtil.getDateFrom(depositRequest.getDate(),"yyyy-MM-dd"))
            			.orElseThrow(()-> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Tagihan Tidak Ditemukan atau sudah dilakukan pengajuan"));
            	credit.setTiketNo(tiketNo);
            	creditRepo.save(credit);
            }
            if(typeTrx.equals("-1")) {
//            	lspay.forEach(tiketNoPay(tiketNo));
//            	paymentRepo.saveAll(lspay);
            	depositRepo.save(depositEntity);
            	return OptionPaymentListResponse.builder()
                      .bankCode(depositEntity.getBankDepCode().getBankId().getBankCode().toString())
                      .bankName(depositEntity.getBankDepCode().getBankId().getName())
                      .accountNo(depositEntity.getBankDepCode().getAccNo())
                      .accountName(depositEntity.getBankDepCode().getAccName())
                      .noTiket(depositEntity.getTiketNo())
                      .screen(CommonConstant.CONFIRM_DEPOSIT.replaceAll("#tiket", depositEntity.getTiketNo()))
                      .imageBank(depositEntity.getBankDepCode().getBankId().getImagePath())
                      .nominal(depositEntity.getNominal().toString())
                      .build(); 
            }
        }
        
//        BigDecimal totalAmmount=new BigDecimal(0);
//        if(depositRequest.getBooks()!=null) {
//        	List<String> lsbooks = depositRequest.getBooks().stream().filter(p-> p.getTypeTrx().equals("1")).map(s -> s.getBookingCode()).collect(Collectors.toList());
//            List<TPaymentEntity> entityList = paymentRepo.findByBookingCodeInAndStatus(lsbooks, PaymentEnum.PENDING.getCode());
//            if(entityList.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseStatus.NOT_FOUND.getReasonPhrase());
//            if(entityList.size()>1) {
//            	tiketNo = opService.getNoTiketCounter();
//            }else if(tiketNo==null) {
//            	tiketNo = entityList.get(0).getBookingCode();
//            }
//            for (TPaymentEntity entity:entityList) {
//                entity.setNoTiket(Optional.ofNullable(tiketNo).orElse(entity.getBookingCode()));
//                totalAmmount.add(entity.getAmount());
//            }
//            if(!depositRequest.getBankDepCode().equalsIgnoreCase("dompet")) {
//            	depositRequest.setNominal(totalAmmount);
//                depositEntity.setDescription(lsbooks.toString());
//                depositEntity.setTiketNo("P"+depositEntity.getTiketNo());
//                depositEntity.setStatus(DepositEnum.REQ_PAY.getValue());
//                depositEntity.setNominal(totalAmmount);
//                depositRepo.save(depositEntity);
//            }else {
//            	depositEntity = new TDepositEntity();
//            }
//            OptionPaymentReq req = OptionPaymentReq.builder()
//            		.books(depositRequest.getBooks())
//            		.paymentOption(depositRequest.getBankDepCode())
//            		.build();
//            
//            MUserEntity user = entityList.stream().findFirst().get().getUserId();
//            return opService.todoVendor(req, tiketNo, entityList, totalAmmount.doubleValue(), user,depositEntity.getBankDepCode());
////            paymentRepo.saveAll(entityList);
//        }
        return this.todoVendorDep(depositRequest, depositEntity.getTiketNo(),  
        		depositEntity.getUserId(), bankDep,depositEntity);
        
        
//        return OptionPaymentListResponse.builder()
//                .bankCode(depositEntity.getBankDepCode().getBankId().getBankCode().toString())
//                .bankName(depositEntity.getBankDepCode().getBankId().getName())
//                .accountNo(depositEntity.getBankDepCode().getAccNo())
//                .accountName(depositEntity.getBankDepCode().getAccName())
//                .noTiket(depositEntity.getTiketNo())
//                .screen(CommonConstant.CONFIRM_DEPOSIT.replaceAll("#tiket", depositEntity.getTiketNo()))
//                .imageBank(depositEntity.getBankDepCode().getBankId().getImagePath())
//                .nominal(depositRequest.getNominal().toString())
//                .build();
    }

	private Consumer<? super TPaymentEntity> tiketNoPay(String tiketNo) {
		return p->p.setNoTiket(tiketNo);
	}

    private TDepositEntity checkPandingTopUp(String userId,BigDecimal nominal){
        return depositRepo.findFirstByUserIdAndStatusAndTrxRequestAndNominal(userId, DepositEnum.REQUEST.getValue(),nominal);
    }

    private MutasiDompetDto toMutasiDto(TMutasiEntity tMutasiEntity){
        DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss");
        MutasiDompetDto mutasiDto= MutasiDompetDto.builder()
                .userId(tMutasiEntity.getUserId().getUserId())
                .trxNo(tMutasiEntity.getTrxNo())
                .descr(tMutasiEntity.getDescr())
                .trxDate(tMutasiEntity.getTrxDate())
                .trxTime(tMutasiEntity.getTrxTime())
                .trxLocalDateTime(LocalDateTime.of(tMutasiEntity.getTrxDate(),tMutasiEntity.getTrxTime()).format(customFormatter))
                .trxType(tMutasiEntity.getTrxType())
                .mutasiEnum(MutasiEnum.getMutasiEnum(tMutasiEntity.getTrxType()))
                .sisaSaldo(tMutasiEntity.getSaldo().multiply(new BigDecimal(-1)))
                .build();
        if(Common.moreThan(tMutasiEntity.getAmount(),new BigDecimal(0))) {
            mutasiDto.setDebet(tMutasiEntity.getAmount().toString());
            mutasiDto.setKredit("");
        }else{
            mutasiDto.setDebet("");
            mutasiDto.setKredit(tMutasiEntity.getAmount().toString());
        }
        return mutasiDto;

    }

    private String getNoTiket(){
        Date date = Calendar.getInstance().getTime();
        String tiket = "0";
        String count = "0000";
        DateFormat formatter = new SimpleDateFormat("yyMMdd");
        String tgl = formatter.format(date);
        System.out.println("Today " + tgl);
        TDepositEntity entity=depositRepo.findFirstByTiketNoLike(tgl);
        tiket=entity.getTiketNo();
        count=Common.getCounter(tiket,6,10);
        return tgl.concat(count);
    }
    
    private OptionPaymentListResponse todoVendorDep(DepositRequest req, String tiket,
			MUserEntity user, MBankDepositEntity bank,TDepositEntity dep) {
		String urlPayment = null;
		String urlLogin = null;
		String flagOption="2";
		String flagTrx = "1";
		ResponseModel resp = new ResponseModel();
		OptionPaymentListResponse opresp = null;
		TPaymentEntity pay = TPaymentEntity.builder()
				.amount(req.getNominal())
				.userId(user)
				.trxDate(LocalDate.now())
				.paymentOption(req.getBankDepCode())
				.grossWeight(new Long("0"))
				.volume(new Long("0"))
				.jumlahLembar(0)
				.bookingCode(tiket)
				.senderEmail(user.getUserId())
				.senderTelp(user.getHp())
				.senderName(user.getName())
				.senderAddress("KAHA Go")
				.build();
		String paymentCode = req.getBankDepCode();
		switch (paymentCode) {
		case "transfer":
			flagOption = "1";
			opresp = transfer.doTransferReqDeposit(req.getNominal(), bank, tiket);
			opresp.setFlagSentOption(flagOption);
//			Double diff = req.getNominal().doubleValue() - Double.valueOf(opresp.getUniqNumber());
			dep.setInsufficientFund(Double.valueOf(opresp.getUniqNumber()).intValue());
			depositRepo.save(dep);
			opresp.setFlagTrx(flagTrx);
			return opresp;
		case "ovo":
			pay.setTenorPayment("0");
			pay.setTypePayment(tOPRepo.findByUserCategoryAndOP(user.getUserCategory(), paymentCode)
					.get().getOptionPayment().getOperatorSw());
			urlPayment= faspayService.sendPaymentAndroid(opService.getReqPayment(pay)).getRedirectUrl();
			break;
		case "akulaku":
			pay.setTenorPayment("0");
			pay.setTypePayment(tOPRepo.findByUserCategoryAndOP(user.getUserCategory(), paymentCode)
					.get().getOptionPayment().getOperatorSw());
			pay.setPhoneNumber(req.getPhonePayment());
			urlPayment= faspayService.sendPaymentAndroid(opService.getReqPayment(pay)).getRedirectUrl();
			break;
		case "kredivo":
			pay.setTenorPayment("0");
			pay.setTypePayment(tOPRepo.findByUserCategoryAndOP(user.getUserCategory(), paymentCode)
					.get().getOptionPayment().getOperatorSw());
			pay.setPhoneNumber(req.getPhonePayment());
			urlPayment= faspayService.sendPaymentAndroid(opService.getReqPayment(pay)).getRedirectUrl();
			break;
		case "linkaja":
			pay.setTenorPayment("0");
			pay.setTypePayment(tOPRepo.findByUserCategoryAndOP(user.getUserCategory(), paymentCode)
					.get().getOptionPayment().getOperatorSw());
			urlPayment= faspayService.sendPaymentAndroid(opService.getReqPayment(pay)).getRedirectUrl();			
			break;
		case "shopeepay":
			pay.setTenorPayment("0");
			pay.setTypePayment(tOPRepo.findByUserCategoryAndOP(user.getUserCategory(), paymentCode)
					.get().getOptionPayment().getOperatorSw());
			ResponseModel respFaspay = faspayService.sendPaymentAndroid(opService.getReqPayment(pay));
			log.info("response shopee pay ==> "+Common.json2String(respFaspay));
			urlPayment= respFaspay.getDeeplink();			
			break;
		case "gopay":
			pay.setTenorPayment("0");
			pay.setTypePayment(tOPRepo.findByUserCategoryAndOP(user.getUserCategory(), paymentCode)
					.get().getOptionPayment().getOperatorSw());
			String endPoint = "/requestPawon/android";
			if(isMobile==false) endPoint = "/requestPawon/web";
			URI uri = URI.create(urlPawoon+endPoint);
			resp = pawonService.requetPaymentAndroid(uri,opService.getReqPayment(pay));
			urlPayment= resp.getRedirectUrl();
			dep.setIdPayment(resp.getIdPayment());
			dep.setIdTicket(resp.getTiketId());
			dep.setIsConfirmTransfer((byte) 2);
			break;
		default:
			break;
		}
		
		depositRepo.save(dep);
		return OptionPaymentListResponse.builder()
				.urllogin(urlLogin)
				.urlpayment(urlPayment)
				.senderName(pay.getSenderName())
				.receiverName(pay.getReceiverName())
				.bookingCode(pay.getNoTiket())
				.flagSentOption(flagOption)
				.urlpayment(urlPayment)
				.flagTrx(flagTrx)
				.tiketTopup(tiket)
				.build();
		
	}

}
