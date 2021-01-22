package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.CategoryOptionPaymentReq;
import com.kahago.kahagoservice.model.request.CategorySwitcherSaveReq;
import com.kahago.kahagoservice.model.request.UserPriorityRequest;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.model.response.UserCategoryResponse;
import com.kahago.kahagoservice.service.CategorySwitcherService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Hendro yuwono
 */
@BaseController
@ResponseBody
public class CategoryUserController {
	@Autowired
    private CategorySwitcherService categorySwitcherService;

    @PostMapping("category-user")
    public void save(CategorySwitcherSaveReq req) {
        categorySwitcherService.save(req);
    }
    
    @ApiOperation("Save User Category Option Payment")
    @PostMapping("/category/optionpayment")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> saveCategoryOptionPayment(CategoryOptionPaymentReq request){
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			categorySwitcherService.saveCategotyOptionPayment(request)
    			);
    }
    
    @ApiOperation("Save User Category User Priority")
    @PostMapping("/category/userpriority")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> saveCategoryUserPriority(@RequestBody UserPriorityRequest request){
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			categorySwitcherService.saveUserPriority(request)
    			);
    }
    
    @ApiOperation("List of Category Option Payment")
    @GetMapping("/category/optionpayment/list")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<UserCategoryResponse>> getAllPaymentCategory(){
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			categorySwitcherService.getAllOptionPaymentByCategoryUser()
    			);
    }
    
    @ApiOperation("List of Category User Priority")
    @GetMapping("/category/userpriority/list")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<UserCategoryResponse>> getAllUserPriority(){
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			categorySwitcherService.getAllUserPriority()
    			);
    }
    @ApiOperation("Edit Category User Priority")
    @PostMapping("/category/userpriority/edit")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> editCategoryUserPriority(@RequestBody UserPriorityRequest request){
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			categorySwitcherService.editUserPriority(request)
    			);
    }
    @ApiOperation("Delete of Category User Priority")
    @DeleteMapping("/category/optionpayment/delete")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> deleteCategoryOptionPayment(CategoryOptionPaymentReq request){
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			categorySwitcherService.deleteOptionPayment(request)
    			);
    }
    
    @ApiOperation("Delete of Category User Priority")
    @DeleteMapping("/category/switcher/delete")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> deleteCategorySwitcher(CategorySwitcherSaveReq request){
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			categorySwitcherService.deleteCategorySwitcher(request)
    			);
    }
    
    @ApiOperation("Delete of Category User Priority")
    @DeleteMapping("/category/userpriority/delete/{idUserCategory}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> deleteUserPriority(@PathVariable("idUserCategory") Integer idUserCategory){
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			categorySwitcherService.deleteUserPriority(idUserCategory)
    			);
    }
}
