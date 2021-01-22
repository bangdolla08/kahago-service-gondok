package com.kahago.kahagoservice.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.DestinationRequest;
import com.kahago.kahagoservice.model.request.PriceListRequest;
import com.kahago.kahagoservice.model.response.DestinationResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.service.MasterDestinationService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ibnu Wasis
 */
@BaseController
@ResponseBody
@Slf4j
public class DestinationsController extends Controller{
	@Autowired
	private MasterDestinationService masterDestinationService;
	
	@ApiOperation(value="List Of Master Destination")
	@GetMapping("/destination/list")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<DestinationResponse>> getDestinations(PriceListRequest request, HttpServletRequest req){
		log.info("===>List Master Destination<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
    	Page<DestinationResponse> lResponse = masterDestinationService.getDestination(request);
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			extraPaging(lResponse),
    			lResponse.getContent()    			
    			);
	}
	
	@ApiOperation(value="Save or Edit Master Destination")
	@PostMapping("/destination/save")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> saveDestination(@RequestBody DestinationRequest request,HttpServletRequest req){
		log.info("===>Save Or Edit Master Destination<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			masterDestinationService.addVendorArea(request)
    			);
	}
	
	@ApiOperation(value="Get Master Destination By Id")
	@GetMapping("/destination/get/{id}")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<DestinationResponse> getAreaById(@PathVariable("id") Integer id,HttpServletRequest req){
		log.info("===>GET Master Destination By ID<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			masterDestinationService.getAreaById(id)
    			);
	}
	
	@ApiOperation(value="Get Price From Destination")
	@GetMapping("/destination/getprice")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> getPriceFromAreaId(@RequestParam("areaId") Integer areaId,
    												 @RequestParam("vendorId") Integer vendorId,
    												 HttpServletRequest req){
		log.info("===>GET Master Destination By ID<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			masterDestinationService.hitPriceByVendorArea(areaId,vendorId)
    			);
	}
}
