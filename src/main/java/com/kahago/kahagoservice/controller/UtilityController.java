package com.kahago.kahagoservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.DimensiReq;
import com.kahago.kahagoservice.model.response.DimensiResp;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.ResponseGlobal;
import com.kahago.kahagoservice.service.UtilityService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@BaseController
@ResponseBody
public class UtilityController {
	@Autowired
	private UtilityService utilService;
	
	@ApiOperation(value = "Get Dimensi", response = DimensiResp.class)
	@GetMapping("/utility/dimensi")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<DimensiResp> getDimensi(DimensiReq req){
		
		 return new Response<DimensiResp>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(), 
				 utilService.getDimensi(req));
	}
}
