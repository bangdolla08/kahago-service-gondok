package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.client.FaspayFeignService;
import com.kahago.kahagoservice.client.PawoonFeignService;
import com.kahago.kahagoservice.client.model.request.Payment;
import com.kahago.kahagoservice.client.model.request.ReqPayment;
import com.kahago.kahagoservice.client.model.response.ResponseModel;
import com.kahago.kahagoservice.component.TransferComponent;
import com.kahago.kahagoservice.entity.MBankDepositEntity;
import com.kahago.kahagoservice.entity.MCounterEntity;
import com.kahago.kahagoservice.entity.MCouponDiscountEntity;
import com.kahago.kahagoservice.entity.MOptionPaymentEntity;
import com.kahago.kahagoservice.entity.MPickupTimeEntity;
import com.kahago.kahagoservice.entity.MProductSwitcherEntity;
import com.kahago.kahagoservice.entity.MUserEntity;
import com.kahago.kahagoservice.entity.TCreditEntity;
import com.kahago.kahagoservice.entity.TDepositEntity;
import com.kahago.kahagoservice.entity.TDiscountEntity;
import com.kahago.kahagoservice.entity.TManifestEntity;
import com.kahago.kahagoservice.entity.TMutasiEntity;
import com.kahago.kahagoservice.entity.TOptionPaymentEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.entity.TPaymentHistoryEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestDetailEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestEntity;
import com.kahago.kahagoservice.enummodel.DepositTypeEnum;
import com.kahago.kahagoservice.enummodel.DeviceEnum;
import com.kahago.kahagoservice.enummodel.MutasiEnum;
import com.kahago.kahagoservice.enummodel.OptionPaymentEnum;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.PickupDetailEnum;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.enummodel.StatusPayEnum;
import com.kahago.kahagoservice.exception.InternalServerException;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.dto.UserDto;
import com.kahago.kahagoservice.model.request.DetailBooking;
import com.kahago.kahagoservice.model.request.DetailBooks;
import com.kahago.kahagoservice.model.request.OptionPaymentReq;
import com.kahago.kahagoservice.model.response.OptionPaymentListResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.repository.MBankDepositRepo;
import com.kahago.kahagoservice.repository.MCounterRepo;
import com.kahago.kahagoservice.repository.MCouponDiscountRepo;
import com.kahago.kahagoservice.repository.TCreditRepo;
import com.kahago.kahagoservice.repository.TDepositRepo;
import com.kahago.kahagoservice.repository.TDiscountRepo;
import com.kahago.kahagoservice.repository.TMutasiRepo;
import com.kahago.kahagoservice.repository.TOptionPaymentRepo;
import com.kahago.kahagoservice.repository.TPaymentHistoryRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestDetailRepo;
import com.kahago.kahagoservice.util.Common;
import com.kahago.kahagoservice.util.CommonConstant;
import com.kahago.kahagoservice.util.DateTimeUtil;

import lombok.Getter;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import static com.kahago.kahagoservice.util.ImageConstant.*;

/**
 * @author bangd ON 22/11/2019
 * @project com.kahago.kahagoservice.service
 */
@Service
public class OptionPaymentService {
	
	private static final Logger log = LoggerFactory.getLogger(OptionPaymentService.class);

	@Value("${transfer.nominal.limit}")
	private BigDecimal limitTransfer;
	@Value("${url.service.pawoon}")
	private String urlPawoon;
    @Autowired
    private TOptionPaymentRepo tOptionPaymentRepo;

