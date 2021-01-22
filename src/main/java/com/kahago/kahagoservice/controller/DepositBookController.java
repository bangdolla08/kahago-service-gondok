package com.kahago.kahagoservice.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.DepositBookRequest;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.service.DepositBookService;
import com.kahago.kahagoservice.util.Common;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@BaseController
@ResponseBody
public class DepositBookController extends Controller{
	@Autowired
	private DepositBookService depBookService;
	@PostMapping("/depositbooks/insert")
    @ApiOperation(value = "Input Barang Titipan")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<SaveResponse>  doSaveDepositBooks(@RequestBody DepositBookRequest req,Principal principal) {
		log.info("==> Deposit Books Save <==");
		log.info("Request => "+Common.json2String(req));
		return new Response<SaveResponse>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(), 
				depBookService.doInsert(req, principal.getName())) ; 
	}
}
