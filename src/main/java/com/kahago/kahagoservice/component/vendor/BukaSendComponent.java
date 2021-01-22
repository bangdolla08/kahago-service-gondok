package com.kahago.kahagoservice.component.vendor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kahago.kahagoservice.entity.MAreaEntity;
import com.kahago.kahagoservice.entity.MVendorAreaEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.model.request.BookRequestBukaSend;
import com.kahago.kahagoservice.model.response.BookResponseBukaSend;
import com.kahago.kahagoservice.repository.MAreaRepo;
import com.kahago.kahagoservice.repository.MVendorAreaRepo;
import com.kahago.kahagoservice.repository.TAreaRepo;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ibnu Wasis
 */
@Component
@Slf4j
public class BukaSendComponent {
	@Autowired
	private MAreaRepo mAreaRepo;
	@Autowired
	private MVendorAreaRepo mVendorAreaRepo;
	
	public BookResponseBukaSend getPayment(TPaymentEntity payment,String url, String clientCode) throws Exception{
		BookRequestBukaSend request = new BookRequestBukaSend();
		//MAreaEntity areaEntity = mAreaRepo.findByKotaEntityAreaKotaId(payment.getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getAreaKotaId());
		MVendorAreaEntity destination = mVendorAreaRepo.findBySwitcherAndPostalCode(payment.getProductSwCode().getSwitcherEntity().getSwitcherCode(), payment.getIdPostalCode().getIdPostalCode());
		String areaFrom = clientCode.substring(0, clientCode.lastIndexOf(","));
		String cityFrom = clientCode.substring(clientCode.lastIndexOf(",")+1);
		request.setBookingCode(payment.getBookingCode());
		request.setAmount(payment.getAmount().toString());
		request.setComodity(payment.getComodity());
		request.setNote(payment.getGoodsDesc());
		request.setPrice(payment.getPrice().doubleValue());
		request.setInsured(payment.getInsurance().doubleValue());
		request.setGrossWeight(payment.getGrossWeight().intValue()*1000);
		request.setJmlBarang(payment.getJumlahLembar());
		request.setSenderEmail(payment.getUserId().getUserId());
		request.setSenderTelp(payment.getSenderTelp());
		request.setSenderName(payment.getSenderName());
		request.setSenderAddress(payment.getSenderAddress());
		request.setReceiverName(payment.getReceiverName());
		request.setReceiverEmail(payment.getReceiverEmail()==null?"":payment.getReceiverEmail());
		request.setReceiverTelp(payment.getReceiverTelp());
		request.setReceiverAddress(payment.getReceiverAddress());
		request.setPostalCodeFrom(payment.getPickupAddrId().getPostalCode().getPostalCode());
		request.setPostalCodeTo(payment.getIdPostalCode().getPostalCode());
		request.setProvinceFrom(payment.getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getProvinsiEntity().getName());
		request.setProvinceTo(payment.getIdPostalCode().getKecamatanEntity().getKotaEntity().getProvinsiEntity().getName());
		String areaTo = destination.getSendRequest().substring(0, destination.getSendRequest().lastIndexOf(","));
		String cityTo = destination.getSendRequest().substring(destination.getSendRequest().lastIndexOf(",")+1);
		request.setAreaTo(areaTo);
		request.setCityTo(cityTo);
		request.setAreaFrom(areaFrom);
		request.setCityFrom(cityFrom);
		request.setCourier(payment.getProductSwCode().getName());
		request.setPriceAdjustment(payment.getProductSwCode().getKomisi().doubleValue());
		request.setExtraCharge(0.0);
		payment.getTbooks().stream().forEach(book->{
			request.setLength(Integer.valueOf(book.getLength()));
			request.setHeight(Integer.valueOf(book.getHeight()));
			request.setWidth(Integer.valueOf(book.getWidth()));
		});
		ResponseEntity<BookResponseBukaSend> response = null;
		try {
			ObjectMapper obj = new ObjectMapper();
			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<BookRequestBukaSend> entity = new HttpEntity<BookRequestBukaSend>(request,header);
			RestTemplate rest = new RestTemplate();
			
			log.info("Request Buka Send => "+obj.writeValueAsString(request));
			response = rest.exchange(url, HttpMethod.POST, entity, BookResponseBukaSend.class);
			log.info("Response Bukas Send ==> "+response.getBody());
		}catch (Exception e) {
			// TODO: handle exception
			log.error("Error Buka Send : "+e.getMessage());
			throw e;
		}
		return response.getBody();
	}
}
