package com.kahago.kahagoservice.service;

import java.math.BigDecimal;

import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.server.ResponseStatusException;

import com.kahago.kahagoservice.component.vendor.DakotaComponent;
import com.kahago.kahagoservice.component.vendor.JETComponent;
import com.kahago.kahagoservice.component.vendor.JNEComponent;
import com.kahago.kahagoservice.component.vendor.PCPComponent;
import com.kahago.kahagoservice.component.vendor.WahanaComponent;
import com.kahago.kahagoservice.entity.MOfficeEntity;
import com.kahago.kahagoservice.entity.MUserEntity;
import com.kahago.kahagoservice.entity.MVendorPropEntity;
import com.kahago.kahagoservice.entity.TDepositEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.entity.TPaymentHistoryEntity;
import com.kahago.kahagoservice.entity.TSttVendorEntity;
import com.kahago.kahagoservice.enummodel.ApprovalTopUpEnum;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.exception.InternalServerException;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.response.BookResponseJNE;
import com.kahago.kahagoservice.model.response.BookResponseJet;
import com.kahago.kahagoservice.model.response.BookResponsePCP;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.repository.MOfficeRepo;
import com.kahago.kahagoservice.repository.MUserRepo;
import com.kahago.kahagoservice.repository.MVendorPropRepo;
import com.kahago.kahagoservice.repository.TPaymentHistoryRepo;
import com.kahago.kahagoservice.repository.TSttVendorRepo;
import com.kahago.kahagoservice.util.Common;
import com.kahago.kahagoservice.util.CommonConstant;
import com.kahago.kahagoservice.util.EmailSender;



/**
 * @author Ibnu Wasis
 */
@Service
public class SelfPrintResiService {
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private MOfficeRepo mOfficeRepo;
	@Autowired
	private MVendorPropRepo mVendorPropRepo;
	@Autowired
	private TSttVendorRepo tSttVendorRepo;
	@Autowired
	private JETComponent jet;
	@Autowired
	private DakotaComponent dakota;
	@Autowired
	private WahanaComponent wahana;
	@Autowired
	private JNEComponent jne;
	@Autowired
	private PCPComponent pcp;
	@Autowired
	private MUserRepo mUserRepo;
	@Autowired
	private TPaymentHistoryRepo tPaymentHistoryRepo;
	
	@Value("${database.server}")
	private String environment;
	
	private static final Integer CODE_LP = 301;
	private static final Integer CODE_SICEPAT = 302;
	private static final Integer CODE_JET = 303;
	private static final Integer CODE_TIKI = 305;
	private static final Integer CODE_PCP = 306;
	private static final Integer CODE_TEBA = 307;
	private static final Integer CODE_DAKOTA = 308;
	private static final Integer CODE_POS = 309;
	private static final Integer CODE_WAHANA = 310;
	private static final Integer CODE_JNE = 311;
	private static final Integer CODE_INDAH = 312;
	private static final Integer PRODUCT_CODE_REGPACK_LP =1;
	private static final Integer FLAG = 1;

	private static final String STATUS_SUCCESS = "200";
	
	private static final Logger logger = LoggerFactory.getLogger(SelfPrintResiService.class);
	
