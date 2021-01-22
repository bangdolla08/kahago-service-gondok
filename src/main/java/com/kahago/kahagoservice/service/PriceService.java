package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.component.TarifComponent;
import com.kahago.kahagoservice.configuration.CommonConfig;
import com.kahago.kahagoservice.entity.MAreaDetailEntity;
import com.kahago.kahagoservice.entity.MAreaEntity;
import com.kahago.kahagoservice.entity.MOfficeEntity;
import com.kahago.kahagoservice.entity.MPostalCodeEntity;
import com.kahago.kahagoservice.entity.MProductSwitcherEntity;
import com.kahago.kahagoservice.entity.TAreaEntity;
import com.kahago.kahagoservice.entity.TPickupAddressEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestDetailEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestEntity;
import com.kahago.kahagoservice.entity.TMinKoliEntity;
import com.kahago.kahagoservice.enummodel.VendorServiceTypeEnum;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.dto.UserDto;
import com.kahago.kahagoservice.model.request.PickupOrderPriceReq;
import com.kahago.kahagoservice.model.request.PriceRequest;
import com.kahago.kahagoservice.model.response.PriceDetail;
import com.kahago.kahagoservice.model.response.PriceResponse;
import com.kahago.kahagoservice.model.response.VendorDetail;
import com.kahago.kahagoservice.repository.MAreaRepo;
import com.kahago.kahagoservice.repository.MOfficeRepo;
import com.kahago.kahagoservice.repository.MPostalCodeRepo;
import com.kahago.kahagoservice.repository.TAreaRepo;
import com.kahago.kahagoservice.repository.TMinKoliRepo;
import com.kahago.kahagoservice.repository.TOfficeRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestDetailRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestRepo;
import com.kahago.kahagoservice.util.DateTimeUtil;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kahago.kahagoservice.util.ImageConstant.*;
import static java.util.stream.Collectors.groupingBy;


@Slf4j
@Service
public class PriceService {
    @Autowired
    private TAreaRepo areaRepo;
    @Autowired
    private MAreaRepo mAreaRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private MPostalCodeRepo postalRepo;
    @Autowired
    private AddressService addressService;
    @Autowired
    private TMinKoliRepo tMinKoliRepo;
    @Autowired
    private WarehouseVerificationService warehouseService;
    @Autowired
    private TPickupOrderRequestRepo pickReqRepo;
    @Autowired
    private TarifComponent tarifComp;
    @Autowired
    private MOfficeRepo officeRepo;
    private PriceRequest priceRequest;
    
    private Integer idUserCategory;
    

    public PriceResponse findPrice(PriceRequest priceRequest,Integer idPickup){
        TPickupAddressEntity pickupAddressEntity=this.addressService.getTPickupAddressEntity(idPickup);
        priceRequest.setOrigin(mAreaRepo.findByKotaEntityAreaKotaId(pickupAddressEntity.getPostalCode().getKecamatanEntity().getKotaEntity().getAreaKotaId()).getAreaId());
        return this.findPrice(priceRequest);
    }

