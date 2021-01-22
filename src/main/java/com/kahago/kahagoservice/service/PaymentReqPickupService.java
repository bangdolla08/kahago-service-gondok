package com.kahago.kahagoservice.service;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.function.Consumer;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.kahago.kahagoservice.client.FaspayFeignService;
import com.kahago.kahagoservice.client.PawoonFeignService;
import com.kahago.kahagoservice.client.model.response.ResponseModel;
import com.kahago.kahagoservice.component.TransferComponent;
import com.kahago.kahagoservice.component.vendor.TikiComponent;
import com.kahago.kahagoservice.entity.MBankDepositEntity;
import com.kahago.kahagoservice.entity.MUserEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestDetailEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestEntity;
import com.kahago.kahagoservice.enummodel.DeviceEnum;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.RequestPickupEnum;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.enummodel.StatusPayEnum;
import com.kahago.kahagoservice.model.request.OptionPaymentReq;
import com.kahago.kahagoservice.model.request.PayPickupRequest;
import com.kahago.kahagoservice.model.response.OptionPaymentListResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.repository.MBankDepositRepo;
import com.kahago.kahagoservice.repository.TOptionPaymentRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestDetailRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestRepo;
import com.kahago.kahagoservice.util.Common;


/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 14 Jan 2020
 */
@Service
public class PaymentReqPickupService {
	@Autowired
	private TPickupOrderRequestRepo poRepo;
	@Autowired
	private TPickupOrderRequestDetailRepo podRepo;
	@Autowired
	private BookService bookService;
	@Autowired
	private OptionPaymentService opService;
	@Autowired
	private TransferComponent transfer;
	@Autowired
    private MBankDepositRepo bankDepRepo;
	@Autowired
    private PawoonFeignService pawonService;
	@Autowired
    private TOptionPaymentRepo tOPRepo;
	@Autowired
    private FaspayFeignService faspayService;
	
	@Value("${transfer.nominal.limit}")
	private BigDecimal limitTransfer;
	@Value("${url.service.pawoon}")
	private String urlPawoon;
	
