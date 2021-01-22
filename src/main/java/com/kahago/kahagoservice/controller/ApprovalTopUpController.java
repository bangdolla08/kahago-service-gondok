package com.kahago.kahagoservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.DepositTypeEnum;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.ApprovalTopUpReq;
import com.kahago.kahagoservice.model.request.PageHeaderRequest;
import com.kahago.kahagoservice.model.response.DepositResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.service.ApprovalTopupService;
import com.kahago.kahagoservice.util.Common;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ibnu Wasis
 */
@Slf4j
@BaseController
@ResponseBody
public class ApprovalTopUpController extends Controller{
	@Autowired
	private ApprovalTopupService approvalTopupService;
	
	@GetMapping("deposits/list")
	@ApiOperation("List of Approval Top Up Deposite")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<List<DepositResponse>> getAllTopUpDeposite(@RequestParam(name="bankId",required=false) String bankId,
																@RequestParam(name="cari",required=false)String cari,
																@RequestParam(name="status",required=false)Integer status,
																@RequestParam(name="startDate", required = false,defaultValue = "01/01/2018") String startDate,
																@RequestParam(name="endDate", required = false,defaultValue = "31/12/2999") String endDate,
																PageHeaderRequest pageable){
		Page<DepositResponse> lTopup = approvalTopupService.getApprovalTopUp(cari,bankId,DepositTypeEnum.DEPOSIT.getValue(),status, startDate,endDate,pageable.getPageRequest());
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				extraPaging(lTopup),
				lTopup.getContent()
				);
	}
	
	@GetMapping("deposits/credit/list")
	@ApiOperation("List of Approval TopUp Credit")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<List<DepositResponse>> getAllTopUpCredit(@RequestParam(name="bankId",required=false) String bankId,
															@RequestParam(name="cari",required=false)String cari,
															@RequestParam(name="status",required=false)Integer status,
															@RequestParam(name="startDate", required = false,defaultValue = "01/01/2018") String startDate,
															@RequestParam(name="endDate", required = false,defaultValue = "31/12/2999") String endDate,
															PageHeaderRequest pageable){
		Page<DepositResponse> lTopup = approvalTopupService.getApprovalTopUp(cari,bankId,DepositTypeEnum.CREDIT.getValue(),status, startDate,endDate,pageable.getPageRequest());
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				extraPaging(lTopup),
				lTopup.getContent()
				);
	}
	
	@PostMapping("deposits/save")
	@ApiOperation("Approve of TopUp Deposit")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<SaveResponse> saveDeposits(@RequestBody ApprovalTopUpReq request){
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				approvalTopupService.saveTopUp(request)
				);
	}
	@PostMapping("deposits/credit/save")
	@ApiOperation("Approve of TopUp Credit")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<SaveResponse> saveCredit(@RequestBody ApprovalTopUpReq request){
		log.info("==> Topup Credit <==");
		log.info("Log Request:=> "+Common.json2String(request));
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				approvalTopupService.saveCredit(request)
				);
	}
	
}
