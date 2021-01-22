package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.TrackRes;
import com.kahago.kahagoservice.model.response.TrackingInternalResponse;
import com.kahago.kahagoservice.service.TrackingService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Hendro yuwono
 */
@BaseController
@ResponseBody
@Validated
public class TrackingController extends Controller {

    @Autowired @Setter
    private TrackingService trackingService;
    
    private static final Logger log = LoggerFactory.getLogger(TrackingController.class);

    @GetMapping("/track/{code}")
    @ApiOperation(value = "Tracking Resi", response = TrackRes.class)
    public Response<List<TrackRes>> findTrack(@PathVariable(name = "code") String code, @RequestParam(name = "vendor_id", required = false) String idVendor,HttpServletRequest req) {
    	log.info("==> Tracking <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                idVendor == null ? trackingService.findByIdBookingOrResi(code) : trackingService.findByResiVendor(code, idVendor));
    }
    
    @GetMapping("/internal/track/{bookingCode}")
    @ApiOperation(value = "Tracking Resi Internal")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<List<TrackingInternalResponse>> getTrackingInternal(@PathVariable("bookingCode") String bookingCode){
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			trackingService.getTrackingInternal(bookingCode)
    			);
    }
}
