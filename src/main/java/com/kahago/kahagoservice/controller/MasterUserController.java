package com.kahago.kahagoservice.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.MasterUserRequest;
import com.kahago.kahagoservice.model.response.MasterUserResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.service.MasterUserService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author Ibnu Wasis
 */
@BaseController
@ResponseBody
public class MasterUserController extends Controller{
	@Autowired
	private MasterUserService masterUserService;
	
	@ApiOperation(value="List Of Master User")
	@GetMapping("/master/user/list")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<MasterUserResponse>> getAllUser(MasterUserRequest request){
		Page<MasterUserResponse> listResp = masterUserService.getAllUser(request, request.getPageRequest());
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				extraPaging(listResp),
				listResp.getContent()
				);
	}
	
	@ApiOperation(value="Save of Master User")
	@PostMapping("/master/user/save")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> addUser(@RequestBody MasterUserRequest request,Principal principal){
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				masterUserService.addUser(request, principal.getName())
				);
	}
	
	@ApiOperation(value="Get User By User Id")
	@GetMapping("/master/user/get/{userId}")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<MasterUserResponse> getByUserId(@PathVariable("userId")String userId){
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				masterUserService.getUserByUserId(userId)
				);
	}
	
	@ApiOperation(value="Edit User")
	@PostMapping("/master/user/edit")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> editUser(@RequestBody MasterUserRequest request,Principal principal){
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				masterUserService.editUser(request, principal.getName())
				);
	}
	
	@ApiOperation(value="Change Password User")
	@PutMapping("/master/user/change/{userId}/{password}")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> changePassword(@PathVariable() String userId, @PathVariable() String password){
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				masterUserService.changePassword(password, userId)
				);
	}
}
