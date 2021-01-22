package com.kahago.kahagoservice.service;

import static com.kahago.kahagoservice.util.ImageConstant.PREFIX_PATH_IMAGE_VENDOR;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.kahago.kahagoservice.model.response.ProductSwitcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kahago.kahagoservice.entity.MProductSwitcherEntity;
import com.kahago.kahagoservice.entity.TProductSurchargeEntity;
import com.kahago.kahagoservice.exception.InternalServerException;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.request.ProductSwitcherRequest;
import com.kahago.kahagoservice.model.request.SurchargeDetailReq;
import com.kahago.kahagoservice.model.response.ProductSwitcherResponse;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.model.response.SurchargeDetailResponse;
import com.kahago.kahagoservice.repository.MModaRepo;
import com.kahago.kahagoservice.repository.MProductSwitcherRepo;
import com.kahago.kahagoservice.repository.MSwitcherRepo;
import com.kahago.kahagoservice.repository.TProductSurchargeRepo;
import com.kahago.kahagoservice.util.DateTimeUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ibnu Wasis
 */
@Service
@Slf4j
public class ProductSwitcherService {
	@Autowired
	private MProductSwitcherRepo mProductSwitcherRepo;
	@Autowired
	private MSwitcherRepo mSwitcherRepo;
	@Autowired
	private MModaRepo mModaRepo;
	@Autowired
	private TProductSurchargeRepo tSurchargeRepo;
	@Autowired
	private CronService cronService;
	
	public Page<ProductSwitcherResponse> getListProduct(String cari,Pageable pageable){
		Page<MProductSwitcherEntity> lProduct = mProductSwitcherRepo.findAllCodeOrName(cari.toUpperCase(), pageable);
		return new PageImpl<>(
				lProduct.getContent().stream().map(this::getDto).collect(Collectors.toList()), 
				lProduct.getPageable(), 
				lProduct.getTotalElements());
	}
	
	public ProductSwitcherResponse getByProductSwCode(Integer id) {
		return getDto(mProductSwitcherRepo.findByProductSwCode(id.longValue()));
	}
	
