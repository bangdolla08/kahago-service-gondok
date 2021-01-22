package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.DeviceEnum;
import com.kahago.kahagoservice.enummodel.OptionPaymentEnum;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.DepositRequest;
import com.kahago.kahagoservice.model.request.OptionPaymentListRequest;
import com.kahago.kahagoservice.model.request.OptionPaymentReq;
import com.kahago.kahagoservice.model.request.PayPickupRequest;
import com.kahago.kahagoservice.model.response.*;
import com.kahago.kahagoservice.service.TopupService;
import com.kahago.kahagoservice.service.OptionPaymentService;
import com.kahago.kahagoservice.service.PaymentReqPickupService;
import com.kahago.kahagoservice.util.Common;

import io.swagger.annotations.ApiImplicitParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author bangd ON 22/11/2019
 * @project com.kahago.kahagoservice.controller
 */
@Controller
@BaseController
@ResponseBody
public class OptionPaymentController {
	
	private static final Logger log = LoggerFactory.getLogger(OptionPaymentController.class);

    @Autowired
    private OptionPaymentService optionPaymentService;
    @Autowired
    private PaymentReqPickupService payReqPickService;

    @GetMapping("/optionpayment/payment/list")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseWithRequest<String,List<OptionPaymentListResponse>> getOptionPayment(OptionPaymentListRequest optionPaymentListRequest,HttpServletRequest req){
    	log.info("==>Option payment List<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        ResponseWithRequest<String,List<OptionPaymentListResponse>> response=new ResponseWithRequest<>();
        response.setRequest(OptionPaymentEnum.PAYMENT.getTitle());
        response.setRc(ResponseStatus.OK.value());
        response.setDescription(ResponseStatus.OK.getReasonPhrase());
        List<OptionPaymentListResponse> listResponses=optionPaymentService
                .getOptionPaymentList(OptionPaymentEnum.PAYMENT,
                        optionPaymentListRequest.getUserId(),
                        optionPaymentListRequest.getNominal(),
                        optionPaymentListRequest.getUserAgent());
        response.setData(listResponses);
        return response;
    }

    @GetMapping("/optionpayment/topup/list")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<OptionPaymentListResponse>> getOptionTopUp(OptionPaymentListRequest optionPaymentListRequest,HttpServletRequest req){
    	log.info("==>Option payment top up<===");
    	log.info("device ==>"+optionPaymentListRequest.getUserAgent());
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                optionPaymentService	
                        .getOptionPaymentList(OptionPaymentEnum.TOP_UP,
                                optionPaymentListRequest.getUserId(),
                                optionPaymentListRequest.getNominal(),
                                optionPaymentListRequest.getUserAgent()));
    }
    
    @PostMapping("/optionpayment/payment/save")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<OptionPaymentListResponse> getOptionSavePay(@RequestBody OptionPaymentReq optionPaymentReq,HttpServletRequest req){
    	log.info("==> Option Payment Save <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	log.info("Request ==> "+Common.json2String(optionPaymentReq));
    	DeviceEnum devEnum = Common.getDevice(req.getHeader("User-Agent"));
    	return optionPaymentService.getSavePay(optionPaymentReq,devEnum);
    }
    
    @Autowired
    private TopupService dompetService;
    @PostMapping("/optionpayment/topup/save")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<OptionPaymentListResponse> topUp(@RequestBody DepositRequest depositRequest,HttpServletRequest req){
    	log.info("==> Option Payment topup Save <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	DeviceEnum devEnum = Common.getDevice(req.getHeader("User-Agent"));
        return new Response<>(
                ResponseStatus.OK.value(),
                ResponseStatus.OK.getReasonPhrase(),
                dompetService.topUpSaldo(depositRequest,devEnum));
    }
    
    @PostMapping("/optionpayment/payment/requestpickup")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<OptionPaymentListResponse> getOptionPayRequestPickup(@RequestBody PayPickupRequest payRequest,HttpServletRequest req){
    	log.info("==> Option Payment Pickup Save <==");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	log.info("Request ==> "+Common.json2String(payRequest));
    	DeviceEnum devEnum = Common.getDevice(req.getHeader("User-Agent"));
    	return payReqPickService.getSavePay(payRequest,devEnum);
    }
    
}
