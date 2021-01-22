package com.kahago.kahagoservice.schedulling;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kahago.kahagoservice.entity.TPickupOrderRequestDetailEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestEntity;
import com.kahago.kahagoservice.enummodel.RequestPickupEnum;
import com.kahago.kahagoservice.repository.TPickupOrderRequestDetailRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestRepo;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PickupOrderRequestSchedulling {
	
	@Autowired
    private TPickupOrderRequestDetailRepo orderRequestDetailRepo;
    @Autowired
    private TPickupOrderRequestRepo orderRequestRepo;
	@Scheduled(cron="*/7 * * * * ?")
	@SneakyThrows
	public void doSetStatus() {
		log.info("==> Set status Pickup Order Request <==");
		List<TPickupOrderRequestEntity> lsHeader = orderRequestRepo
				.findByStatusAndUserIdNoPage(Arrays.asList(RequestPickupEnum.IN_WAREHOUSE.getValue()), null, null, null);
		for(TPickupOrderRequestEntity pick:lsHeader) {
			List<TPickupOrderRequestDetailEntity> lsDetail = orderRequestDetailRepo.findAllByOrderRequestEntityAndStatus(pick,RequestPickupEnum.IN_WAREHOUSE.getValue());
			if(lsDetail.isEmpty()) {
				log.info("==> Set status Finish "+pick.getPickupOrderId());
				pick.setStatus(RequestPickupEnum.FINISH_BOOK.getValue());
				orderRequestRepo.saveAndFlush(pick);
			}
		}
		
	}
}