    @Autowired
    private BankService bankService;
    @Autowired
    private BookService bookService;
    @Autowired
    private UserService userService;
    @Autowired
    private TOptionPaymentRepo tOPRepo;
    private UserDto userDto;
    @Autowired
    private MCounterRepo counterRepo;
    @Autowired
    private TPaymentRepo payRepo;
    @Autowired
    private MCouponDiscountRepo couponDiscRepo;
    @Autowired
    private MBankDepositRepo bankDepRepo;
    @Autowired
    private TMutasiRepo mutasiRepo;
    @Autowired
    private FaspayFeignService faspayService;
    @Autowired
    private PawoonFeignService pawonService;
    @Autowired
    private TransferComponent transfer;
    @Autowired
    private TPickupOrderRequestDetailRepo pickupOrderDetailRepo;
    @Autowired
    private RequestPickUpService reqPickService;
    @Autowired
    private TPaymentHistoryRepo payHistRepo;
    @Autowired
    private HistoryTransactionService historyService;
    @Autowired
    private TDepositRepo depRepo;
    @Getter
    private double persenDiskon = 0;
    private Boolean isRobot = true;
    private ResponseModel resp;
    @Autowired
    private TDiscountRepo discountRepo;
    @Autowired
    private DiscountService discService;
    @Autowired
    private TCreditRepo creditRepo;
    public List<OptionPaymentListResponse> getOptionPaymentList(OptionPaymentEnum optionPaymentEnum,
                                                                String userId, BigDecimal nominal,String userAgent){
        List<OptionPaymentListResponse> optionPayments=null;
        if(userAgent != null && userAgent.toUpperCase().equals("WEB")) {
        	return getListPaymentWeb(userId, optionPaymentEnum);
        }
        userDto=userService.getMUserEntity(userId);
        if(optionPaymentEnum==OptionPaymentEnum.PAYMENT)
            optionPayments=
                    tOptionPaymentRepo.finOptionPaymentPayment(userDto.getMUserEntity().getUserCategory().getSeqid(), LocalTime.now(),LocalTime.of(0,0)).stream().map(this::toOptionPaymentResponse).collect(Collectors.toList());
        else
            optionPayments=
                    tOptionPaymentRepo.finOptionPaymentTopup(userDto.getMUserEntity().getUserCategory().getSeqid(),LocalTime.now(),LocalTime.of(0,0)).stream().map(this::toOptionPaymentResponse).collect(Collectors.toList());
        return optionPayments;
    }

    private OptionPaymentListResponse toOptionPaymentResponse(TOptionPaymentEntity tOptionPaymentEntity){
    	String images = PREFIX_PATH_IMAGE_PAYMENT_OPTION+ tOptionPaymentEntity.getOptionPayment().getPathImage()
    			.substring(tOptionPaymentEntity.getOptionPayment().getPathImage().lastIndexOf("/") + 1);
    	String imagesPng = PREFIX_PATH_IMAGE_PAYMENT_OPTION 
    			+ tOptionPaymentEntity.getOptionPayment().getPathImage()
    			.substring(tOptionPaymentEntity.getOptionPayment().getPathImage().lastIndexOf("/") + 1)
    			.split("[.]")[0]+".png";
        OptionPaymentListResponse optionPaymentResponse= OptionPaymentListResponse.builder()
                .paymentOptionId(tOptionPaymentEntity.getOptionPayment().getSeqid())
                .minimalTransaction(tOptionPaymentEntity.getOptionPayment().getMinNominal())
                .paymentCode(tOptionPaymentEntity.getOptionPayment().getCode())
                .paymentName(tOptionPaymentEntity.getOptionPayment().getDescription())
                .usePhone(tOptionPaymentEntity.getOptionPayment().getIsPhone().toString())
                .paymentImage(images)
                .paymentImagePng(imagesPng)
                .build();
        if(optionPaymentResponse.getPaymentCode().equals("transfer"))
        {
            optionPaymentResponse.setBanks(bankService.getBankDeposits());
        }
        if(optionPaymentResponse.getPaymentCode().equalsIgnoreCase("dompet")){
            optionPaymentResponse.setSaldo(userDto.getBalance().multiply(new BigDecimal(-1)));
        }

        return optionPaymentResponse;
    }
    
