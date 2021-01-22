package com.kahago.kahagoservice.schedulling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kahago.kahagoservice.repository.TAreaRepo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DifferenceSchedulling {
	@Autowired
	private TAreaRepo areaRepo;
	
	@Scheduled(cron = "0 0 1 * * ?")
	public void doCheckDiffPrice() {
		log.info("==> Checking Selisih Price <==");
		areaRepo.callSPDiff();
	}
}
