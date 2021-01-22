package com.kahago.kahagoservice.controller;

import java.util.List;

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
import com.kahago.kahagoservice.model.request.ResumePickupReq;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.ResumePickupResponse;
import com.kahago.kahagoservice.service.ResumePickupService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author Ibnu Wasis
 */
@BaseController
@ResponseBody
@Api(value="Resume Pickup Controller",description="Operating of resume pickup")
public class ResumePickupController {
	@Autowired
	ResumePickupService resumeService;
	
	private static final Logger log = LoggerFactory.getLogger(ResumePickupController.class);
	
	@ApiOperation(value="List of Resume Pickup")
	@PostMapping("/resume/pickups")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<List<ResumePickupResponse>> getList(@RequestBody ResumePickupReq req,HttpServletRequest request){
		log.info("==> List of Resume pickup <==");
    	log.info("device ==>"+request.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				resumeService.getDataResume(req)
				);
	}
	@ApiOperation(value="Detail Resume Pickup")
	@GetMapping("/resume/pickups/detail")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<ResumePickupResponse> getDetail(@RequestParam(value="bookingCode") String bookingCode,@RequestParam(value="noManifest") String noManifest,HttpServletRequest req){
		log.info("==> Detail of Resume pickup <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				resumeService.getDetail(bookingCode, noManifest)
				);
	}
	
	
}
