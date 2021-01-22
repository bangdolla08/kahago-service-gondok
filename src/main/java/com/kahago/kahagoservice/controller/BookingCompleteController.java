package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.CompleteBookReq;
import com.kahago.kahagoservice.model.request.ListBookingCompleteReq;
import com.kahago.kahagoservice.model.response.BookDataResponse;
import com.kahago.kahagoservice.model.response.ListBookingCompleteResponse;
import com.kahago.kahagoservice.model.response.PickupOrderDetail;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.service.BookService;
import com.kahago.kahagoservice.service.RequestPickUpService;
import com.kahago.kahagoservice.util.Common;

import io.swagger.annotations.ApiImplicitParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author bangd ON 02/01/2020
 * @project com.kahago.kahagoservice.controller
 */

@BaseController
@ResponseBody
@Validated
public class BookingCompleteController extends Controller  {
    @Autowired
    private RequestPickUpService pickUpService;
    @Autowired
    private BookService bookService;
    
    private static final Logger log = LoggerFactory.getLogger(BookingCompleteController.class);
    
    @GetMapping("/bookinguncomplete")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<ListBookingCompleteResponse>> bookingList(ListBookingCompleteReq bookingCompleteReq,HttpServletRequest req) throws ParseException {
    	log.info("==> Booking uncomplete <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	Page<ListBookingCompleteResponse> completeResponses=pickUpService.getListBook(bookingCompleteReq,bookingCompleteReq.getPageRequest());
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                extraPaging(completeResponses),
                completeResponses.getContent());
    }
    @PostMapping("/bookinguncomplete/create")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> bookDataComplate(@RequestBody CompleteBookReq request,HttpServletRequest req,Principal principal){
    	log.info("==> Booking uncomplete Create<==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                bookService.complateBookingReq(request,principal.getName()));
    }
    
    @PostMapping("/bookinguncomplete/allcreate")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> allcompletebooking(@RequestBody List<CompleteBookReq> request,
    												  HttpServletRequest req,
    												  Principal principal){
    	log.info("==> Booking uncomplete Create All<==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	log.info("Payload => "+Common.json2String(request));
    	return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                bookService.completeBookingReqAll(request,principal.getName())
                );
    }
    @GetMapping("bookinguncomplete/{qrcode}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<PickupOrderDetail> getOrderPickup(@PathVariable(value="qrcode")  String qrcode,HttpServletRequest req){
    	log.info("==> Booking uncomplete By qrcode <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                    pickUpService.getPickupOrderDetail(qrcode));
    }
    
    @PostMapping("/bookinguncomplete/titipan")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> updateBookingCode(@RequestParam("bookingCode")String bookingCode,
    												@RequestParam("qrcode")String qrcode,
    												Principal principal,
    												HttpServletRequest req){
    	log.info("==> Update Booking Code <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			bookService.updateBookingCode(qrcode, bookingCode, principal.getName())
    			);
    }
}
