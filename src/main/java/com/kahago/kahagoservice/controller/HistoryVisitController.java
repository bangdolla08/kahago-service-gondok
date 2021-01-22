package com.kahago.kahagoservice.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.HistoryVisitRequest;
import com.kahago.kahagoservice.model.response.HistoryVisitResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.service.HistoryVisitService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ibnu Wasis
 */
@BaseController
@ResponseBody
@Slf4j
public class HistoryVisitController {
	@Autowired
	private HistoryVisitService historyVisitService;
	
	@PostMapping("/history/visit")
	@ApiOperation(value="Check History visiting menu")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<HistoryVisitResponse> getHistoryVisiting(@RequestBody HistoryVisitRequest req,Principal principal){
		log.info("==> Historing Visit Menu <==");
		log.info("Request Param : "+req.getUrl()+","+req.getAction()+","+req.getParam()+","+principal.getName()+","+req.getFlag());
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				historyVisitService.getHistoryVisitingMenu(req,principal.getName())
				);
	}
}
