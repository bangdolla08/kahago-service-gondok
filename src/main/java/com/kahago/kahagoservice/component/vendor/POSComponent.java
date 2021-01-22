package com.kahago.kahagoservice.component.vendor;

import java.util.ArrayList;
import java.util.List;

import com.kahago.kahagoservice.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.kahago.kahagoservice.entity.MManifestPosEntity;
import com.kahago.kahagoservice.entity.MOfficeEntity;
import com.kahago.kahagoservice.entity.MPropPosEntity;
import com.kahago.kahagoservice.entity.MUserEntity;
import com.kahago.kahagoservice.entity.MVendorAreaEntity;
import com.kahago.kahagoservice.entity.TBookEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.model.request.BookRequestPos;
import com.kahago.kahagoservice.model.request.ItemsJet;
import com.kahago.kahagoservice.model.response.BookResponseJet;
import com.kahago.kahagoservice.repository.MManifestPosRepo;
import com.kahago.kahagoservice.repository.MPropPosRepo;
import com.kahago.kahagoservice.repository.MVendorAreaRepo;
import com.kahago.kahagoservice.util.Common;

/**
 * @author Ibnu Wasis
 */
@Component
public class POSComponent {
	private static final Logger logger = LoggerFactory.getLogger(POSComponent.class);
	@Autowired
	private MPropPosRepo mPosRepo;
	@Autowired
	private MManifestPosRepo manifestPosRepo;
	@Autowired
	private MVendorAreaRepo vendorAreaRepo;
	public BookResponseJet getPayment(String clientcode,String stt,
			String operatorsw,String url,TPaymentEntity pay,MOfficeEntity office, MUserEntity user) throws Exception{
		MPropPosEntity propPos = mPosRepo.findByOfficeCode(office.getOfficeCode());
		MManifestPosEntity manifestPos = manifestPosRepo.findByUseridAndStatus(clientcode,1).orElseThrow(() -> new NotFoundException("There are still active manifests for this user"));
		String phoneSender=pay.getSenderTelp().replace("+", "").replace("-", "");
		String phoneRecv =pay.getReceiverTelp().replace("+", "").replace("-", "");
		
		List<MVendorAreaEntity> area = vendorAreaRepo.findAllByPostalCodeIdAndSwitcherCode(pay.getIdPostalCode(), pay.getProductSwCode().getSwitcherEntity().getSwitcherCode());
		BookRequestPos rbs = new BookRequestPos();
		rbs.setUserid(clientcode);
		rbs.setBookCode(pay.getBookingCode());
		rbs.setOrigin(pay.getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getName());
		rbs.setDestination(pay.getDestination());
		rbs.setProductCode(pay.getProductSwCode().getOperatorSw());
		rbs.setGoodsDescription(Common.getString(pay.getGoodsDesc()));
		rbs.setGoodsPrice(pay.getPriceGoods().toString());
		rbs.setNotes(pay.getNote());
		rbs.setSenderName(Common.getString(pay.getSenderName()));
		rbs.setSenderAddress(Common.getString(pay.getSenderAddress()));
		rbs.setSenderPhone(phoneSender);
//		rbs.setSenderPostalCode(String.valueOf(pay.getPickupAddress().getPostalCode().getPostalCode()));
		rbs.setSenderPostalCode(office.getPostalCode());
		rbs.setSenderCity(pay.getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getName());
		rbs.setSenderSubdistrict(pay.getPickupAddrId().getPostalCode().getKecamatanEntity().getKecamatan());
		rbs.setSenderCountry("Indonesia");

		rbs.setReceiverName(Common.getString(pay.getReceiverName()));
		rbs.setReceiverAddress(Common.getString(pay.getReceiverAddress()));
		rbs.setReceiverPhone(phoneRecv);
		rbs.setReceiverPostalCode(area.get(0).getSendRequest());
		rbs.setReceiverSubDistrict(pay.getIdPostalCode().getKecamatanEntity().getKecamatan());
		rbs.setReceiverCity(pay.getIdPostalCode().getKecamatanEntity().getKotaEntity().getName());
		rbs.setReceiverCountry("Indonesia");

		rbs.setQuantity(String.valueOf(pay.getTbooks().size()));
		rbs.setWeight(String.valueOf(pay.getGrossWeight()));
		rbs.setVolume(String.valueOf(pay.getVolume()));
		rbs.setAgenid(propPos.getAgenid());
		rbs.setTransref(manifestPos.getTransref());
		rbs.setManifestnumber(manifestPos.getManifestNumber());
		rbs.setSign(manifestPos.getSign());
		List<ItemsJet> pli = new ArrayList<>();
		for(TBookEntity tbook : pay.getTbooks()) {
			ItemsJet item = new ItemsJet();
			item.setLength(tbook.getLength());
			item.setHeight(tbook.getHeight());
			item.setWidth(tbook.getWidth());
			item.setWeight(tbook.getGrossWeight());
			pli.add(item);
		}
		rbs.setItems(pli);
		logger.info("Request to POS:" +rbs.toString());
		HttpEntity<BookRequestPos> request = new HttpEntity<>(rbs);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<BookResponseJet> response = null;
		try {
			response = restTemplate
					.exchange(url, HttpMethod.POST, request, BookResponseJet.class);
		}catch (Exception e) {
			// TODO: handle exception
//			pay.setStatus(PaymentEnum.REQUEST.getCode());
			logger.error("Error Request POS :"+e.getMessage());
			throw e;
		}
		logger.info("Response From POS: "+ response.getStatusCodeValue());
		logger.info("Response Body: "+ response.getBody().toString());
		return response.getBody();
		
	}
}
