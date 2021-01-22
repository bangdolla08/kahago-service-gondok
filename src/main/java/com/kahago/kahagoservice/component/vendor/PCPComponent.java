package com.kahago.kahagoservice.component.vendor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.kahago.kahagoservice.entity.MAreaEntity;
import com.kahago.kahagoservice.entity.TAreaEntity;
import com.kahago.kahagoservice.entity.TBookEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.model.request.BookRequestPCP;
import com.kahago.kahagoservice.model.request.ItemPCP;
import com.kahago.kahagoservice.model.response.BookResponsePCP;
import com.kahago.kahagoservice.repository.MAreaRepo;
import com.kahago.kahagoservice.repository.TAreaRepo;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ibnu Wasis
 */
@Component
@Slf4j
public class PCPComponent {
	@Autowired
	private TAreaRepo tAreaRepo;
	@Autowired
	private MAreaRepo areaRepo;
	
	private static final String ORIGIN = "SUB1642100";
	private static final Integer SHIPTYPEID = 3;
	private static final Integer CONTENTID = 6;
	private static final Integer GOODSID = 28;
	private static final Integer HANDLINGID = 1;
	public BookResponsePCP getPayment(String url,TPaymentEntity pay,String clientCode) throws Exception{
		log.info("==> PCP Book <===");
		MAreaEntity origin = areaRepo.findByKotaEntityAreaKotaId(pay.getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getAreaKotaId());
		List<TAreaEntity> tArea = tAreaRepo
				.findTOPByProductSwCodeAndAreaIdAndAreaOriginIdOrderByTarifAsc(pay.getProductSwCode(), pay.getIdPostalCode(),
						origin.getAreaId());
		String destination = tArea.get(0).getAreaSwitcher();
		BookRequestPCP request = new BookRequestPCP();
		request.setDestinationId(destination);
		request.setDestinationCityId(Integer.valueOf(destination.substring(5, 8)));
		request.setDestinationProvinceId(Integer.valueOf(destination.substring(3, 5)));
		request.setOriginId(clientCode);
		request.setServiceId(getServiceId(pay));
		request.setShipTypeId(SHIPTYPEID);
		request.setTotalActualWeight(pay.getGrossWeight().doubleValue());
		request.setContentId(CONTENTID);
		request.setGoodsId(GOODSID);
		request.setHandlingId(HANDLINGID);
		request.setShipperName(pay.getSenderName());
		request.setShipperAddress(pay.getSenderAddress());
		request.setShipperTelephone(pay.getSenderTelp());
		request.setReceiverName(pay.getReceiverName());
		request.setReceiverAddress(pay.getReceiverAddress());
		request.setReceiverTelephone(pay.getReceiverTelp());
		request.setIsInsurance(pay.getInsurance().compareTo(BigDecimal.ZERO) > 0?true:false);
		request.setIsPacking(pay.getExtraCharge().compareTo(BigDecimal.ZERO) > 0?true:false);
		request.setValueOfInsurance(pay.getInsurance().doubleValue());
		request.setValueOfGoods(pay.getPriceGoods().doubleValue());
		request.setShipperZipCode(pay.getPickupAddrId().getPostalCode().getPostalCode());
		request.setReceiverZipCode(pay.getIdPostalCode().getPostalCode());
		request.setNotes(pay.getGoodsDesc());
		List<ItemPCP> items = new ArrayList<ItemPCP>();
		int seq = 1;
		for(TBookEntity tb : pay.getTbooks()) {
			ItemPCP item = new ItemPCP();
			item.setSeq(seq);
			item.setActualWeight(Double.valueOf(tb.getGrossWeight()));
			item.setHeight(Integer.valueOf(tb.getHeight()));
			item.setLength(Integer.valueOf(tb.getLength()));
			item.setVolume(Integer.valueOf(tb.getVolWeight()));
			item.setWidth(Integer.valueOf(tb.getWidth()));
			items.add(item);
			seq = seq+1;
		}
		request.setItems(items);
		log.info("Request to PCP:" +request.toString());
		HttpEntity<BookRequestPCP> req = new HttpEntity<>(request);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<BookResponsePCP> response = null;
		try {
			response = restTemplate.exchange(url, HttpMethod.POST, req, BookResponsePCP.class);
		} catch (Exception e) {
			// TODO: handle exception
			log.error("Error Request PCP :"+e.getMessage());
			e.printStackTrace();
			throw e;
		}
		log.info("Response From PCP: "+ response.getStatusCodeValue());
		log.info("Response Body: "+ response.getBody().toString());
		return response.getBody();
	}
	
	private Integer getServiceId(TPaymentEntity pay) {
		Integer result = 0;
		if(pay.getProductSwCode().getName().equals("TREX")) {
			result = 45;
		}else if(pay.getProductSwCode().getName().equals("JET")){
			result = 44;
		}else if(pay.getProductSwCode().getName().equals("GODA")) {
			result = 46;
		}else {
			result = 43;
		}
		
		return result;
	}
}
