package com.kahago.kahagoservice.service;

import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.entity.TPickupDetailEntity;
import com.kahago.kahagoservice.entity.TPickupEntity;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.PickupDetailEnum;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.repository.TPickupDetailRepo;
import com.kahago.kahagoservice.repository.TPickupRepo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Hendro yuwono
 */
@Service
@Transactional
public class IncomingCounterService {

    @Autowired
    private TPaymentRepo paymentRepo;

    @Autowired
    private TPickupDetailRepo pickupDetailRepo;

    @Autowired
    private TPickupRepo pickupRepo;

    @Autowired
    private HistoryTransactionService historyTransactionService;

    @Transactional
    public SaveResponse incoming(String userId, String officeCode, String bookingCode) {
        TPaymentEntity payment = paymentRepo.findById(bookingCode).orElseThrow(() -> new NotFoundException("Booking code is not found"));
        TPickupDetailEntity pickupDetail = pickupDetailRepo.findByBookIdBookingCode(payment.getBookingCode()).orElseThrow(() -> new NotFoundException("Booking code is not found"));

        TPaymentEntity oldPayment = new TPaymentEntity();
        BeanUtils.copyProperties(payment, oldPayment);
        if (pickupDetail.getStatus().equals(1) && payment.getStatus().equals(3)) {

            historyTransactionService.createHistory(oldPayment, payment, userId);
            payment.setStatus(PaymentEnum.getPaymentEnum(26).getCode());
            payment.setOfficeCode(officeCode);
            pickupDetail.setStatus(2);

            paymentRepo.save(payment);
            pickupDetailRepo.save(pickupDetail);

            List<TPickupDetailEntity> listOfPickupDetail = pickupDetailRepo.findByPickupId(pickupDetail.getPickupId());

            long totalHistory = listOfPickupDetail.stream().map(TPickupDetailEntity::getStatus).filter(v -> v.equals(PickupDetailEnum.HISTORY.getValue())).count();
            long totalOnGoing = listOfPickupDetail.stream().map(TPickupDetailEntity::getStatus).filter(v -> v.equals(PickupDetailEnum.ASSIGN_PICKUP.getValue())).count();
            long totalProcess = listOfPickupDetail.stream().map(TPickupDetailEntity::getStatus).filter(v -> v.equals(PickupDetailEnum.IN_COURIER.getValue())).count();

            TPickupEntity pickupEntity = pickupRepo.findById(pickupDetail.getPickupId().getIdPickup()).orElseThrow(() -> new NotFoundException("Pickup is not found"));
            if (totalOnGoing > 0) {
                pickupEntity.setStatus(0);
            } else if (totalProcess > 0) {
                pickupEntity.setStatus(1);
            }
            pickupRepo.save(pickupEntity);

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
