package com.kahago.kahagoservice.component.vendor;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.internal.util.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.kahago.kahagoservice.entity.MAreaEntity;
import com.kahago.kahagoservice.entity.MOfficeEntity;
import com.kahago.kahagoservice.entity.MVendorAreaEntity;
import com.kahago.kahagoservice.entity.MVendorPropEntity;
import com.kahago.kahagoservice.entity.TAreaEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.model.request.BookRequestSicepat;
import com.kahago.kahagoservice.model.request.PackageListItem;
import com.kahago.kahagoservice.model.response.BookResponseSicepat;
import com.kahago.kahagoservice.repository.MAreaRepo;
import com.kahago.kahagoservice.repository.MVendorAreaRepo;
import com.kahago.kahagoservice.repository.MVendorPropRepo;
import com.kahago.kahagoservice.repository.TAreaRepo;
import com.kahago.kahagoservice.util.Common;

/**
 * @author Ibnu Wasis
 */
@Component
public class SicepatComponent {
	@Autowired
	private MVendorAreaRepo vendorAreaRepo;
	@Autowired
	private MVendorPropRepo vendorProp;
	private static final Logger logger = LoggerFactory.getLogger(SicepatComponent.class);
	
	public BookResponseSicepat getPayment(String clientcode,String stt,
			String operatorsw,String url,TPaymentEntity pay,MOfficeEntity office)throws Exception {
		BookRequestSicepat rbs = new BookRequestSicepat();
//		MAreaEntity originKaha = areaRepo.findByKotaEntityAreaKotaId(pay.getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getAreaKotaId());
//		List<TAreaEntity> tArea = tAreaRepo
//				.findTOPByProductSwCodeAndAreaIdAndAreaOriginIdOrderByTarifAsc(pay.getProductSwCode(), pay.getIdPostalCode(),
//						originKaha.getAreaId());
		MVendorPropEntity prop = vendorProp.findAllBySwitcherCodeAndActionAndOrigin(pay.getProductSwCode().getSwitcherEntity(), "book", pay.getOrigin());
		List<MVendorAreaEntity> area = vendorAreaRepo.findAllByPostalCodeIdAndSwitcherCode(pay.getIdPostalCode(), pay.getProductSwCode().getSwitcherEntity().getSwitcherCode());
		String phoneSender=pay.getSenderTelp().replace("+", "").replace("-", "");
		String phoneRecv =pay.getReceiverTelp().replace("+", "").replace("-", "");
		rbs.setReferenceNumber(pay.getBookingCode());
		rbs.setPickupMerchantName("Kaha Go");
		rbs.setPickupAddress(office.getAddress());
		rbs.setPickupCity(office.getCity());
		rbs.setPickupMerchantPhone(office.getTelp());
		rbs.setPickupMerchantEmail("admin@kahago.com");
		PackageListItem pli = new PackageListItem();
		pli.setCustPackageId(pay.getBookingCode());
		pli.setReceiptNumber(stt);
		pli.setOriginCode(prop.getClientCode());
		pli.setDeliveryType(pay.getProductSwCode().getDisplayName());
		pli.setParcelCategory(pay.getComodity());
		pli.setParcelContent(Common.getString(pay.getGoodsDesc()));
		pli.setParcelQty(String.valueOf(pay.getJumlahLembar()));
		pli.setParcelUom("Pcs");
		pli.setParcelValue(pay.getPriceGoods().toString());
		pli.setTotalWeight((pay.getGrossWeight()>
			pay.getVolume())?String.valueOf(pay.getGrossWeight())
				:String.valueOf(pay.getVolume())
				);
//		pli.setShipperCode(shipperCode);
		pli.setShipperName(Common.getString(pay.getSenderName()));
		pli.setShipperAddress(Common.getString(pay.getSenderAddress()));
		pli.setShipperProvince(pay.getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getProvinsiEntity().getName());
		pli.setShipperCity(pay.getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getName());
		pli.setShipperDistrict(pay.getPickupAddrId().getPostalCode().getKecamatanEntity().getKecamatan());
		pli.setShipperZip(pay.getPickupAddrId().getPostalCode().getPostalCode());
		pli.setShipperPhone(phoneSender);
		
		pli.setRecipientTitle("Mr");
		pli.setRecipientName(Common.getString(pay.getReceiverName()));
		pli.setRecipientAddress(Common.getString(pay.getReceiverAddress()));
		pli.setRecipientProvince(pay.getIdPostalCode().getKecamatanEntity().getKotaEntity().getProvinsiEntity().getName());
		pli.setRecipientCity(pay.getIdPostalCode().getKecamatanEntity().getKotaEntity().getName());
		pli.setRecipientDistrict(pay.getIdPostalCode().getKecamatanEntity().getKecamatan());
		pli.setRecipientZip(String.valueOf(pay.getIdPostalCode().getPostalCode()));
		pli.setRecipientPhone(phoneRecv);
		pli.setDestinationCode(area.get(0).getSendRequest());
		pli.setNotes(pay.getNote());
		List<PackageListItem> lpli = new ArrayList<PackageListItem>();
		lpli.add(pli);
		rbs.setDatas(lpli);
		HttpEntity<BookRequestSicepat> request = new HttpEntity<>(rbs);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<BookResponseSicepat> response = null;
		try {
			response = restTemplate
					  .exchange(url, HttpMethod.POST, request, BookResponseSicepat.class);
		}catch (Exception e) {
			// TODO: handle exception
//			pay.setStatus(PaymentEnum.REQUEST.getCode());
			logger.error("Error Request SICepat :"+e.getMessage());
			throw e;    		
		}
		logger.info("Response From Si Cepat: "+ response.getStatusCodeValue());
		logger.info("Response Body: "+ response.getBody().toString());
		return response.getBody();
	}

}
