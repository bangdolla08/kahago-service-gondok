package com.kahago.kahagoservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.response.BookResponse;
import com.kahago.kahagoservice.model.response.Credit;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.service.BillService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@BaseController
@ResponseBody
@Api(value = "Bill Information", description = "Information of Billing Transaction")
public class BillController{
	@Autowired
	private BillService billService;
	@GetMapping("/bill/credit")
	@ApiOperation(value = "Get Bill Credit", response = Response.class)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<List<Credit>> getBillCredit(String userid) {
		log.info("===> Get List Bill Credit");
		return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                billService.getBillCredit(userid)
        );
	}
}
