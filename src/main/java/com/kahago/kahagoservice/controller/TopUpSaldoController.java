package com.kahago.kahagoservice.controller;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.TopUpRequest;
import com.kahago.kahagoservice.model.response.BankDeposit;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.service.TopUpSaldoService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ibnu Wasis
 */
@BaseController
@ResponseBody
@Slf4j
public class TopUpSaldoController {
	@Autowired
	private TopUpSaldoService topUpSaldoService;
	
	@PostMapping("/topupsaldo/save")
	@ApiOperation(value="Top Up Saldo multiple User")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> topupSaldoSave(@RequestBody TopUpRequest request,Principal principal, HttpServletRequest req){
		log.info("Top Up Saldo multiple User");
		log.info("device ==> "+req.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				topUpSaldoService.saveTopUp(request, principal.getName())
				);
	}
	
	@GetMapping("/topupsaldo/listbank")
	@ApiOperation(value="List Bank Top Up Saldo multiple User")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<BankDeposit>> getBankTopup(HttpServletRequest req){
		log.info("List Bank Top up Saldo");
		log.info("device ==> "+req.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				topUpSaldoService.findBankTopUp()
				);
	}
}
