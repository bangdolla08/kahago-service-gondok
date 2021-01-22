package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.EditProfileReq;
import com.kahago.kahagoservice.model.request.PasswordReq;
import com.kahago.kahagoservice.model.request.UserRequest;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.StatusResponse;
import com.kahago.kahagoservice.model.response.ProfileRes;
import com.kahago.kahagoservice.model.response.UserListRes;
import com.kahago.kahagoservice.model.response.UserOfCategoryResp;
import com.kahago.kahagoservice.service.UserService;
import com.kahago.kahagoservice.validation.UserMustExist;
import io.swagger.annotations.ApiImplicitParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Email;
import java.security.Principal;
import java.util.List;

/**
 * @author Hendro yuwono
 */
@BaseController
@ResponseBody
@Validated
public class UserController extends Controller {

    @Autowired
    private UserService userService;
    
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/user")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<UserOfCategoryResp>> findAll(Pageable pageable) {
        Page<UserOfCategoryResp> profileDtos = userService.findAllUser(pageable);
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(), extraPaging(profileDtos), profileDtos.getContent());
    }

    @PutMapping("/user/{id}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.OK)
    public Response editUser(@PathVariable String id, @RequestBody UserRequest request) {
        userService.editUser(id, request);
        return new Response(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase());
    }

    @GetMapping("/user/{id}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<UserOfCategoryResp> getUser(@PathVariable String id) {
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(), userService.findByIds(id));
    }

    @GetMapping("/user/profile")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<ProfileRes> showProfile(@RequestParam(name = "user_id") @UserMustExist String userId,HttpServletRequest req) {
    	log.info("==> User profile <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(), userService.profileUser(userId));
    }

//    @PutMapping("/user/profile/{id}")
//    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
//    public Response<ProfileRes> editProfile(@PathVariable String id, @RequestBody UserRequest request, Principal principal) {
//        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(), userService.profileUser(principal.getName()));
//    }

    @PutMapping("/user/profile/{userId}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<ProfileRes.Profile> editProfile(@RequestBody EditProfileReq request, @PathVariable @UserMustExist String userId,HttpServletRequest req) {
//        userService.saveEdit(request, userId);
    	log.info("==> User profile By userId <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(ResponseStatus.OK.value(), 
        			ResponseStatus.OK.getReasonPhrase(),
        			userService.getEditProfile(request, userId));
    }

    @PostMapping("/user/profile/{userId}/password")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<String> editPassword(@RequestBody PasswordReq request, @PathVariable @Email @UserMustExist String userId,HttpServletRequest req) {
    	log.info("==> User profile by password <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	userService.changePassword(request, userId);
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase());
    }

    @GetMapping("/user/forgot")
    public Response<String> forgetPassword(@RequestParam(name = "user_id") @Email @UserMustExist String userId,HttpServletRequest req) throws MessagingException {
    	log.info("==> forgot password <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	userService.forgotPassword(userId);
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase());
    }

    @GetMapping("/user/courierlist")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<UserListRes>> getUserId(String userId,HttpServletRequest req) {
    	log.info("==> Courier List <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                userService.getUserDriver());
    }
    @GetMapping("/user/list")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<UserListRes>> getListUserId(HttpServletRequest req) {
    	log.info("==> Courier List <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                userService.getListUserPickupRequest());
    }
    @GetMapping("/user/list/all")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<UserListRes>> getListAll(HttpServletRequest req) {
    	log.info("==> Courier List <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                userService.getListUserPickupRequest());
    }
    
    @PostMapping("/user/newpass")
    public Response<String> newPass(@RequestBody PasswordReq request,HttpServletRequest req){
    	log.info("==> New password <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	return userService.newPass(request);
    }
    
    @GetMapping("/user/cekunittype")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<StatusResponse> getUnitType(@RequestParam("office_code") String officeCode,Principal principal){
    	log.info("==> Cek Unit Type <==");
    	log.info("==> Request ==> "+officeCode);
    	
    	return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                userService.cekUser(officeCode, principal.getName()));
    }

}
