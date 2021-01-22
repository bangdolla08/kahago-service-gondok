package com.kahago.kahagoservice.component.vendor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.kahago.kahagoservice.entity.MVendorPropEntity;
import com.kahago.kahagoservice.entity.MAreaEntity;
import com.kahago.kahagoservice.entity.MVendorAreaEntity;
import com.kahago.kahagoservice.entity.TAreaEntity;
import com.kahago.kahagoservice.entity.TMapLayananEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.model.request.BookRequestIndah;
import com.kahago.kahagoservice.model.response.BookResponseIndah;
import com.kahago.kahagoservice.repository.MAreaRepo;
import com.kahago.kahagoservice.repository.MVendorAreaRepo;
import com.kahago.kahagoservice.repository.TAreaRepo;
import com.kahago.kahagoservice.repository.TMapLayananRepo;
import com.kahago.kahagoservice.util.Common;

/**
 * @author Ibnu Wasis
 */
@Component
public class IndahCargoComponent {
	@Autowired
	private TAreaRepo tAreaRepo;
	@Autowired
	private TMapLayananRepo tMapLayananRepo;
	@Autowired
	private MVendorAreaRepo vendorAreaRepo;
	@Autowired
	private MAreaRepo areaRepo;
	private static final Logger logger = LoggerFactory.getLogger(IndahCargoComponent.class);
	public static final String ID_KOTA_SUB = "265";
	public static final String ID_KOTA_SDA = "251";
	public BookResponseIndah getPayment(String url,TPaymentEntity pay,MVendorPropEntity vendorProp) throws Exception{
		logger.info("===>Indah Book<====");
		MAreaEntity origin = areaRepo.findByKotaEntityAreaKotaId(pay.getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getAreaKotaId());
		List<TAreaEntity> tArea = tAreaRepo
				.findTOPByProductSwCodeAndAreaIdAndAreaOriginIdOrderByTarifAsc(pay.getProductSwCode(), pay.getIdPostalCode(),
						origin.getAreaId());
		String phoneSender=pay.getSenderTelp().replace("+", "").replace("-", "");
		String phoneRecv =pay.getReceiverTelp().replace("+", "").replace("-", "");
		BookRequestIndah req = new BookRequestIndah();
//		if(getOriginSub(pay.getOrigin())) {
//			req.setOrigin(ID_KOTA_SUB);
//		}else {
//			req.setOrigin(ID_KOTA_SDA);
//		}
		MVendorAreaEntity vendorArea = vendorAreaRepo.findBySwitcherAndPostalCode(vendorProp.getSwitcherCode().getSwitcherCode(), pay.getIdPostalCode().getIdPostalCode());
		TMapLayananEntity layanan = tMapLayananRepo.findByIdPostalCodeAndSwitcherCode(pay.getIdPostalCode().getIdPostalCode(),vendorProp.getSwitcherCode().getSwitcherCode());
		int totalGross = (pay.getGrossWeight().intValue()>pay.getVolume().intValue()) ? pay.getGrossWeight().intValue() : pay.getVolume().intValue();
		totalGross += Optional.ofNullable(pay.getTotalPackKg().intValue()).orElse(0);
		TAreaEntity area = tArea.get(0);
		BigDecimal payAmt = new BigDecimal((area.getTarif().doubleValue()/area.getMinimumKg())).multiply(new BigDecimal(totalGross));
	
		if(totalGross<area.getMinimumKg()) {
			totalGross = area.getMinimumKg();
			payAmt = area.getTarif();
		}
		req.setOrigin(vendorProp.getClientCode());
		req.setDestination(vendorArea.getSendRequest());
		req.setAmount(payAmt.toString());
		req.setBookingCode(pay.getBookingCode());
		req.setComodity(pay.getComodity());
		req.setGrossWeight(String.valueOf(totalGross));
		req.setIdModa(String.valueOf("5"));
		req.setSenderName(Common.getString(pay.getSenderName()));
		req.setSenderAddress(Common.getString(pay.getSenderAddress()));
		req.setSenderTelp(phoneSender);
		req.setReceiverName(Common.getString(pay.getReceiverName()));
		req.setReceiverAddress(Common.getString(pay.getReceiverAddress()));
		req.setReceiverTelp(phoneRecv);
		req.setNoResi(pay.getStt());
		req.setJmlBarang(pay.getJumlahLembar().toString());
		logger.info("Booking Request :"+req.toString());
		HttpEntity<BookRequestIndah> request = new HttpEntity<BookRequestIndah>(req);
		ResponseEntity<BookResponseIndah> response = null;
		RestTemplate rest = new RestTemplate();
		try {
			response = rest.exchange(url, HttpMethod.POST, request, BookResponseIndah.class);
		}catch (Exception e) {
			// TODO: handle exception
//			pay.setStatus(PaymentEnum.REQUEST.getCode());
			throw e;
		}
		return response.getBody();
	}
	public Boolean getOriginSub(String origin) {
		if(origin.equals("Surabaya")) {
			return true;
		}
		return false;
	}

}
