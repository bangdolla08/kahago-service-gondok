package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.entity.MAreaEntity;
import com.kahago.kahagoservice.entity.TPickupAddressEntity;
import com.kahago.kahagoservice.enummodel.SaveStatusEnum;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.dto.UserDto;
import com.kahago.kahagoservice.model.request.AddressListRequest;
import com.kahago.kahagoservice.model.request.PickupAddressRequest;
import com.kahago.kahagoservice.model.response.PickupAddressResponse;
import com.kahago.kahagoservice.repository.MAreaRepo;
import com.kahago.kahagoservice.repository.TPickupAddressRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bangd ON 18/11/2019
 * @project com.kahago.kahagoservice.service
 */
@Service
public class PickupAddressService {
    @Autowired
    private TPickupAddressRepo tPickupAddressRepo;

    @Autowired
    private AreaService areaService;
    @Autowired
    private UserService userService;
    @Autowired
    private MAreaRepo mAreaRepo;
    

    public List<PickupAddressResponse> getListPickup(String userId, String origin){
        Integer originCityId=null;
        MAreaEntity mAreaEntity=null ;
        if(origin!=null)
            mAreaEntity=areaService.getOriginEntity(origin);
        if(mAreaEntity!=null)
            originCityId=mAreaEntity.getKotaEntity().getAreaKotaId();
        /*Page<TPickupAddressEntity> tPickupAddressEntities=tPickupAddressRepo.findAllUserIdAndOrigin(pageable,userId,originCityId);
        return new PageImpl<>(
                tPickupAddressEntities.stream().map(this::pickupAddressResponse).collect(Collectors.toList()),
                tPickupAddressEntities.getPageable(),
                tPickupAddressEntities.getTotalElements());*/
        List<TPickupAddressEntity> tPickupAddressEntities = tPickupAddressRepo.findAllUserIdAndOriginId(userId, originCityId);
        
        return tPickupAddressEntities.stream().map(this::pickupAddressResponse).collect(Collectors.toList());
    }

    public TPickupAddressEntity getPickupAddressEntity(Integer pickupId){
        return tPickupAddressRepo.getOne(pickupId);
    }

    public List<PickupAddressResponse> getFromUserRef(AddressListRequest addressListRequest){
        return tPickupAddressRepo.getByIdOrRef(addressListRequest.getUserIdAndAccountNo()).stream().map(this::pickupAddressResponse).collect(Collectors.toList());
    }

    public PickupAddressResponse savePickup(PickupAddressRequest pickupAddressRequest, SaveStatusEnum saveStatusEnum){
        TPickupAddressEntity tPickupAddressEntity=null;
        if(pickupAddressRequest.getPickupAddressId()==null) {
            tPickupAddressEntity = new TPickupAddressEntity();
        }else {
            tPickupAddressEntity = tPickupAddressRepo.getOne(pickupAddressRequest.getPickupAddressId());
            if(tPickupAddressEntity==null)
                throw new NotFoundException("Pickup Address Id tidak ditemukan");
        }
        if(saveStatusEnum!=SaveStatusEnum.DELETE) {
            tPickupAddressEntity.setAddress(pickupAddressRequest.getAddress());
            UserDto userDto = userService.getMUserEntity(pickupAddressRequest.getUserId());
            tPickupAddressEntity.setUserId(userDto.getMUserEntity());
            tPickupAddressEntity.setDescription(pickupAddressRequest.getDescription());
            tPickupAddressEntity.setPostalCode(areaService.getPostalCodeEntity(pickupAddressRequest.getIdPostalCode()));
            tPickupAddressEntity.setLatitude(pickupAddressRequest.getLatitude());
            tPickupAddressEntity.setLongitude(pickupAddressRequest.getLongitude());
        }
        tPickupAddressEntity.setFlag(saveStatusEnum.getFlagStatusEnum().getValueInteger());
        tPickupAddressEntity.setStatusAlive(saveStatusEnum.getFlagStatusEnum().getValueInteger());
        tPickupAddressEntity=this.tPickupAddressRepo.save(tPickupAddressEntity);
        return pickupAddressResponse(tPickupAddressEntity);
    }

    private PickupAddressResponse pickupAddressResponse(TPickupAddressEntity tPickupAddressEntity){
    	String origin="";
    	MAreaEntity area = mAreaRepo.findByKotaEntityAreaKotaId(tPickupAddressEntity.getPostalCode().getKecamatanEntity().getKotaEntity().getAreaKotaId());
    	if(area != null)origin = area.getAreaId();
        return PickupAddressResponse.builder()
                .address(tPickupAddressEntity.getAddress())
                .latitude(tPickupAddressEntity.getLatitude())
                .longitude(tPickupAddressEntity.getLongitude())
                .idPostalCode(tPickupAddressEntity.getPostalCode().getIdPostalCode())
                .postalCode(tPickupAddressEntity.getPostalCode().getPostalCode())
                .userId(tPickupAddressEntity.getUserId().getUserId())
                .phoneNumber(tPickupAddressEntity.getUserId().getHp())
                .name(tPickupAddressEntity.getUserId().getName())
                .pickupAddressId(tPickupAddressEntity.getPickupAddrId())
                .description(tPickupAddressEntity.getDescription())
                .kecamatan(tPickupAddressEntity.getPostalCode().getKecamatanEntity().getKecamatan())
                .kelurahan(tPickupAddressEntity.getPostalCode().getKelurahan())
                .kota(tPickupAddressEntity.getPostalCode().getKecamatanEntity().getKotaEntity().getName())
                .provinsi(tPickupAddressEntity.getPostalCode().getKecamatanEntity().getKotaEntity().getProvinsiEntity().getName())
                .origin(origin==null?"":origin)
                .build();
    }


}
