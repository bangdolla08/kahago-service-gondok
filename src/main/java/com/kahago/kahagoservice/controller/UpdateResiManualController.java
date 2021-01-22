package com.kahago.kahagoservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.PageHeaderRequest;
import com.kahago.kahagoservice.model.response.BookDataResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.service.UpdateResiManualService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author Ibnu Wasis
 */
@BaseController
@ResponseBody
@Api(description="Update Resi Manual")
public class UpdateResiManualController extends Controller{
	@Autowired
	private UpdateResiManualService updateResiManualService;
	
	@GetMapping("updateresi/list")
	@ApiOperation("List Of Update Resi")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<List<BookDataResponse>> getListData(PageHeaderRequest paHeaderRequest){
		Page<BookDataResponse> lPayment = updateResiManualService.getListPayment(paHeaderRequest.getPageRequest());
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				extraPaging(lPayment),
				lPayment.getContent()
				);
				
	}
	
	@GetMapping("updateresi/save")
	@ApiOperation("Save Update Resi")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<SaveResponse> saveUpdate(@RequestParam("bookingCode")String bookingCode,
			                                 @RequestParam("stt")String stt){
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				updateResiManualService.updateResi(bookingCode, stt)
				);
	}
}
