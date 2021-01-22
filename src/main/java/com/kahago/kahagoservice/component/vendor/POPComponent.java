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

import com.kahago.kahagoservice.entity.MAreaEntity;
import com.kahago.kahagoservice.entity.TAreaEntity;
import com.kahago.kahagoservice.entity.TBookEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.model.request.BookRequestPop;
import com.kahago.kahagoservice.model.request.ItemsJet;
import com.kahago.kahagoservice.model.response.BookResponseJet;
import com.kahago.kahagoservice.repository.MAreaRepo;
import com.kahago.kahagoservice.repository.TAreaRepo;
import com.kahago.kahagoservice.util.Common;

/**
 * @author Ibnu Wasis
 */
@Component
public class POPComponent {
	@Autowired
	private TAreaRepo tAreaRepo;
	@Autowired
	private MAreaRepo areaRepo;
	private static final Logger logger = LoggerFactory.getLogger(POPComponent.class);
	
	public BookResponseJet getPayment(String clientcode,String stt,
			String operatorsw,String url,TPaymentEntity pay) throws Exception{
		
		MAreaEntity originKaha = areaRepo.findByKotaEntityAreaKotaId(pay.getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getAreaKotaId());
		List<TAreaEntity> tArea = tAreaRepo
				.findTOPByProductSwCodeAndAreaIdAndAreaOriginIdOrderByTarifAsc(pay.getProductSwCode(), pay.getIdPostalCode(),
						originKaha.getAreaId());
		String phoneSender=pay.getSenderTelp().replace("+", "").replace("-", "");
		String phoneRecv =pay.getReceiverTelp().replace("+", "").replace("-", "");
		BookRequestPop rbs = new BookRequestPop();
		List<ItemsJet> pli = new ArrayList<>();
		rbs.setOrigin("SUB");
		rbs.setDestination(tArea.get(0).getAreaSwitcher());
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
		rbs.setReceiverAddress(Common.getString(pay.getSenderAddress()));
		rbs.setReceiverPhone(phoneRecv);
		rbs.setReceiverPostalCode(String.valueOf(pay.getIdPostalCode().getPostalCode()));
		rbs.setReceiverDistrict(pay.getIdPostalCode().getKecamatan());
		rbs.setReceiverProvince(pay.getIdPostalCode().getProvinsi());
		rbs.setReceiverCity(pay.getIdPostalCode().getKota());
		rbs.setReceiverEmail("admin@kahago.com");
		for (TBookEntity tbook : pay.getTbooks()) {
			ItemsJet item = new ItemsJet();
			
			item.setWeight(tbook.getGrossWeight());
			pli.add(item);
		}
		rbs.setItems(pli);

		logger.info("Request to POP:" +rbs.toString());
		HttpEntity<BookRequestPop> request = new HttpEntity<>(rbs);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<BookResponseJet> response = null;
		try {
			response = restTemplate
					  .exchange(url, HttpMethod.POST, request, BookResponseJet.class);
		}catch (Exception e) {
			// TODO: handle exception
//			pay.setStatus(PaymentEnum.REQUEST.getCode());
			logger.error("Error Request POP :"+e.getMessage());
			throw e;
		}
		logger.info("Response From POP: "+ response.getStatusCodeValue());
		logger.info("Response Body: "+ response.getBody().toString());
		return response.getBody();
	}

}
