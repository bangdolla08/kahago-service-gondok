package com.kahago.kahagoservice.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.kahago.kahagoservice.model.request.ManifestListRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.kahago.kahagoservice.configuration.BaseController;
import com.kahago.kahagoservice.enummodel.ResponseStatus;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.request.ManifestPickupRequest;
import com.kahago.kahagoservice.model.request.PickupAddressRequest;
import com.kahago.kahagoservice.model.response.ManifestPickup;
import com.kahago.kahagoservice.model.response.ManifestPickupResponse;
import com.kahago.kahagoservice.model.response.Response;
import com.kahago.kahagoservice.service.ManifestPickupService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author Ibnu Wasis
 */
@BaseController
@ResponseBody
@Api(value="Manifest Pickup", description="Operating about Manifest Courier")
public class ManifestPickupController extends Controller {
	@Autowired
	private ManifestPickupService pickupService;
	
	private static final Logger log = LoggerFactory.getLogger(ManifestPickupController.class);
	@ApiOperation(value="List of Manifest Courier")
	@PostMapping("/pickups")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<List<ManifestPickup>> getManifest(@RequestBody @Valid ManifestPickupRequest request,HttpServletRequest req)throws NotFoundException{
		log.info("==>List of Manifest Courier<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		if(request.getStatusCode()==null)request.setStatusCode(0);
		List<ManifestPickup> lmanifest = pickupService.findByCourierId(request);
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				lmanifest
				);
	}
	@ApiOperation(value= "Detail Pickup")
	@PostMapping("/pickups/detail")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<List<ManifestPickupResponse>> getDetailManifest(@RequestBody @Valid ManifestPickupRequest request,HttpServletRequest req) throws NotFoundException{
		log.info("==>Detail pickup<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		List<ManifestPickupResponse> ldetailManifest = pickupService.getDetailManifest(request);
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				ldetailManifest
				);
	}
	
	@ApiOperation(value="Detail Pickup By QRCode")
	@GetMapping("/pickups/detail/{param}")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<ManifestPickupResponse> getDetailByQrcode(@PathVariable(value="param",required=true) String param,@RequestParam(value="userId",required=true)String userId,HttpServletRequest req)throws NotFoundException{
		log.info("==>Detail by Param<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				pickupService.getDetailManifestByQrCode(param,userId)
				);
	}
	
	
	public Response<String> saveManifest(@RequestPart(value="file",required=false) MultipartFile file,@RequestParam(value="noManifest",required=true) String noManifest,
			@RequestParam(value="bookingCode",required=true) String bookingCode,@RequestParam(value="qrcode",required=true)String qrcode,@RequestParam(value="CourierId", required=true) String userId,HttpServletRequest req){
		log.info("==>Save Manifest<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		if(qrcode == null)qrcode = "";
		return pickupService.getSaveManifest(file, bookingCode, noManifest, qrcode, userId);
	}
	@ApiOperation(value="Save manifest pickup")
	@PostMapping(value="/pickups/save",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<String> saveManifest(String file,@RequestParam(value="noManifest",required=true) String noManifest,
			@RequestParam(value="bookingCode",required=true) String bookingCode,@RequestParam(value="qrcode",required=true)String qrcode,@RequestParam(value="CourierId", required=true) String userId,HttpServletRequest req){
		log.info("==>Save Manifest<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		if(qrcode == null)qrcode = "";
		return pickupService.getSaveManifestFile(file, bookingCode, noManifest, qrcode, userId);
	}
	@ApiOperation(value="Update Pickup Address when first time pickup")
	@PostMapping("/pickups/edit")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<String> updatePickupAddress(@RequestBody @Valid PickupAddressRequest request,HttpServletRequest req){
		log.info("==>Update pickup address<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		return pickupService.editPickupAddress(request);
	}
	
	@ApiOperation(value="get Notif pickup")
	@GetMapping("/pickups/notifotw")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<String> getNotifOtw(@RequestParam(value="courierId")String courierId,@RequestParam(value="bookingCode") String bookingCode){
		return pickupService.getNotifOtw(courierId, bookingCode);
	}

	@ApiOperation(value="update/add detail pickup request")
	@PostMapping(value="/pickups/pickuprequest",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<String> updateDetailPickupRequest(  @RequestParam("images")String images,
														@RequestParam(value="courierId",required=true) String userId,
														@RequestParam(value="qrcodeExt",required=true)String qrcodeExt,
														@RequestParam(value="qrcode",required=false)String qrcode,
														@RequestParam(value="pickupRequestId",required=true)String orderId,
														HttpServletRequest req){
		log.info("==>Add detail pickup request<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
				
		return pickupService.updatePickupRequest(userId, qrcode, orderId, qrcodeExt,images);
	}
	
	@ApiOperation(value="get Detail Pickup Request")
	@GetMapping("/pickups/pickuprequest/detail")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<ManifestPickupResponse> getDetailPickupRequest(@RequestParam(value="courierId",required=true) String userId,
																	@RequestParam(value="pickupRequestId",required=true)String orderId,
																	HttpServletRequest req){
		log.info("==>Detail pickup request<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		return new Response<>(
				ResponseStatus.OK.value(),
				ResponseStatus.OK.getReasonPhrase(),
				pickupService.getDetailRequestPickup(userId, orderId)
				);
	}
	
	@ApiOperation(value="Save manifest request pickup")
	@PostMapping("/pickups/pickuprequest/save")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<String> saveManifestRequestPickup(@RequestPart(value="file",required=false) MultipartFile file,@RequestParam(value="noManifest",required=true) String noManifest,
			@RequestParam(value="pickupOrderId",required=true) String orderId,@RequestParam(value="courierId", required=true) String courierId,HttpServletRequest req){
		log.info("==>save manifest pickup request<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
		return pickupService.getSaveManifestRequestPickup(file, courierId, noManifest, orderId);
	}
	
	@ApiOperation(value="Delete manifest request detail pickup")
	@DeleteMapping("/pickups/pickuprequest/delete")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
	public Response<String> deleteManifestRequestPickup(@RequestParam(value="qrcode",required=true)String qrcode,HttpServletRequest req){
		log.info("==>Delete manifest pickup request<===");
    	log.info("device ==>"+req.getHeader("User-Agent"));
    	return pickupService.deleteRequestDetail(qrcode);
	}
	
	
	
}
