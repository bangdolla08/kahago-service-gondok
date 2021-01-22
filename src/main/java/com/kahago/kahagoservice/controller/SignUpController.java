package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.request.RegistrationUser;
import com.kahago.kahagoservice.model.request.SignUpReq;
import com.kahago.kahagoservice.service.RegistrationService;
import com.kahago.kahagoservice.service.SignUpService;

import io.swagger.annotations.ApiImplicitParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.MessagingException;
import javax.validation.Valid;

/**
 * @author Hendro yuwono
 */
@Controller
@ResponseBody
@Validated
@BaseController
public class SignUpController {

    @Autowired
    private SignUpService signUpService;
    @Autowired
    private RegistrationService registerService;

    @PostMapping("/signup")
    public Response signUp(@RequestBody SignUpReq request) throws MessagingException {
        signUpService.saveRegister(request);

        return new Response(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase());
    }
    @PostMapping("/register")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response createUser(@RequestBody RegistrationUser reg) {
    	registerService.createUser(reg);
    	return new Response(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase());
    }
}
