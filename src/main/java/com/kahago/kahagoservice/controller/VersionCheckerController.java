package com.kahago.kahagoservice.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.VersionResponse;
import com.kahago.kahagoservice.service.VersionCheckerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author Ibnu Wasis
 */
@Controller
@ResponseBody
@Api(value="version cheker Controller")
public class VersionCheckerController {
	@Autowired
	private VersionCheckerService vCheckerService;
	
	private static final Logger log = LoggerFactory.getLogger(VersionCheckerController.class);
	
	@ApiOperation(value="version")
	@GetMapping("/app/version")
	public Response<VersionResponse> getVersion(HttpServletRequest req){
		log.info("==> Version cheker <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				vCheckerService.getVersion(null)
				);
	}
	@ApiOperation(value="version")
	@GetMapping("/api/app/version")
	public Response<VersionResponse> getVersionAPI(HttpServletRequest req){
		log.info("==> Version cheker <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				vCheckerService.getVersion(null)
				);
	}
	@ApiOperation(value="version")
	@GetMapping("/app/version/ios")
	public Response<VersionResponse> getVersionIOS(HttpServletRequest req){
		log.info("==> Version cheker <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				vCheckerService.getVersion("ios")
				);
	}
	
	@ApiOperation(value="version")
	@GetMapping("/api/app/version/ios")
	public Response<VersionResponse> getVersionAPIIOS(HttpServletRequest req){
		log.info("==> Version cheker <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				vCheckerService.getVersion("ios")
				);
	}
	
}
