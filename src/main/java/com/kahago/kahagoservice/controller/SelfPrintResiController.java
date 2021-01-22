package com.kahago.kahagoservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.service.SelfPrintResiService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author Ibnu Wasis
 */
@BaseController
@ResponseBody
@Api(description="Digunakan Untuk cetak Resi sendiri")
public class SelfPrintResiController {
	@Autowired
	private SelfPrintResiService selfPrintResiService;
	
	@ApiOperation(value="Cetak/Terbit Resi Sendiri")
	@PostMapping("/printresi")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<SaveResponse> printResi(@RequestParam("bookingCode")String bookingCode,
											@RequestParam("userId")String userId){
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				selfPrintResiService.PrintResi(bookingCode, userId)
				);
	}
}
