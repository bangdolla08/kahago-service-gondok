package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.response.MonitorManifestResponse;
import com.kahago.kahagoservice.model.response.MonitorTransResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.service.MonitoringTransService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hendro yuwono
 */
@BaseController
@ResponseBody
public class MonitoringTransController {

    @Autowired
    private MonitoringTransService transService;

    @ApiOperation(value = "Show status transaction ")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @GetMapping("/monitoring/trans")
    public Response<MonitorTransResponse> countTransaction(@RequestParam(defaultValue = "day") String keyword,
    														@RequestParam(value="origin",required=false)String origin) {
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                transService.countingByStatus(keyword,origin));
    }
    
    @ApiOperation(value="Show Total Pending Manifest")
    @GetMapping("/monitoring/manifest")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<MonitorManifestResponse>> getTotalPendingManifest(){
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			transService.getTotalPendingManifestByPickupDate()
    			);
    }
}