	public SaveResponse PrintResi(String bookingCode,String userId) {
		String url ="";
		String noResi="";
		try {
			TPaymentEntity payment = paymentService.get(bookingCode);
			if(payment.getStatus().equals(PaymentEnum.PENDING.getCode()))throw new NotFoundException("Pesanan Belum Dibayar !");
			payment = getStt(payment);
			if(payment.getStt() != null && !payment.getStt().equals("-")) {
				url = "api/resi/kahago?bookingcode="
						+ payment.getBookingCode()+
						"&userid="+ payment.getUserId().getUserId();
				noResi = payment.getStt();
			}else {
				return SaveResponse.builder()
						.saveStatus(0)
						.saveInformation("Gagal Terbit Resi")
						.linkResi(url)
						.build();
			}
			TPaymentHistoryEntity payHistory = tPaymentHistoryRepo.findFirstByBookingCodeAndLastStatusOrderByLastUpdateDesc(payment, payment.getStatus());
			payHistory.setIsRefund(0);
			tPaymentHistoryRepo.save(payHistory);
		}catch (ResponseStatusException e) {
			// TODO: handle exception
			Log.error("Error Vendor :"+e.getMessage());
			throw new ResponseStatusException(e.getStatus(), e.getReason());
		}
		catch (Exception e) {
			// TODO: handle exception
			logger.error("Error Self Print Resi :"+e.toString());
			e.printStackTrace();
			sendEmail(bookingCode, e.toString());
			throw new NotFoundException("Silahkan dicoba kembali dalam kurun waktu 15 menit. Terima kasih");
		}		
		
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Berhasil Terbit Resi")
				.linkResi(url)
				.noResi(noResi)
				.build();
	}
	
	private TPaymentEntity getStt(TPaymentEntity payment) throws Exception {
		String resi = "";
		TPaymentEntity pay = paymentService.get(payment.getBookingCode());
		if(pay.getGrossWeight().compareTo(pay.getMinWeight().longValue()) < 0) {
			pay.setGrossWeight(pay.getMinWeight().longValue());
		}
		pay.setTrxTime(payment.getTrxTime());
		
		MOfficeEntity office = mOfficeRepo.findAllByOfficeCode(pay.getOfficeCode());
		MVendorPropEntity vendorProp = mVendorPropRepo.findAllBySwitcherCodeAndActionAndOrigin(pay.getProductSwCode().getSwitcherEntity(), "book", pay.getOrigin());
		String origin = (pay.getProductSwCode().getSwitcherEntity().getSwitcherCode()==301)?null:pay.getOrigin();
		TSttVendorEntity sttE = tSttVendorRepo
				.findFirstBySwitcherCodeAndFlagAndOrigin(pay.getProductSwCode().getSwitcherEntity().getSwitcherCode(), 0,origin);
		String stt="-";
		if(sttE!=null) {
			stt = sttE.getStt();
		}
			
		if(vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_LP)) {
			logger.info("Terbit Resi Lion Parcel");
			pay.setStt(stt);
		}else if(vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_JET)) {
			logger.info("Terbit Resi JET Express");
			BookResponseJet respon = jet.getPayment(vendorProp.getClientCode(), stt, pay.getProductSwCode().getOperatorSw(), 
					vendorProp.getUrl(), pay, office);
			if(!respon.getRc().equals(ResponseStatus.OK.value())) {
				logger.info("Failed from vendor JET RC:" + respon.getRc());
//				pay.setStatus(PaymentEnum.REQUEST.getCode());
				throw new Exception("Failed By vendor: " + respon.getDescription());
			}
			stt = respon.getNoResi();
			pay.setStt(stt);
		}else if(vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_TIKI)) {
			logger.info("Terbit Resi TIKI");
			pay.setStt(stt);
		}
		else if(vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_SICEPAT)) {
			logger.info("Terbit Resi SICEPAT");
			pay.setStt(stt);
		}
		else if (vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_PCP)) {
			logger.info("Terbit Resi PCP");
			BookResponsePCP respon = pcp.getPayment(vendorProp.getUrl(), pay,vendorProp.getClientCode());
			if(!respon.getStatus()) {
				logger.info("Failed from vendor PCP :"+respon.getMsg());
				throw new Exception("Failed By vendor: " + respon.getMsg());
			}
			stt = respon.getAwbNo().substring(12);
			pay.setStt(stt);
		}else if (vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_TEBA)) {
			logger.info("Terbit Resi TEBA");
			stt = payment.getBookingCode();
			pay.setStt(stt);
		}else if(vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_DAKOTA)) {
			logger.info("Terbit Resi Lion DAKOTA");
			BookResponseJet respon = dakota.getPayment(vendorProp.getClientCode(), stt, pay.getProductSwCode().getOperatorSw(),
					vendorProp.getUrl(), pay);
			if(!respon.getRc().equals(ResponseStatus.OK.value())) {
				logger.info("Failed from vendor Dakota RC:" + respon.getRc());
//				pay.setStatus(PaymentEnum.REQUEST.getCode());
				throw new Exception("Failed By vendor: " + respon.getDescription());
			}

			stt = respon.getNoResi();
			pay.setResi(respon.getUrlResi());
			pay.setStt(stt);
		}else if (vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_POS)) {
			logger.info("Terbit Resi POS");
			pay.setStt(payment.getStt());
		}else if(vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_WAHANA)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Untuk Produk ini tidak dapat menggunakan fitur ini");
