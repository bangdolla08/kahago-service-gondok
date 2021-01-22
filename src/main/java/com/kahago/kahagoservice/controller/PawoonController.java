package com.kahago.kahagoservice.controller;

import io.swagger.annotations.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.BookRequest;
import com.kahago.kahagoservice.model.request.CancelBookReq;
import com.kahago.kahagoservice.model.request.PawoonRequest;
import com.kahago.kahagoservice.model.request.PaylaterIssuedRequest;
import com.kahago.kahagoservice.model.response.BookDataResponse;
import com.kahago.kahagoservice.model.response.BookResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.ResponseGlobal;
import com.kahago.kahagoservice.service.BookService;
import com.kahago.kahagoservice.service.PawoonService;
import com.kahago.kahagoservice.service.PaylaterService;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 4 Des 2019
 */
@BaseController
@ResponseBody
@Validated
public class PawoonController extends Controller {
	
	private static final Logger log = LoggerFactory.getLogger(PawoonController.class);

    @Autowired
    private PawoonService pawoonService;
    @ApiOperation(value = "Pawoon Status", response = ResponseGlobal.class)
    @PostMapping("/pawoon/status")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<String> getStatusTrx(@RequestBody PawoonRequest req){
    	return pawoonService.getStatus(req);
    }
}