	private Boolean isMobile = true;
	
	
	@Transactional(rollbackOn = Exception.class)
	public Response<OptionPaymentListResponse> getSavePay(PayPickupRequest payRequest,DeviceEnum devEnum) {
		// TODO Auto-generated method stub
		Response<OptionPaymentListResponse> resp = new Response<>();
		resp.setRc(String.valueOf(HttpStatus.OK.value()));
    	resp.setDescription(HttpStatus.OK.getReasonPhrase());
		TPickupOrderRequestEntity po = poRepo.findByPickupOrderId(payRequest.getPickupOrderId());
		List<TPickupOrderRequestDetailEntity> lsPOD = podRepo
				.findAllByOrderRequestEntity(po);
		isMobile = true;
		MBankDepositEntity bankDep = null;
		if(DeviceEnum.WEB==devEnum) this.isMobile=false;
		if(!payRequest.getPaymentOption().equalsIgnoreCase("dompet")) {
//			bankDep = bankDepRepo
//	    			.findAllByIsRobotAndBankCode(isMobile,payRequest.getPaymentOption().toUpperCase())
//	    			.stream().findAny()
//	    			.orElse(bankDepRepo.findByBankId(payRequest.getPaymentOption().toUpperCase()).stream().findAny().get());
			bankDep = bankDepRepo
        			.findByBankId(payRequest.getPaymentOption().toUpperCase())
        			.stream().findAny()
        			.orElse(null);
    		if(bankDep==null) bankDep= bankDepRepo.findById(Integer.valueOf(payRequest.getPaymentOption())).get();
	    	payRequest.setPaymentOption(bankDep.getBankId().getBankCode().toLowerCase());
	        if(bankDep.getIsBank()) {
	    		if(payRequest.getNominal().doubleValue() 
	        			< limitTransfer.doubleValue()) {
	        		throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,ResponseStatus.LIMIT_TRANSFER.getReasonPhrase());
	        	}
	    		payRequest.setPaymentOption("transfer");
	    	} 
		}
		resp.setData(todoVendor(payRequest, lsPOD, po.getUserEntity(), bankDep));
		return resp;
	}
	
	public OptionPaymentListResponse todoVendor(PayPickupRequest req, List<TPickupOrderRequestDetailEntity> lsPay,
			MUserEntity user,MBankDepositEntity bank) {
		TPickupOrderRequestDetailEntity tPOD = lsPay.stream().findFirst().get();
		Double saldo = bookService.calculateSaldo(req.getNominal().doubleValue(), user.getUserId());
		String urlPayment = null;
		String urlLogin = null;
		String flagOption="2";
		OptionPaymentListResponse opresp = null;
		ResponseModel resp = new ResponseModel();
		TPaymentEntity pay = TPaymentEntity.builder()
				.amount(new BigDecimal(req.getNominal().longValue()))
				.userId(user)
				.trxDate(tPOD.getCreateDate().toLocalDate())
				.paymentOption(req.getPaymentOption())
				.productSwCode(tPOD.getProductSwitcherEntity())
				.grossWeight(new Long("0"))
				.volume(new Long("0"))
				.jumlahLembar(0)
				.senderEmail(user.getUserId())
				.senderTelp(user.getHp())
				.senderName(user.getName())
				.bookingCode(req.getPickupOrderId())
				.build();
		lsPay.stream().forEach(p-> {
			p.setPaymentOption(req.getPaymentOption().toLowerCase());
			p.setNoTiket(p.getOrderRequestEntity().getPickupOrderId());
			p.setIsPay(2);
		});
		switch (req.getPaymentOption()) {
		case "dompet":
			opService.doDompet(req.getPickupOrderId(), pay, req.getNominal().doubleValue(), saldo);
			flagOption="0";
			lsPay.stream().forEach(p-> p.setIsPay(1));
			break;
		case "transfer":
			flagOption = "1";
			opresp = transfer.doTransferReqPickup(tPOD.getOrderRequestEntity(), 
					pay.getAmount(),bank, req.getPickupOrderId());
			opresp.setFlagSentOption(flagOption);
			return opresp;
		case "ovo":
			pay.setTenorPayment("0");
			pay.setTypePayment(tOPRepo.findByUserCategoryAndOP(user.getUserCategory(), req.getPaymentOption())
					.get().getOptionPayment().getOperatorSw());
			urlPayment= faspayService.sendPaymentAndroid(opService.getReqPayment(pay)).getRedirectUrl();
			break;
		case "akulaku":
			pay.setTenorPayment("0");
			pay.setTypePayment(tOPRepo.findByUserCategoryAndOP(user.getUserCategory(), req.getPaymentOption())
					.get().getOptionPayment().getOperatorSw());
			pay.setPhoneNumber(req.getPhonePayment());
			urlPayment= faspayService.sendPaymentAndroid(opService.getReqPayment(pay)).getRedirectUrl();
			break;
		case "kredivo":
			pay.setTenorPayment("0");
			pay.setTypePayment(tOPRepo.findByUserCategoryAndOP(user.getUserCategory(), req.getPaymentOption())
					.get().getOptionPayment().getOperatorSw());
			pay.setPhoneNumber(req.getPhonePayment());
			urlPayment= faspayService.sendPaymentAndroid(opService.getReqPayment(pay)).getRedirectUrl();
			break;
		case "linkaja":
			pay.setTenorPayment("0");
			pay.setTypePayment(tOPRepo.findByUserCategoryAndOP(user.getUserCategory(), req.getPaymentOption())
					.get().getOptionPayment().getOperatorSw());
			urlPayment= faspayService.sendPaymentAndroid(opService.getReqPayment(pay)).getRedirectUrl();			
			break;
		case "gopay":
			pay.setTenorPayment("0");
			pay.setBookingCode(req.getPickupOrderId());
			pay.setSenderAddress("KAHA Go");
			pay.setTypePayment(tOPRepo.findByUserCategoryAndOP(user.getUserCategory(), req.getPaymentOption())
					.get().getOptionPayment().getOperatorSw());
			String endPoint = "/requestPawon/android";
			if(isMobile==false) endPoint = "/requestPawon/web";
			URI uri = URI.create(urlPawoon+endPoint);
			resp = pawonService.requetPaymentAndroid(uri,opService.getReqPayment(pay));
			urlPayment = resp.getRedirectUrl();
			lsPay.stream().forEach(updatePawoon(resp));
			break;
		default:
			break;
		}
		
		podRepo.saveAll(lsPay);
		return OptionPaymentListResponse.builder()
				.urllogin(urlLogin)
				.urlpayment(urlPayment)
				.bookingCode(req.getPickupOrderId())
				.flagSentOption(flagOption)
				.urlpayment(urlPayment)
				.build();
		
	}

	private Consumer<? super TPickupOrderRequestDetailEntity> updatePawoon(ResponseModel resp) {
		return p->{
			p.setIdPayment(resp.getIdPayment());
			p.setIdTicket(resp.getTiketId());
		};
	}
	
}