//			logger.info("Terbit Resi WAHANA");
//			BookResponseJet respon = wahana.getPayment(vendorProp.getClientCode(), stt, pay.getProductSwCode().getOperatorSw(), vendorProp.getUrl(), pay, office);
//			if(!respon.getRc().equals(ResponseStatus.OK.value())) {
//				logger.info("Failed from vendor Wahana RC:" + respon.getRc());
//				throw new Exception("Book Failed : " + "Failed By vendor: " + respon.getDescription());
//			}
//
//			stt = respon.getNoResi();
//			pay.setDatarekon(respon.getRoutingCode());
//			resi = "api/resi/wahana?bookingcode="+payment.getBookingCode()
//					+ "&userid=" + pay.getUserId().getUserId();
//			pay.setResi(resi);
//			pay.setStt(stt);
		}else if(vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_JNE)) {
			logger.info("Terbit Resi JNE");
			BookResponseJNE respon = jne.getPayment(vendorProp.getClientCode(), stt, pay.getProductSwCode().getOperatorSw(), vendorProp.getUrl(), pay, office);
			if(!respon.getRc().equals(STATUS_SUCCESS)) {
				logger.info("Failed from vendor JNE RC:" + respon.getRc());
				throw new Exception("Book Failed : " + "Failed By vendor: " + respon.getDescription());
			}
			stt=respon.getNoResi();
			pay.setStt(stt);
		}else if(vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_INDAH)) {
			logger.info("Terbit Resi INDAH");
			pay.setStt(sttE.getStt());
		}
		
		if(vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_LP)
				|| vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_SICEPAT)
				|| vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_TIKI)
				//|| vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_PCP)
				|| vendorProp.getSwitcherCode().getSwitcherCode().equals(CODE_INDAH)) {
			//if(!pay.getProductSwCode().getProductSwCode().equals((PRODUCT_CODE_REGPACK_LP.longValue()))) {
				sttE.setFlag(FLAG);
				tSttVendorRepo.save(sttE);
			//}
			
		}
		pay.setStatus(payment.getStatus());
		if(payment.getStatus().equals(PaymentEnum.CANCEL_BY_WAREHOUSE.getCode())) {
			Double bal = pay.getUserId().getBalance().doubleValue() -
					payment.getAmount().doubleValue();
			pay.getUserId().setBalance(new BigDecimal(bal));
			mUserRepo.save(pay.getUserId());
		}
		logger.info("Save Payment");
//		log.info(Common.json2String(pay));
		paymentService.save(pay);
		return pay;
	}
	
	@Async("asyncExecutor")
	public void sendEmail (String bookingCode, String message) {
		try {
			String mailto = "ibnu.wasis@kaha.co.id,riszky.septiaji@kaha.co.id,abdullah.baraqbah@kaha.co.id,hendro.yuwono@kaha.co.id,adytriafp@kaha.co.id";
			String ccEmail ="";
			EmailSender.sendNotifError(mailto, ccEmail, EmailSender.getContentError(bookingCode, message),environment);
			//EmailSender.sendNotifDeposit(mailto, ccEmail, EmailSender.getContentError(bookingCode, message));
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Error Send Email :"+e.toString());
			e.printStackTrace();
		}
	}
}
