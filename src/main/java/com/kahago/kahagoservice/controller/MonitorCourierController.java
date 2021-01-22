package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.response.CourierManifestMonitoringRes;
import com.kahago.kahagoservice.model.response.ProfileRes;
import com.kahago.kahagoservice.model.response.ResPickupTime;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.service.AssignPickupService;
import com.kahago.kahagoservice.service.MonitoringCourierService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author bangd ON 25/02/2020
 * @project com.kahago.kahagoservice.controller
 */
@BaseController
@ResponseBody
@Api(value = "Untuk BOC ", description = "Monitor Courier Data")
public class MonitorCourierController {
    @Autowired
    private MonitoringCourierService courierService;

    private static final Logger log = LoggerFactory.getLogger(MonitorCourierController.class);
    @GetMapping("/monitoringcourier/listCourier")
    @ApiOperation(value = "Get Total by userId full")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<ProfileRes.Profile>> getCourier(HttpServletRequest req){
        log.info("==> Assign pickup get Time <==");
        log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                courierService.getCourierList());
    }
    @GetMapping("/monitoringcourier/detail")
    @ApiOperation(value = "Get Total by userId full")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<CourierManifestMonitoringRes> getRescourierMonitorDetail(
            String courierId,
            HttpServletRequest req){
        log.info("==> Assign pickup get Time <==");
        log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                courierService.getManifestDetail(courierId,true));
    }

    @GetMapping("/monitoringcourier")
    @ApiOperation(value = "Get Total by userId")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<CourierManifestMonitoringRes> getRescourierMonitor(
            String courierId,
            HttpServletRequest req){
        log.info("==> Assign pickup get Time <==");
        log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                courierService.getManifestDetail(courierId,false));
    }
}
