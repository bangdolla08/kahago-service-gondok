package com.kahago.kahagoservice.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestEntity;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.enummodel.PickupDetailEnum;
import com.kahago.kahagoservice.enummodel.PickupEnum;
import com.kahago.kahagoservice.enummodel.RequestPickupEnum;
import com.kahago.kahagoservice.model.response.ResPickupTime;
import com.kahago.kahagoservice.repository.TPaymentRepo;
import com.kahago.kahagoservice.repository.TPickupOrderRequestRepo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PickupTimeService {
	@Autowired
	private TPaymentRepo payRepo;
	@Autowired
	private TPickupOrderRequestRepo pickupOrderRepo;
	public List<ResPickupTime> getPickupTime(){
		List<TPaymentEntity> lspay = payRepo.findAllByStatusAndTime(PaymentEnum.REQUEST.getCode(), LocalDate.now());
		List<TPickupOrderRequestEntity> lsPickup = pickupOrderRepo.findAllByOrderDateAndStatus(LocalDate.now(),RequestPickupEnum.REQUEST.getValue());
		List<ResPickupTime> lsRespPick = new ArrayList<ResPickupTime>();
		lsRespPick = lspay.stream().map(this::getDataPay).collect(Collectors.toList());
		lsRespPick.addAll(lsPickup.stream().map(this::getDataPick).collect(Collectors.toList()));
//		LinkedHashSet<ResPickupTime> lsHs = new LinkedHashSet<>(lsRespPick);
		
		List<ResPickupTime> lsRespPick2 = lsRespPick.stream().distinct().collect(Collectors.toList());
		lsRespPick2.sort((ResPickupTime o1,ResPickupTime o2) -> o1.getPickupTime().compareTo(o2.getPickupTime()));
		return lsRespPick2;
	}
	
	private ResPickupTime getDataPay(TPaymentEntity pay) {
		return ResPickupTime.builder()
				.pickupTimeId(pay.getPickupTimeId().getIdPickupTime())
				.pickupTime(pay.getPickupTime())
				.pickupDate(pay.getPickupDate().toString())
				.pickupDay(pay.getPickupDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()))
				.build();
	}
	private ResPickupTime getDataPick(TPickupOrderRequestEntity pick) {
		return ResPickupTime.builder()
				.pickupTimeId(pick.getPickupTimeEntity().getIdPickupTime())
				.pickupTime(pick.getPickupTimeEntity().getTimeFrom().format(DateTimeFormatter.ofPattern("kk:mm:ss"))
						.concat(" - ").concat(pick.getPickupTimeEntity().getTimeTo().format(DateTimeFormatter.ofPattern("kk:mm:ss"))))
				.pickupDate(pick.getOrderDate().toString())
				.pickupDay(pick.getOrderDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()))
				.build();
	}
}
