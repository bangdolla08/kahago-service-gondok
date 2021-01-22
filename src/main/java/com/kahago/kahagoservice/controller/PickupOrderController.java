package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.dto.RequestPickupReqDto;
import com.kahago.kahagoservice.model.request.PickupListRequest;
import com.kahago.kahagoservice.model.request.PickupOrderListRequest;
import com.kahago.kahagoservice.model.request.RequestBook;
import com.kahago.kahagoservice.model.request.RequestPickUpReq;
import com.kahago.kahagoservice.model.response.BookDataResponse;
import com.kahago.kahagoservice.model.response.ListBookingCompleteResponse;
import com.kahago.kahagoservice.model.response.RequestPickUpResp;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.ResponseWithRequest;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.model.response.StatusResponse;
import com.kahago.kahagoservice.service.PickupOrderService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 15 Mei 2020
 */
@BaseController
@ResponseBody
@Api(value = "Untuk meminta dan check list pickup data")
public class PickupOrderController extends Controller {
    private static final Logger log = LoggerFactory.getLogger(PickupOrderController.class);

    @Autowired
    private RequestPickUpService pickUpService;

    @Autowired
    private PickupOrderService pickupOrderService;
    
    @ApiOperation("List of Request Pickup Order")
    @PostMapping("/pickup/order/list")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseWithRequest<PickupOrderListRequest, List<ListBookingCompleteResponse>> getListRequestPickup(@RequestBody PickupOrderListRequest request,HttpServletRequest req){
    	log.info("==> List of Request Pickup <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	ResponseWithRequest<PickupOrderListRequest, List<ListBookingCompleteResponse>> response = new ResponseWithRequest<>();
    	Page<ListBookingCompleteResponse> result = pickUpService.getListRequestPickupOrder(request, request.getPageRequest());
    	response.setRequest(request);
    	response.setData(result.getContent());
    	response.setPage(extraPaging(result));
    	response.setRc(ResponseStatus.OK.value());
    	response.setDescription(ResponseStatus.OK.getReasonPhrase());
    	return response;
    }
    
    @ApiOperation("List of Request Pickup Order Detail")
    @GetMapping("/pickup/order/list/{code}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<BookDataResponse>> getListRequestPickup(@PathVariable String code, RequestBook request, HttpServletRequest req){
    	log.info("==> List of Request Pickup Detail<==");
    	log.info("Param ==>" + code);
    	return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				pickupOrderService.getlistDetailOrder(code,request)
				);
    }

    @ApiOperation("Deletion of Request Pickup Order")
    @DeleteMapping("/pickup/order")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> getListRequestPickupDelete(@RequestParam String code,@RequestParam String reason, Principal principal){
    	log.info("==> Deletion of Request Pickup Detail<==");
    	log.info("Param ==> " + code);
    	log.info("Reason ==> "+reason);
    	return new Response<>(
    			ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				pickupOrderService.doCancelPickup(code,reason,principal.getName())
				);
    }
    
}