    public PriceResponse findPrice(PriceRequest priceRequest){
        PriceResponse priceResponses=new PriceResponse();
        this.priceRequest=priceRequest;
        UserDto userDto=null;
        if(priceRequest.getUserId()!=null)
            userDto=userService.getMUserEntity(priceRequest.getUserId());
        if(userDto!=null) {
        	idUserCategory =userDto.getMUserEntity().getUserCategory().getSeqid();
        }

        List<TAreaEntity> tAreaEntities;
        if(priceRequest.getCommodityId()!=null) {
        	if(priceRequest.getCommodityId()==0)
        		priceRequest.setCommodityId(null);
        }
        if(userDto==null)
            tAreaEntities=areaRepo.searchTarif(
                    priceRequest.getOrigin(),
                    priceRequest.getDestination(),
//                    postalCode.getIdPostalCode(),
                    priceRequest.getCommodityId(),
                    priceRequest.getWeight());
        else
            tAreaEntities=areaRepo.searchTarif(
                    priceRequest.getOrigin(),
                    priceRequest.getDestination(),
//                    postalCode.getIdPostalCode(),
                    priceRequest.getCommodityId(),
                    priceRequest.getWeight(),
                    idUserCategory);
        Stream<PriceDetail> priceDetailStream=tAreaEntities
                .stream().filter(x->x.getLimitMinimum() <= priceRequest.getWeight()).map(this::toDto);
        Map<String, PriceDetail> priceDetailMap=
                priceDetailStream
                        .collect(Collectors.toMap(PriceDetail::getProductCode, Function.identity(),
                                BinaryOperator.minBy(Comparator.comparing(PriceDetail::getPrice))));
        List<PriceDetail> priceDetails=new ArrayList<>(priceDetailMap.values());
        Collections.sort(priceDetails,new Comparator<PriceDetail>() {
            @Override public int compare(PriceDetail p1, PriceDetail p2) {
                return p1.getPrice().compareTo(p2.getPrice()); // Ascending
            }
        });
        Map<VendorDetail,Long> vendorMap=priceDetails.stream().map(this::toDtoVendor)
                        .collect(groupingBy(Function.identity(),Collectors.counting()));
        priceDetails.forEach(surchargeList());
        priceResponses.setPrices(priceDetails);
        priceResponses.setVendors(new ArrayList<>(vendorMap.keySet()));
        return priceResponses;
    }
    private PriceDetail toDto(TAreaEntity entity) {
        BigDecimal tarif=entity.getTarif();
        Integer minWeight = entity.getMinimumKg();
        if(priceRequest.getWeight()>=entity.getLimitMinimum()){
            if(priceRequest.getWeight()<=entity.getMinimumKg()&&entity.getLimitMinimum()!=1){
                tarif=tarif.divide(new BigDecimal(entity.getMinimumKg()),0, RoundingMode.CEILING);
                if(priceRequest.getWeight()<=entity.getMinimumKg())
                    entity.setMinimumKg(priceRequest.getWeight());
                //minWeight = priceRequest.getWeight();
            }
            if(priceRequest.getWeight()>entity.getMinimumKg()&&entity.getLimitMinimum()!=1){
                Integer nextKg=priceRequest.getWeight()-entity.getMinimumKg();
                
                if(entity.getProductSwCode().getIsNextrate()==true) {
                	tarif=tarif.add(entity.getNextRate().multiply(new BigDecimal(nextKg)));
                	tarif=tarif.divide(new BigDecimal(priceRequest.getWeight()),0, RoundingMode.CEILING);
                }else {
                	tarif = tarif.divide(new BigDecimal(entity.getMinimumKg()),0,RoundingMode.CEILING);
                }
                if(priceRequest.getWeight()<=entity.getMinimumKg())
                    entity.setMinimumKg(priceRequest.getWeight());
            }
//            if(entity.getMinimumKg()>1)
//            	minWeight = priceRequest.getWeight();
        }
        TMinKoliEntity minKoli = null;
        
        if(idUserCategory != null) {
        	minKoli = tMinKoliRepo.findByProductSwCodeAndIdUserCategory(entity.getProductSwCode().getProductSwCode(), idUserCategory);
        }
        return PriceDetail.builder()
                .productCode(entity.getProductSwCode().getProductSwCode()+"")
                .productName(entity.getProductSwCode().getDisplayName())
                .product(entity.getVendor().getName())
                .serviceType(VendorServiceTypeEnum.getPaymentEnum(entity.getProductSwCode().getServiceType()).getDescription())
                .duration(entity.getStartDay()+"-"+entity.getEndDay()+" Day")
                .price(tarif)
                .minWeight(minWeight)
                .namaModa(entity.getProductSwCode().getJenisModa().getNamaModa())
                .pembagiVolume(entity.getProductSwCode().getPembagiVolume().intValue())
                .kgSurcharge(entity.getProductSwCode().getKgSurcharge())
                .vendorCode(entity.getVendor().getSwitcherCode())
                .urlImage(PREFIX_PATH_IMAGE_VENDOR + entity.getVendor().getImg().substring(entity.getVendor().getImg().lastIndexOf("/")+1))
//                .urlImage(CommonConfig.getUrlVendorImage(entity.getVendor().getSwitcherCode()))
                .maxJumlahKoli(entity.getProductSwCode().getMaxJumlahKoli())
                .maxKgKoli(entity.getProductSwCode().getMaxKgKoli())
                .pembulatanVolume(entity.getProductSwCode().getPembulatanVolume())
                .cutOff(DateTimeUtil.getTime(entity.getProductSwCode().getCutoff()))
                .minKoli(minKoli==null?0:minKoli.getMinKoli())
                .build();
    }

