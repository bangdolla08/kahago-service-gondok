package com.kahago.kahagoservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.CronRequest;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.service.CronService;
import com.kahago.kahagoservice.util.Common;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 24 Jun 2020
 */
@Slf4j
@BaseController
@ResponseBody
public class CronCallbackController extends Controller{
	@Autowired
	private CronService cronService;
	
	@PostMapping(value="/cron/update/area",consumes = MediaType.APPLICATION_JSON_VALUE)
	public Response<SaveResponse> getResultUpdate(@RequestBody CronRequest req){
		log.info("===> Cron Area <===");
		log.info("Request => "+Common.json2String(req));
		
		return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                cronService.getResultCron(req)
        );
	}
	
	@PostMapping("/cron/update/vendor")
	public Response<SaveResponse> getResultUpdateVendor(@RequestBody CronRequest req){
		log.info("===> Cron Vendor <===");
		log.info("Request => "+Common.json2String(req));
		
		return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                cronService.getResultCron(req)
        );
	}
	
}
