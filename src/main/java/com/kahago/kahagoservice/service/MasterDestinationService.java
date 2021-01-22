package com.kahago.kahagoservice.service;

import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kahago.kahagoservice.client.FeignService;
import com.kahago.kahagoservice.entity.MAreaDetailEntity;
import com.kahago.kahagoservice.entity.MPostalCodeEntity;
import com.kahago.kahagoservice.entity.MVendorAreaEntity;
import com.kahago.kahagoservice.exception.InternalServerException;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.request.DestinationRequest;
import com.kahago.kahagoservice.model.request.PriceListRequest;
import com.kahago.kahagoservice.model.response.AreaResponse;
import com.kahago.kahagoservice.model.response.DestinationResponse;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.repository.MAreaDetailRepo;
import com.kahago.kahagoservice.repository.MPostalCodeRepo;
import com.kahago.kahagoservice.repository.MSwitcherRepo;
import com.kahago.kahagoservice.repository.MVendorAreaRepo;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ibnu Wasis
 */
@Service
@Slf4j
public class MasterDestinationService {
	@Autowired
	private MAreaDetailRepo mAreaDetailRepo;
	@Autowired
	private MSwitcherRepo switcherRepo;
	@Autowired
	private MVendorAreaRepo mVendorAreaRepo;
	@Autowired
	private MPostalCodeRepo mPostalCodeRepo;
	@Autowired
	private FeignService feignService;
	@Value("${url.cron.price}")
	private String urlCron;
	
	public Page<DestinationResponse> getDestination(PriceListRequest request){
		log.info("==> List Master Destination <==");
		Page<MVendorAreaEntity> lVendorArea = mVendorAreaRepo.findAllByRequestName(request.getAreaCode(), request.getSwitcherCode(), request.getSearch(), request.getPageRequest());
		
		return new PageImpl<>(
				lVendorArea.getContent().stream().map(this::toResponse).collect(Collectors.toList()),
				request.getPageRequest(), 
				lVendorArea.getTotalElements());
	}
	
	private DestinationResponse toResponse(MVendorAreaEntity entity) {
		String vendorName = switcherRepo.findById(entity.getSwitcherCode()).orElseThrow(()->new NotFoundException("Vendor Tidak Ditemukan !")).getDisplayName();
		return DestinationResponse.builder()
				.id(entity.getSeqid().intValue())
				.priceAreaCode(entity.getRequestName())
				.bookingAreaCode(entity.getSendRequest())
				.postalCode(entity.getPostalCodeId().getPostalCode())
				.kelurahan(entity.getPostalCodeId().getKelurahan())
				.kecamatan(entity.getAreaId().getKecamatan())
				.kota(entity.getAreaId().getKotaEntity().getName())
				.provinsi(entity.getAreaId().getKotaEntity().getProvinsiEntity().getName())
				.vendor(vendorName)
				.idPostalCode(entity.getPostalCodeId().getIdPostalCode())
				.vendorId(entity.getSwitcherCode())
				.build();
	}
	
	@Transactional
	public SaveResponse addVendorArea(DestinationRequest request) {
		log.info("==> add/update Destinaion <==");
		MVendorAreaEntity entity = new MVendorAreaEntity();
		if(request.getId() != null) {
			entity = mVendorAreaRepo.findById(request.getId().longValue()).orElseThrow(()->new NotFoundException("Data Tidak Ditemukan !"));
		}
		MAreaDetailEntity areaId = mAreaDetailRepo.findById(request.getAreaId()).orElseThrow(()->new NotFoundException("Area Tidak Ditemukan !"));
		MPostalCodeEntity postalCode = mPostalCodeRepo.findById(request.getIdPostalCode()).orElseThrow(()->new NotFoundException("Postal Code Tidak Ditemukan !"));
		if(!areaId.equals(postalCode.getKecamatanEntity())) throw new NotFoundException("Area dengan Area Postal code tidak sama !");
		entity.setRequestName(request.getPriceAreaCode());
		entity.setSendRequest(request.getBookingAreaCode());
		entity.setAreaId(areaId);
		entity.setPostalCodeId(postalCode);
		entity.setFlagOrigin(Byte.valueOf("0"));
		entity.setLastupdate(LocalDateTime.now());
		entity.setSwitcherCode(request.getSwitcherCode());
		mVendorAreaRepo.save(entity);
		
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Berhasil Save or Update Destination")
				.build();
	}
	
	public DestinationResponse getAreaById(Integer id) {
		log.info("==> get Area By Id <==");
		MVendorAreaEntity entity = mVendorAreaRepo.findById(id.longValue()).orElseThrow(()->new NotFoundException("Data Tidak Ditemukan !"));
		return toResponse(entity);
	}
	@Async
	public SaveResponse hitPriceByVendorArea(Integer areaId, Integer vendorId) {
		log.info("==> Hit Price from Area <==");
		List<String> lorigin = new ArrayList<String>(Arrays.asList("SUB","SDA"));
		SaveResponse result = SaveResponse.builder()
								.saveStatus(1)
								.saveInformation("Berhasil Get Price dari cron")
								.build();
		AreaResponse areaRes = new AreaResponse();
		try {
			for(String origin:lorigin) {
				URI uri = URI.create(urlCron+origin+"/"+areaId+"/"+vendorId);
				areaRes = feignService.fetchPriceArea(uri);
				log.info("Response : From "+ areaRes.getFromCode()+"To "+areaRes.getToCode());
			}
			
		}catch (FeignException e) {
			// TODO: handle exception
			log.error(e.getMessage());
			e.printStackTrace();
			throw new InternalServerException(e.getMessage());
		}
		MAreaDetailEntity area = mAreaDetailRepo.findById(Integer.valueOf(areaRes.getToCode())).orElseThrow(()->new NotFoundException("Area Tidak Ditemukan !"));
		result.setSaveInformation("Berhasil Get Price dari cron From "+ lorigin.get(0)+"/"+areaRes.getFromCode()+" To "+area.getKecamatan());
		return result;
	}
}
