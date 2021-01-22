package com.kahago.kahagoservice.client;

import java.net.URI;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.kahago.kahagoservice.client.model.request.Payment;
import com.kahago.kahagoservice.client.model.request.ReqPayment;
import com.kahago.kahagoservice.client.model.response.ResponseModel;
import com.kahago.kahagoservice.entity.TPaymentEntity;

@FeignClient(url = "${url.service.pawoon}",name="PAWOON-SERVICE")
public interface PawoonFeignService {
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseModel requetPaymentAndroid(URI uri,Payment body);
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, value = "/requestPawon/status/{tag}/{idTicket}/{idPayment}")
	ResponseModel requestStatusPawon(@PathVariable("tag") String tag,@PathVariable("idTicket") String idTicket,@PathVariable("idPayment") String idPayment);
}
