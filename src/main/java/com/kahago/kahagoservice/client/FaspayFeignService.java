package com.kahago.kahagoservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

import com.kahago.kahagoservice.client.model.request.Payment;
import com.kahago.kahagoservice.client.model.response.ResponseModel;
import com.kahago.kahagoservice.entity.TPaymentEntity;

@FeignClient(url = "${url.service.faspay}",name = "FASPAY-SERVICE")
public interface FaspayFeignService {
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,value = "/requestpostdata/android")
	ResponseModel sendPaymentAndroid(Payment body);
	
}
