package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.service.BarangTitipanService;

import io.swagger.annotations.ApiImplicitParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

/**
 * @author Hendro yuwono
 */
@BaseController
@ResponseBody
public class BarangTitipanController {

    @Autowired
    private BarangTitipanService barangTitipanService;

    @GetMapping("/barangtitipan/return")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> status(String qrcode, String reason, Principal principal) {
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(), barangTitipanService.rejectBarang(qrcode, principal.getName(),reason));
    }
}
