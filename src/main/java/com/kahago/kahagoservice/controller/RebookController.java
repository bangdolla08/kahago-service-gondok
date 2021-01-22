package com.kahago.kahagoservice.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.RebookReq;
import com.kahago.kahagoservice.model.response.RebookResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.service.RebookService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author Ibnu Wasis
 */
@BaseController
@ResponseBody
@Api(value="Rebook Controller",description="Operating about Rebook")
public class RebookController {
	@Autowired
	private RebookService rbookService;
	
	private static final Logger log = LoggerFactory.getLogger(RebookController.class);
	
	@ApiOperation(value="list of Expired Payment")
	@GetMapping("rebook/list")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<RebookResponse> getAllExpired(@RequestParam(value="userId") String userId,
												  @RequestParam(value="startDate",required=false) String startDate,
												  @RequestParam(value="endDate",required=false) String endDate,
												  HttpServletRequest req){
		log.info("==>Rebook list <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				rbookService.getAllExpired(userId, startDate, endDate)
				);
	}
	
	@ApiOperation(value="save rebooking")
	@PostMapping("rebook/save")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<String> saveRebooking(@RequestBody RebookReq request,HttpServletRequest req){
		log.info("==>Save rebooking <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		return rbookService.rebookSave(request);
	}
}
