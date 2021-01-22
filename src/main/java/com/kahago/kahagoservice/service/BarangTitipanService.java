package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.entity.TPickupOrderRequestDetailEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestEntity;
import com.kahago.kahagoservice.entity.TWarehouseReceiveDetailEntity;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.repository.TPickupOrderRequestDetailRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestRepo;
import com.kahago.kahagoservice.repository.TWarehouseReceiveDetailRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hendro yuwono
 */
@Service
@Transactional
public class BarangTitipanService {

    @Autowired
    private TPickupOrderRequestDetailRepo pickupOrderRequestDetailRepo;

    @Autowired
    private TWarehouseReceiveDetailRepo warehouseReceiveDetailRepo;

    @Autowired
    private TPickupOrderRequestRepo pickupOrderRequestRepo;

    @Transactional
    public SaveResponse rejectBarang(String qrcode, String userId, String reason) {
        boolean orderReqExist = pickupOrderRequestDetailRepo.existsByQrCodeOrQrcodeExtAndStatus(qrcode,qrcode, 3);
        boolean warehouseExist = warehouseReceiveDetailRepo.existsByQrcodeRequestAndStatus(qrcode, 0);

        if (orderReqExist && warehouseExist) {

            TPickupOrderRequestDetailEntity requestDetail = pickupOrderRequestDetailRepo.findByQrCodeOrQrcodeExt(qrcode,qrcode);
            TPickupOrderRequestEntity requestHeader = pickupOrderRequestRepo.findById(requestDetail.getOrderRequestEntity().getPickupOrderId()).orElseThrow(() -> new NotFoundException("Id not found"));
            TWarehouseReceiveDetailEntity receiveDetail = warehouseReceiveDetailRepo.findByQrcodeRequest(qrcode).orElseThrow(() -> new NotFoundException("Id not found"));
            receiveDetail.setUpdateBy(userId);
            receiveDetail.setReason(reason);
            receiveDetail.setStatus(3);

            if (!requestDetail.getStatus().equals(9)) {
                requestHeader.setStatus(4);
            }
            requestDetail.setStatus(6);

            warehouseReceiveDetailRepo.save(receiveDetail);
            pickupOrderRequestDetailRepo.save(requestDetail);
            pickupOrderRequestRepo.save(requestHeader);


            return SaveResponse.builder()
                    .saveInformation("Berhasil")
                    .saveStatus(1)
                    .build();
        } else {
             return SaveResponse.builder()
                    .saveInformation("Gagal")
                    .saveStatus(0)
                    .build();
        }
    }
}
