package com.kahago.kahagoservice.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.HistoryTopUpReq;
import com.kahago.kahagoservice.model.response.HistoryTopUpResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.service.HistoryTopUpService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author Ibnu Wasis
 */
@BaseController
@ResponseBody
@Api(value="History Topup Controller", description="Operating about Top up History")
public class HistoryTopupController {
	@Autowired
	private HistoryTopUpService historyTopUpService;
	
	private static final Logger log = LoggerFactory.getLogger(HistoryTopupController.class);
	
	@ApiOperation(value="List of Top up History")
	@PostMapping("/report/topup")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<List<HistoryTopUpResponse>> getHistory(@RequestBody HistoryTopUpReq request,HttpServletRequest req){
		log.info("==>History of Top Up<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				historyTopUpService.getHistory(request)
				);
	}
}
