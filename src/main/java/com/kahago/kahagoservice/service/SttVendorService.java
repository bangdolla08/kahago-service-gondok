package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.entity.MSwitcherEntity;
import com.kahago.kahagoservice.entity.MVendorAreaEntity;
import com.kahago.kahagoservice.entity.TSttVendorEntity;
import com.kahago.kahagoservice.model.request.PageHeaderRequest;
import com.kahago.kahagoservice.model.request.SttVendorReq;
import com.kahago.kahagoservice.model.response.ResponseListSttVendor;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.model.response.SttMonitorRes;
import com.kahago.kahagoservice.repository.TSttVendorRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.kahago.kahagoservice.util.ImageConstant.PREFIX_PATH_IMAGE_VENDOR;

/**
 * @author BangDolla08
 * @created 06/10/20-October-2020 @at 16.50
 * @project kahago-service
 */
@Service
@Slf4j
public class SttVendorService {
    @Autowired
    private TSttVendorRepo sttVendorRepo;
    @Autowired
    private VendorService vendorService;

    // TODO: 06/10/20 todo create crud stt vendor and active vendor and de active vendor
    public List<SttMonitorRes> getCountLeftStt() {
        List<SttMonitorRes> sttMonitorResList = new ArrayList<>();
        Object[][] objects = sttVendorRepo.countGroup(0);
        for (int i = 0; i < objects.length; i++) {
            Object origin = objects[i][0];
            if (origin == null)
                origin = "Undefined";
            sttMonitorResList.add(SttMonitorRes.builder()
                    .vendorCode((Integer) objects[i][1])
                    .originData((String) origin)
                    .countingData((BigInteger) objects[i][2])
                    .build());
        }
        sttMonitorResList.forEach(sttMonitorRes -> {
            MSwitcherEntity switcherEntity = vendorService.getSwitcherEntity(sttMonitorRes.getVendorCode());
            sttMonitorRes.setVendorName(switcherEntity.getName());
            sttMonitorRes.setVendorImg(PREFIX_PATH_IMAGE_VENDOR + switcherEntity.getImg().substring(switcherEntity.getImg().lastIndexOf("/") + 1));
        });
        return sttMonitorResList;
    }

    public SaveResponse saveSttVendor(List<SttVendorReq> sttVendorReqList, String userCreate) {
        List<TSttVendorEntity> vendorEntity = new ArrayList<>();
        sttVendorReqList.forEach(sttVendorReq -> {
            TSttVendorEntity sttVendorEntity = new TSttVendorEntity();
            if (sttVendorReq.getIdSttVendor() != null) {
                sttVendorEntity = sttVendorRepo.getOne(sttVendorReq.getIdSttVendor());
                sttVendorEntity.setUpdateBy(userCreate);
                sttVendorEntity.setUpdateDate(LocalDateTime.now());
            } else {
                sttVendorEntity.setCreateBy(userCreate);
                sttVendorEntity.setCreateDate(LocalDateTime.now());
            }
            sttVendorEntity.setStt(sttVendorReq.getStt());
            sttVendorEntity.setFlag(sttVendorReq.getFlag());
            sttVendorEntity.setOrigin(sttVendorReq.getOrigin());
            sttVendorEntity.setSwitcherCode(sttVendorReq.getSwitcherCode());
            vendorEntity.add(sttVendorEntity);
        });
        if (vendorEntity.size() > 0) {
            sttVendorRepo.saveAll(vendorEntity);
        }
        return SaveResponse.builder()
                .saveInformation("Berhasil Save Stt")
                .saveStatus(1)
                .build();
    }

    public Page<ResponseListSttVendor> getListSttVendor(Integer flag, Integer switcherCode, String origin, String stt, PageHeaderRequest pageHeaderRequest) {
        Page<TSttVendorEntity> sttVendorEntityPage = sttVendorRepo.findByFlagAndSwitcherCodeAndOrigin(flag, switcherCode, origin, stt, pageHeaderRequest.getPageRequest());
        return new PageImpl<>(
                sttVendorEntityPage.getContent().stream().map(this::toResponse).collect(Collectors.toList()),
                pageHeaderRequest.getPageRequest(),
                sttVendorEntityPage.getTotalElements());
    }



    public ResponseListSttVendor toResponse(TSttVendorEntity tSttVendorEntity) {
        return ResponseListSttVendor.builder()
                .flag(tSttVendorEntity.getFlag())
                .idSttVendor(tSttVendorEntity.getSeq())
                .origin(tSttVendorEntity.getOrigin())
                .stt(tSttVendorEntity.getStt())
                .switcherCode(tSttVendorEntity.getSwitcherCode())
                .build();
    }

}
