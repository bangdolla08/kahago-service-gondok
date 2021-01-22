package com.kahago.kahagoservice.component.vendor;

import java.util.List;

import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.kahago.kahagoservice.entity.MAreaEntity;
import com.kahago.kahagoservice.entity.MVendorAreaEntity;
import com.kahago.kahagoservice.entity.TAreaEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.model.request.BookRequestLP;
import com.kahago.kahagoservice.model.response.BookResponseLP;
import com.kahago.kahagoservice.repository.MAreaRepo;
import com.kahago.kahagoservice.repository.MVendorAreaRepo;
import com.kahago.kahagoservice.repository.TAreaRepo;
import com.kahago.kahagoservice.util.Common;
import com.kahago.kahagoservice.util.DateTimeUtil;

/**
 * @author Ibnu Wasis
 */
@Component
public class LionParcelComponent {
	private static final Logger logger = LoggerFactory.getLogger(LionParcelComponent.class);
	
	@Autowired
	private MAreaRepo areaRepo;
	@Autowired
	private MVendorAreaRepo vendorAreaRepo;
	public BookResponseLP getPayment(String clientcode,String stt,
			String operatorsw,String url,TPaymentEntity pay) throws Exception{
		MAreaEntity originKaha = areaRepo.findByKotaEntityAreaKotaId(pay.getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getAreaKotaId());
//		List<TAreaEntity> tArea = tAreaRepo
//				.findTOPByProductSwCodeAndAreaIdAndAreaOriginIdOrderByTarifAsc(pay.getProductSwCode(), pay.getIdPostalCode(),
//						originKaha.getAreaId());
		List<MVendorAreaEntity> area = vendorAreaRepo.findAllByPostalCodeIdAndSwitcherCode(pay.getIdPostalCode(), pay.getProductSwCode().getSwitcherEntity().getSwitcherCode());
		String serviceType = "PACKAGE";
		if(pay.getServiceType().equalsIgnoreCase("dokumen")) {
			serviceType = "DOCUMENT";
		}
		String phoneSender=pay.getSenderTelp().replace("+", "").replace("-", "");
		String phoneRecv =pay.getReceiverTelp().replace("+", "").replace("-", "");
		BookRequestLP blLp = new BookRequestLP();
		blLp.setClientCode(clientcode);
		blLp.setUserType("Corporate Customer");
		blLp.setTrackingNumber(stt);
//		blLp.setTrackingNumber("-");
//		blLp.setPackageId("264792122-4GF");
		blLp.setPackageId(pay.getBookingCode());
//		blLp.setOrderNumberTag("264792122-4108");
		blLp.setExternalNumber("");
		blLp.setOrderNumberTag(pay.getBookingCode());
		blLp.setPackageDate(DateTimeUtil.getDateTime("yyyy-MM-dd"));
		blLp.setProductType(operatorsw);
		blLp.setServiceType(serviceType);
		blLp.setCommodityType("GENERAL");
		blLp.setNumberOfPieces(String.valueOf(pay.getJumlahLembar()));
		blLp.setGrossWeight(String.valueOf(pay.getGrossWeight()));
		blLp.setVolumeWeight(String.valueOf(pay.getVolume()));
		blLp.setShipperName(Common.getString(pay.getSenderName()));
		blLp.setPickupAddress(Common.getString(pay.getSenderAddress()));
		blLp.setPickupLocation(pay.getOrigin());
		blLp.setPickupPhone(phoneSender);
		blLp.setPickupEmail("admin@kahago.com");
		blLp.setReceiverName(Common.getString(pay.getReceiverName()));
		blLp.setReceiverAddress(pay.getReceiverAddress() + "," + pay.getIdPostalCode().getKelurahan() + ","
				+ pay.getIdPostalCode().getKecamatanEntity().getKecamatan() + "," + pay.getIdPostalCode().getKecamatanEntity().getKotaEntity().getName()
				+ "," + pay.getIdPostalCode().getProvinsi());
		blLp.setReceiverLocation(area.get(0).getSendRequest());
		blLp.setReceiverPhone(phoneRecv);
		blLp.setReceiverEmail("-");
		/* stag --comment */
		HttpEntity<BookRequestLP> request = new HttpEntity<>(blLp);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<BookResponseLP> response = null;
		logger.info("Request:> "+Common.json2String(request));
		try {
			response = restTemplate
					  .exchange(url, HttpMethod.POST, request, BookResponseLP.class);
		}catch (Exception e) {
			// TODO: handle exception
//			pay.setStatus(PaymentEnum.REQUEST.getCode());
			logger.error("Error Request LP :"+e.getMessage());
			throw e;    		
		}
		logger.info("Response From LP: "+ response.getStatusCodeValue());
		logger.info("Response Body: "+ response.getBody().toString());
		return response.getBody();
	}
}
