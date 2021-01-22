package com.kahago.kahagoservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.PageHeaderRequest;
import com.kahago.kahagoservice.model.response.RespTransferRespone;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.service.ResponseTransferService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author Ibnu Wasis
 */
@BaseController
@ResponseBody
public class ResponseTransferController extends Controller{
	@Autowired
	private ResponseTransferService responseTransferService;
	
	@GetMapping("/transfer/list")
	@ApiOperation("List Of Response Transfer")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<RespTransferRespone>> getAllByDate(@RequestParam(name="startDate",required=false)String startDate,
    														@RequestParam(name="endDate",required=false)String endDate,
    														PageHeaderRequest pageable){
		Page<RespTransferRespone> response = responseTransferService.getRespTransfer(startDate, endDate, pageable.getPageRequest());
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				extraPaging(response),
				response.getContent()
				);
	}
}
