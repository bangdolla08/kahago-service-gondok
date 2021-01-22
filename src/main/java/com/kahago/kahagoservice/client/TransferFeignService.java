package com.kahago.kahagoservice.client;

import java.net.URI;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.kahago.kahagoservice.client.model.request.Payment;
import com.kahago.kahagoservice.client.model.request.ReqPayment;
import com.kahago.kahagoservice.client.model.request.ReqTransfer;
import com.kahago.kahagoservice.client.model.response.ResponseModel;
import com.kahago.kahagoservice.client.model.response.TransferVendorResponse;
import com.kahago.kahagoservice.entity.TPaymentEntity;

import feign.Headers;
import feign.RequestLine;

//@FeignClient(url="${url.service.transfer}", name="TRANSFER-SERVICE")
public interface TransferFeignService {
//	@RequestLine("POST ")
//	@Headers("Content-Type: application/x-www-form-urlencoded")
//	ResponseEntity<String> requestStatusTransfer(ReqTransfer reqtransfer);
}
