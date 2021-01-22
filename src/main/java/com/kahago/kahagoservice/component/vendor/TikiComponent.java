package com.kahago.kahagoservice.component.vendor;
/**
 * @author Ibnu Wasis
 */

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.kahago.kahagoservice.entity.MVendorAreaEntity;
import com.kahago.kahagoservice.entity.TBookEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.model.request.BookRequestJet;
import com.kahago.kahagoservice.model.request.ItemsJet;
import com.kahago.kahagoservice.model.response.BookResponseJet;
import com.kahago.kahagoservice.repository.MVendorAreaRepo;
import com.kahago.kahagoservice.util.Common;

@Component
public class TikiComponent {
	private static final Logger logger = LoggerFactory.getLogger(TikiComponent.class);
	@Autowired
	private MVendorAreaRepo vendorAreaRepo;
	public BookResponseJet getPayment(String clientcode,String stt,
			String operatorsw,String url,TPaymentEntity pay)throws Exception {
		BookRequestJet rbs = new BookRequestJet();
		String phoneSender=pay.getSenderTelp().replace("+", "").replace("-", "");
		String phoneRecv =pay.getReceiverTelp().replace("+", "").replace("-", "");
		MVendorAreaEntity area = vendorAreaRepo.findAllByPostalCodeIdAndSwitcherCode(pay.getIdPostalCode(), pay.getProductSwCode().getSwitcherEntity().getSwitcherCode())
				.stream().findAny().get();
		rbs.setBookCode(pay.getBookingCode());
		rbs.setStt(stt);
		rbs.setIsInsurance("false");
		List<ItemsJet> pli = new ArrayList<ItemsJet>();
		rbs.setOrigin(pay.getOrigin().toUpperCase());
		rbs.setDestination(area.getPostalCodeId().getKecamatanEntity().getKecamatan().toUpperCase() + ", " + area.getPostalCodeId().getKecamatanEntity().getKotaEntity().getName().toUpperCase());
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
		for(TBookEntity tbook:pay.getTbooks()) {
			ItemsJet item = new ItemsJet();
			item.setLength(tbook.getLength());
			item.setHeight(tbook.getHeight());
			item.setWidth(tbook.getWidth());
			item.setWeight(tbook.getGrossWeight());
			pli.add(item);
		}
		rbs.setItems(pli);
		HttpEntity<BookRequestJet> request = new HttpEntity<BookRequestJet>(rbs);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<BookResponseJet> response = null;
		try {
			response = restTemplate
					  .exchange(url, HttpMethod.POST, request, BookResponseJet.class);
		}catch (Exception e) {
			// TODO: handle exception
//			pay.setStatus(PaymentEnum.REQUEST.getCode());
			logger.error("Error Request TIKI :"+e.getMessage());
			throw e;
    		
		}
		logger.info("Response From TIKI: "+ response.getStatusCodeValue());
		logger.info("Response Body: "+ response.getBody().toString());
		return response.getBody();
	}
}
