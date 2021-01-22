package com.kahago.kahagoservice.controller;

import java.math.BigDecimal;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.response.DetailTrxResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.service.ReviewBookService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author Ibnu Wasis
 */
@BaseController
@ResponseBody
@Api(value="Review Book",description="Review of booking")
public class ReviewBookController {
	@Autowired
	private ReviewBookService reviewBookService;
	
	private static final Logger log = LoggerFactory.getLogger(ReviewBookController.class);
	
	@ApiOperation(value="Review Booking")
	@GetMapping("/review/{bookingCode}")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<DetailTrxResponse> getReviewBook(@PathVariable String bookingCode,HttpServletRequest req)throws NotFoundException{
		log.info("==>Review Book<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				reviewBookService.getReviewBook(bookingCode)
				);
	}
	
	@ApiOperation(value="Review verifikasi")
	@GetMapping("/review/verifikasi/{bookingCode}")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<DetailTrxResponse> getReviewVerifikasi(@PathVariable String bookingCode,HttpServletRequest req){
		log.info("==>Review Verifikasi<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				reviewBookService.getReviewVerifikasi(bookingCode)
				);
	}
	
	@ApiOperation(value="Review Paylater")
	@GetMapping("/review/paylater/{userId}")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<HashMap<String, BigDecimal>> getReviewPaylater(@PathVariable String userId,HttpServletRequest req){
		log.info("==>Review Paylater<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				reviewBookService.getReviewPaylater(userId)
				);
	}
	
	
}
