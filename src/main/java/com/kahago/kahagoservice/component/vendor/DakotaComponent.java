package com.kahago.kahagoservice.component.vendor;

import java.util.List;

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
import com.kahago.kahagoservice.model.request.BookRequestDakota;
import com.kahago.kahagoservice.model.response.BookResponseJet;
import com.kahago.kahagoservice.repository.MAreaRepo;
import com.kahago.kahagoservice.repository.MVendorAreaRepo;
import com.kahago.kahagoservice.repository.TAreaRepo;
import com.kahago.kahagoservice.util.Common;

/**
 * @author Ibnu Wasis
 */
@Component
public class DakotaComponent {
	@Autowired
	private MAreaRepo areaRepo;
	@Autowired
	private MVendorAreaRepo vendorAreaRepo;
	
	private static final Logger logger = LoggerFactory.getLogger(DakotaComponent.class);
	
	public BookResponseJet getPayment(String clientcode,String stt,
			String operatorsw,String url,TPaymentEntity pay) throws Exception{
		logger.info("-- DAKOTA Book --");
		String phoneSender=pay.getSenderTelp().replace("+", "").replace("-", "");
		String phoneRecv =pay.getReceiverTelp().replace("+", "").replace("-", "");
		MAreaEntity origin = areaRepo.findByKotaEntityAreaKotaId(pay.getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getAreaKotaId());
//		List<TAreaEntity> tArea = tAreaRepo
//				.findTOPByProductSwCodeAndAreaIdAndAreaOriginIdOrderByTarifAsc(pay.getProductSwCode(), pay.getIdPostalCode(),
//						origin.getAreaId());
		List<MVendorAreaEntity> area = vendorAreaRepo.findAllByPostalCodeIdAndSwitcherCode(pay.getIdPostalCode(),pay.getProductSwCode().getSwitcherEntity().getSwitcherCode());
		String dest = area.get(0).getSendRequest();
		BookRequestDakota rbs = new BookRequestDakota();
		rbs.setBookCode(pay.getBookingCode());
		rbs.setOrigin(pay.getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getName());
		rbs.setDestination(dest);
		rbs.setProductCode(pay.getProductSwCode().getOperatorSw());
		rbs.setGoodsDescription(Common.getString(pay.getGoodsDesc()));
		rbs.setGoodsPrice(pay.getPriceGoods().toString());
		rbs.setNotes(pay.getNote());
		rbs.setSenderName(Common.getString(pay.getSenderName()));
		rbs.setSenderAddress(Common.getString(pay.getSenderAddress()));
		rbs.setSenderPhone(phoneSender);
		rbs.setSenderPostalCode("60162");
		rbs.setSenderEmail("admin@kahago.com");
		
		rbs.setReceiverName(Common.getString(pay.getReceiverName()));
		rbs.setReceiverAddress(Common.getString(pay.getReceiverAddress()));
		rbs.setReceiverPhone(phoneRecv);
		rbs.setReceiverPostalCode(String.valueOf(pay.getIdPostalCode().getPostalCode()));
		rbs.setReceiverDistrict(dest.split("[&]")[2].split("[=]")[1]);
		rbs.setReceiverSubDistrict(dest.split("[&]")[1].split("[=]")[1]);
		rbs.setReceiverProvince(dest.split("[&]")[3].split("[=]")[1]);
		rbs.setReceiverCity(dest.split("[&]")[0].split("[=]")[1]);
		rbs.setReceiverEmail("admin@kahago.com");
		rbs.setReceiverIsland("");
		
		rbs.setTotalItem(String.valueOf(pay.getJumlahLembar()));
		rbs.setWeight(String.valueOf(pay.getGrossWeight()));
		rbs.setVolume(String.valueOf(pay.getVolume()));
		
		rbs.setApikey(clientcode);
		
		logger.info("Request to Dakota:" +rbs.toString());
		HttpEntity<BookRequestDakota> request = new HttpEntity<>(rbs);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<BookResponseJet> response = null;
		try {
			response = restTemplate
					  .exchange(url, HttpMethod.POST, request, BookResponseJet.class);
		}catch (Exception e) {
			// TODO: handle exception
//			pay.setStatus(PaymentEnum.REQUEST.getValue());
			logger.error("Error Request Dakota :"+e.getMessage());
			throw e;
    		
		}
		logger.info("Response From Dakota: "+ response.getStatusCodeValue());
		logger.info("Response Body: "+ response.getBody().toString());
		return response.getBody();
	}
	
	
	
}
