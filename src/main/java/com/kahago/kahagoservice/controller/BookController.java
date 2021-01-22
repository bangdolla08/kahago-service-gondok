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
import com.kahago.kahagoservice.model.request.PaylaterIssuedRequest;
import com.kahago.kahagoservice.model.response.BookDataResponse;
import com.kahago.kahagoservice.model.response.BookResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.ResponseGlobal;
import com.kahago.kahagoservice.service.BookService;
import com.kahago.kahagoservice.service.EditBookService;
import com.kahago.kahagoservice.service.PaylaterService;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author Hendro yuwono
 */
@BaseController
@ResponseBody
@Validated
public class BookController extends Controller {
	
	private static final Logger log = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookService bookService;
    @Autowired
    private EditBookService editBookService;
    @Autowired
    private PaylaterService paylaterService;
    
    private ObjectMapper mapper;
    @ApiOperation(value = "Booking", response = BookResponse.class)
    @PostMapping("/book")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<BookResponse> get(@RequestBody @Valid BookRequest booking,@RequestHeader("User-Agent") String header,Principal principal) throws JsonProcessingException {
    	mapper = new ObjectMapper();
    	log.info("==> Booking Inisiate <==");
    	log.info("=> Header : "+header);
    	log.info("principal ==> "+principal.getName());
    	log.info("Request => "+ mapper.writeValueAsString(booking));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                bookService.booking(booking,header)
        );
    }
    @ApiOperation(value = "Cancel-Booking", response = ResponseGlobal.class)
    @PostMapping("/book/cancel")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<String> getCancel(@RequestBody @Valid CancelBookReq booking,HttpServletRequest req) {
    	log.info("==>Cancel Booking<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        return bookService.cancelBook(booking);
    }
    
    @ApiOperation(value = "Edit-Booking", response = ResponseGlobal.class)
    @PostMapping("/book/edit")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<BookResponse> getEdit(@RequestBody @Valid BookRequest booking,HttpServletRequest req) {
    	log.info("==>Edit Booking<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                editBookService.editBook(booking)
        );
    }
    
    @ApiOperation(value = "Paylater", response = Response.class)
    @PostMapping("/book/paylater/list")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<BookDataResponse>> getPaylaterList(@RequestBody PaylaterIssuedRequest request,HttpServletRequest req) {
    	log.info("===> Paylater List <===");
    	log.info("device => "+req.getHeader("User-Agent"));
        return new Response<List<BookDataResponse>>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                paylaterService.getListPaylater(request)
        );
    }
    
    @ApiOperation(value = "Paylater Issued", response = ResponseGlobal.class)
    @PostMapping("/book/paylater/issued")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<BookDataResponse> getIssuedPaylater(@RequestBody PaylaterIssuedRequest request,HttpServletRequest req) {
    	log.info("===> Paylater issued <===");
    	log.info("device => "+req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                paylaterService.getIssuedPaylater(request)
        );
    }
}
