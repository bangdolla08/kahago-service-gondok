package com.kahago.kahagoservice.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.kahago.kahagoservice.model.response.*;
import org.jfree.util.Log;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.kahago.kahagoservice.client.CronFeignService;
import com.kahago.kahagoservice.entity.MAreaEntity;
import com.kahago.kahagoservice.entity.MAreaProvinsiEntity;
import com.kahago.kahagoservice.entity.MVendorAreaEntity;
import com.kahago.kahagoservice.entity.TAreaEntity;
import com.kahago.kahagoservice.enummodel.AreaEnum;
import com.kahago.kahagoservice.model.request.CronRequest;
import com.kahago.kahagoservice.repository.MAreaDetailRepo;
import com.kahago.kahagoservice.repository.MAreaRepo;
import com.kahago.kahagoservice.repository.MPostalCodeRepo;
import com.kahago.kahagoservice.repository.MProductSwitcherRepo;
import com.kahago.kahagoservice.repository.MSwitcherRepo;
import com.kahago.kahagoservice.repository.MVendorAreaRepo;
import com.kahago.kahagoservice.repository.TAreaRepo;
import com.kahago.kahagoservice.util.Common;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 24 Jun 2020
 */
@Slf4j
@Service
public class CronService {
	@Value("${url.cron.ip}")
	private String urlCronIp;
	@Autowired
	private CronFeignService cronNet;
	private static String URL_CRON="/mapping/cron";
	@Autowired
	private TAreaRepo areaRepo;
	@Autowired
	private MPostalCodeRepo postalCodeRepo;
	@Autowired
	private MSwitcherRepo switcherRepo;
	@Autowired
	private MProductSwitcherRepo pswRepo;
	@Autowired
	private MVendorAreaRepo vendorAreaRepo;
	@Autowired
	private MAreaDetailRepo areaDetailRepo;
	@Autowired
	private MAreaRepo mAreaRepo;
	public SaveResponse getResultCron(CronRequest req) {
		List<TArea> lsArea = req.getAreas();
		List<Long> lsIdArea = lsArea.stream().map(a->Long.valueOf(a.getSeqid())).collect(Collectors.toList());
		List<TAreaEntity> lsAreaEntity = areaRepo
				.findBySeqidIn(lsIdArea);
		lsAreaEntity = lsAreaEntity.stream().map(m->
				updateArea(m, lsArea.stream()
						.filter(p->p.getSeqid()==m.getSeqid().intValue()).collect(Collectors.toList()).get(0))).collect(Collectors.toList());
		areaRepo.saveAll(lsAreaEntity);
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Success")
				.build();
	}

	
	private TAreaEntity updateArea(TAreaEntity areaEntity,TArea area) {
		areaEntity.setAreaSwitcher(area.getAreaSwitcher());
		areaEntity.setEndDay(area.getEndDay());
		areaEntity.setStartDay(area.getStartDay());
		areaEntity.setLastUpdate(LocalDateTime.now());
		areaEntity.setLimitMinimum(area.getLimitMinimum());
		areaEntity.setMinimumKg(area.getMinimumKg());
		areaEntity.setNextRate(new BigDecimal(area.getNextRate()));
		areaEntity.setTarif(new BigDecimal(area.getTarif()));
		areaEntity.setAreaId(postalCodeRepo.findByIdPostalCode(area.getAreaId()));
		areaEntity.setProductSwitcher(area.getProductSwitcher());
		areaEntity.setVendor(switcherRepo.findById(area.getVendor()).get());
		areaEntity.setStatus(true);
		areaEntity.setAreaOriginId(area.getAreaOriginId());
		areaEntity.setProductSwCode(pswRepo.findByProductSwCode(area.getProductSwCode().longValue()));
		areaEntity.setSeqid(areaEntity.getSeqid());
		return areaEntity;
	}
	
	private MVendorAreaEntity updateVendorArea(VendorAreaDetail area) {
		MVendorAreaEntity areaEntity = vendorAreaRepo.findByAreaIdAndPostalCode(area.getAreaId(), area.getPostalCodeId(), area.getSwitcherCode());
		if(areaEntity==null) {
			areaEntity = new MVendorAreaEntity();
			areaEntity.setAreaId(areaDetailRepo.findByAreaDetailId(area.getAreaId()));
			areaEntity.setPostalCodeId(postalCodeRepo.findByIdPostalCode(area.getPostalCodeId()));
			areaEntity.setRequestName(null);
			areaEntity.setSendRequest(null);
			areaEntity.setStatus(3);
			areaEntity.setUpdateBy("System");
			areaEntity.setSwitcherCode(area.getSwitcherCode());
			areaEntity.setLastupdate(LocalDateTime.now());
			areaEntity.setIsCheck(false);
			areaEntity.setFlagOrigin((byte)0);
			return areaEntity;
		}
		areaEntity.setRequestName(area.getRequestName());
		areaEntity.setSendRequest(area.getSendRequest());
		areaEntity.setIsCheck(area.getIsCheck());
		areaEntity.setLastupdate(LocalDateTime.now());
		areaEntity.setStatus(1);
		areaEntity.setUpdateBy("System");
		areaEntity.setSeqid(areaEntity.getSeqid());
		return areaEntity;
	}
	
	@Async("asyncExecutor")
	public void doUpdateToCron(VendorArea body,Integer Area) {
		log.info("==> Update to Cron <==");
		log.info("==> Request ==> "+Common.json2String(body));
		switch (AreaEnum.getEnum(Area)) {
		case PROV:
			cronNet.updateProv(body);
			break;
		case CITY:
			cronNet.updateKota(body);
			break;
		case KEC:
			cronNet.updateKecamatan(body);
			break;
		case KEL:
			cronNet.updateKelurahan(body);
			break;
		default:
			break;
		}
		
	}
	public void doUpdateVendorArea(VendorAreaDetail body) {
		log.info("==> Update vendor Area");
		log.info("==> Request ==> "+Common.json2String(body));
		cronNet.updateVendorArea(body);
	}

	public void doUpdateProductSwitcher(ProductSwitcher productSwitcher){
		log.info("==> Update Product Switcher");
		log.info("==> Request ==> "+Common.json2String(productSwitcher));
		cronNet.updateProductSwitcher(productSwitcher);
	}
	
	public SaveResponse doUpdateTArea(Integer areadetail) {
		log.info("==> Update TArea <==");
		log.info("==> Request "+areadetail);
		cronNet.updateUrgentArea(areadetail);
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Success")
				.build();
	}
	
	public SaveResponse doUpdateTAreByVendorAndIdPostalCode(Integer idPostalCode, Integer vendor) {
		log.info("==> Update TArea <==");
		log.info("==> Request "+idPostalCode+" vendor : "+vendor);
		List<MAreaEntity> lArea = mAreaRepo.findAll();
		for(MAreaEntity area:lArea) {
			cronNet.updateAreaByIdPostalCodeAndVendor(vendor.toString(), area.getAreaId(), idPostalCode.toString());
		}
		
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Success")
				.build();
	}
	
}
