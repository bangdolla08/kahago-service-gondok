package com.kahago.kahagoservice.controller;

import io.swagger.annotations.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.BookRequest;
import com.kahago.kahagoservice.model.request.CancelBookReq;
import com.kahago.kahagoservice.model.request.CompleteBookReq;
import com.kahago.kahagoservice.model.request.PaylaterIssuedRequest;
import com.kahago.kahagoservice.model.response.BookDataResponse;
import com.kahago.kahagoservice.model.response.BookResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.ResponseGlobal;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.service.BookCounterService;
import com.kahago.kahagoservice.service.BookService;
import com.kahago.kahagoservice.service.EditBookService;
import com.kahago.kahagoservice.service.PaylaterService;
import com.kahago.kahagoservice.util.Common;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 24 Apr 2020
 */
@BaseController
@ResponseBody
@Validated
public class BookCounterController extends Controller {
	
	private static final Logger log = LoggerFactory.getLogger(BookCounterController.class);

    @Autowired
    private BookCounterService bookCounterService;
    @ApiOperation(value = "Booking", response = BookResponse.class)
    @PostMapping("/bookcounter")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> get(@RequestBody List<CompleteBookReq> request,Principal principal) throws JsonProcessingException {
    	log.info("==> Booking Counter Inisiate <==");
    	log.info("principal ==> "+principal.getName());
    	log.info("Request => "+ Common.json2String(request));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                bookCounterService.doBookCounter(request,principal.getName())
        );
    }
}
