package com.kahago.kahagoservice.component.vendor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kahago.kahagoservice.entity.MAreaEntity;
import com.kahago.kahagoservice.entity.MOfficeEntity;
import com.kahago.kahagoservice.entity.MVendorAreaEntity;
import com.kahago.kahagoservice.entity.TAreaEntity;
import com.kahago.kahagoservice.entity.TBookEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.model.request.BookRequestJNE;
import com.kahago.kahagoservice.model.request.ItemJne;
import com.kahago.kahagoservice.model.response.BookResponseJNE;
import com.kahago.kahagoservice.repository.MAreaRepo;
import com.kahago.kahagoservice.repository.MVendorAreaRepo;
import com.kahago.kahagoservice.repository.TAreaRepo;
import com.kahago.kahagoservice.util.Common;

/**
 * @author Ibnu Wasis
 */
@Component
public class JNEComponent {
	private static final Logger logger = LoggerFactory.getLogger(JNEComponent.class);
	@Autowired
	private MAreaRepo areaRepo;
	@Autowired
	private MVendorAreaRepo vendorAreaRepo;
	public BookResponseJNE getPayment(String clientcode,String stt,
			String operatorsw,String url,TPaymentEntity pay,MOfficeEntity office) throws Exception {
		MAreaEntity originKaha = areaRepo.findByKotaEntityAreaKotaId(pay.getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getAreaKotaId());
//		List<TAreaEntity> tArea = tAreaRepo
//				.findTOPByProductSwCodeAndAreaIdAndAreaOriginIdOrderByTarifAsc(pay.getProductSwCode(), pay.getIdPostalCode(),
//						originKaha.getAreaId());
		List<MVendorAreaEntity> area = vendorAreaRepo.findAllByPostalCodeIdAndSwitcherCode(pay.getIdPostalCode(), pay.getProductSwCode().getSwitcherEntity().getSwitcherCode());
		String phoneSender=pay.getSenderTelp().replace("+", "").replace("-", "");
		String phoneRecv =pay.getReceiverTelp().replace("+", "").replace("-", "");
		String branch = clientcode.split("[|]")[0];
		String origin = clientcode.split("[|]")[1];
		BookRequestJNE bookRequestJNE = new BookRequestJNE();
		bookRequestJNE.setBookCode(pay.getBookingCode());
		bookRequestJNE.setBranch(branch);
		bookRequestJNE.setSenderName(replaceRegex(pay.getSenderName()));
		bookRequestJNE.setSenderAddress(replaceRegex(pay.getSenderAddress()));
		bookRequestJNE.setSenderCity(pay.getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getName());
		bookRequestJNE.setSenderRegion(pay.getPickupAddrId().getPostalCode().getKecamatanEntity().getKecamatan());
		bookRequestJNE.setSenderPostalCode(pay.getPickupAddrId().getPostalCode().getPostalCode()+"");
		bookRequestJNE.setSenderPhone(phoneSender);
		bookRequestJNE.setReceiverName(replaceRegex(pay.getReceiverName()));
		bookRequestJNE.setReceiverAddress(replaceRegex(pay.getReceiverAddress()));
		bookRequestJNE.setReceiverCity(pay.getIdPostalCode().getKecamatanEntity().getKotaEntity().getName());
		bookRequestJNE.setReceiverDistrict(pay.getIdPostalCode().getKecamatanEntity().getKecamatan());
		bookRequestJNE.setReceiverPostalCode(pay.getIdPostalCode().getPostalCode()+"");
		bookRequestJNE.setReceiverPhone(phoneRecv);
		bookRequestJNE.setTotalItem(pay.getJumlahLembar()+"");
		List<ItemJne> litem = new ArrayList<ItemJne>();
		for (TBookEntity tBook:pay.getTbooks()) {
			ItemJne itemJne=new ItemJne();
			if(Double.valueOf(tBook.getGrossWeight()).intValue()>Double.valueOf(tBook.getVolWeight()).intValue())
				itemJne.setWeight(Double.valueOf(tBook.getGrossWeight()).intValue()+"");
			else
				itemJne.setWeight(Double.valueOf(tBook.getVolWeight()).intValue()+"");
			litem.add(itemJne);
		}
		bookRequestJNE.setItems(litem);
		bookRequestJNE.setGoodsDescription(replaceRegex(pay.getGoodsDesc()));
		bookRequestJNE.setGoodsPrice(pay.getPriceGoods()+"");
		bookRequestJNE.setProductCode(pay.getProductSwCode().getOperatorSw());
		if(pay.getInsurance().compareTo(new BigDecimal("0"))==1)
			bookRequestJNE.setInsurance("Y");
		else
			bookRequestJNE.setInsurance("N");
		bookRequestJNE.setOrigin(origin);
		bookRequestJNE.setDestination(area.get(0).getSendRequest());
		bookRequestJNE.setPrice(pay.getPrice()+"");
		bookRequestJNE.setNotes(replaceRegex(pay.getNote()));
		
			ObjectMapper obj = new ObjectMapper();
			
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<BookResponseJNE> response = null;
			HttpEntity<BookRequestJNE> request = new HttpEntity<>(bookRequestJNE);
		try {
			logger.info("Request to JNE:=> "+obj.writeValueAsString(bookRequestJNE));
			response = restTemplate
					.exchange(url, HttpMethod.POST, request, BookResponseJNE.class);
		}catch (Exception e) {
			// TODO: handle exception
//			pay.setStatus(PaymentEnum.REQUEST.getCode());
			logger.error("Error Request JNE :"+e.getMessage());
			throw e;
		}
		logger.info("Response From JNE: "+ response.getStatusCodeValue());
		logger.info("Response Body: "+ response.getBody().toString());
		return response.getBody();
	}
	
	private String replaceRegex(String fromString) {
		Pattern pattern = Pattern.compile("[A-Za-z0-9-.()/,]*");
		String [] strToArray = fromString.split("");
		for(String str : strToArray) {
			Matcher matcher = pattern.matcher(str);
			if(!matcher.matches()) {
				fromString = fromString.replace(str, "");
			}
		}
		return fromString;
	}
}
