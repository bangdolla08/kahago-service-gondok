package com.kahago.kahagoservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.OptionPaymentRequest;
import com.kahago.kahagoservice.model.response.OptionPaymentResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.service.MasterOptPaymentService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author Ibnu Wasis
 */
@BaseController
@ResponseBody
@Api(description="View Of Master")
public class MasterController {
	@Autowired
	private MasterOptPaymentService mOptPaymentService;
	
	@ApiOperation("Master Of Option Payment")
	@GetMapping("/master/optionpayment")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<List<OptionPaymentResponse>> getAllOptPayment(){
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				mOptPaymentService.getAllOptionPayment()
				);
	}
	
	@ApiOperation("Add Master Of Option Payment")
	@PostMapping("/master/optionpayment/save")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<SaveResponse> saveOptPayment(@RequestBody OptionPaymentRequest request){
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				mOptPaymentService.saveOptionPayment(request)
				);
	}
	
	@ApiOperation("Edit Master Of Option Payment")
	@PostMapping("/master/optionpayment/edit")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<SaveResponse> editOptPayment(@RequestBody OptionPaymentRequest request){
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				mOptPaymentService.saveEdit(request)
				);
	}
	
	@ApiOperation("Aktif/NonAktif Master Of Option Payment")
	@DeleteMapping("/master/optionpayment/delete/{id}")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<SaveResponse> deleteOptPayment(@PathVariable("id") Integer id){
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				mOptPaymentService.deleteMOptionPayment(id)
				);
	}
}
