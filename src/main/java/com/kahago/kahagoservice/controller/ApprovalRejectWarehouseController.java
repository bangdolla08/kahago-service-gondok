package com.kahago.kahagoservice.controller;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.kahago.kahagoservice.model.response.BookDataResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.ApprovalRejectWarehouseReq;
import com.kahago.kahagoservice.model.response.ApprovalRejectWarehouseResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.model.response.UrlResiResponse;
import com.kahago.kahagoservice.service.ApprovalRejectWarehouseService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author Ibnu Wasis
 */
@BaseController
@ResponseBody
@Validated
public class ApprovalRejectWarehouseController extends Controller{
	@Autowired
	private ApprovalRejectWarehouseService approvalRejectWarehouseService;
	
	private static final Logger log = LoggerFactory.getLogger(ApprovalRejectWarehouseController.class);

	@ApiOperation("List of hold by warehouse")
	@GetMapping("/holdwarehouse/list")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<List<ApprovalRejectWarehouseResponse>> getListHoldWarehouse(ApprovalRejectWarehouseReq request,HttpServletRequest req){
		log.info("==> List Hold Warehouse <==");
		log.info("device ==>"+req.getHeader("User-Agent"));
		Page<ApprovalRejectWarehouseResponse> result = approvalRejectWarehouseService.getListWarehouse(request,request.getPageRequest());
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				extraPaging(result),
				result.getContent());
	}


	@ApiOperation("List of hold by warehouse")
	@GetMapping("/holdwarehouse/list/{codesearch}")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<BookDataResponse> getItemHoldWarehouse(@PathVariable(value="codesearch")  String codesearch, @RequestParam(name="office_code") String officeCode,Principal principal, HttpServletRequest req){
		log.info("==> List Hold Warehouse <==");
		log.info("device ==>"+req.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				approvalRejectWarehouseService.getBookData(codesearch,officeCode,principal.getName()));
	}



	@ApiOperation("Approve Or Reject booking")
	@PostMapping("holdwarehouse/save")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> doSave(@RequestBody ApprovalRejectWarehouseReq approvalRejectWarehouseReq,
    								HttpServletRequest req,
    								Principal principal){
		log.info("==> Approve Or Reject Warehouse <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	return approvalRejectWarehouseService.doApproveReject(approvalRejectWarehouseReq.getIdWarehouseDetail(), approvalRejectWarehouseReq.getIsConfirmRejectApprove(),principal.getName(),approvalRejectWarehouseReq.getBookId());
	}

}
