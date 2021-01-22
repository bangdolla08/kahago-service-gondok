package com.kahago.kahagoservice.controller;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.BlastType;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.model.request.CouponDiscountListReq;
import com.kahago.kahagoservice.model.request.CouponDiscountRequest;
import com.kahago.kahagoservice.model.request.ImageRequest;
import com.kahago.kahagoservice.model.request.PageHeaderRequest;
import com.kahago.kahagoservice.model.response.CouponDiscountResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.service.CouponDiscountService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author Ibnu Wasis
 */
@BaseController
@ResponseBody
@Api(value="CRUD Coupon Discount",description="CRUD Operating Coupon Discount")
public class CouponDiscountController extends Controller{
	@Autowired
	private CouponDiscountService couponDiscountService;
	
	private static final Logger log = LoggerFactory.getLogger(CouponDiscountController.class);
	private ObjectMapper mapper;
	
	@GetMapping("coupon/list")
	@ApiOperation("List of Coupon Discount")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<List<CouponDiscountResponse>> getAll(CouponDiscountListReq request){
		
		Page<CouponDiscountResponse> lDiscount = couponDiscountService.getAllDiscount(request.getCode(), request.getReference(), request.getPageRequest());
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				extraPaging(lDiscount),
				lDiscount.getContent()				
				);
	}
	@PostMapping("coupon/save")
	@ApiOperation("Save Coupon Discount")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> saveCoupon(@RequestBody CouponDiscountRequest request,Principal principal)throws JsonProcessingException{
		mapper = new ObjectMapper();
		log.info("Request => "+ mapper.writeValueAsString(request));
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				couponDiscountService.saveAdd(request,principal.getName())
				);
	}
	
	@DeleteMapping("coupon/disable/{id}")
	@ApiOperation("Disable Coupon")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<SaveResponse> disableCoupon(@PathVariable("id")int id){
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				couponDiscountService.deleteCoupon(id)
				);
	}
	
	@PostMapping("coupon/upload")
	@ApiOperation("Buat Tes Upload Image aja")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public Response<String> upload(@RequestBody ImageRequest image){
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				couponDiscountService.uploadFile(image, "BLS-", "cobates", "-01")
				);
	}
	
	@PostMapping("coupon/edit")
	@ApiOperation("Edit Coupon")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<SaveResponse> saveEdit(@RequestBody CouponDiscountRequest request,Principal principal) throws JsonProcessingException{
		mapper = new ObjectMapper();
		log.info("Request => "+ mapper.writeValueAsString(request));
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				couponDiscountService.editSave(request,principal.getName())
				);
	}
	
	@GetMapping("/coupon/blast/{idBlast}")
	@ApiOperation("Blast Coupon")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<String> blastCoupon(@PathVariable("idBlast")Integer idBlast){
		couponDiscountService.blastPromo(idBlast, BlastType.COUPON);
		
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase()
				);
	}
	
	@GetMapping("/coupon/get/{idCoupon}")
	@ApiOperation("Get By id Coupon")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<CouponDiscountResponse> getById(@PathVariable("idCoupon") Integer idCoupon){
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				couponDiscountService.getById(idCoupon)
				);
	}
}
