package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.entity.TPickupEntity;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.CreateManifestReq;
import com.kahago.kahagoservice.model.request.ManifestListRequest;
import com.kahago.kahagoservice.model.response.*;
import com.kahago.kahagoservice.service.AssignPickupService;
import com.kahago.kahagoservice.service.ManifestPickupService;
import com.kahago.kahagoservice.util.Common;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author bangd ON 17/12/2019
 * @project com.kahago.kahagoservice.controller
 */
@BaseController
@ResponseBody
@Api(value = "Untuk BOC ", description = "Assign Pickup KahaGo")
public class AssignPickupController  extends Controller{
    private static final String baseAssignPickup="/pickuptime/";
    @Autowired
    private AssignPickupService assignPickupService;
    @Autowired
    private ManifestPickupService pickupService;
    
    private static final Logger log = LoggerFactory.getLogger(AssignPickupController.class);
    @GetMapping(baseAssignPickup+"gettime")
    @ApiOperation(value = "Get Time yang aktive")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<ResPickupTime>> getResPickTime(HttpServletRequest req){
    	log.info("==> Assign pickup get Time <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                assignPickupService.getActiveTimePickup());
    }

    @GetMapping(baseAssignPickup+"getbooking")
    @ApiOperation(value = "Get Booking most Assign Pick")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<BookDataResponse>> getBookingAssign(
            @RequestParam(required = false,defaultValue = "",value = "user_id") String user_id,
            @RequestParam(required = false,defaultValue = "",value = "date") String date,
            @RequestParam(required = false,defaultValue = "",value = "pickup_time_id") Integer pickup_time_id,
            @RequestParam(required = false,value="areaKotaId")Integer areaKotaId,
            HttpServletRequest req) throws ParseException {
    	log.info("==> Assign pickup get Booking <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                assignPickupService.getDataNeedToAssignPickup(user_id,date,pickup_time_id,areaKotaId));
    }

    @PostMapping(baseAssignPickup+"generateassign")
    @ApiOperation(value = "Save Assign pickup")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> getBookingAssign(@RequestBody CreateManifestReq createManifestReq,HttpServletRequest req){
//        Boolean resultSave=assignPickupService.savePickupData(createManifestReq);
//        if(resultSave)
    	log.info("==> Assign pickup save <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                assignPickupService.savePickupData(createManifestReq));
    }
    @ApiOperation(value="Get List just Header")
    @GetMapping(baseAssignPickup+"getmanifest")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<ManifestPickup>> manifestPickupsAll(ManifestListRequest manifestListRequest,HttpServletRequest req){
    	log.info("==> Assign pickup get Manifest <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	log.info("Request => "+Common.json2String(manifestListRequest));
        Page<ManifestPickup> manifestPickupPage=pickupService.getAllManifest(manifestListRequest.getPageRequest(),manifestListRequest);
        return new Response<>(ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                extraPaging(manifestPickupPage),
                manifestPickupPage.getContent()
        );
    }

    @ApiOperation(value="Get List just Header")
    @GetMapping(baseAssignPickup+"getmanifest/{manifestId}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<ManifestDetailRes> manifestPickupsAll(@PathVariable(value="manifestId") String manifestId,HttpServletRequest req){
        log.info("==> Assign pickup Manifest pickup All <==");
        log.info("device ==>"+req.getHeader("User-Agent"));
        ManifestDetailRes manifestPickupPage=pickupService.getBookDataResponsesByManifest(manifestId);
        return new Response<>(ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                manifestPickupPage
        );
    }
    @ApiOperation(value="Get List just Header")
    @GetMapping(baseAssignPickup+"getmanifest/{manifestId}/{pickupAddrId}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<AssignPickupResponse> manifestPickupsAll(@PathVariable(value="manifestId") String manifestId,@PathVariable(value="pickupAddrId") Integer pickupAddrId,HttpServletRequest req){
        log.info("==> Assign pickup Manifest pickup All <==");
        log.info("device ==>"+req.getHeader("User-Agent"));
        AssignPickupResponse assignPickupResponse=pickupService.getDetailStatus(manifestId,pickupAddrId);
        return new Response<>(ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                assignPickupResponse
        );
    }

    @ApiOperation(value="Apakah Bolleh Assing Pickup Time")
    @GetMapping(baseAssignPickup+"statustime")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<ValidateTimeToAsiggn> getStatusTime(@RequestParam(required = false,defaultValue = "",value = "date") String date,
                                                     @RequestParam(required = false,defaultValue = "",value = "pickup_time_id") Integer pickup_time_id,
                                                     @RequestParam(value="noManifest",required=false,defaultValue="") String code,
                                                     HttpServletRequest req) throws ParseException {
        log.info("==> Assign pickup Manifest pickup All <==");
        log.info("device ==>"+req.getHeader("User-Agent"));
        ValidateTimeToAsiggn manifestPickupPage=assignPickupService.getTimeStatus(date,pickup_time_id,code);
        return new Response<>(ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                manifestPickupPage
        );
    }
    @GetMapping(baseAssignPickup+"getlist")
    @ApiOperation(value = "Get List Assign Pickup")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<AssignPickupResponse>> getListAssign(@RequestParam(name="userId",required=false)String userId,
                                                              @RequestParam(name="areaDetailId",required=false)Integer areaDetailId,
                                                              @RequestParam(name="areaKotaId",required=false)Integer areaKotaId,
                                                              @RequestParam(name="idPickupTime",required=false)Integer idPickupTime,
                                                              @RequestParam(name="pickupDate",required=false)String pickupDate){
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			assignPickupService.getListAssignPickup(userId, areaDetailId, areaKotaId, idPickupTime, pickupDate)
    			);
    }
    
    @DeleteMapping(baseAssignPickup+"remove/{bookingCode}")
    @ApiOperation(value="Remove Detail pickup")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<String> deleteDetail(@PathVariable(value="bookingCode")String bookingCode,
    									 @RequestParam(value="courierId")String courierId,
    									 Principal principal){
    	return assignPickupService.removePickupDetail(bookingCode, courierId,principal.getName());
    }
    
    @GetMapping(baseAssignPickup+"notif")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<String> getNotif(@RequestParam("noManifest") String code){
    	TPickupEntity pickup = pickupService.findByCode(code);
    	assignPickupService.getNotif(pickup);
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase()
    			);
    }
    
    @DeleteMapping(baseAssignPickup+"deleteManifest")
    @ApiOperation(value="Remove Manifest")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<String> deleteManifest(@RequestParam("noManifest")String code,
    										@RequestParam("courierId")String courierId,
    										Principal principal){
    	return assignPickupService.deleteManifest(code, principal.getName());
    }
    
    @PostMapping(baseAssignPickup+"addManifest")
    @ApiOperation(value="Add Detail Manifest")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> addDetailManifest(@RequestBody CreateManifestReq request, Principal principal){
    	log.info("==> Add Detail Manifest <==");
    	log.info("Payload => "+ Common.json2String(request));
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			assignPickupService.addDetailManifest(request,principal.getName())
    			);
    }
}
