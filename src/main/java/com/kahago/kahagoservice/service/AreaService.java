package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.entity.*;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.PageHeaderRequest;
import com.kahago.kahagoservice.model.request.PriceListRequest;
import com.kahago.kahagoservice.model.request.TotalTrxRequest;
import com.kahago.kahagoservice.model.response.KecamatanResponse;
import com.kahago.kahagoservice.model.response.KelurahanResponse;
import com.kahago.kahagoservice.model.response.OriginResponse;
import com.kahago.kahagoservice.model.response.OriginsV1Res;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.model.response.TotalTrxResponse;
import com.kahago.kahagoservice.model.response.VendorAreaDetail;
import com.kahago.kahagoservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bangd ON 17/11/2019
 * @project com.kahago.kahagoservice.service
 */
@Service
public class AreaService {
    @Autowired
    private MAreaKotaRepo mAreaKotaRepo;
    @Autowired
    private MAreaDetailRepo mAreaDetailRepo;
    @Autowired
    private MAreaProvinsiRepo mAreaProvinsiRepo;
    @Autowired
    private MPostalCodeRepo mPostalCodeRepo;
    @Autowired
    private OfficeCodeService officeCodeService;
    @Autowired
    private MAreaRepo mAreaRepo;
    @Autowired
    private TAreaRepo tAreaRepo;
    @Autowired
    private TAreaAverageRepo tAreaAvgRepo;
    @Autowired
    private MVendorAreaRepo vendorAreaRepo;
    @Autowired
    private PriceVendorService priceVendorService;
    public List<OriginResponse> getOrigin(){
        return mAreaRepo.findAllByStatus(true).stream().map(this::toDtoOrigin).collect(Collectors.toList());
    }

    public List<OriginResponse> getOrigin(String officeCode){
        List<String> regionList=officeCodeService.regionCodeList(officeCode);
        return mAreaRepo.findAllByAreaIdAndStatus(regionList,true).stream().map(this::toDtoOrigin).collect(Collectors.toList());
    }

    public List<OriginsV1Res.Origin> getOriginV1() {
        return mAreaRepo.findAllByStatus(true).stream().map(this::toDtoOriginV1).collect(Collectors.toList());
    }

    public List<KecamatanResponse> getKecamatan(){
        return mAreaDetailRepo.findAll().stream().map(this::toDtoKecamatan).collect(Collectors.toList());
    }

    public List<KelurahanResponse> getKelurahan(Integer areaId){
        return mPostalCodeRepo.findAllByKecamatanEntityAreaDetailId(areaId).stream().map(this::toDtoKelurahan).collect(Collectors.toList());
    }
    public List<KelurahanResponse> getProvice(){
        return mAreaProvinsiRepo.findAll().stream().map(this::toDtoKelurahan).collect(Collectors.toList());
    }

    public MAreaKotaEntity getCityEntity(Integer idCity){
        return mAreaKotaRepo.getOne(idCity);
    }

    public List<KelurahanResponse> getCity(Integer provinceId){
        return mAreaKotaRepo.findAllByProvinsiEntityAreaProvinsiId(provinceId).stream().map(this::toDtoKelurahan).collect(Collectors.toList());
    }

    public List<KelurahanResponse> getKecamatan(Integer cityId){
        return mAreaDetailRepo.findAllByKotaEntityAreaKotaId(cityId).stream().map(this::toDtoKelurahan).collect(Collectors.toList());
    }

    public List<KelurahanResponse> getKelurahanByPostalCode(String postalCode){
    	List<KelurahanResponse> resp = mPostalCodeRepo.findByPostalCodeLike(postalCode).stream().map(this::toDtoKelurahan).collect(Collectors.toList());
    	if(resp==null || resp.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseStatus.NOT_FOUND.getReasonPhrase());
        return resp;
    }

    public MPostalCodeEntity getPostalCodeEntity(Integer postalCodeId){
        return mPostalCodeRepo.getOne(postalCodeId);
    }

    public MAreaEntity getOriginEntity(String originId){
        return mAreaRepo.getOne(originId);
    }

    public String getFullAddressByPostalCode(Integer idPostalCode){
        MPostalCodeEntity entity=this.getPostalCodeEntity(idPostalCode);
        return getFullAddressByPostalCode(entity);
    }

    public String getFullAddressByPostalCode(MPostalCodeEntity entity){
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(entity.getKelurahan());
        stringBuilder.append(" , ");
        stringBuilder.append(entity.getKecamatanEntity().getKecamatan());
        stringBuilder.append(" , ");
        stringBuilder.append(entity.getKecamatanEntity().getKotaEntity().getName());
        return stringBuilder.toString();
    }

    private KelurahanResponse toDtoKelurahan(MAreaProvinsiEntity provinsiEntity){
        MPostalCodeEntity entity=new MPostalCodeEntity();
        MAreaDetailEntity detailEntity=new MAreaDetailEntity();
        MAreaKotaEntity areaKotaEntity=new MAreaKotaEntity();
        areaKotaEntity.setProvinsiEntity(provinsiEntity);
        detailEntity.setKotaEntity(areaKotaEntity);
        entity.setKecamatanEntity(detailEntity);
        return toDtoKelurahan(entity);
    }
    private KelurahanResponse toDtoKelurahan(MAreaKotaEntity kotaEntity){
        MPostalCodeEntity entity=new MPostalCodeEntity();
        MAreaDetailEntity detailEntity=new MAreaDetailEntity();
        detailEntity.setKotaEntity(kotaEntity);
        entity.setKecamatanEntity(detailEntity);
        return toDtoKelurahan(entity);
    }

