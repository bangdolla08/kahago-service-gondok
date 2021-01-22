package com.kahago.kahagoservice.component.vendor;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.kahago.kahagoservice.entity.MOfficeEntity;
import com.kahago.kahagoservice.entity.TBookEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.model.request.BookRequestJet;
import com.kahago.kahagoservice.model.request.ItemsJet;
import com.kahago.kahagoservice.model.response.BookResponseJet;
import com.kahago.kahagoservice.util.Common;


/**
 * @author Ibnu Wasis
 */
@Component
public class JETComponent {
	private static final Logger logger = LoggerFactory.getLogger(JETComponent.class);
	
	public BookResponseJet getPayment(String clientcode,String stt,
			String operatorsw,String url,TPaymentEntity pay,MOfficeEntity office) throws Exception{
		logger.info("JET Payment");
		String phoneSender=pay.getSenderTelp().replace("+", "").replace("-", "");
		String phoneRecv =pay.getReceiverTelp().replace("+", "").replace("-", "");
		BookRequestJet rbs = new BookRequestJet();
		rbs.setBookCode(pay.getBookingCode());
		rbs.setIsInsurance("false");
		List<ItemsJet> pli = new ArrayList<>();
		rbs.setOrigin(pay.getOrigin().toUpperCase());
		rbs.setDestination(pay.getIdPostalCode().getKecamatanEntity().getKecamatan().toUpperCase() + ", " + pay.getIdPostalCode().getKecamatanEntity().getKotaEntity().getName().toUpperCase());
		rbs.setProductCode(pay.getProductSwCode().getName());
		rbs.setGoodsDescription(Common.getString(pay.getGoodsDesc()));
		rbs.setGoodsPrice(pay.getPriceGoods().toString());
		rbs.setNotes(pay.getNote());
		rbs.setSenderName(Common.getString(pay.getSenderName()));
		rbs.setSenderAddress(Common.getString(pay.getSenderAddress()));
		rbs.setSenderPhone(phoneSender);
		rbs.setSenderPostalCode("0");
		rbs.setSenderEmail(pay.getUserId().getUserId());
		if(pay.getInsurance().doubleValue()>0) {
			rbs.setIsInsurance("true");
		}
		rbs.setReceiverName(Common.getString(pay.getReceiverName()));
		rbs.setReceiverAddress(Common.getString(pay.getReceiverAddress()));
		rbs.setReceiverPhone(phoneRecv);
		rbs.setReceiverEmail(pay.getUserId().getUserId());
		rbs.setReceiverPostalCode(String.valueOf(pay.getIdPostalCode().getPostalCode()));
		
		rbs.setPickupName("PT KAHA International Holiday");
		rbs.setPickupAddress("Jl Nyamplungan");
		rbs.setPickupEmail("admin@kahago.com");
		rbs.setPickupPhone("089898998");
		rbs.setPickupPostalCode("60162");
		for(TBookEntity tbook:pay.getTbooks()) {
			ItemsJet item = new ItemsJet();
			item.setLength(tbook.getLength());
			item.setHeight(tbook.getHeight());
			item.setWidth(tbook.getWidth());
			item.setWeight(tbook.getGrossWeight());
			item.setPackagingCode("");
			item.setPackagingQty("");
			pli.add(item);
		}
		rbs.setItems(pli);
		HttpEntity<BookRequestJet> request = new HttpEntity<>(rbs);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<BookResponseJet> response = null;
		try {
			response = restTemplate
					  .exchange(url, HttpMethod.POST, request, BookResponseJet.class);
		}catch (Exception e) {
			// TODO: handle exception
//			pay.setStatus(PaymentEnum.REQUEST.getValue());
			logger.error("Error Request JET :"+e.getMessage());
			throw e;
    		
		}
		logger.info("Response From JET: "+ response.getStatusCodeValue());
		logger.info("Response Body: "+ response.getBody().toString());
		return response.getBody();
	}
}
