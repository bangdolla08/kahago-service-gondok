package com.kahago.kahagoservice.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.VerifikasiIncoming;
import com.kahago.kahagoservice.model.response.VerifikasiRespon;
import com.kahago.kahagoservice.service.VerifikasiIncomingService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author Ibnu Wasis
 */
@BaseController
@ResponseBody
@Api(value="Verifikasi Booking", description="Operating about Verifikasi")
public class VerifikasiIncomingController {
	@Autowired
	private VerifikasiIncomingService vIncomingService;
	
	private static final Logger log = LoggerFactory.getLogger(VerifikasiIncomingController.class);
	
	@ApiOperation(value="view Pesanan")
	@GetMapping("/verifikasi/view/{bookingCode}")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<VerifikasiRespon> getVerifikasi(@PathVariable String bookingCode,HttpServletRequest req){
		log.info("==> View Pesanan <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				vIncomingService.getVerifikasi(bookingCode)
				);
	}
	
	@ApiOperation(value="Verifikasi Pesanan")
	@GetMapping("/verifikasi/save/{action}")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<String> doSave(@PathVariable String action,@RequestParam(value="bookingCode") String bookingCode,HttpServletRequest req){
		log.info("==> Verifikasi Pesanan <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		return vIncomingService.doSave(action, bookingCode);
	}
	
	@ApiOperation(value="Verifikasi List Pesanan")
	@GetMapping("/verifikasi/incoming/list")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<List<VerifikasiIncoming>> getListVerifikasi(String userId,HttpServletRequest req){
		log.info("==> Verifikasi List Pesanan <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				vIncomingService.getlistVerifikasi(userId)
				);
	}
	
	@ApiOperation(value="Payment Verifikasi Incoming")
	@PostMapping("/verifikasi/incoming/pay")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<VerifikasiRespon> getPayVerifikasi(@RequestBody VerifikasiRespon req,HttpServletRequest request){
		log.info("==> Payment Verifikasi Incoming <==");
    	log.info("device ==>"+request.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				vIncomingService.getPay(req)
				);
	}
	
}
