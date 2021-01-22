package com.kahago.kahagoservice.schedulling;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kahago.kahagoservice.client.FeignService;
import com.kahago.kahagoservice.entity.MSwitcherEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.enummodel.PaymentEnum;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.response.BookResponseBukaSend;
import com.kahago.kahagoservice.repository.MSwitcherRepo;
import com.kahago.kahagoservice.repository.TPaymentRepo;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ibnu Wasis
 */
@Component
@Slf4j
public class BukaSendScheduling {
	@Autowired
	private TPaymentRepo tPaymentRepo;
	@Autowired
	private MSwitcherRepo mSwitcherRepo;
	@Autowired
	private FeignService feignService;
	@Value("${url.bukasend.detail}")
	private String url;
	
	private static final Integer SWITCHER_CODE_BUKASEND = 314;
	@Scheduled(fixedDelay= 10 * 1000)
	public void checkResiNo() {
		log.info("Buka Send Check Resi No");
		MSwitcherEntity switcher = mSwitcherRepo.findById(SWITCHER_CODE_BUKASEND).orElseThrow(()->new NotFoundException("Data Tida Ditemukan"));
		List<TPaymentEntity> lPayment = tPaymentRepo.findAllByTrxDateAndStatusAndProductSwCodeSwitcherEntity(LocalDate.now(), PaymentEnum.ACCEPT_IN_WAREHOUSE.getCode(), switcher);
		for(TPaymentEntity pay : lPayment) {
			if(pay.getStt().contains("|") && pay.getStt().contains("-")) {
				URI uri = URI.create(url+"/"+pay.getStt().substring(0, pay.getStt().lastIndexOf("|")));
				BookResponseBukaSend response = feignService.fetchGetDetailBukaSend(uri);
				log.info("Response Body : "+response.toString());
				if(!response.getResiNo().equals("-")) {
					String stt = response.getIdBukaSend()+"|"+response.getResiNo();
					pay.setStt(stt);
				}
			}
		}
		tPaymentRepo.saveAll(lPayment);
	}

}
