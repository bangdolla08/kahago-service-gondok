package com.kahago.kahagoservice.component.vendor;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kahago.kahagoservice.entity.MAreaEntity;
import com.kahago.kahagoservice.entity.MOfficeEntity;
import com.kahago.kahagoservice.entity.MPostalCodeEntity;
import com.kahago.kahagoservice.entity.MVendorAreaEntity;
import com.kahago.kahagoservice.entity.TAreaEntity;
import com.kahago.kahagoservice.entity.TBookEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.request.BookRequestJet;
import com.kahago.kahagoservice.model.request.ItemsJet;
import com.kahago.kahagoservice.model.response.BookResponseJet;
import com.kahago.kahagoservice.repository.MAreaRepo;
import com.kahago.kahagoservice.repository.MPostalCodeRepo;
import com.kahago.kahagoservice.repository.MVendorAreaRepo;
import com.kahago.kahagoservice.repository.TAreaRepo;
import com.kahago.kahagoservice.util.Common;

/**
 * @author Ibnu Wasis
 */
@Component
public class WahanaComponent {
	private static final Logger logger = LoggerFactory.getLogger(WahanaComponent.class);
//	@Autowired
//	private TAreaRepo tAreaRepo;
//	@Autowired
//	private MAreaRepo areaRepo;
//	@Autowired
//	private MPostalCodeRepo mPostalCodeRepo;
	@Autowired
	private MVendorAreaRepo vendorAreaRepo;
	public BookResponseJet getPayment(String clientcode,String stt,
			String operatorsw,String url,TPaymentEntity pay,MOfficeEntity office)throws Exception {
//		MAreaEntity originKaha = areaRepo.findByKotaEntityAreaKotaId(pay.getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getAreaKotaId());
//		List<MPostalCodeEntity> lPostalCode = mPostalCodeRepo.findAllByKecamatanEntityAreaDetailId(pay.getIdPostalCode().getKecamatanEntity().getAreaDetailId());
//		List<TAreaEntity> tArea = new ArrayList<>();
//		for(MPostalCodeEntity pc : lPostalCode) {
//			 tArea = tAreaRepo.findTOPByProductSwCodeAndAreaIdOrderByTarifAsc(pay.getProductSwCode(), pc);
//			 if(tArea.size() > 0) {
//				 break;
//			 }
//		}		
		List<MVendorAreaEntity> area = vendorAreaRepo.findAllByPostalCodeIdAndSwitcherCode(pay.getIdPostalCode(), pay.getProductSwCode().getSwitcherEntity().getSwitcherCode());
		MVendorAreaEntity areaVendor = area.stream().findAny().orElseThrow(()-> new NotFoundException("Data Destinasi Tidak Ditemukan"));
		String phoneSender=pay.getSenderTelp().replace("+", "").replace("-", "");
		String phoneRecv =pay.getReceiverTelp().replace("+", "").replace("-", "");
		String originId = clientcode.split("[|]")[0];
		String key = clientcode.split("[|]")[1];
		String partnerId = clientcode.split("[|]")[2];
		BookRequestJet rbs = new BookRequestJet();
		rbs.setBookCode(pay.getBookingCode());
		rbs.setStt(stt);
		rbs.setIsInsurance("false");
		List<ItemsJet> pli = new ArrayList<>();
		rbs.setApiKey(key);
		rbs.setPartnerId(partnerId);
		rbs.setOrigin(originId);
		rbs.setDestination(areaVendor.getSendRequest());
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
		
		for (TBookEntity tbook :pay.getTbooks()) {
			ItemsJet item = new ItemsJet();
			item.setLength(tbook.getLength());
			item.setHeight(tbook.getHeight());
			item.setWidth(tbook.getWidth());
			item.setWeight(tbook.getGrossWeight());
			pli.add(item);
		}
		
		rbs.setItems(pli);
		HttpEntity<BookRequestJet> request = new HttpEntity<>(rbs);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<BookResponseJet> response = null;
		try {
			ObjectMapper obj = new ObjectMapper();
			logger.info("Request to wahana:=> "+obj.writeValueAsString(rbs));
			response = restTemplate
					.exchange(url, HttpMethod.POST, request, BookResponseJet.class);
		}catch (Exception e) {
			// TODO: handle exception
//			pay.setStatus(PaymentEnum.REQUEST.getCode());
			logger.error("Error Request Wahana :"+e.getMessage());
			throw e;
		}
		logger.info("Response From Wahana: "+ response.getStatusCodeValue());
		logger.info("Response Body: "+ response.getBody().toString());
		return response.getBody();
	}
}
