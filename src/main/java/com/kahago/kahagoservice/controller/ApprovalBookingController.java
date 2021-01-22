package com.kahago.kahagoservice.controller;

import java.security.Principal;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.AppBookingRequest;
import com.kahago.kahagoservice.model.request.PageHeaderRequest;
import com.kahago.kahagoservice.model.request.TotalTrxRequest;
import com.kahago.kahagoservice.model.response.BookDataResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.model.response.TotalTrxResponse;
import com.kahago.kahagoservice.schedulling.PaymentSchedulling;
import com.kahago.kahagoservice.service.ApprovalBookingService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author Ibnu Wasis
 */
@BaseController
@ResponseBody
public class ApprovalBookingController extends Controller{
	@Autowired
	private ApprovalBookingService approvalBookingService;
	
	
	private static final Logger log = LoggerFactory.getLogger(ApprovalBookingController.class);
	
	@ApiOperation(value="List of Approval Booking")
	@GetMapping("/payment/list")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<List<BookDataResponse>> getListPayment(AppBookingRequest request,HttpServletRequest req){
		log.info("===>List Approval Booking<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
		Page<BookDataResponse> lpayment =
				approvalBookingService.getListAppBooking(
						request.getPageRequest(),
						request.getUserId(),
						request.getStatus(),
						request.getBookingCode(),request.getSearchString(),
						request.getSwitcherCode());
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				this.extraPaging(lpayment),
				lpayment.getContent()
				);
	}
	
	@ApiOperation(value="List of Approval Booking")
	@GetMapping("/payment/detail/{bookingCode}")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<BookDataResponse> getDetailBooking(@PathVariable(value="bookingCode")String bookingCode,HttpServletRequest req) {
		log.info("===>Detail Booking<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				approvalBookingService.getDetailBooking(bookingCode)
				);
	}
	
	@ApiOperation(value="Get Total Transaksi")
	@PostMapping("/payment/totaltrx")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<TotalTrxResponse> getTotalTrx(@RequestBody TotalTrxRequest request,
												  HttpServletRequest req){
		log.info("===>Total Transaksi<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				approvalBookingService.getTotalTrx(request)
				);
	}
	
//	@ApiOperation(value="Get Notif Expired Payment")
//	@GetMapping("/payment/notif/{bookingCode}")
//	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
//	public Response<String> getNotif(@PathVariable("bookingCode")String bookingCode){
//		paymentSchedulling.NotifExpiredBook(bookingCode);
//		return new Response<>(
//				ResponseStatus.OK.value(),
//				ResponseStatus.OK.getReasonPhrase()				
//				);
//	}
	
	@ApiOperation("List payment from Booking Complete")
	@GetMapping("/payment/bookingcomplete/list")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<List<BookDataResponse>> getPaymentOfBookingComplete(AppBookingRequest request,HttpServletRequest req){
		log.info("===>List Payment from Booking Complete<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
		Page<BookDataResponse> lpayment = approvalBookingService.getListBookingComplete(
				request.getPageRequest(),
				request.getUserId(), 
				request.getBookingCode(),
				request.getSearchString(),
				request.getSwitcherCode());
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				extraPaging(lpayment),
				lpayment.getContent()
				);
	}
	
	@ApiOperation(value="Get Total Transaksi In corier/DRAFT")
	@PostMapping("/payment/totalalltrx")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<TotalTrxResponse> getAllTotalTrx(@RequestBody TotalTrxRequest request,HttpServletRequest req){
		log.info("===>Total All Transaksi<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
    	
    	return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				approvalBookingService.getTotalAllTrx(request)
				);
	}
	@ApiOperation(value="Get Total Transaksi In corier/DRAFT")
	@PostMapping("/payment/totalalltrxlate")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<TotalTrxResponse> getAllTotalTrxLate(@RequestBody TotalTrxRequest request,HttpServletRequest req){
		log.info("===>Total All Transaksi<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
    	
    	return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				approvalBookingService.getTotalAllTrxLate(request)
				);
	}
	@ApiOperation(value="Get Total Booking UnComplete")
	@PostMapping("/payment/totaluncomplte")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<TotalTrxResponse> getAllTotalUncomplete(@RequestBody TotalTrxRequest request,HttpServletRequest req){
		log.info("===>Total Transaksi Uncomplete<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
    	
    	return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				approvalBookingService.getAllBookingUnComplete(request)
				);
	}
	
	@ApiOperation(value="Get Total Manifest")
	@PostMapping("/payment/totalmanifest")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<TotalTrxResponse> getAllTotalManifest(@RequestBody TotalTrxRequest request,HttpServletRequest req){
		log.info("===>Total Manifest<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
    	
    	return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				approvalBookingService.getTotalManifest(request)
				);
	}
	
	@ApiOperation(value="Cancel Booking By Admin")
	@PostMapping("/payment/cancelbooking")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<SaveResponse> cancelBooking(@RequestParam()String bookingCode,@RequestParam() String reason,Principal principal,HttpServletRequest req){
		log.info("===>Cancel Booking<===");
    	log.info("device ==> "+req.getHeader("User-Agent"));
    	return new Response<>(
    			ResponseStatus.OK.value(),
    			ResponseStatus.OK.getReasonPhrase(),
    			approvalBookingService.doResetBookingByBookingCode(bookingCode,reason, principal.getName())
    			);
	}
	
}
