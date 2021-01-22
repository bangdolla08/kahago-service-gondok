package com.kahago.kahagoservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.DeviceEnum;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.response.ResPickupTime;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.service.PickupTimeService;
import com.kahago.kahagoservice.util.Common;

import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@BaseController
@ResponseBody
public class PickupTimeController extends Controller{

	@Autowired
	private PickupTimeService pickTimeService;
	
	 @GetMapping("/pickuptime/list")
	 @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<List<ResPickupTime>> getPickTimeToday(){
		log.info("==> Picktime TOday <==");
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                pickTimeService.getPickupTime());
	}
}
