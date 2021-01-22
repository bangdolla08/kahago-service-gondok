package com.kahago.kahagoservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.VendorGoodsRequest;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.model.response.VendorGoodsResponse;
import com.kahago.kahagoservice.service.VendorGoodsService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author Ibnu Wasis
 */
@BaseController
@ResponseBody
public class VendorGoodsController {
	@Autowired
	private VendorGoodsService vGoodsService;
	
	@GetMapping("/goodsvendor/list")
	@ApiOperation("List Of Vendor Goods")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<VendorGoodsResponse>> getAllVendor(){
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				vGoodsService.getVendor()
				);
	}
	
	@GetMapping("/goodsvendor/{switcherCode}")
	@ApiOperation("get Good By Switcher Code")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<VendorGoodsResponse>> getBySwitcherCode(@PathVariable("switcherCode") Integer switcherCode,
    															 @RequestParam(name="productSwCode",required=false)Integer productSwCode){
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				vGoodsService.getAllGoodsByProduct(productSwCode, switcherCode)
				);
	}
	
	@PostMapping("/goodsvendor/save")
	@ApiOperation("Save/Edit Goods Vendor")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> saveVendorGoods(@RequestBody VendorGoodsRequest request){
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				vGoodsService.saveVendorGoods(request)
				);
	}
}
