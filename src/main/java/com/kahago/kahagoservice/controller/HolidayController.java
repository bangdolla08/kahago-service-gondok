package com.kahago.kahagoservice.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.response.FreedayResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.service.FreedayService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author Ibnu Wasis
 */
@BaseController
@ResponseBody
@Api(value="Holiday Controller")
public class HolidayController {
    @Autowired
    private FreedayService freedayService;
    
    private static final Logger log = LoggerFactory.getLogger(HolidayController.class);
    
	@ApiOperation(value="List of holidays")
    @GetMapping("/holiday/list")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<FreedayResponse>> getAllHoliday(HttpServletRequest req){
		log.info("==>List of holidays<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			freedayService.getAllFreeday()
    			);
    }
	
	@ApiOperation(value="Add Holiday")
	@PostMapping("holiday/save")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<String> saveHoliday(@RequestParam(value="Hari",required=false)String dayName,
    									@RequestParam(value="Tanggal(yyyy-MM-dd)",required=false)String date,
    									@RequestParam(value="Deskripsi")String description,
    									HttpServletRequest req){
		log.info("==>Add Holidays<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		return freedayService.FreedaySave(dayName, date, description);
	}
	
	@ApiOperation(value="Delete Holiday")
	@DeleteMapping("/holiday/delete")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<String> deleteHoliday(@RequestParam(value="Hari",required=false)String dayName,
										  @RequestParam(value="Tanggal(yyyy-MM-dd)",required=false)String date,
											HttpServletRequest req){
		log.info("==>Delete Holiday<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		return freedayService.deleteHoliday(dayName, date);
	}
}
