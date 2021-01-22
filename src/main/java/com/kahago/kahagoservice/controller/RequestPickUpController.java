package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.dto.RequestPickupReqDto;
import com.kahago.kahagoservice.model.request.PickupListRequest;
import com.kahago.kahagoservice.model.request.RequestPickUpReq;
import com.kahago.kahagoservice.model.response.ListBookingCompleteResponse;
import com.kahago.kahagoservice.model.response.RequestPickUpResp;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.ResponseWithRequest;
import com.kahago.kahagoservice.service.RequestPickUpService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author bangd ON 16/12/2019
 * @project com.kahago.kahagoservice.controller
 */
@BaseController
@ResponseBody
@Api(value = "Untuk meminta dan check list pickup data")
public class RequestPickUpController extends Controller {
    private static final String urlPick = "pickup/";
    private static final Logger log = LoggerFactory.getLogger(RequestPickUpController.class);

    @Autowired
    private RequestPickUpService pickUpService;

    @PostMapping(urlPick+"requestpickup")
    @ApiOperation(value = "Sesuaikan dengan inputan yang dibutuhkan")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<RequestPickUpResp> getRolePickup(@RequestBody RequestPickUpReq requestPickUpReq,HttpServletRequest req,Principal principal) throws ParseException {
    	log.info("==> Request pickup save <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	log.info("principal ==> "+principal.getName());
    	RequestPickupReqDto pickupReqDto=pickUpService.validateInput(requestPickUpReq);
        return new Response<>(ResponseStatus.OK.value(),ResponseStatus.OK.getReasonPhrase(),pickUpService.setRequestBook(requestPickUpReq,pickupReqDto,principal.getName()));
    }
    
    @ApiOperation("List of Request Pickup")
    @GetMapping(urlPick+"listrequest")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseWithRequest<PickupListRequest, List<ListBookingCompleteResponse>> getListRequestPickup(PickupListRequest request,Pageable pageable,HttpServletRequest req){
    	log.info("==> List of Request Pickup <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	ResponseWithRequest<PickupListRequest, List<ListBookingCompleteResponse>> response = new ResponseWithRequest<>();
    	Page<ListBookingCompleteResponse> result = pickUpService.getListRequestPickup(request, pageable);
    	response.setRequest(request);
    	response.setData(result.getContent());
    	response.setPage(extraPaging(result));
    	response.setRc(ResponseStatus.OK.value());
    	response.setDescription(ResponseStatus.OK.getReasonPhrase());
    	return response;
    }
    
    @ApiOperation("Cancel Request Pickup")
    @DeleteMapping(urlPick+"cancel")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<String> cancelOrderPickup(@RequestParam(value="pickupOrderId")String pickupOrderId,
    											@RequestParam(value="userId")String userId,
    											@RequestParam(value="qrcode",required=false,defaultValue="-")String qrcode){
    	return pickUpService.cancelRequestPickup(userId, pickupOrderId,qrcode);
    }
    
}
