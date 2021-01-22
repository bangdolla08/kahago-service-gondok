package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.response.ResPickupTime;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.service.PickupService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Hendro yuwono
 */
@BaseController
@ResponseBody
public class PickupController extends Controller {

    @Autowired
    private PickupService pickupService;
    
    private static final Logger log = LoggerFactory.getLogger(PriceController.class);

    @GetMapping("/pickups/time")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<ResPickupTime>> find(Principal principal,HttpServletRequest req) {
    	log.info("==>Pickup time <=="+principal.getName());
    	log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                pickupService.findByTimeNow(principal.getName())
        );
    }
    
    @ApiOperation("List Time Pickup By User Category")
    @GetMapping("/pickups/find/{userCategory}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<ResPickupTime>> findByUserCategory(@PathVariable("userCategory")Integer idUserCategory){
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			pickupService.findPickupTimeByUserCategory(idUserCategory)
    			);
    }
    
    @ApiOperation("Active / UnActived Time Pickup")
    @PostMapping("/pickups/actived")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> activedTimePickup(@RequestParam("idUserCategory")Integer idUserCategory,
    												@RequestParam("idTimePickup")Integer idTimePickup){
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			pickupService.activeOrUnActivedTimePickup(idUserCategory, idTimePickup)
    			);
    }
    
    @ApiOperation("Add Time Pickup at Category")
    @PostMapping("/pickups/add")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> addTimePickup(@RequestParam("idUserCategory")Integer idUserCategory,
    												@RequestParam("idTimePickup")Integer idTimePickup){
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			pickupService.addTimePickup(idUserCategory, idTimePickup)
    			);
    }
}