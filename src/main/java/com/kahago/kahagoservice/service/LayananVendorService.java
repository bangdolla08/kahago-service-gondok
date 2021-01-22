package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.client.PosFeignService;
import com.kahago.kahagoservice.client.model.response.RespOpenLayanan;
import com.kahago.kahagoservice.entity.*;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.response.StatusResponse;
import com.kahago.kahagoservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.net.URI;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

/**
 * @author Hendro yuwono
 */
@Service
public class LayananVendorService {

    @Autowired
    private MOfficeRepo mOfficeRepo;

    @Autowired
    private MVendorPropRepo vendorPropRepo;

    @Autowired
    private PosFeignService posFeignService;

    @Autowired
    private MManifestPosRepo manifestPosRepo;

    @Autowired
    private MUserRepo userRepo;

    @Autowired
    private TOfficeRepo tOfficeRepo;

    @Autowired
    private MPropPosRepo mPropPosRepo;

    @Transactional
    public StatusResponse createManifest(String userId) {
        MUserEntity userEntity = userRepo.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        List<TOfficeEntity> tOfficeEntity = tOfficeRepo.findByUserIdUserId(userEntity.getUserId());
        if (tOfficeEntity.size() == 0) {
            throw new NotFoundException(userEntity.getUserId() +" is not registered in office");
        }

        MVendorPropEntity vendorPropEntity = vendorPropRepo.findBySwitcherCodeSwitcherCodeAndActionAndOrigin(
                309,
                "manifest",
                tOfficeEntity.get(0).getOfficeCode().getCity()
        ).orElseThrow(() -> new NotFoundException("Not found vendor properties"));

        MManifestPosEntity manifestPosEntity = manifestPosRepo
                .findByUseridAndStatus(vendorPropEntity.getClientCode(), 1)
                .orElse(null);
        if(manifestPosEntity!=null) {
        	throw new NotFoundException("There are still no active manifests for this user, Please Open Manifest");
        }
//                .orElseThrow(() -> new NotFoundException("There are still active manifests for this user"));


        MPropPosEntity propPosEntity = mPropPosRepo.findByOfficeCode(tOfficeEntity.get(0).getOfficeCode().getOfficeCode());

        String count = "0000";
        count = counterManifest(count, propPosEntity.getCounterManifest());
        URI uri = URI.create(vendorPropEntity.getUrl());
        RespOpenLayanan respCreateManifest = posFeignService.createManifestPos(
                uri,
                count,
                vendorPropEntity.getClientCode(),
                propPosEntity.getParentPos(),
                propPosEntity.getAgenid()
        );


        if (respCreateManifest.getRespcode().equals("000")) {
            MManifestPosEntity entity = MManifestPosEntity.builder()
                    .manifestNumber(respCreateManifest.getManifestnumber())
                    .status(1)
                    .transref(respCreateManifest.getTransref())
                    .userid(vendorPropEntity.getClientCode())
                    .sign(respCreateManifest.getSign())
                    .lastUpdate(new Timestamp(Instant.now().toEpochMilli()))
                    .build();
            manifestPosRepo.save(entity);

            propPosEntity.setCounterManifest(Integer.valueOf(count));
            mPropPosRepo.save(propPosEntity);

            return new StatusResponse("Manifest has been created", true, "Created","");
        } else {
            return new StatusResponse("Failed create manifest", false, "Failed","");
        }
    }

    public StatusResponse openLayanan() {
        RespOpenLayanan respOpenLayanan = posFeignService.openLayananPos();
        if (respOpenLayanan.getRespcode().equals("000")) {
            return new StatusResponse("Status layanan telah aktif", true, "Opened","");
        } else {
            return new StatusResponse("Status layanan tidak aktif", false, "Not Opened","");
        }
    }

    @Transactional
    public StatusResponse closeManifest(String userId) {
        MUserEntity userEntity = userRepo.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        List<TOfficeEntity> tOfficeEntity = tOfficeRepo.findByUserIdUserId(userEntity.getUserId());
        if (tOfficeEntity.size() == 0) {
            throw new NotFoundException(userEntity.getUserId() +" is not registered in office");
        }

        MVendorPropEntity vendorPropEntity = vendorPropRepo.findBySwitcherCodeSwitcherCodeAndActionAndOrigin(
                309,
                "close",
                tOfficeEntity.get(0).getOfficeCode().getCity()
        ).orElseThrow(() -> new NotFoundException("Not found vendor properties"));

        Boolean isExist = manifestPosRepo.existsByUseridAndStatus(vendorPropEntity.getClientCode(), 1);
        if (!isExist) {
            return new StatusResponse("Manifest dengan branch "+tOfficeEntity.get(0).getOfficeCode().getCity()+ " tidak aktif", false, "Non Active","");
        }

        MManifestPosEntity manifestPosEntity = manifestPosRepo
                .findByUseridAndStatus(vendorPropEntity.getClientCode(), 1)
                .orElseThrow(() -> new NotFoundException("There are still active manifests for this user"));

        MPropPosEntity propPosEntity = mPropPosRepo.findByOfficeCode(tOfficeEntity.get(0).getOfficeCode().getOfficeCode());
        URI uri = URI.create(vendorPropEntity.getUrl());
        RespOpenLayanan respCloseManifest = posFeignService.closeManifestPos(
                uri,
                manifestPosEntity.getManifestNumber(),
                vendorPropEntity.getClientCode(),
                propPosEntity.getParentPos(),
                propPosEntity.getAgenid()
        );

        if (respCloseManifest.getRespcode().equals("000")) {
            manifestPosEntity.setStatus(0);
            manifestPosRepo.save(manifestPosEntity);
            String urlPrint = "api/report/manifest/pos?manifest="+manifestPosEntity.getManifestNumber();

            return new StatusResponse("Manifest close has been successful", true, "Created",urlPrint);
        } else {
            return new StatusResponse("Failed close manifest", false, "Failed","");
        }
    }

    public StatusResponse checkManifest(String userId) {
        MUserEntity userEntity = userRepo.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        List<TOfficeEntity> tOfficeEntity = tOfficeRepo.findByUserIdUserId(userEntity.getUserId());
        if (tOfficeEntity.size() == 0) {
            throw new NotFoundException(userEntity.getUserId() +" is not registered in office");
        }

        MVendorPropEntity vendorPropEntity = vendorPropRepo.findBySwitcherCodeSwitcherCodeAndActionAndOrigin(
                309,
                "manifest",
                tOfficeEntity.get(0).getOfficeCode().getCity()
        ).orElseThrow(() -> new NotFoundException("Not found vendor properties"));

        Boolean isExist = manifestPosRepo.existsByUseridAndStatus(vendorPropEntity.getClientCode(), 1);
        if (isExist) {
            return new StatusResponse("Manifest dengan branch "+tOfficeEntity.get(0).getOfficeCode().getCity()+ " telah aktif", true, "Active","");
        } else {
            return new StatusResponse("Manifest dengan branch "+tOfficeEntity.get(0).getOfficeCode().getCity()+ " tidak aktif", false, "Non Active","");
        }
    }

    private String counterManifest(String count, int hit) {
        String parsing = count;
        hit += 1;
        String ahit = String.valueOf(hit);
        parsing = parsing.substring(0, parsing.length() - ahit.length());
        return parsing + ahit;
    }
}
