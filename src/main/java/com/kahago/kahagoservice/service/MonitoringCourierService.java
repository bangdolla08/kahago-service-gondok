package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.entity.MUserEntity;
import com.kahago.kahagoservice.entity.TPickupDetailEntity;
import com.kahago.kahagoservice.entity.TPickupEntity;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.PickupDetailEnum;
import com.kahago.kahagoservice.enummodel.RequestPickupEnum;
import com.kahago.kahagoservice.model.response.BookDataResponse;
import com.kahago.kahagoservice.model.response.CourierManifestMonitoringRes;
import com.kahago.kahagoservice.model.response.ManifestDetailRes;
import com.kahago.kahagoservice.model.response.ProfileRes;
import com.kahago.kahagoservice.repository.TPickupDetailRepo;
import com.kahago.kahagoservice.repository.TPickupRepo;
import com.sun.mail.imap.protocol.INTERNALDATE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author bangd ON 25/02/2020
 * @project com.kahago.kahagoservice.service
 */
@Service
public class MonitoringCourierService {
    @Autowired
    private TPickupRepo pickupRepo;
    @Autowired
    private TPickupDetailRepo pickupDetailRepo;
    @Autowired
    private PaymentService paymentService;
    public List<ProfileRes.Profile> getCourierList(){
        return pickupRepo.findCourierResponsibility().stream().map(this::toDtoProfile).collect(Collectors.toList());
    }
    public CourierManifestMonitoringRes getManifestDetail(String userDrive,Boolean fullData){
        List<CourierManifestMonitoringRes.ManifestBook> manifestBookList=new ArrayList<>();
        ProfileRes.Profile profile=null;
        List<TPickupEntity> pickupEntities=pickupRepo.findPickupEntitiesByCourier(userDrive);
        Integer countTotalBook=0;
        Integer countTotalItem=0;
        Integer countTotalBookInCourier=0;
        Integer countTotalItemInCourier=0;
        for (TPickupEntity pickupEntity:pickupEntities) {
            if(profile==null){
                profile=toDtoProfile(pickupEntity.getCourierId());
            }
            List<TPickupDetailEntity> pickupDetailEntities=pickupDetailRepo.findByPickupIdIdPickup(pickupEntity.getIdPickup());
            countTotalBook+=pickupDetailEntities.size();
//            pickupDetailEntities.stream().filter(filterPickup()).collect(Collectors.toList());
            x:
            for (TPickupDetailEntity detailEntity:pickupDetailEntities) {
                Boolean itemIsInCourier=detailEntity.getStatus().equals(PickupDetailEnum.IN_COURIER.getValue());
                countTotalBookInCourier+=itemIsInCourier?1:0;
                if(detailEntity.getBookId()!=null) {
                	if(detailEntity.getStatus() < PaymentEnum.ASSIGN_PICKUP.getCode() 
                			&& detailEntity.getStatus()> PaymentEnum.PICKUP_BY_KURIR.getCode()) continue x;
                    countTotalItemInCourier += itemIsInCourier ? detailEntity.getBookId().getJumlahLembar() : 0;
                    countTotalItem+=detailEntity.getBookId().getJumlahLembar();
                }else {
                    countTotalItemInCourier += itemIsInCourier ? detailEntity.getPickupOrderRequestEntity().getQty() : 0;
                    countTotalItem+=detailEntity.getPickupOrderRequestEntity().getQty();
                }
                    BookDataResponse bookDataResponse=null;
                    if(detailEntity.getBookId()!=null){
                        bookDataResponse=paymentService.toBookDataResponse(detailEntity.getBookId());
                    }else{
                        bookDataResponse=paymentService.getDetailRequestPickup(detailEntity.getPickupOrderRequestEntity());
                    }
                    if(fullData) {
                    	if(detailEntity.getBookId()!=null) {
                    		if(detailEntity.getBookId().getStatus().equals(PaymentEnum.ASSIGN_PICKUP.getCode())
                        			||detailEntity.getBookId().getStatus().equals(PaymentEnum.PICKUP_BY_KURIR.getCode())) {
                        		manifestBookList.add(
                                        CourierManifestMonitoringRes
                                                .ManifestBook.builder()
                                                .manifestNumber(detailEntity.getPickupId().getCode())
                                                .pickupDestination(detailEntity.getPickupAddrId().getPostalCode().getKelurahan()+", "+
                                                        detailEntity.getPickupAddrId().getPostalCode().getKecamatanEntity().getKecamatan()+", "+
                                                        detailEntity.getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getName())
                                                .bookDataResponse(bookDataResponse)
                                                .build()
                                );                    		
                        	}
                    	}else {
                    		if(detailEntity.getPickupOrderRequestEntity().getStatus().equals(RequestPickupEnum.ASSIGN_PICKUP.getValue())
                    				|| detailEntity.getPickupOrderRequestEntity().getStatus().equals(RequestPickupEnum.IN_COURIER.getValue())) {
                    			manifestBookList.add(
                                        CourierManifestMonitoringRes
                                                .ManifestBook.builder()
                                                .manifestNumber(detailEntity.getPickupId().getCode())
                                                .pickupDestination(detailEntity.getPickupAddrId().getPostalCode().getKelurahan()+", "+
                                                        detailEntity.getPickupAddrId().getPostalCode().getKecamatanEntity().getKecamatan()+", "+
                                                        detailEntity.getPickupAddrId().getPostalCode().getKecamatanEntity().getKotaEntity().getName())
                                                .bookDataResponse(bookDataResponse)
                                                .build()
                                ); 
                    		}
                    	}
                    	
                    }
                    
            }
        }

        return CourierManifestMonitoringRes.builder()
                .profile(profile)
                .countManifest(pickupEntities.size())
                .totalBook(countTotalBook)
                .totalBookInCourier(countTotalBookInCourier)
                .totalItem(countTotalItem)
                .totalItemInCourier(countTotalItemInCourier)
                .detail(manifestBookList)
                .build();
    }
	private Predicate<? super TPickupDetailEntity> filterPickup() {
		return p-> (p.getBookId().getStatus()==PaymentEnum.ASSIGN_PICKUP.getCode()
				|| p.getBookId().getStatus()==PaymentEnum.PICKUP_BY_KURIR.getCode())
				&& p.getBookId()!=null;
	}


    private ProfileRes.Profile toDtoProfile(MUserEntity userEntity){
        return ProfileRes.Profile.builder()
                .userId(userEntity.getUserId())
                .name(userEntity.getName())
                .hp(userEntity.getHp())
                .build();
    }
}
