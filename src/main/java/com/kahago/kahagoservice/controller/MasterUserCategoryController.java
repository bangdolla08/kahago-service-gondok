package com.kahago.kahagoservice.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.UserCategoryRequest;
import com.kahago.kahagoservice.model.response.MenuList;
import com.kahago.kahagoservice.model.response.OptionPaymentListResponse;
import com.kahago.kahagoservice.model.response.OptionPaymentResponse;
import com.kahago.kahagoservice.model.response.ResPickupTime;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.model.response.UserCategoryResponse;
import com.kahago.kahagoservice.model.response.VendorResponse;
import com.kahago.kahagoservice.service.MasterUserCategoryService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author Ibnu Wasis
 */
@BaseController
@ResponseBody
public class MasterUserCategoryController {
	@Autowired
	private MasterUserCategoryService masterUserCategoryService;
	
	@ApiOperation(value="List User Category")
	@GetMapping("/master/usercategory/list")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<UserCategoryResponse>> getAllUserCategory(){
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				masterUserCategoryService.getAllUserCategory()
				);
	}
	
	@ApiOperation(value="List Pickup Time By User Category")
	@GetMapping("/master/usercategory/pikcuptime/{idUserCategory}")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<List<ResPickupTime>> getPickupTimeUserCategory(@PathVariable()Integer idUserCategory){
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				masterUserCategoryService.getListPickupTimeByCategory(idUserCategory)
				);
	}
	
	@ApiOperation(value="List Vendor By User Category")
	@GetMapping("/master/usercategory/vendor/{idUserCategory}")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<List<VendorResponse>> getVendorByUserCategory(@PathVariable()Integer idUserCategory){
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				masterUserCategoryService.getAllVendorByUserCategory(idUserCategory)
				);
	}
	
	@ApiOperation(value="List Option Payment By User Category")
	@GetMapping("/master/usercategory/optionpayment/{idUserCategory}")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<List<OptionPaymentResponse>> getOptionPaymentByUserCategory(@PathVariable()Integer idUserCategory){
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				masterUserCategoryService.getAllOptionPaymentByUserCategory(idUserCategory)
				);
	}
	
	/*@ApiOperation(value="List Option Payment By User Category")
	@GetMapping("/master/usercategory/menu/{idUserCategory}")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<List<MenuList>> getMenuByUserCategory(@PathVariable()Integer idUserCategory){
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				masterUserCategoryService.getAllMenuByUserCategory(idUserCategory)
				);
	}*/
	
	@ApiOperation(value="Get User Category By Id")
	@GetMapping("/master/usercategory/{idUserCategory}")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<UserCategoryResponse> getUserCategoryById(@PathVariable()Integer idUserCategory){
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				masterUserCategoryService.getUserCategoryById(idUserCategory)
				);
	}
	
	@ApiOperation(value="Add Or Edit User Category")
	@PostMapping("/master/usercategory/save")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<SaveResponse> addOrEditUserCategory(@RequestBody UserCategoryRequest request,Principal principal){
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				masterUserCategoryService.addOrEditCategoryUser(request,principal.getName())
				);
	}
	
	@ApiOperation(value="Add Or Remove Pickup Time")
	@PostMapping("/master/usercategory/pikcuptime/save")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<SaveResponse> savePickupTimeUserCategory(@RequestBody UserCategoryRequest request){
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				masterUserCategoryService.addOrRemovePickupTimeUserCategory(request)
				);
	}
	
	@ApiOperation(value="Add Or Remove Vendor")
	@PostMapping("/master/usercategory/vendor/save")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<SaveResponse> saveVendorUserCategory(@RequestBody UserCategoryRequest request){
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				masterUserCategoryService.saveVendorUserCategory(request)
				);
	}
	
	@ApiOperation(value="Add Or Remove Option Payment")
	@PostMapping("/master/usercategory/optionpayment/save")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<SaveResponse> saveOptionPaymentUserCategory(@RequestBody UserCategoryRequest request){
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				masterUserCategoryService.saveOptionPaymentUserCategory(request)
				);
	}
	
	@ApiOperation(value="Add Or Remove Menu ")
	@PostMapping("/master/usercategory/menu/save")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<SaveResponse> saveMenuUserCategory(@RequestBody UserCategoryRequest request){
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				masterUserCategoryService.saveMenuByUserCategory(request)
				);
	}
}