    private KelurahanResponse toDtoKelurahan(MAreaDetailEntity detailEntity){
        MPostalCodeEntity entity=new MPostalCodeEntity();
        entity.setKecamatanEntity(detailEntity);
        return toDtoKelurahan(entity);
    }

    private KelurahanResponse toDtoKelurahan(MPostalCodeEntity mPostalCodeEntity){
        return KelurahanResponse.builder()
                .idPostalCode(mPostalCodeEntity.getIdPostalCode())
                .kelurahan(mPostalCodeEntity.getKelurahan())
                .postalCode(mPostalCodeEntity.getPostalCode())
                .areaId(mPostalCodeEntity.getKecamatanEntity().getAreaDetailId())
                .kecamatan(mPostalCodeEntity.getKecamatanEntity().getKecamatan())
                .kotaId(mPostalCodeEntity.getKecamatanEntity().getKotaEntity().getAreaKotaId())
                .kota(mPostalCodeEntity.getKecamatanEntity().getKotaEntity().getName())
                .privasiId(mPostalCodeEntity.getKecamatanEntity().getKotaEntity().getProvinsiEntity().getAreaProvinsiId())
                .provinsi(mPostalCodeEntity.getKecamatanEntity().getKotaEntity().getProvinsiEntity().getName())
                .build();
    }



    private KecamatanResponse toDtoKecamatan(MAreaDetailEntity mAreaDetailEntity){
        return KecamatanResponse.builder()
                .idKecamatan(mAreaDetailEntity.getAreaDetailId())
                .kecamatan(mAreaDetailEntity.getKecamatan())
                .kota(mAreaDetailEntity.getKotaEntity().getName())
                .province(mAreaDetailEntity.getKotaEntity().getProvinsiEntity().getName())
                .build();
    }

    private OriginResponse toDtoOrigin(MAreaEntity mAreaEntity){
        return OriginResponse
                .builder()
                .originId(mAreaEntity.getAreaId())
                .originName(mAreaEntity.getAreaName())
                .status(mAreaEntity.getStatus()?1:0)
                .areaKotaId(mAreaEntity.getKotaEntity().getAreaKotaId())
                .build();
    }

    private OriginsV1Res.Origin toDtoOriginV1(MAreaEntity entity) {
        return OriginsV1Res.Origin.builder()
                .status(entity.getStatus() ? "1" : "0")
                .areaOriginId(entity.getAreaId())
                .areaOriginName(entity.getAreaName())
                .build();
    }

    public TotalTrxResponse getTotalLateSync() {
    	PageHeaderRequest page = new PageHeaderRequest();
    	LocalDateTime tgl = LocalDateTime.now();
    	tgl = tgl.minusDays(7);
//    	Page<VendorAreaDetail> postalCodeEntities = priceVendorService.getLateSyncArea(page.getPageRequest());
    	Integer ttlTrx = tAreaRepo.countPostalCode(tgl);
    	
    	return TotalTrxResponse.builder().totalAllTrx(ttlTrx.intValue()).build();
    	
    }
    
    public TotalTrxResponse getTotalBySelisih() {
    	Integer ttlTrx = tAreaAvgRepo.countBySelisih();
    	
    	return TotalTrxResponse.builder().totalAllTrx(ttlTrx).build();
    	
    }
    
    public TotalTrxResponse getTotalVendorAreaBystatus(TotalTrxRequest req) {
    	List<Integer> lsStatus = req.getStatus().stream().map(s->s.getStatus()).collect(Collectors.toList());
    	Integer ttlTrx = vendorAreaRepo.countVendorAreaByStatus(lsStatus);
    	
    	return TotalTrxResponse.builder().totalAllTrx(ttlTrx).build();
    }
    private static final Boolean IS_CHECK = false;
    public Page<VendorAreaDetail> getDataVendorPage(PriceListRequest req){
    	Integer areaCode = null;
    	if(req.getAreaCode() != null) {
    		areaCode = Integer.valueOf(req.getAreaCode());
    	}
    	Page<TAverageArea> lsTareaAvg = tAreaAvgRepo.findAllAVG(req.getStatusVendorArea(), req.getSwitcherCode(), areaCode,IS_CHECK,req.getPageRequest());
    	
    	return new PageImpl<>(
    			lsTareaAvg.getContent().stream().map(p-> priceVendorService.toDto(p.getArea())).collect(Collectors.toList()),
                req.getPageRequest(),
                lsTareaAvg.getTotalElements());
    }
    
    @Transactional
    public SaveResponse updateFlagArea(VendorAreaDetail request) {
    	List<TAverageArea> averageArea = tAreaAvgRepo.findByAreaIdAndVendorCode(request.getSwitcherCode(), request.getPostalCodeId());
    	for(TAverageArea ta : averageArea) {
    		ta.setIsCheck(true);
        	tAreaAvgRepo.save(ta);
    	}
    	
    	return SaveResponse.builder().saveStatus(1).build();
    }
    
}
