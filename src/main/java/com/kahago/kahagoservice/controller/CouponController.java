package com.kahago.kahagoservice.controller;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.DiscountReq;
import com.kahago.kahagoservice.model.response.CouponRes;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.service.DiscountService;
import io.swagger.annotations.ApiImplicitParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Hendro yuwono
 */
@BaseController
@ResponseBody
public class CouponController {

    @Autowired
    private DiscountService discountService;
    
    private static final Logger log = LoggerFactory.getLogger(CouponController.class);

    @GetMapping("/coupon")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<CouponRes>> getAllCoupon(@RequestParam String userId,HttpServletRequest req) {
    	log.info("==>List of coupon<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
        return new Response<>(ResponseStatus.OK.value(), ResponseStatus.OK.getReasonPhrase(), discountService.checkDiscount(userId));
    }
    
    @PostMapping("/coupon/voucher/{action}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response getSaveCoupon(@RequestBody DiscountReq req,@PathVariable String action,HttpServletRequest request,Principal principal) {
    	log.info("==>List of coupon<===");
    	log.info("device ==>"+request.getHeader("User-Agent"));
    	log.info("==> user "+principal.getName());
    	if(req.getUserId()==null) req.setUserId(principal.getName());
        return discountService.validasiVoucher(req,action);
    }
    
}
