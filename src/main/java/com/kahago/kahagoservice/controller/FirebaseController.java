package com.kahago.kahagoservice.controller;
/**
 * @author Ibnu Wasis
 */

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.model.request.TokenRequest;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.service.FirebaseService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@BaseController
@Api(value="Firebase Controller")
@ResponseBody
public class FirebaseController {
	@Autowired
	private FirebaseService firebaseService;
	
	private static final Logger log = LoggerFactory.getLogger(FirebaseController.class);
	
	@ApiOperation(value="update token")
	@PostMapping("/token/update")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<String> updateToken(@RequestBody TokenRequest request,HttpServletRequest req){
		log.info("==>Update Token<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		return firebaseService.updateToken(request);
	}
}
