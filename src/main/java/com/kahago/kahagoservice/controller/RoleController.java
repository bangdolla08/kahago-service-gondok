package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.RoleResp;
import com.kahago.kahagoservice.model.response.ProfileRes;
import com.kahago.kahagoservice.service.RoleService;
import com.kahago.kahagoservice.service.UserService;
import com.kahago.kahagoservice.validation.UserMustExist;
import io.swagger.annotations.ApiImplicitParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 4 Mar 2020
 */
@BaseController
@ResponseBody
@Validated
public class RoleController extends Controller {

    @Autowired
    private RoleService roleService;
    
    private static final Logger log = LoggerFactory.getLogger(RoleController.class);

    @GetMapping("/role/list")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<RoleResp>> getRole(HttpServletRequest req,Principal principal) {
    	log.info("==> User profile <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	log.info("Username ==>"+principal.getName());
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(), roleService.getRoleResp());
    }

}
