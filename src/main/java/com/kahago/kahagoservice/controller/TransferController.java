package com.kahago.kahagoservice.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.TransferRequest;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.ResponseGlobal;
import com.kahago.kahagoservice.service.TransferService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@BaseController
@ResponseBody
@Api(value = "Transfer Update Confirmation", description = "Operations pertaining to update Transfer Confirmation")
public class TransferController {
	@Autowired
	private TransferService transferService;
	
	private static final Logger log = LoggerFactory.getLogger(TransferController.class);
	
	@ApiOperation(value = "Transfer Update Pay", response = ResponseGlobal.class)
	@PostMapping("/transfer/pay")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response doUpdatePay(@RequestBody TransferRequest trf,HttpServletRequest req) {
		log.info("==> Transfer Update Pay <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		transferService.doUpdatePay(trf);
		return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase());
	}
	
	@ApiOperation(value = "Transfer Update Topup", response = ResponseGlobal.class)
	@PostMapping("/transfer/topup")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response doUpdateTopup(@RequestBody TransferRequest trf,HttpServletRequest req) {
		log.info("==> Transfer Update Topup <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		transferService.doUpdateTopup(trf);
		return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase());
	}
}
