package com.kahago.kahagoservice.controller;

import java.security.Principal;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.AppBookingRequest;
import com.kahago.kahagoservice.model.request.OutgoingCounterReq;
import com.kahago.kahagoservice.model.request.OutgoingRequest;
import com.kahago.kahagoservice.model.request.PageHeaderRequest;
import com.kahago.kahagoservice.model.request.SaveCourierReq;
import com.kahago.kahagoservice.model.request.TotalTrxRequest;
import com.kahago.kahagoservice.model.response.BookDataResponse;
import com.kahago.kahagoservice.model.response.OutgoingCounterResp;
import com.kahago.kahagoservice.model.response.OutgoingResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.model.response.TotalTrxResponse;
import com.kahago.kahagoservice.schedulling.PaymentSchedulling;
import com.kahago.kahagoservice.service.ApprovalBookingService;
import com.kahago.kahagoservice.service.OutgoingCounterService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;


/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 30 Apr 2020
 */
@BaseController
@ResponseBody
public class OutgoingCounterController extends Controller{
	@Autowired
	private OutgoingCounterService outgoingService;
	
	
	private static final Logger log = LoggerFactory.getLogger(OutgoingCounterController.class);
	
	@ApiOperation(value="Outgoing Counter List")
	@GetMapping("/outgoing/counter/list")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<List<OutgoingResponse>> getOutgoingCounter(OutgoingRequest request,HttpServletRequest req,Principal principal){
		log.info("===>List Outgoing Counter <===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
    	Page<OutgoingResponse> outresp = outgoingService.getListPage(request.getPageRequest(), request);
    	return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                extraPaging(outresp),
                outresp.getContent()
        );
	}
	
	@ApiOperation(value="Outgoing Counter Create")
	@PostMapping("/outgoing/counter/create")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<OutgoingResponse> doCreateCounter(@RequestBody OutgoingRequest request,HttpServletRequest req,
			Principal principal){
		log.info("===>List Approval Booking<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                outgoingService.create(request,principal.getName()));
	}
	
	@ApiOperation(value="Outgoing Counter Create")
	@GetMapping("/outgoing/counter/detail/book")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public  Response<BookDataResponse> getDetailBookCounter(@RequestParam String bookId,@RequestParam String officeCode,HttpServletRequest req,
			Principal principal){
		log.info("===>List Approval Booking<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                outgoingService.getDetailBooking(bookId, officeCode));
	}
	@ApiOperation(value="Outgoing Counter Create Detail")
	@PutMapping("/outgoing/counter/detail/{code}")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<OutgoingResponse> doCreateCounterDetail(@PathVariable String code,@RequestBody OutgoingRequest request,HttpServletRequest req,
			Principal principal){
		log.info("===>List Approval Booking<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                outgoingService.createDetail(code, request, principal.getName()));
	}
	@ApiOperation(value="Delete Outgoing Counter  Detail")
	@DeleteMapping("/outgoing/counter/detail/{code}")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<OutgoingResponse> doDeleteCounterDetail(@PathVariable String code,@RequestBody OutgoingRequest request,HttpServletRequest req,
			Principal principal){
		log.info("===>List Approval Booking<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                outgoingService.deleteDetail(code, request, principal.getName()));
	}
	
	@ApiOperation(value="Processing Outgoing Counter ")
	@PostMapping("/outgoing/counter/save")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<OutgoingResponse> doProcess(@RequestBody SaveCourierReq request,HttpServletRequest req,
			Principal principal){
		log.info("===>List Approval Booking<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                outgoingService.prosesDetail(request, principal.getName()));
	}
}