    private VendorDetail toDtoVendor(PriceDetail priceDetail){
        return VendorDetail.builder()
                .vendorCode(priceDetail.getVendorCode())
                .vendorName(priceDetail.getProduct()).build();
    }
    
    public PriceResponse getTarifPickupRequest(PickupOrderPriceReq req,String userLogin) {

    	String userid = "";
    	String origin = "";
    	if(req.getPickupOrderId().isEmpty()) {
    		userid = userLogin;
    	}else {
    		TPickupOrderRequestEntity pickReq = Optional.ofNullable(pickReqRepo.findByPickupOrderId(req.getPickupOrderId()))
        			.orElseThrow(this::exception);
    		userid = pickReq.getUserEntity().getUserId();
    	}
    	
    	MAreaEntity area = null;
    	if(req.getOriginId()==null) {
    		MOfficeEntity office = officeRepo.findById(req.getOfficeCode()).orElseThrow(()-> new NotFoundException("Office Code Tidak Ditemukan"));
    		origin = office.getRegionCode();
    	}else {
    		area = mAreaRepo.findByKotaEntityAreaKotaId(req.getOriginId());
    		origin = area.getAreaId();
    	}
    	
    	PriceRequest priceReq = PriceRequest.builder()
    			.origin(origin)
    			.destination(Integer.valueOf(req.getIdKecamatan()))
    			.weight(Integer.valueOf(req.getWeight()))
    			.userId(userid)
    			.commodityId(new Long(0))
    			.build();
//    	this.vendorCode = Integer.valueOf(req.getVendorCode());
    	PriceResponse priceResp = this.findPrice(priceReq);
    	List<PriceDetail> priceDetails = priceResp.getPrices();
    	priceDetails.forEach(surchargeList());
    	priceResp.setPrices(priceDetails);
    	priceResp.setVendors(null);
    	return priceResp;
    }

	private Consumer<? super PriceDetail> surchargeList() {
		return p->p.setSurcharge(warehouseService.getListSurcharge(Integer.valueOf(p.getProductCode())));
	}

	

    public PriceDetail getPricePickup(MProductSwitcherEntity psw,MAreaDetailEntity areaId, String origin) {
    	TAreaEntity area = areaRepo
    			.findTOPByProductSwCodeAndMPostalCodeAndAreaOriginIdOrderByTarifAsc(psw, areaId, origin).stream().findFirst().get();
    	
    	return  PriceDetail.builder()
                .productCode(area.getProductSwCode().getProductSwCode()+"")
                .productName(area.getProductSwCode().getDisplayName())
                .product(area.getVendor().getName())
                .serviceType(VendorServiceTypeEnum.getPaymentEnum(area.getProductSwCode().getServiceType()).getDescription())
                .duration(area.getStartDay()+"-"+area.getEndDay()+" Day")
                .price(area.getTarif())
                .minWeight(area.getMinimumKg())
                .namaModa(area.getProductSwCode().getJenisModa().getNamaModa())
                .pembagiVolume(area.getProductSwCode().getPembagiVolume().intValue())
                .kgSurcharge(area.getProductSwCode().getKgSurcharge())
                .vendorCode(area.getVendor().getSwitcherCode())
                .urlImage(PREFIX_PATH_IMAGE_VENDOR + area.getVendor().getImg().substring(area.getVendor().getImg().lastIndexOf("/")+1))
                .maxJumlahKoli(area.getProductSwCode().getMaxJumlahKoli())
                .maxKgKoli(area.getProductSwCode().getMaxKgKoli())
                .pembulatanVolume(area.getProductSwCode().getPembulatanVolume())
                .cutOff(DateTimeUtil.getTime(area.getProductSwCode().getCutoff()))
                .minKoli(1)
                .build();
    }
    private Predicate<PriceDetail> getPriceByVendor(Integer vendor){
    	return p -> p.getVendorCode()==vendor;
    }
    
	private ResponseStatusException exception() {
		return new ResponseStatusException(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase());
	}
	
	
}
