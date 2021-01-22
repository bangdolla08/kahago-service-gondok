package com.kahago.kahagoservice.controller;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.kahago.kahagoservice.model.request.ListGudangApprovalReq;
import com.kahago.kahagoservice.model.response.BookDataResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.entity.TWarehouseReceiveDetailEntity;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.WarehouseVerificationReq;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.model.response.UrlResiResponse;
import com.kahago.kahagoservice.model.response.WarehouseVerificationResponse;
import com.kahago.kahagoservice.service.WarehouseVerificationService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author Ibnu Wasis
 */
@BaseController
@ResponseBody
@Api(value="Warehouse Verification Controller")
public class WarehauseVerificationController extends Controller {
	@Autowired
	WarehouseVerificationService verificationService;
	
	private static final Logger log = LoggerFactory.getLogger(WarehauseVerificationController.class);
	
	@ApiOperation(value="List Of Warehouse Verification")
	@GetMapping("/verifikasigudang/list")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<List<BookDataResponse>> getAll(ListGudangApprovalReq listGudangApprovalReq,HttpServletRequest req){
		log.info("==> List of Warehouse Verification <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		Page<BookDataResponse> ldetail = verificationService.getAll(listGudangApprovalReq);
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				this.extraPaging(ldetail),
				ldetail.getContent()
				);
	}

	@ApiOperation(value="List Of Warehouse Verification")
	@GetMapping("/verifikasigudang/list/{codesearch}")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<BookDataResponse> getDetail(@PathVariable(value="codesearch")  String codesearch,@RequestParam(name="office_code") String officeCode,HttpServletRequest req){
		log.info("==> List Warehouse by code <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		BookDataResponse ldetail = verificationService.getDetail(codesearch,officeCode);
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				ldetail
		);
	}
	
	@ApiOperation(value="Approve / Hold Warehouse of booking")
	@PostMapping("/verifikasigudang/save")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<SaveResponse> saveWarehouse(@RequestBody WarehouseVerificationReq request, HttpServletRequest req, Principal principal){
		log.info("==> Approve Warehouse <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				verificationService.saveWarehouseVerification(request,principal.getName())
				);
	}
	
	@ApiOperation(value="Approve / Reject Booking By Counter")
	@PostMapping("/verifikasicounter/save")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<SaveResponse> verifikasiCounter(@RequestParam("bookingCode")String bookingCode,
													@RequestParam("status")Boolean status,
													Principal principal,
													HttpServletRequest req){
		log.info("==> Approve By Counter <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			verificationService.approveCounter(bookingCode, status, principal.getName())
    			);
	}
}