	private ProductSwitcherResponse getDto(MProductSwitcherEntity entity) {
		Boolean status = false;
		if(entity.getStatus().equals(Byte.valueOf("0"))) {
			status=true;
		}
		return ProductSwitcherResponse.builder()
				.productSwCode(entity.getProductSwCode().intValue())
				.displayName(entity.getDisplayName())
				.vendorName(entity.getSwitcherEntity().getName())
				.Address(entity.getSwitcherEntity().getAddress())
				.pic(entity.getSwitcherEntity().getPic())
				.phonePic(entity.getSwitcherEntity().getPicTelp())
				.estimasi(entity.getStartDay().toString()+"-"+entity.getEndDay().toString()+" Hari")
				.tarif(entity.getTarif().toString())
				.minWeight(entity.getMinWeight()==null?"":entity.getMinWeight().toString())
				.cutoff(entity.getCutoff())
				.status(status)
				.liburStart(entity.getLiburStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
				.liburEnd(entity.getLiburEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
				.productVendorCode(entity.getOperatorSw())
				.serviceType(entity.getServiceType().toString())
				.JenisModa(entity.getJenisModa().getNamaModa())
				.maxKgKoli(entity.getMaxKgKoli())
				.maxKoli(entity.getMaxJumlahKoli())
				.pembagiVolume(entity.getPembagiVolume().intValue())
				.pembulatanVolume(entity.getPembulatanVolume().toString())
				.priority(entity.getPrioritySeq())
				.kgSurcharge(entity.getKgSurcharge())
				.komisi(entity.getKomisi())
				.isNextRate(entity.getIsNextrate())
				.surcharges(getSurcharge(entity))
				.isLeadTime(entity.getIsLeadtime())
				.imageVendor(PREFIX_PATH_IMAGE_VENDOR + entity.getSwitcherEntity().getImg().substring(entity.getSwitcherEntity().getImg().lastIndexOf("/")+1))
				.switcherCode(entity.getSwitcherEntity().getSwitcherCode())
				.modaId(entity.getJenisModa().getIdModa())
				.isAutosync((entity.getAutosync().intValue()==1)?true:false)
				.build();
	} 
	@Transactional()
	public SaveResponse saveProductSwitcher(ProductSwitcherRequest request, String userAdmin) {
		MProductSwitcherEntity product = mProductSwitcherRepo.findByName(request.getDisplayName());
		String status = "1";
		if(request.getStatus()) {
			status = "0";
		}
		if(product != null) {
			throw new InternalServerException("Nama sudah digunakan di product lain !");
		}else {
			product = new MProductSwitcherEntity();
		}
		
		product.setName(request.getDisplayName());
		product.setDisplayName(request.getDisplayName());
		product.setSwitcherEntity(mSwitcherRepo.getOne(request.getSwitcherCode()));
		product.setOperatorSw(request.getProductVendorCode());
		product.setCutoff(request.getCutoff());
		product.setJenisModa(mModaRepo.findByIdModa(request.getJenisModa()));
		product.setStatus(Byte.valueOf(status));
		product.setPembagiVolume(Double.valueOf(request.getPembagiVolume().toString()));
		product.setPembulatanVolume(request.getPembulatanVolume());
		product.setKgSurcharge(0);
		product.setMaxJumlahKoli(request.getMaxKoli());
		product.setMaxKgKoli(request.getMaxKgKoli());
		product.setPrioritySeq(request.getPriority());
		product.setStartDay(Byte.valueOf("0"));
		product.setEndDay(Byte.valueOf("0"));
		product.setTarif(0);
		product.setKomisi(0);
		product.setServiceType(0);
		product.setTrxArray(0);
		product.setTipeFix("1");
		product.setAutosync(Byte.valueOf((request.getIsAutosync())?"1":"0"));
		product.setIsLeadtime(request.getIsLeadTime());
		try {
			product.setLiburStart(DateTimeUtil.getDateFrom(request.getLiburStart(), "ddMMyyyy"));
			product.setLiburEnd(DateTimeUtil.getDateFrom(request.getLiburEnd(), "ddMMyyyy"));
		}catch (Exception e) {
			// TODO: handle exception
			log.error(e.getMessage());
			e.printStackTrace();
			return SaveResponse.builder()
					.saveStatus(0)
					.saveInformation("Gagal Simpan Product Switcher")
					.build();
		}
		product.setLastUpdate(Timestamp.valueOf(LocalDateTime.now()));
		product.setLastUser(userAdmin);
		if(request.getKomisi()==null) {
			product.setKomisi(0);
		}else {
			product.setKomisi(request.getKomisi());
		}
		product.setIsNextrate(request.getIsNextRate());
		product = mProductSwitcherRepo.save(product);
		if(request.getSurcharges()!=null) {
			setSurcharge(product, request.getSurcharges(), userAdmin);
		}
		this.cronService.doUpdateProductSwitcher(ProductSwitcher.builder()
				.autosync(product.getAutosync().intValue())
				.cutoff(product.getCutoff())
				.displayName(product.getDisplayName())
				.endDay(product.getEndDay().intValue())
				.jenisModa(product.getJenisModa().getIdModa())
				.komisi(product.getKomisi())
				.lastUpdate(product.getLastUpdate())
				.lastUser(product.getLastUser())
				.liburEnd(product.getLiburEnd().toString())
				.liburStart(product.getLiburStart().toString())
				.minWeight(product.getMinWeight().toString())
				.name(product.getName())
				.operatorSw(product.getOperatorSw())
				.productSwCode(product.getProductSwCode().intValue())
				.serviceType(product.getServiceType().toString())
				.startDay(product.getStartDay().intValue())
				.status(product.getStatus().toString())
				.switcherCode(product.getSwitcherEntity().getSwitcherCode())
				.tarif(product.getTarif())
				.build());
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Berhasil Simpan Product Switcher")
				.build();
	}
	@Transactional()
	public SaveResponse saveEdit(ProductSwitcherRequest request,String userAdmin) {
		MProductSwitcherEntity product = mProductSwitcherRepo.findByProductSwCode(request.getProductSwCode().longValue());
		if(product == null) {
			throw new NotFoundException("Data Tidak Ditemukan");
		}
		String status = "1";
		if(request.getStatus()) {
			status = "0";
		}
		product.setName(request.getDisplayName());
		product.setDisplayName(request.getDisplayName());
		product.setSwitcherEntity(mSwitcherRepo.getOne(request.getSwitcherCode()));
		product.setOperatorSw(request.getProductVendorCode());
		product.setCutoff(request.getCutoff());
		product.setJenisModa(mModaRepo.findByIdModa(request.getJenisModa()));
		product.setStatus(Byte.valueOf(status));
		product.setPembagiVolume(Double.valueOf(request.getPembagiVolume().toString()));
		product.setPembulatanVolume(request.getPembulatanVolume());
		product.setKgSurcharge(0);
		product.setMaxJumlahKoli(request.getMaxKoli());
		product.setMaxKgKoli(request.getMaxKgKoli());
		product.setPrioritySeq(request.getPriority());
		product.setIsLeadtime(request.getIsLeadTime());
		product.setAutosync(Byte.valueOf((request.getIsAutosync())?"1":"0"));
		try {
			product.setLiburStart(DateTimeUtil.getDateFrom(request.getLiburStart(), "ddMMyyyy"));
			product.setLiburEnd(DateTimeUtil.getDateFrom(request.getLiburEnd(), "ddMMyyyy"));
		}catch (Exception e) {
			// TODO: handle exception
			log.info(e.getMessage());
			e.printStackTrace();
			return SaveResponse.builder()
					.saveStatus(0)
					.saveInformation("Gagal Simpan Product Switcher")
					.build();
		}
		product.setLastUpdate(Timestamp.valueOf(LocalDateTime.now()));
		product.setLastUser(userAdmin);
		if(request.getSurcharges().size() > 0) {
			setSurcharge(product, request.getSurcharges(), userAdmin);
		}	
		mProductSwitcherRepo.save(product);
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Berhasil Simpan Product Switcher")
				.build();
		
	}
	
	@Transactional()
	public SaveResponse nonActiveProduct(Integer productSwCode) {
		MProductSwitcherEntity product = mProductSwitcherRepo.findByProductSwCode(productSwCode.longValue());
		if(product == null) {
			throw new NotFoundException("Data Tidak Ditemukan");
		}
		if(product.getStatus().equals(Byte.valueOf("0"))) {
			product.setStatus(Byte.valueOf("1"));
		}else {
			product.setStatus(Byte.valueOf("0"));
		}
		mProductSwitcherRepo.save(product);
		return SaveResponse.builder()
				.saveStatus(1)
				.saveInformation("Berhasil Non Aktifkan Product Switcher")
				.build();
	}
	
	private List<SurchargeDetailResponse> getSurcharge(MProductSwitcherEntity product){
		List<SurchargeDetailResponse> result = new ArrayList<SurchargeDetailResponse>();
		List<TProductSurchargeEntity> lSurcharge = tSurchargeRepo.findAllByProductSwCodeAndStatus(product.getProductSwCode().intValue(),true);
		if(lSurcharge != null) {
			for(TProductSurchargeEntity se:lSurcharge) {
				SurchargeDetailResponse surRes = new SurchargeDetailResponse();
				surRes.setId(se.getId());
				surRes.setPersen(se.getPercent());
				surRes.setStart(se.getStartKg().toString());
				surRes.setTo(se.getToKg()==null?"":se.getToKg().toString());
				result.add(surRes);
			}
		}
		return result;
	}
	
	@Transactional
	void setSurcharge(MProductSwitcherEntity product,List<SurchargeDetailReq> lrequest,String userAdmin) {
		List<TProductSurchargeEntity> lSurcharge = tSurchargeRepo.findAllByProductSwCode(product.getProductSwCode().intValue());
		if(lSurcharge.size()==0) {
			for(SurchargeDetailReq surcharge : lrequest) {
				TProductSurchargeEntity entity = new TProductSurchargeEntity();
				entity.setSwitcherCode(product.getSwitcherEntity().getSwitcherCode());
				entity.setProductSwCode(product.getProductSwCode().intValue());
				entity.setPercent(surcharge.getPersen());
				entity.setStartKg(surcharge.getStart());
				entity.setToKg(surcharge.getTo());
				entity.setStatus(surcharge.getStatus());
				entity.setCreatedDate(LocalDateTime.now());
				entity.setCreatedBy(userAdmin);
				entity.setUpdateDate(LocalDateTime.now());
				entity.setUpdateBy(userAdmin);
				tSurchargeRepo.save(entity);
			}
		}else {
			for(SurchargeDetailReq surcharge:lrequest) {
				TProductSurchargeEntity entity = new TProductSurchargeEntity();
				for(TProductSurchargeEntity se:lSurcharge) {
					if(surcharge.getId()!=null && se.getId().equals(surcharge.getId())) {
						se.setStartKg(surcharge.getStart());
						se.setToKg(surcharge.getTo());
						se.setPercent(surcharge.getPersen());
						se.setStatus(surcharge.getStatus());
						se.setUpdateBy(userAdmin);
						se.setUpdateDate(LocalDateTime.now());
						entity = se;
					}
				}
				if(entity.getId()==null) {
					entity.setSwitcherCode(product.getSwitcherEntity().getSwitcherCode());
					entity.setProductSwCode(product.getProductSwCode().intValue());
					entity.setPercent(surcharge.getPersen());
					entity.setStartKg(surcharge.getStart());
					entity.setToKg(surcharge.getTo());
					entity.setStatus(surcharge.getStatus());
					entity.setCreatedDate(LocalDateTime.now());
					entity.setCreatedBy(userAdmin);
					entity.setUpdateDate(LocalDateTime.now());
					entity.setUpdateBy(userAdmin);
				}
				tSurchargeRepo.save(entity);
			}
		}
		
		
		
	}
}