    private List<OptionPaymentListResponse> getListPaymentWeb(String userId,OptionPaymentEnum optionPaymentEnum){
    	MUserEntity user =userService.get(userId);
    	List<OptionPaymentListResponse> result = new ArrayList<OptionPaymentListResponse>();
    	List<TOptionPaymentEntity> lOption = null;
    	if(optionPaymentEnum==OptionPaymentEnum.PAYMENT)
    	lOption = tOptionPaymentRepo.findOptionPaymentPaymentByCode("transfer", user.getUserCategory());
    	else
    	lOption = tOptionPaymentRepo.findOptionPaymentTopUpByCode("transfer", user.getUserCategory());
    	for(TOptionPaymentEntity op : lOption) {
    		String images = PREFIX_PATH_IMAGE_PAYMENT_OPTION+ op.getOptionPayment().getPathImage()
        			.substring(op.getOptionPayment().getPathImage().lastIndexOf("/") + 1);
            OptionPaymentListResponse optionPaymentResponse= OptionPaymentListResponse.builder()
                    .paymentOptionId(op.getOptionPayment().getSeqid())
                    .minimalTransaction(op.getOptionPayment().getMinNominal())
                    .paymentCode(op.getOptionPayment().getCode())
                    .paymentName(op.getOptionPayment().getDescription())
                    .usePhone(op.getOptionPayment().getIsPhone().toString())
                    .paymentImage(images)
                    .build();
            if(optionPaymentResponse.getPaymentCode().equals("transfer"))
            {
                optionPaymentResponse.setBanks(bankService.getBankByDepositType(user.getDepositType()));
            }
            result.add(optionPaymentResponse);
    	}
    	return result;
    }

    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.READ_UNCOMMITTED,rollbackFor = Exception.class)
    public Response<OptionPaymentListResponse> getSavePay(OptionPaymentReq req,DeviceEnum devEnum){
    	log.info("===> Option Pay Save <===");
    	List<DetailBooks> lsbooks = req.getBooks().stream().distinct().collect(Collectors.toList());
    	String tiket = req.getBooks().stream().findFirst().get().getBookingCode();
    	Response<OptionPaymentListResponse> resp = new Response<>();
    	resp.setRc(String.valueOf(HttpStatus.OK.value()));
    	resp.setDescription(HttpStatus.OK.getReasonPhrase());
    	if(DeviceEnum.WEB==devEnum) this.isRobot=false;
    	MBankDepositEntity bankDep = null;
    	if(!req.getPaymentOption().equalsIgnoreCase("dompet")) {
    		bankDep = bankDepRepo
        			.findByBankId(req.getPaymentOption().toUpperCase())
        			.stream().findAny()
        			.orElse(null);
    		if(bankDep==null) bankDep= bankDepRepo.findById(Integer.valueOf(req.getPaymentOption())).get();
        	req.setPaymentOption(bankDep.getBankId().getBankCode().toLowerCase());
        	if(bankDep.getIsBank()) {
        		if(isRobot) bankDep = bankDepRepo.findAllByIsRobot().stream().findFirst().get();
        		if(Double.valueOf(req.getAmount()) 
            			< bankDep.getMinNominal()) {
            		throw new NotFoundException(ResponseStatus.LIMIT_TRANSFER.getReasonPhrase());
            	}
        		req.setPaymentOption("transfer");
        	}
    	}
    	
    	List<String> lsvbook = lsbooks.stream().filter(b->b.getTypeTrx().equals("1")).map(s->s.getBookingCode()).collect(Collectors.toList());
    	List<DetailBooks> lsPickOrder = lsbooks.stream().filter(b->b.getTypeTrx().equals("2")).collect(Collectors.toList());
    	List<TPickupOrderRequestDetailEntity> lsPickup = lsPickOrder.stream()
    			.map(reqPickService::getPickupOrderDetail).collect(Collectors.toList());
    	List<TPaymentEntity> lsPay = payRepo.findAllById(lsvbook);
    	List<TPickupOrderRequestDetailEntity> lsPickupOld = null;
    	List<TPaymentHistoryEntity> lsPayHist = lsvbook.stream().map(this::doPayHist).collect(Collectors.toList());
    	List<TPaymentEntity> oldPayment = new ArrayList<>();
		oldPayment.addAll(lsPay);
    	if(lsbooks.size() > 1) tiket = getNoTiketCounter();
    	Double totalAmount = lsPickup.stream()
    			.mapToDouble(po -> po.getAmount().doubleValue()).sum();
    	String discValue = Optional.ofNullable(req.getDiscountValue()).orElse("0");
    	totalAmount += lsPay.stream()
    			.mapToDouble(p -> p.getAmount().doubleValue()).sum();
    	if(Double.valueOf(discValue)>0) { //checking discount
    		MCouponDiscountEntity couponDiscount= couponDiscRepo.findByCouponCode(req.getDiscountCode()).get();
    		if(couponDiscount!=null) {
    			this.persenDiskon = couponDiscount.getPercentageDiscount();
    			if(couponDiscount.getIsOneUse()) {
    				couponDiscount.setIsOneUse(true);
    				couponDiscRepo.save(couponDiscount);
    			}
    			BigDecimal discSisa = discService.getValueDiskon(couponDiscount, totalAmount,req.getUserId());
    			lsPay.forEach(discService.setDiscountValue2(discService.getPercentageDisc(totalAmount, couponDiscount,discSisa.doubleValue()), couponDiscount.getCouponCode()));
    			Double disc = lsPay.stream().mapToDouble(p->p.getDiscountValue().doubleValue()).sum();
    			disc = Math.floor(disc);
    	    	if(disc < discSisa.doubleValue()) {
    	    		Double sisaDisc = discSisa.doubleValue() - disc.longValue();
    	    		TPaymentEntity pays = lsPay.stream().filter(p->p.getAmount().doubleValue()-sisaDisc>0).findAny().orElse(null);
    	    		if(pays!=null) {
    	    			lsPay.remove(pays);
    	        		pays.setDiscountValue(new BigDecimal(pays.getDiscountValue().doubleValue()+sisaDisc));
    	        		pays.setAmount(new BigDecimal(pays.getAmount().doubleValue()-sisaDisc));
    	        		lsPay.add(pays);
    	    		}else {
    	    			Double persendisc = (sisaDisc / lsPay.size())/100;
    	    			lsPay.forEach(discService.setDiscountValue2(persendisc, couponDiscount.getCouponCode()));
    	    		}
    	    	}
    		}
    	}
    	totalAmount -= Double.parseDouble(discValue);
    	
    	MProductSwitcherEntity psw = null;
    	MPickupTimeEntity pickTime = null;
    	BigDecimal amountuniq = new BigDecimal("0");
    	Integer jumlahKoli = new Integer(0);
    	BigDecimal uniqNumber = new BigDecimal("0");
    	if(lsPay.isEmpty()) {
    		psw = lsPickup.stream().findAny().get().getProductSwitcherEntity();
    		pickTime = lsPickup.stream().findAny().get().getOrderRequestEntity().getPickupTimeEntity();
    		jumlahKoli += lsPickup.size();
    	}else {
    		psw = lsPay.stream().findAny().get().getProductSwCode();
    		amountuniq = Optional.ofNullable(lsPay.stream().findAny().get().getAmountUniq()).orElse(amountuniq);
    		uniqNumber = Optional.ofNullable(lsPay.stream().findAny().get().getInsufficientFund()).orElse(uniqNumber);
    		pickTime = lsPay.stream().findAny().get().getPickupTimeId();
    		jumlahKoli += lsPay.size();
    	}
    	if(lsPayHist.size()>0) {
    		totalAmount -= lsPayHist.stream().filter(o-> o!=null).mapToDouble(op -> op.getLastAmount().doubleValue() - op.getAmount().doubleValue()).sum();
    	}
    	if(amountuniq.doubleValue()>0) {
    		List<TPaymentEntity> paymentOld = (lsPay.stream().findAny().get().getNoTiket().length()>1)?
        			payRepo.findAllByNoTiket(lsPay.stream().findAny().get().getNoTiket()):lsPay;
    		Double amountOld = paymentOld.stream().filter(p-> p.getNoTiket()!=null).mapToDouble(op->op.getAmount().doubleValue() - op.getDiscountValue().doubleValue()).sum();
    		if(totalAmount == amountOld) {
    			amountuniq = new BigDecimal("0");
    		}
    	}
    	
    	MUserEntity user = userService.get(req.getUserId());
    	TPaymentEntity pays = TPaymentEntity.builder()
				.amount(new BigDecimal(totalAmount))
				.userId(user)
				.trxDate(LocalDate.now())
				.trxTime(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm")))
				.paymentOption(req.getPaymentOption())
				.productSwCode(psw)
				.grossWeight(new Long("0"))
				.volume(new Long("0"))
				.jumlahLembar(0)
				.bookingCode(tiket)
				.discountValue(new BigDecimal(discValue))
				.senderEmail(user.getUserId())
				.senderTelp(user.getHp())
				.senderName(user.getName())
				.pickupTimeId(pickTime)
				.senderAddress("KAHA Go")
				.amountUniq(amountuniq)
				.insufficientFund(uniqNumber)
				.jumlahLembar(jumlahKoli)
				.build();
    	OptionPaymentListResponse ops = todoVendor2(req, tiket,totalAmount, user, bankDep,pays);
    	resp.setData(ops);
    	lsPay.stream().forEach(p->p.setPaymentOption(req.getPaymentOption().toLowerCase()));
    	switch (req.getPaymentOption()) {
		case "dompet":
			lsPay.stream().forEach(updatePaymentWH(tiket,PaymentEnum.REQUEST));
			lsPickup.stream().forEach(updatePickup(tiket,StatusPayEnum.PAID));
			createHistoryPayment(oldPayment, lsPay);
			break;
		case "transfer":
			TDepositEntity dep = depRepo.findByDescription(StringUtils.join(lsvbook, ",")).orElse(null);
			if(dep!=null) {
				double nominal = lsPay.stream().mapToDouble(p->p.getAmount().doubleValue()).sum();
				dep.setNominal(new BigDecimal(nominal));
				dep.setDescription(tiket);
				depRepo.save(dep);
			}
			lsPay.stream().forEach(updateTransfer(ops,bankDep,tiket));
			lsPickup.stream().forEach(updatePickup(tiket,StatusPayEnum.NOT_PAID));
			lsPickup.stream().forEach(updateTransferPickup(ops,bankDep));
			break;
		case "gopay":
			lsPay.stream().forEach(updateTicketPawoon(this.resp,req.getPaymentOption()));
			lsPay.stream().forEach(updatePaymentWH(tiket,PaymentEnum.PENDING));
			lsPickup.stream().forEach(updatePickup(tiket,StatusPayEnum.VERIFICATION));
			lsPickup.stream().forEach(updateTicketPawoonPick(this.resp,req.getPaymentOption()));
			break;
		case "ovo":
			lsPay.stream().forEach(updatePaymentWH(tiket,PaymentEnum.PENDING));
			lsPickup.stream().forEach(updatePickup(tiket,StatusPayEnum.VERIFICATION));
			break;
		case "linkaja":
			lsPay.stream().forEach(updatePaymentWH(tiket,PaymentEnum.PENDING));
			lsPickup.stream().forEach(updatePickup(tiket,StatusPayEnum.VERIFICATION));
			break;
		case "shopeepay":
			lsPay.stream().forEach(updatePaymentWH(tiket,PaymentEnum.PENDING));
			lsPickup.stream().forEach(updatePickup(tiket,StatusPayEnum.VERIFICATION));
			break;
		case "akulaku":
			lsPay.stream().forEach(updatePaymentWH(tiket,PaymentEnum.PENDING));
			lsPickup.stream().forEach(updatePickup(tiket,StatusPayEnum.VERIFICATION));
			break;
		default:
			break;
		}
    	log.info("Respon => "+resp.toString());
    	
    	return resp;
    }
    private TPaymentHistoryEntity doPayHist(String book) {
    	List<Integer> lsStatus = new ArrayList<>();
    	lsStatus.add(PaymentEnum.UNPAID_RECEIVE.getCode());
    	List<TPaymentHistoryEntity> payHist = payHistRepo.findHistoryByBookingCodeAndLastStatusNotInLimit(book,lsStatus);
    	return payHist.stream().findFirst().orElse(null);
    }

	private Consumer<? super TPickupOrderRequestDetailEntity> updateTicketPawoonPick(ResponseModel resp2,String paymentOpt) {
		// TODO Auto-generated method stub
		return p->{
			p.setIdTicket(resp2.getTiketId());
			p.setIdPayment(resp2.getIdPayment());
			p.setCountPawoon(0);
			p.setPaymentOption(paymentOpt);
		};
	}

	private Consumer<? super TPickupOrderRequestDetailEntity> updateTransferPickup(OptionPaymentListResponse ops,
			MBankDepositEntity bankDep) {
		// TODO Auto-generated method stub
		return p->{
			p.setAmountUniq(new BigDecimal(ops.getTotalNominal()));
			p.setPaymentOption(bankDep.getBankDepCode().toString());
		};
	}

	private Consumer<? super TPickupOrderRequestDetailEntity> updatePickup(String tiket,StatusPayEnum status) {
		return p->{
			p.setIsPay(status.getCode());
			p.setNoTiket(tiket);
		};
	}

    public OptionPaymentListResponse todoVendor2(OptionPaymentReq req, String tiket, Double totalAmount,
			MUserEntity user,MBankDepositEntity bank,TPaymentEntity pays) {
		Double saldo = bookService.calculateSaldo(totalAmount, user.getUserId());
		String urlPayment = null;
		String urlLogin = null;
		TPaymentEntity pay = pays;
		String flagOption="2";
		OptionPaymentListResponse opresp = null;
		switch (req.getPaymentOption()) {
		case "dompet":
			doDompet(tiket, pay, totalAmount, saldo);
			flagOption="0";
			break;
		case "transfer":
			flagOption = "1";
			opresp = transfer.doTransfer(pay, bank,tiket,limitTransfer.doubleValue());
			opresp.setFlagSentOption(flagOption);
			return opresp;
		case "ovo":
			pay.setTenorPayment("0");
			pay.setTypePayment(tOPRepo.findByUserCategoryAndOP(user.getUserCategory(), req.getPaymentOption())
					.get().getOptionPayment().getOperatorSw());
			pay.setAmount(new BigDecimal(totalAmount));
			urlPayment= faspayService.sendPaymentAndroid(getReqPayment(pay)).getRedirectUrl();
			break;
		case "akulaku":
			pay.setTenorPayment("0");
			pay.setTypePayment(tOPRepo.findByUserCategoryAndOP(user.getUserCategory(), req.getPaymentOption())
					.get().getOptionPayment().getOperatorSw());
			pay.setAmount(new BigDecimal(totalAmount));
			pay.setPhoneNumber(req.getPhonePayment());
			urlPayment= faspayService.sendPaymentAndroid(getReqPayment(pay)).getRedirectUrl();
			break;
		case "kredivo":
			pay.setTenorPayment("0");
			pay.setTypePayment(tOPRepo.findByUserCategoryAndOP(user.getUserCategory(), req.getPaymentOption())
					.get().getOptionPayment().getOperatorSw());
			pay.setAmount(new BigDecimal(totalAmount));
			pay.setPhoneNumber(req.getPhonePayment());
			urlPayment= faspayService.sendPaymentAndroid(getReqPayment(pay)).getRedirectUrl();
			break;
		case "linkaja":
			pay.setTenorPayment("0");
			pay.setTypePayment(tOPRepo.findByUserCategoryAndOP(user.getUserCategory(), req.getPaymentOption())
					.get().getOptionPayment().getOperatorSw());
			pay.setAmount(new BigDecimal(totalAmount));
			urlPayment= faspayService.sendPaymentAndroid(getReqPayment(pay)).getRedirectUrl();
			break;
		case "shopeepay":
			pay.setTenorPayment("0");
			pay.setTypePayment(tOPRepo.findByUserCategoryAndOP(user.getUserCategory(), req.getPaymentOption())
					.get().getOptionPayment().getOperatorSw());
			pay.setAmount(new BigDecimal(totalAmount));
			urlPayment= faspayService.sendPaymentAndroid(getReqPayment(pay)).getDeeplink();
			break;
		case "gopay":
//			lsPay.stream().forEach(p -> p.setStatusPay(StatusPayEnum.VERIFICATION.getCode()));
//			pay = lsPay.stream().findFirst().get();
			pay.setTenorPayment("0");
			pay.setTypePayment(tOPRepo.findByUserCategoryAndOP(user.getUserCategory(), req.getPaymentOption())
					.get().getOptionPayment().getOperatorSw());
			pay.setAmount(new BigDecimal(totalAmount));
			String endPoint = "/requestPawon/android";
//			if(isRobot==false) endPoint = "/requestPawon/web";
			URI uri = URI.create(urlPawoon+endPoint);
			this.resp = pawonService.requetPaymentAndroid(uri,getReqPayment(pay));
			urlPayment = this.resp.getRedirectUrl();
			break;
		default:
			break;
		}
		return OptionPaymentListResponse.builder()
				.urllogin(urlLogin)
				.senderName(pay.getSenderName())
				.receiverName(pay.getReceiverName())
				.urlResi(Common.getResi(pay))
				.bookingCode(pay.getNoTiket())
				.flagSentOption(flagOption)
				.urlpayment(urlPayment)
				.build();
		
	}
	private Consumer<? super TPaymentEntity> updateTicketPawoon(ResponseModel resp,String paymentOpt) {
		return r->{
			r.setIdPayment(resp.getIdPayment());
			r.setIdTicket(resp.getTiketId());
			r.setPaymentOption(paymentOpt);
			r.setCountPawoon(0);
			r.setStatusPay(StatusPayEnum.VERIFICATION.getCode());
		};
	}

	public Consumer<? super TPaymentEntity> updateTransfer(OptionPaymentListResponse opresp,MBankDepositEntity bank,String tiket) {
		return p -> {
			p.setInsufficientFund(new BigDecimal(opresp.getUniqNumber()));
			p.setAmountUniq(new BigDecimal(opresp.getTotalNominal()));
			p.setPaymentOption(bank.getBankDepCode().toString());
			p.setIsConfirmTransfer((byte) 0);
			p.setNoTiket(tiket);
			p.setStatusPay(StatusPayEnum.NOT_PAID.getCode());
		};
	}

	public void doDompet(String tiket, TPaymentEntity pay, Double totalAmount,
			Double saldo) {
		if(saldo<0) {
			throw new NotFoundException("Saldo Anda Tidak Cukup");
		}
		MUserEntity user = pay.getUserId();
		//validasi user credit yang masih memiliki tagihan
		if(user.getDepositType().equals(DepositTypeEnum.CREDIT.getValue())) {
			List<TCreditEntity> lcredit = creditRepo.findByUserAndNominalGraterZero(user.getUserId(), "0");
			if(lcredit.size() > 0) {
				if(lcredit.get(0).getCreditDay().compareToIgnoreCase(user.getCreditDay()) > 0) {
					throw new InternalServerException("Masih terdapat tagihan yang belum terbayarkan !");
				}
			}
		}
		pay.setAmount(new BigDecimal(totalAmount));
		String description  = MutasiEnum.BOOKING.getKeterangan()
				.concat(" Via ").concat(pay.getPaymentOption().toUpperCase());
		TMutasiEntity mutasi =  bookService.insertMutasi(pay, tiket, description, MutasiEnum.BOOKING);
		mutasiRepo.save(mutasi);
		bookService.insertTCredit(pay, pay.getUserId(), new BigDecimal("1"));
		userService.save(bookService.updateBalanceUser(pay.getUserId().getUserId(), mutasi.getSaldo().doubleValue()));
	}

	public Consumer<? super TPaymentEntity> updatePaymentWH(String tiket,PaymentEnum payStat) {
		return p -> {
			if((PaymentEnum.UNPAID_RECEIVE==PaymentEnum.getPaymentEnum(p.getStatus()) ||
					PaymentEnum.HOLD_BY_ADMIN==PaymentEnum.getPaymentEnum(p.getStatus())) 
					&& payStat == PaymentEnum.REQUEST) {
				p.setStatus(PaymentEnum.FINISH_INPUT_AND_PAID.getCode());
			}else {
				p = bookService.insertWarehouse(p, p.getUserId(),payStat);
			}
    		p.setIsConfirmTransfer((byte) 1);
    		p.setNoTiket(tiket);
    	};
	}
	
//	private Consumer<? super TPaymentEntity> updateTiket(String tiket){
//		return p->{
//			p.setNoTiket(tiket);
//		};
//	}
    public String getNoTiketCounter() {
		String tiket = "0";
		String count = "00000";
		String tgl = DateTimeUtil.getDateTime("yyMMdd");
		System.out.println("Today " + tgl);
		MCounterEntity counter = counterRepo.findAll().stream().findFirst().get();
		counter.setTiket(counter.getTiket()+1);
		tiket = counter.getTiket().toString();
		count = count.substring(0, count.length() - tiket.length());
		count += tiket;
		
		counterRepo.save(counter);
		tiket = "P" + tgl + tiket;
		return tiket;
	}
    
    public Payment getReqPayment(TPaymentEntity pay) {
		// TODO Auto-generated method stub
		return Payment.builder()
				.bookingCode(pay.getBookingCode())
				.stt(pay.getStt())
				.amount(String.valueOf(pay.getAmount().longValue()))
				.destination(pay.getDestination())
				.discountCode(pay.getDiscountCode())
				.discountValue(pay.getDiscountValue())
				.extraCharge(pay.getExtraCharge())
				.comodity(pay.getComodity())
				.goodsDesc(pay.getGoodsDesc())
				.grossWeight(pay.getGrossWeight().intValue())
				.idPayment(pay.getIdPayment())
				.origin(pay.getOrigin())
				.userId(pay.getUserId().getUserId())
				.jumlahLembar(pay.getJumlahLembar())
				.noTiket(pay.getNoTiket())
				.phoneNumber(pay.getPhoneNumber())
				.volume(pay.getVolume().intValue())
				.receiverAddress(pay.getReceiverAddress())
				.receiverEmail(pay.getReceiverEmail())
				.receiverName(pay.getReceiverName())
				.receiverTelp(pay.getReceiverTelp())
				.senderAddress(pay.getSenderAddress())
				.senderEmail(pay.getSenderEmail())
				.senderName(pay.getSenderName())
				.senderTelp(pay.getSenderTelp())
				.serviceType(pay.getServiceType())
				.trxDate(pay.getTrxDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
				.trxTime(pay.getTrxTime())
				.typePayment(pay.getTypePayment())
				.build();
	}
    
    private void createHistoryPayment(List<TPaymentEntity> oldPayment,List<TPaymentEntity> newPayment) {
    	for(TPaymentEntity tp:oldPayment) {
    		for(TPaymentEntity t:newPayment) {
    			if(t.getBookingCode().equals(tp.getBookingCode())) {
    				historyService.createHistory(t, tp, t.getUserId().getUserId());
    			}
    		}
    	}
    }
 
}
