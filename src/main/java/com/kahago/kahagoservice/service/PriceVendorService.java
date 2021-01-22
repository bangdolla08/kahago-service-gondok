package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.entity.*;
import com.kahago.kahagoservice.enummodel.AreaEnum;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.request.PriceListRequest;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.model.response.VendorArea;
import com.kahago.kahagoservice.model.response.VendorAreaDetail;
import com.kahago.kahagoservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.kahago.kahagoservice.util.ImageConstant.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PriceVendorService {

    @Autowired
    private MVendorAreaRepo vendorAreaRepo;
    @Autowired
    private TAreaRepo areaRepo;
    @Autowired
    private MAreaProvinsiRepo areaProvinsiRepo;
    @Autowired
    private MAreaKotaRepo areaKotaRepo;
    @Autowired
    private MAreaDetailRepo areaDetailRepo;
    @Autowired
    private MPostalCodeRepo postalCodeRepo;
    private Integer switcherCode;
    @Autowired
    private MSwitcherRepo switcherRepo;
    @Autowired
    private CronService cronService;
    @Autowired
    private MVendorAreaRepo mVendorAreaRepo;
    @Autowired
    private TPaymentRepo tPaymentRepo;

    /**
     * Create all data yang berhubungan dengan area hanya di perbolehkan 1 persatu untuk create nya
     *
     * @param vendorArea all data kosongin jika ingin membuat data baru
     * @param create     creator login user
     * @return
     */
    public SaveResponse saveArea(VendorArea vendorArea, String create) {
        SaveResponse saveResponse = new SaveResponse();
        
        //Create new Province
        if (vendorArea.getProvinceId() == null) {
            MAreaProvinsiEntity areaProvinsiEntity = new MAreaProvinsiEntity();
            areaProvinsiEntity.setName(vendorArea.getProvince());
            areaProvinsiEntity.setCreateBy(create);
            areaProvinsiEntity.setCreateDate(LocalDateTime.now());
            areaProvinsiEntity = areaProvinsiRepo.save(areaProvinsiEntity);
            cronService.doUpdateToCron(toDto(areaProvinsiEntity), AreaEnum.PROV.getNumber());
            return SaveResponse.builder()
                    .saveStatus(1)
                    .saveInformation("Provinsi telah ditambahkan " + areaProvinsiEntity.getAreaProvinsiId())
                    .build();
        }
        MAreaProvinsiEntity provinsiEntity = areaProvinsiRepo.findByAreaProvinsiId(vendorArea.getProvinceId());
        //initial kota,kecamatan, kelurahan
        MAreaKotaEntity kotaEntity = new MAreaKotaEntity();
        MAreaDetailEntity areaDetailEntity = new MAreaDetailEntity();
        MPostalCodeEntity postalCodeEntity = new MPostalCodeEntity();
        if (provinsiEntity == null) {
            throw new NotFoundException("Id provinsi tidak ditemukan");
        }

        if (!provinsiEntity.getName().equals(vendorArea.getProvince())&& !vendorArea.getProvince().isEmpty()) {
            provinsiEntity.setName(vendorArea.getProvince());
            provinsiEntity.setUpdateBy(create);
            provinsiEntity.setUpdateDate(LocalDateTime.now());
            areaProvinsiRepo.save(provinsiEntity);
            cronService.doUpdateToCron(toDto(provinsiEntity), AreaEnum.PROV.getNumber());
        }
        //Create New City
        if (vendorArea.getCityId() == null) {
        	if(vendorArea.getCity() == null) {
        		return SaveResponse.builder()
                        .saveStatus(1)
                        .saveInformation("Data tersimpan")
                        .build();
        	}else {
        		kotaEntity = new MAreaKotaEntity();
                kotaEntity.setName(vendorArea.getCity());
                kotaEntity.setProvinsiEntity(provinsiEntity);
                kotaEntity.setTlc(vendorArea.getTlc());
                kotaEntity.setTitle(vendorArea.getTitle());
                kotaEntity.setCreateBy(create);
                kotaEntity.setCreateDate(LocalDateTime.now());
                areaKotaRepo.save(kotaEntity);
                cronService.doUpdateToCron(toDto(kotaEntity), AreaEnum.CITY.getNumber());
                return SaveResponse.builder()
                        .saveStatus(1)
                        .saveInformation("Kota Telah ditambahkan")
                        .build();
        	}
            
        }else {
        	kotaEntity = areaKotaRepo.findByAreaKotaId(vendorArea.getCityId());
            if (kotaEntity == null) {
                throw new NotFoundException("Id Kota kota tidak ditemukan");
            }
            //change city name
            if (!kotaEntity.getName().equals(vendorArea.getCity()) && !vendorArea.getCity().isEmpty()) {
                kotaEntity.setName(vendorArea.getCity());
            }
            //change province
            if (!kotaEntity.getProvinsiEntity().getAreaProvinsiId().equals(vendorArea.getProvinceId()) && vendorArea.getProvinceId() != null) {
                kotaEntity.setProvinsiEntity(provinsiEntity);
            }
            //change title
            if(vendorArea.getTitle()!=null && 
            		!kotaEntity.getTitle().equals(vendorArea.getTitle())&&
            		!vendorArea.getTitle().isEmpty()) {
            	kotaEntity.setTitle(vendorArea.getTitle());
            }
            //change tlc
            if(vendorArea.getTlc() != null && 
            		!kotaEntity.getTlc().equals(vendorArea.getTlc())&&
            		!vendorArea.getTlc().isEmpty()) {                
                kotaEntity.setTlc(vendorArea.getTlc());
            }
            kotaEntity.setUpdateBy(create);
            kotaEntity.setUpdateDate(LocalDateTime.now());
            areaKotaRepo.save(kotaEntity);
            cronService.doUpdateToCron(toDto(kotaEntity), AreaEnum.CITY.getNumber());
        }

        
        if (vendorArea.getKecamatanId() == null) {
        	if(vendorArea.getKecamatan() == null) {
        		return SaveResponse.builder()
                        .saveStatus(1)
                        .saveInformation("Data tersimpan")
                        .build();
        	}else {
        		//create new area detail
        		areaDetailEntity = new MAreaDetailEntity();
                areaDetailEntity.setKotaEntity(kotaEntity);
                areaDetailEntity.setAreaId("0");
                areaDetailEntity.setKota("0");
                areaDetailEntity.setProvince("0");
                areaDetailEntity.setStatus("1");
                areaDetailEntity.setIsPush("0");
                areaDetailEntity.setKecamatan(vendorArea.getKecamatan());
                areaDetailEntity.setCreateBy(create);
                areaDetailEntity.setCreateDate(LocalDateTime.now());
                areaDetailRepo.save(areaDetailEntity);
                cronService.doUpdateToCron(toDto(areaDetailEntity), AreaEnum.KEC.getNumber());
                return SaveResponse.builder()
                        .saveStatus(1)
                        .saveInformation("Kecamatan ditambahkan")
                        .build();
        	}
            
        }else {
        	areaDetailEntity = areaDetailRepo.findByAreaDetailId(vendorArea.getKecamatanId());
            if (areaDetailEntity == null) {
                throw new NotFoundException("Id Kecamatan tidak ditemukan");
            }
            //change kecamatan name
            if (!areaDetailEntity.getKecamatan().equals(vendorArea.getKecamatan()) && !vendorArea.getKecamatan().isEmpty()) {
            	areaDetailEntity.setKecamatan(vendorArea.getKecamatan());
            }
            //change city
            if (!areaDetailEntity.getKotaEntity().getAreaKotaId().equals(vendorArea.getCityId())) {
            	areaDetailEntity.setKotaEntity(kotaEntity);
            }
            areaDetailEntity.setUpdateBy(create);
        	areaDetailEntity.setUpdateDate(LocalDateTime.now());
            areaDetailRepo.save(areaDetailEntity);
            cronService.doUpdateToCron(toDto(areaDetailEntity), AreaEnum.KEC.getNumber());
        }
        
        //Create create postal code for new
        if (vendorArea.getPostalCodeId() == null) {
        	if(vendorArea.getKelurahan() == null) {
        		return SaveResponse.builder()
                        .saveStatus(1)
                        .saveInformation("Data tersimpan")
                        .build();
        	}else {
        		postalCodeEntity = new MPostalCodeEntity();
                postalCodeEntity.setKecamatanEntity(areaDetailEntity);
                postalCodeEntity.setKelurahan(vendorArea.getKelurahan());
                postalCodeEntity.setPostalCode(vendorArea.getPostalCode());
                postalCodeEntity.setCreateBy(create);
                postalCodeEntity.setCreateDate(LocalDateTime.now());
                postalCodeRepo.save(postalCodeEntity);
                cronService.doUpdateToCron(toDto(postalCodeEntity), AreaEnum.KEL.getNumber());
                return SaveResponse.builder()
                        .saveStatus(1)
                        .saveInformation("Kelurahan ditambahkan")
                        .build();
        	}
            
        }else {
        	postalCodeEntity = postalCodeRepo.findByIdPostalCode(vendorArea.getPostalCodeId());
            if (postalCodeEntity == null) {
                throw new NotFoundException("Postal Code Tidak Ditemukan");
            }
            List<MVendorAreaEntity> lvendorArea = mVendorAreaRepo.findAllByPostalCodeId(vendorArea.getPostalCodeId());
            //change kelurahan name
            if (!postalCodeEntity.getKelurahan().equals(vendorArea.getKelurahan()) && !vendorArea.getKelurahan().isEmpty()) {
                postalCodeEntity.setKelurahan(vendorArea.getKelurahan());
            }
            //change kecamatamn
            if (!postalCodeEntity.getKecamatanEntity().getAreaDetailId().equals(vendorArea.getKecamatanId())) {
                postalCodeEntity.setKecamatanEntity(areaDetailEntity);
                if(lvendorArea.size() > 0) {
                	MAreaDetailEntity areaId = areaDetailEntity;
                	lvendorArea.forEach(x->x.setAreaId(areaId));
                    mVendorAreaRepo.saveAll(lvendorArea);
                }
                
            }
            //change postalcode
            if(vendorArea.getPostalCode() != null && !postalCodeEntity.getPostalCode().equals(vendorArea.getPostalCode())&&!vendorArea.getPostalCode().isEmpty()) {
            	postalCodeEntity.setPostalCode(vendorArea.getPostalCode());
            }
            postalCodeEntity.setUpdateDate(LocalDateTime.now());
            postalCodeEntity.setUpdateBy(create);
            postalCodeRepo.save(postalCodeEntity);
            cronService.doUpdateToCron(toDto(postalCodeEntity), AreaEnum.KEL.getNumber());
        }

        
        return SaveResponse.builder()
                .saveStatus(1)
                .saveInformation("Data tersimpan")
                .build();
    }

    /**
     * Create save data to send to vendor untuk perubahan data dari list
     *
     * @param vendorAreaDetail semua parameter untuk ingin membuat udit
     * @param create           creator login user
     * @return
     */
    public SaveResponse saveRequestToVendor(VendorAreaDetail vendorAreaDetail, String create) {
        MVendorAreaEntity vendorAreaEntity = vendorAreaRepo.findByAreaIdAndPostalCode(vendorAreaDetail.getAreaId(),
                vendorAreaDetail.getPostalCodeId(),
                vendorAreaDetail.getSwitcherCode());
        vendorAreaEntity.setStatus(0);
        vendorAreaEntity.setRequestName(vendorAreaDetail.getRequestName());
        vendorAreaEntity.setSendRequest(vendorAreaDetail.getSendRequest());
        vendorAreaEntity.setIsCheck(false);
        vendorAreaEntity.setUpdateBy(create);
        vendorAreaEntity.setLastupdate(LocalDateTime.now());
        vendorAreaRepo.save(vendorAreaEntity);
        cronService.doUpdateVendorArea(vendorAreaDetailDto(vendorAreaEntity));
        return SaveResponse.builder().saveStatus(1).build();
    }

    public SaveResponse deactiveRequestToVendor(VendorAreaDetail vendorAreaDetail, String create) {
        MVendorAreaEntity vendorAreaEntity = vendorAreaRepo.findByAreaIdAndPostalCode(vendorAreaDetail.getAreaId(),
                vendorAreaDetail.getPostalCodeId(),
                vendorAreaDetail.getSwitcherCode());
        vendorAreaEntity.setStatus(0);
        vendorAreaEntity.setRequestName(vendorAreaDetail.getRequestName());
        vendorAreaEntity.setSendRequest(vendorAreaDetail.getSendRequest());
        vendorAreaEntity.setIsCheck(true);
        vendorAreaEntity.setUpdateBy(create);
        vendorAreaEntity.setLastupdate(LocalDateTime.now());
        vendorAreaRepo.save(vendorAreaEntity);
        cronService.doUpdateVendorArea(vendorAreaDetailDto(vendorAreaEntity));
        return SaveResponse.builder().saveStatus(1).build();
    }

    public SaveResponse saveUpdateArea(VendorAreaDetail vendorAreaDetail, String create) {
    	MPostalCodeEntity post = postalCodeRepo.findByIdPostalCode(vendorAreaDetail.getPostalCodeId());
    	SaveResponse resp = cronService.doUpdateTArea(post.getKecamatanEntity().getAreaDetailId());
    	return resp;
    }
    
    public Page<VendorArea> getProblemList(PriceListRequest request) {
        Page<MPostalCodeEntity> postalCodeEntities = vendorAreaRepo.findByPostalCode(request.getSearch(), request.getSwitcherCode(), request.getStatusVendorArea(), request.getPageRequest());
        return new PageImpl<>(
                postalCodeEntities.getContent().stream().map(this::areaToDto).collect(Collectors.toList()),
                request.getPageRequest(),
                postalCodeEntities.getTotalElements());
    }

    public Page<VendorAreaDetail> getProblemListVendor(PriceListRequest request){
        Page<MVendorAreaEntity> vendorAreaEntities=vendorAreaRepo.findBy(request.getSearch(), request.getSwitcherCode(), request.getStatusVendorArea(),request.getIdPostalCode(), request.getPageRequest());
        return new PageImpl<>(
                vendorAreaEntities.getContent().stream().map(this::toDtoWithHeader).collect(Collectors.toList()),
                request.getPageRequest(),
                vendorAreaEntities.getTotalElements());
    }

    private VendorAreaDetail toDtoWithHeader(MVendorAreaEntity entity){
        VendorAreaDetail areaDetail=vendorAreaDetailDto(entity);
        areaDetail.setVendorArea(toDto(entity.getPostalCodeId()));
        return areaDetail;
    }


    public Page<VendorArea> listKecamatan(PriceListRequest request) {
        Page<MAreaDetailEntity> areaDetailEntities = areaDetailRepo.findBy(request.getSearch(), request.getPageRequest());
        return new PageImpl<>(
                areaDetailEntities.getContent().stream().map(this::toDto).collect(Collectors.toList()),
                request.getPageRequest(),
                areaDetailEntities.getTotalElements());
    }

    public Page<VendorArea> listCity(PriceListRequest request){
        Page<MAreaKotaEntity> areaKotaEntities=areaKotaRepo.findBy(request.getSearch(),request.getPageRequest());
        return new PageImpl<>(
                areaKotaEntities.getContent().stream().map(this::toDto).collect(Collectors.toList()),
                request.getPageRequest(),
                areaKotaEntities.getTotalElements());
    }
    public Page<VendorArea> listProvince(PriceListRequest request){
        Page<MAreaProvinsiEntity> areaProvinsiEntities=areaProvinsiRepo.findAll(request.getSearch(), request.getPageRequest());
        return new PageImpl<>(
                areaProvinsiEntities.getContent().stream().map(this::toDto).collect(Collectors.toList()),
                request.getPageRequest(),
                areaProvinsiEntities.getTotalElements());
    }

    public Page<VendorArea> getAreaVendor(PriceListRequest request) {
    	 Page<MPostalCodeEntity> postalCodeEntities = null;
    	 if(request.getSearch() != null) {
    		 TPaymentEntity payment = tPaymentRepo.findByBookingCodeIgnoreCaseContaining(request.getSearch());
     		if(payment != null) {
     			request.setIdPostalCode(payment.getIdPostalCode().getIdPostalCode());
     			request.setSearch(null);
     		}     			
    	 }
    	if(request.getIdPostalCode() != null) {    		
    		postalCodeEntities = postalCodeRepo.findBySearch(request.getSearch(),request.getIdPostalCode(), request.getPageRequest());
    	}else {
    		if(request.getTypeSearch()!=null){
                if(request.getTypeSearch()==1){
                    return listProvince(request);
                }else if(request.getTypeSearch()==2){
                    return listCity(request);
                }else if(request.getTypeSearch()==3){
                    return listKecamatan(request);
                }
            	postalCodeEntities = postalCodeRepo.findBySearch(request.getSearch(),request.getIdPostalCode(), request.getPageRequest());
            }else {
            	postalCodeEntities = postalCodeRepo.findBySearch(request.getSearch(),request.getIdPostalCode(), request.getPageRequest());
            }
    	}
        return new PageImpl<>(
                postalCodeEntities.getContent().stream().map(this::toDto).collect(Collectors.toList()),
                request.getPageRequest(),
                postalCodeEntities.getTotalElements());
    }

    public List<VendorAreaDetail> getListVendorRequest(Integer postalCode){
        List<MVendorAreaEntity> vendorAreaEntities = vendorAreaRepo.findAllByPostalCodeId(postalCode);
        return vendorAreaEntities.stream().map(this::vendorAreaDetailDto).collect(Collectors.toList());
    }

    public Page<VendorAreaDetail> getLateSyncArea(Pageable pageable) {
        LocalDateTime localDateTimeNow = LocalDateTime.now();
//        localDateTimeNow = localDateTimeNow.minus(7, ChronoUnit.DAYS);
        localDateTimeNow = localDateTimeNow.minusDays(7);
        Page<TAreaEntity> postalCodeEntities = areaRepo.findByMoreDat(localDateTimeNow, pageable);
        return new PageImpl<>(
                postalCodeEntities.getContent().stream().map(this::toDto).collect(Collectors.toList()),
                pageable,
                postalCodeEntities.getTotalElements());
    }

    public VendorAreaDetail toDto(TAreaEntity areaEntity){
        MVendorAreaEntity entity=vendorAreaRepo.findBySwitcherAndPostalCode(areaEntity.getProductSwCode().getSwitcherEntity().getSwitcherCode(),areaEntity.getAreaId().getIdPostalCode());
        VendorAreaDetail vendorAreaDetail= vendorAreaDetailDto(entity);
        vendorAreaDetail.setVendorArea(toDto(areaEntity.getAreaId()));
        vendorAreaDetail.setProductName(areaEntity.getProductSwCode().getName());
        return vendorAreaDetail;
    }

    public SaveResponse getCount() {
        LocalDateTime localDateTimeNow = LocalDateTime.now();
        localDateTimeNow = localDateTimeNow.minus(7, ChronoUnit.DAYS);
        Integer countLate = areaRepo.countPostalCode(localDateTimeNow);
        return SaveResponse.builder().saveInformation(countLate + "").build();
    }

    public List<VendorArea> getProvinceList() {
        List<VendorArea> vendorAreas = areaProvinsiRepo.findAll().stream().map(this::toDto).collect(Collectors.toList());
        return vendorAreas;
    }

    public List<VendorArea> getCityList(Integer areaProvince) {
        List<VendorArea> vendorAreas = areaKotaRepo
                .findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return vendorAreas;
    }

    public List<VendorArea> getKecamatanList(Integer cityId) {
        List<VendorArea> vendorAreas = areaDetailRepo.findAll().stream()
                .map(this::toDto).collect(Collectors.toList());
        return vendorAreas;
    }

    public List<VendorArea> getKelurahanList(Integer kecId) {
        List<VendorArea> vendorAreas = postalCodeRepo.findAll().stream()
                .map(this::toDto).collect(Collectors.toList());
        return vendorAreas;
    }

    private VendorArea toDto(MPostalCodeEntity entity) {
        return VendorArea.builder()
                .province(entity.getKecamatanEntity().getKotaEntity().getProvinsiEntity().getName())
                .provinceId(entity.getKecamatanEntity().getKotaEntity().getProvinsiEntity().getAreaProvinsiId())
                .city(entity.getKecamatanEntity().getKotaEntity().getName())
                .cityId(entity.getKecamatanEntity().getKotaEntity().getAreaKotaId())
                .kecamatan(entity.getKecamatanEntity().getKecamatan())
                .kecamatanId(entity.getKecamatanEntity().getAreaDetailId())
                .kelurahan(entity.getKelurahan())
                .postalCode(entity.getPostalCode())
                .postalCodeId(entity.getIdPostalCode())
                .title(entity.getKecamatanEntity().getKotaEntity().getTitle())
                .tlc(entity.getKecamatanEntity().getKotaEntity().getTlc())
                .build();
    }

    private VendorArea toDto(MAreaDetailEntity entity) {
        return VendorArea.builder()
                .provinceId(entity.getKotaEntity().getProvinsiEntity().getAreaProvinsiId())
                .province(entity.getKotaEntity().getProvinsiEntity().getName())
                .cityId(entity.getKotaEntity().getAreaKotaId())
                .city(entity.getKotaEntity().getName())
                .title(entity.getKotaEntity().getTitle())
                .tlc(entity.getKotaEntity().getTlc())
                .kecamatan(entity.getKecamatan())
                .kecamatanId(entity.getAreaDetailId())
                .build();
    }

    private VendorArea toDto(MAreaProvinsiEntity mAreaProvinsiEntity) {
        return VendorArea.builder()
                .provinceId(mAreaProvinsiEntity.getAreaProvinsiId())
                .province(mAreaProvinsiEntity.getName())
                .build();
    }

    private VendorArea toDto(MAreaKotaEntity areaKotaEntity) {
        return VendorArea.builder()
                .province(areaKotaEntity.getProvinsiEntity().getName())
                .provinceId(areaKotaEntity.getProvinsiEntity().getAreaProvinsiId())
                .city(areaKotaEntity.getName())
                .cityId(areaKotaEntity.getAreaKotaId())
                .tlc(areaKotaEntity.getTlc())
                .title(areaKotaEntity.getTitle())
                .build();
    }

    private VendorArea areaToDto(MPostalCodeEntity entity) {
        VendorArea vendorArea =
                VendorArea.builder()
                        .province(entity.getKecamatanEntity().getKotaEntity().getProvinsiEntity().getName())
                        .provinceId(entity.getKecamatanEntity().getKotaEntity().getProvinsiEntity().getAreaProvinsiId())
                        .city(entity.getKecamatanEntity().getKotaEntity().getName())
                        .cityId(entity.getKecamatanEntity().getKotaEntity().getAreaKotaId())
                        .kecamatan(entity.getKecamatanEntity().getKecamatan())
                        .kecamatanId(entity.getKecamatanEntity().getAreaDetailId())
                        .kelurahan(entity.getKelurahan())
                        .postalCode(entity.getPostalCode())
                        .postalCodeId(entity.getIdPostalCode())
                        .tlc(entity.getKecamatanEntity().getKotaEntity().getTlc())
                        .title(entity.getKecamatanEntity().getKotaEntity().getTitle())
                        .build();
        List<MVendorAreaEntity> vendorAreaEntities = vendorAreaRepo.findAllByPostalCodeIdAndSwitcherCode(entity, switcherCode);
        if (vendorAreaEntities != null) {
            vendorArea.setVendorAreaDetail(vendorAreaEntities.stream().map(this::vendorAreaDetailDto).collect(Collectors.toList()));
        }
        return vendorArea;
    }

    private VendorAreaDetail vendorAreaDetailDto(MVendorAreaEntity entity) {
        MSwitcherEntity vendor = switcherRepo.getOne(entity.getSwitcherCode());
        return VendorAreaDetail.builder()
                .areaId(entity.getAreaId().getAreaDetailId())
                .isOrigin(entity.getFlagOrigin() == 1)
                .postalCodeId(entity.getPostalCodeId().getIdPostalCode())
                .requestName(entity.getRequestName())
                .sendRequest(entity.getSendRequest())
                .switcherCode(entity.getSwitcherCode())
                .switcherImage(PREFIX_PATH_IMAGE_VENDOR + vendor.getImg().substring(vendor.getImg().lastIndexOf("/") + 1))
                .tlc(entity.getAreaId().getKotaEntity().getTlc())
                .title(entity.getAreaId().getKotaEntity().getTitle())
                .isCheck(entity.getIsCheck())
                .status(entity.getStatus())
                .longKelurahan(entity.getAreaId().getKotaEntity().getProvinsiEntity().getName()+", "+
                        entity.getAreaId().getKotaEntity().getName()+", "+
                        entity.getAreaId().getKecamatan()+", "+
                        entity.getPostalCodeId().getKelurahan())
                .build();
    }
    
    public SaveResponse saveUpdateAreaByIdPostalCodeAndVendor(VendorAreaDetail vendorAreaDetail) {
    	MPostalCodeEntity post = postalCodeRepo.findByIdPostalCode(vendorAreaDetail.getPostalCodeId());
    	SaveResponse resp = cronService.doUpdateTAreByVendorAndIdPostalCode(post.getIdPostalCode(), vendorAreaDetail.getSwitcherCode());
    	return resp;
    }

}
